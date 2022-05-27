package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.*
import computer.architecture.cpu.register.Registers
import computer.architecture.utils.Logger

class ControlUnit_SingleCycle(
    private val memory: Memory,
    private val logger: Logger
) : IControlUnit {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()

    override fun process(): Int {
        var cycle = 0
        var cycleResult = CycleResult()

        while (true) {
            logger.printCycle(cycle)

            val pc = cycleResult.nextPc
            if (pc == -1) {
                return cycleResult.value
            }

            cycleResult = cycleExecution(pc)
            cycle++
        }
    }

    private fun cycleExecution(pc: Int): CycleResult {
        val ifResult = fetch(pc)
        val idResult = decode(ifResult)
        val exResult = execute(idResult)
        val maResult = memoryAccess(exResult)
        val wbResult = writeBack(maResult)

        logger.log(ifResult, idResult, exResult, maResult, wbResult)

        val nextPc = mux(exResult.jump, exResult.nextPc, pc + 4)
        return CycleResult(
            nextPc = nextPc,
            valid = true,
            value = registers[2]
        )
    }

    private fun fetch(pc: Int): FetchResult {
        val instruction = memory.read(pc)
        return FetchResult(true, pc, instruction)
    }

    private fun decode(ifResult: FetchResult): DecodeResult {
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(opcode = instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            valid = true,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readData1 = registers[instruction.rs],
            readData2 = registers[instruction.rt],
            writeReg = writeRegister,
            controlSignal = controlSignal
        )
    }

    private fun execute(idResult: DecodeResult): ExecutionResult {
        val controlSignal = idResult.controlSignal
        val aluValue = alu.execute(idResult)

        val branchCondition = and(aluValue == 1, controlSignal.branch)
        var nextPc = mux(branchCondition, idResult.immediate, idResult.pc)
        nextPc = mux(controlSignal.jump, idResult.address, nextPc)
        nextPc = mux(controlSignal.jr, idResult.readData1, nextPc)

        return ExecutionResult(
            valid = true,
            pc = idResult.pc, // TODO :: only for logging
            readData2 = idResult.readData2,
            writeReg = idResult.writeReg,
            aluValue = aluValue,
            nextPc = nextPc,
            jump = (branchCondition || controlSignal.jump || controlSignal.jr),
            controlSignal = controlSignal
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        val controlSignal = exResult.controlSignal
        val memReadValue = memory.read(
            memRead = controlSignal.memRead,
            address = exResult.aluValue,
        )

        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)

        memory.write(
            memWrite = controlSignal.memWrite,
            address = exResult.aluValue,
            value = exResult.readData2
        )

        return MemoryAccessResult(
            valid = true,
            pc = exResult.pc, // TODO :: only for logging
            regWriteValue = regWriteValue,
            writeReg = exResult.writeReg,
            controlSignal = controlSignal
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (maResult.controlSignal.regWrite) {
            registers.write(
                register = maResult.writeReg,
                data = maResult.regWriteValue,
            )
        }

        return WriteBackResult(
            valid = maResult.valid,
            pc = maResult.pc, // TODO :: only for logging
            writeReg = maResult.writeReg,
            regWriteValue = maResult.regWriteValue,
            controlSignal = maResult.controlSignal
        )
    }
}
