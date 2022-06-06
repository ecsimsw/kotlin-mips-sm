package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Or.Companion.or
import computer.architecture.cpu.ALUnit
import computer.architecture.cpu.DecodeUnit
import computer.architecture.cpu.StallUnit
import computer.architecture.cpu.cache.WriteThroughDirectMappedCache
import computer.architecture.cpu.dto.*
import computer.architecture.cpu.register.Registers
import computer.architecture.utils.Logger

abstract class SingleProcessingPipelineControlUnit(
    memory: Memory,
) : IControlUnit {
    protected val registers = Registers(32)
    protected val stallUnit = StallUnit()
    protected val latches = Latches()

    private val cache = WriteThroughDirectMappedCache(memory, 4, 8)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()

    abstract fun cycleExecution(valid: Boolean, pc: Int): CycleResult

    override fun process(): List<Int> {
        var cycle = 0
        var cycleResult = CycleResult()
        var isEnd = false

        Logger.init()
        while (true) {
            Logger.printCycle(cycle)

            isEnd = or(isEnd, cycleResult.isEnd)
            val pc = stallUnit.next(cycleResult.nextPc)
            val valid = stallUnit.valid && !isEnd

            cycleResult = cycleExecution(valid, pc)

            if (cycleResult.lastCycle) {
                return listOf(cycleResult.value)
            }

            latches.flushAll()
            cycle++
        }
    }

    fun fetch(valid: Boolean, pc: Int): FetchResult {
        if (!valid) {
            return FetchResult(valid, 0, 0)
        }
        val instruction = cache.read(pc)
        return FetchResult(
            valid = valid && (instruction != 0),
            pc = pc,
            instruction = instruction
        )
    }

    fun decode(ifResult: FetchResult): DecodeResult {
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(ifResult.valid, instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        var nextPc = mux(controlSignal.jump, instruction.address, ifResult.pc)
        nextPc = mux(controlSignal.jr, registers[instruction.rs], nextPc)

        return DecodeResult(
            valid = ifResult.valid,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readReg1 = instruction.rs,
            readReg2 = instruction.rt,
            readData1 = registers[instruction.rs],
            readData2 = registers[instruction.rt],
            writeReg = writeRegister,
            jump = controlSignal.jump || controlSignal.jr,
            nextPc = nextPc,
            controlSignal = controlSignal
        )
    }

    fun execute(idResult: DecodeResult): ExecutionResult {
        val controlSignal = idResult.controlSignal
        val aluValue = alu.execute(idResult)

        val branchCondition = and(aluValue == 1, controlSignal.branch)
        return ExecutionResult(
            valid = idResult.valid,
            pc = idResult.pc,
            readData2 = idResult.readData2,
            writeReg = idResult.writeReg,
            aluValue = aluValue,
            nextPc = idResult.immediate,
            branch = branchCondition,
            controlSignal = controlSignal
        )
    }

    fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        val controlSignal = exResult.controlSignal

        val memReadValue = if (controlSignal.memRead) cache.read(exResult.aluValue) else 0
        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)
        if (controlSignal.memWrite) {
            cache.write(exResult.aluValue, exResult.readData2)
        }

        return MemoryAccessResult(
            valid = exResult.valid,
            pc = exResult.pc,
            regWriteValue = regWriteValue,
            address = exResult.aluValue,
            memReadValue = memReadValue,
            memWriteValue = exResult.readData2,
            writeReg = exResult.writeReg,
            controlSignal = controlSignal
        )
    }

    fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (maResult.controlSignal.regWrite) {
            registers.write(
                register = maResult.writeReg,
                data = maResult.regWriteValue,
            )
        }

        return WriteBackResult(
            valid = maResult.valid,
            pc = maResult.pc,
            regWriteValue = maResult.regWriteValue,
            writeReg = maResult.writeReg,
            controlSignal = maResult.controlSignal
        )
    }
}
