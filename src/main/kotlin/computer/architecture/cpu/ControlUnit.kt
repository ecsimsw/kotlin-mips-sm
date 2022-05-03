package computer.architecture.cpu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latch
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val logger: Logger = Logger.init(),
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()
    private val latch = Latch()

    fun process(): Int {
        var cycle = 1

        while (registers.pc != -1) {
            cycle++
            val ifResult = fetch(cycle, registers.pc)
            val idResult = decode(latch.popFetchResult())
            val exResult = execute(latch.popDecodeResult())
            val maResult = memoryAccess(latch.popExecutionResult())
            val wbResult = writeBack(latch.popMemoryAccessResult())

            latch.pushFetchResult(ifResult)
            latch.pushDecodeResult(idResult)
            latch.pushExecutionResult(exResult)
            latch.pushMemoryAccessResult(maResult)
            latch.pushWriteBackResult(wbResult)
            latch.flush()

            logger.save(
                fetchResult = ifResult,
                decodeResult = idResult,
                executionResult = exResult,
                memoryAccessResult = maResult,
                writeBackResult = wbResult
            )
            logger.flushCycleLog()
        }
        return registers[2]
    }

    private fun fetch(cycle: Int, pc: Int): FetchResult {
        val fetchResult = FetchResult(cycle, registers.pc, memory.read(pc))
        registers.pc += 4
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val instruction = decodeUnit.parse(registers.pc, fetchResult.instruction)
        val controlSignal = decodeUnit.controlSignal(instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readData1 = registers[instruction.rs],
            readData2 = registers[instruction.rt],
            writeRegister = writeRegister,
            controlSignal = controlSignal
        )
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        val controlSignal = decodeResult.controlSignal
        var src1 = mux(controlSignal.shift, decodeResult.readData2, decodeResult.readData1)
        src1 = mux(controlSignal.upperImm, decodeResult.immediate, src1)

        var src2 = mux(controlSignal.aluSrc, decodeResult.immediate, decodeResult.readData2)
        src2 = mux(controlSignal.shift, decodeResult.shiftAmt, src2)
        src2 = mux(controlSignal.upperImm, 16, src2)

        val aluResult = alu.operate(
            aluOp = controlSignal.aluOp,
            src1 = src1,
            src2 = src2
        )

        var nextPc = registers.pc

        val branchCondition = and(aluResult.isTrue, controlSignal.branch)
        nextPc = mux(branchCondition, decodeResult.immediate, nextPc)
        nextPc = mux(controlSignal.jump, decodeResult.address, nextPc)
        nextPc = mux(controlSignal.jr, decodeResult.readData1, nextPc)

        return ExecutionResult(
            aluValue = aluResult.value,
            memWriteData = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister,
            nextPc = nextPc,
            controlSignal = controlSignal
        )
    }

    private fun memoryAccess(executionResult: ExecutionResult): MemoryAccessResult {
        val controlSignal = executionResult.controlSignal
        val readData = memory.read(
            memRead = controlSignal.memRead,
            address = executionResult.aluValue,
        )

        memory.write(
            memWrite = controlSignal.memWrite,
            address = executionResult.aluValue,
            value = executionResult.memWriteData
        )

        return MemoryAccessResult(
            readData = readData,
            writeData = executionResult.memWriteData,
            aluValue = executionResult.aluValue,
            writeRegister = executionResult.writeRegister,
            nextPc = executionResult.nextPc,
            controlSignal = controlSignal
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        val controlSignal = memoryAccessResult.controlSignal
        var writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluValue)
        writeData = mux(controlSignal.jal, registers.pc + 4, writeData)

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )

        registers.pc = memoryAccessResult.nextPc

        return WriteBackResult(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData,
            nextPc = memoryAccessResult.nextPc,
            controlSignal = controlSignal
        )
    }
}
