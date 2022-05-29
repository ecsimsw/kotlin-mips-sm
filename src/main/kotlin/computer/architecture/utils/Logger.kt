package computer.architecture.utils

import computer.architecture.cpu.*
import computer.architecture.cpu.Opcode

open class Logger(
    private val loggingSignal: LoggingSignal
) {
    private var cycleCount = 0
    private var numberOfExecutedMA = 0
    private var numberOfWriteBack = 0
    private var numberOfTakenBranches = 0
    private var executedOpcodes = mutableMapOf<Opcode, Int>()
    private var executedOpcodeType = mutableMapOf<Opcode.Type, Int>()
    private var executedInstructionSet = mutableSetOf<Int>()

    companion object {
        val NONE = Logger(LoggingSignal())
        val RESULT_ONLY = Logger(LoggingSignal(result = true))
    }

    fun init() {
        cycleCount = 0
        numberOfExecutedMA = 0
        numberOfWriteBack = 0
        numberOfTakenBranches = 0
        executedOpcodes = mutableMapOf()
        executedOpcodeType = mutableMapOf()
        executedInstructionSet = mutableSetOf()
    }

    open fun log(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        collect(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
        printCycleLog(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
    }

    open fun printCycleLog(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        printFetchResult(fetchResult)
        printDecodeResult(decodeResult)
        printExecutionResult(executionResult)
        printMemoryAccessResult(memoryAccessResult)
        printWriteBackResult(writeBackResult)
    }

    open fun collect(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        executedInstructionSet.add(fetchResult.instruction)

        if (decodeResult.valid) {
            executedOpcodes[decodeResult.controlSignal.opcode] =
                executedOpcodes.getOrDefault(decodeResult.controlSignal.opcode, 0) + 1
            executedOpcodeType[decodeResult.controlSignal.opcode.type] =
                executedOpcodeType.getOrDefault(decodeResult.controlSignal.opcode.type, 0) + 1
        }

        if (executionResult.valid) {
            if (executionResult.controlSignal.branch && executionResult.aluValue == 1) {
                numberOfTakenBranches++
            }
        }

        if (memoryAccessResult.valid) {
            if (memoryAccessResult.controlSignal.memRead || memoryAccessResult.controlSignal.memWrite) {
                numberOfExecutedMA++
            }
        }

        if (writeBackResult.valid) {
            if (writeBackResult.controlSignal.regWrite) {
                numberOfWriteBack++
            }
        }
    }

    fun printCycle(printOrNot: Boolean, cycleCount: Int) {
        checkPrintRange(cycleCount)
        if(!printOrNot) {
            return
        }
        this.cycleCount = cycleCount
        try {
            Thread.sleep(loggingSignal.sleepTime)
            if (!loggingSignal.cycle) return
            if (this.cycleCount % loggingSignal.cyclePrintPeriod == 0) {
                println("cycle : ${this.cycleCount}")
            }
        } catch (e: InterruptedException) {
        }
    }

    private fun checkPrintRange(cycleCount: Int) {
        if (cycleCount >= loggingSignal.from && cycleCount <= loggingSignal.to) {
            loggingSignal.cycle = true
            loggingSignal.fetch = true
            loggingSignal.decode = true
            loggingSignal.execute = true
            loggingSignal.memoryAccess = true
            loggingSignal.writeBack = true
        } else {
            loggingSignal.cycle = false
            loggingSignal.fetch = false
            loggingSignal.decode = false
            loggingSignal.execute = false
            loggingSignal.memoryAccess = false
            loggingSignal.writeBack = false
        }
    }

    fun printCycle(cycleCount: Int) {
       printCycle(true, cycleCount)
    }

    private fun printFetchResult(result: FetchResult) {
        if (!loggingSignal.fetch) return
        if (!result.valid) {
            printStep("IF", result.pc)
            printNop()
            return
        }

        printStep("IF", result.pc)
        print("pc : 0x${(result.pc).toHexString(2)}, ")
        print("instruction : 0x${result.instruction.toHexString(8)}")
        println()
    }

    private fun printDecodeResult(result: DecodeResult) {
        val opcode = result.controlSignal.opcode
        if (!loggingSignal.decode) return
        if (!result.valid) {
            printStep("ID", result.pc)
            printNop()
            return
        }

        printStep("ID", result.pc)

        var msg = "opcode : ${opcode}, "
        msg += "rs : ${result.readReg1}, rt : ${result.readReg2} "
        if (opcode.type == Opcode.Type.R) {
            msg += "readData1 : ${result.readData1}, readData2 : ${result.readData2}"
        }

        if (opcode.type == Opcode.Type.I) {
            msg += "readData1 : ${result.readData1} [0x${result.readData2.toHexString()}], " +
                    "immediate : ${result.immediate} [0x${result.immediate.toHexString()}]"
        }

        if (opcode.type == Opcode.Type.J) {
            msg += "address : 0x${result.address.toHexString()}"
        }
        println(msg)
    }

    private fun printExecutionResult(result: ExecutionResult) {
        if (!loggingSignal.execute) return
        if (!result.valid) {
            printStep("EX", result.pc)
            printNop()
            return
        }

        printStep("EX", result.pc)
        val msg = "result : ${result.aluValue} [0x${result.aluValue.toHexString()}], " +
                "nextPc : 0x${result.nextPc.toHexString()}"
        println(msg)
    }

    private fun printMemoryAccessResult(result: MemoryAccessResult) {
        if (!loggingSignal.memoryAccess) return
        if (!result.valid) {
            printStep("MA", result.pc)
            printNop()
            return
        }
        printStep("MA", result.pc)
        var msg = ""
        if (result.controlSignal.memRead) {
            msg = "M[0x${result.address.toHexString()}] = ${result.memReadValue} [0x${result.memReadValue.toHexString()}]"
        }
        if (result.controlSignal.memWrite) {
            msg = "M[0x${result.address.toHexString()}] = ${result.memWriteValue} [0x${result.memWriteValue.toHexString()}]"
        }
        println(msg)
    }

    private fun printWriteBackResult(result: WriteBackResult) {
        if (!loggingSignal.writeBack) return
        if (!result.valid) {
            printStep("WB", result.pc)
            printNop()
            println()
            return
        }

        printStep("WB", result.pc)
        var msg = ""
        if (result.controlSignal.regWrite) {
            msg = "R[${result.writeReg}] = ${result.regWriteValue} [0x${result.regWriteValue.toHexString()}]"
        }
        println(msg + "\n")
    }

    fun printProcessResult(resultValue: Int) {
        if (!loggingSignal.result) return

        println("=== Result === ")
        println("cycle count : $cycleCount")
        println("result value : $resultValue")
        println()

        println("=== executed instructions ===")
        println("executed memory access : $numberOfExecutedMA")
        println("executed taken branches : $numberOfTakenBranches")
        println("executed write back : $numberOfWriteBack")
        println("kinds : ${executedInstructionSet.size}")
        print("[] : ")
        executedInstructionSet.forEach { print("0x${it.toHexString(8)} ") }
        println()
        println()

        println("=== executed opcode type ===")
        println("type R : ${executedOpcodeType.getOrDefault(Opcode.Type.R, 0)}")
        println("type I : ${executedOpcodeType.getOrDefault(Opcode.Type.I, 0)}")
        println("type J : ${executedOpcodeType.getOrDefault(Opcode.Type.J, 0)}")
        println()

        println("=== executed opcode === ")
        println("kinds : ${executedOpcodes.size}")
        print("[] : ")
        executedOpcodes.forEach { print("$it ") }
        println()
    }

    private fun printStep(stepName: String, pc: Int) {
        print("[$stepName] [$pc] :: ")
    }

    private fun printNop() {
        println("[NOP]")
    }
}
