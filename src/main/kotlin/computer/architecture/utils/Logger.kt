package computer.architecture.utils

import computer.architecture.cpu.*

class Logger(
    private val loggingSignal: LoggingSignal
) {
    private var cycleCount = 0
    private var numberOfExecutedMA = 0
    private var numberOfWriteBack = 0
    private var numberOfTakenBranches = 0
    private val executedOpcodes = mutableMapOf<Opcode, Int>()
    private val executedOpcodeType = mutableMapOf<Opcode.Type, Int>()
    private val executedInstructionSet = mutableSetOf<Int>()

    private val cycleLogs = Array(5) { CycleLog(0) }

    companion object {
        fun init(
            cycle: Boolean = false,
            fetch: Boolean = false,
            decode: Boolean = false,
            execute: Boolean = false,
            memoryAccess: Boolean = false,
            writeBack: Boolean = false,
            resultInformation: Boolean = false,
            sleepTime: Long = 0L
        ): Logger {
            val signals = LoggingSignal(
                cycle = cycle,
                fetch = fetch,
                decode = decode,
                execute = execute,
                memoryAccess = memoryAccess,
                writeBack = writeBack,
                resultInformation = resultInformation,
                sleepTime = sleepTime
            )
            return Logger(signals)
        }
    }

    fun save(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        cycleLogs[0].fetchResult = fetchResult
        cycleLogs[1].decodeResult = decodeResult
        cycleLogs[2].executionResult = executionResult
        cycleLogs[3].memoryAccessResult = memoryAccessResult
        cycleLogs[4].writeBackResult = writeBackResult
    }

    fun flushCycleLog() {
        printCycleLog(cycleLogs[4])
        cycleLogs[4] = cycleLogs[3]
        cycleLogs[3] = cycleLogs[2]
        cycleLogs[2] = cycleLogs[1]
        cycleLogs[1] = cycleLogs[0]
    }

    private fun printCycleLog(cycleLog: CycleLog) {
        cycleCount(cycleLog.fetchResult.cycle)
        fetchLog(cycleLog.fetchResult)
        decodeLog(cycleLog.decodeResult)
        executeLog(cycleLog.executionResult)
        memoryAccessLog(cycleLog.memoryAccessResult)
        writeBackLog(cycleLog.writeBackResult)
    }

    private fun cycleCount(cycleCount: Int) {
        this.cycleCount = cycleCount

        try {
            Thread.sleep(loggingSignal.sleepTime)
            if (!loggingSignal.cycle) return
            if (this.cycleCount % 1000000 == 0) {
                println("cycle : ${this.cycleCount}")
            }
        } catch (e: InterruptedException) {
        }
    }

    private fun fetchLog(result: FetchResult) {
        executedInstructionSet.add(result.instruction)
        if (!loggingSignal.fetch) return

        printStep("IF")
        print("cyl : ${result.cycle}, ")
        print("pc : 0x${(result.pc).toHexString(2)}, ")
        print("instruction : 0x${result.instruction.toHexString(8)}")
        println()
    }

    private fun decodeLog(result: DecodeResult) {
        val opcode = result.controlSignal.opcode
        val opCodeCount = executedOpcodes.getOrDefault(opcode, 0)
        executedOpcodes[opcode] = opCodeCount + 1

        val opCodeTypeCount = executedOpcodeType.getOrDefault(opcode.type, 0)
        executedOpcodeType[opcode.type] = opCodeTypeCount + 1

        if (!loggingSignal.decode) return

        printStep("ID")
        var msg = "opcode : ${opcode}, "
        if (opcode.type == Opcode.Type.R) {
            msg += "readData1 : ${result.readData1}, readData2 : ${result.readData2}"
        }

        if (opcode.type == Opcode.Type.I) {
            msg += "readData1 : ${result.readData1} [0x${result.readData1.toHexString()}], " +
                    "immediate : ${result.immediate} [0x${result.immediate.toHexString()}]"
        }

        if (opcode.type == Opcode.Type.J) {
            msg += "address : 0x${result.address.toHexString()}"
        }
        println(msg)
    }

    private fun executeLog(result: ExecutionResult) {
        if (result.controlSignal.branch && result.aluValue == 1) numberOfTakenBranches++
        if (!loggingSignal.execute) return

        printStep("EX")
        val msg = "result : ${result.aluValue} [0x${result.aluValue.toHexString()}], " +
                "nextPc : 0x${result.nextPc.toHexString()}"
        println(msg)
    }

    private fun memoryAccessLog(result: MemoryAccessResult) {
        val controlSignal = result.controlSignal
        if (controlSignal.memRead || controlSignal.memWrite) numberOfExecutedMA++
        if (!loggingSignal.memoryAccess) return

        printStep("MA")
        var msg = ""
        if (controlSignal.memRead) {
            msg = "M[0x${result.aluValue.toHexString()}] = ${result.readData} [0x${result.readData.toHexString()}]"
        }
        if (controlSignal.memWrite) {
            msg = "M[0x${result.aluValue.toHexString()}] = ${result.writeData} [0x${result.writeData.toHexString()}]"
        }
        println(msg)
    }

    private fun writeBackLog(result: WriteBackResult) {
        if (result.controlSignal.regWrite) numberOfWriteBack++
        if (!loggingSignal.writeBack) return

        printStep("WB")
        var msg = ""
        if (result.regWrite) {
            msg = "R[${result.writeRegister}] = ${result.writeData} [0x${result.writeData.toHexString()}]"
        }
        println(msg + "\n")
    }

    fun printProcessResult(resultValue: Int) {
        if (!loggingSignal.resultInformation) return

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

    private fun printStep(stepName: String) {
        print("[$stepName] :: ")
    }

    private fun Int.toHexString(): String {
        return Integer.toHexString(this).uppercase()
    }

    private fun Int.toHexString(digits: Int): String {
        val hexString = Integer.toHexString(this)
        var newBinary = hexString
        if (newBinary.length < digits) {
            for (i in 0 until digits - hexString.length) {
                newBinary = "0$newBinary"
            }
        }
        return newBinary.uppercase()
    }
}

data class LoggingSignal(
    var cycle: Boolean = false,
    var fetch: Boolean = false,
    var decode: Boolean = false,
    var execute: Boolean = false,
    var memoryAccess: Boolean = false,
    var writeBack: Boolean = false,
    var resultInformation: Boolean = false,
    var sleepTime: Long = 0L
)

class CycleLog(
    private val cycle: Int
) {
    var fetchResult = FetchResult(cycle)
    var decodeResult = DecodeResult()
    var executionResult = ExecutionResult()
    var memoryAccessResult = MemoryAccessResult()
    var writeBackResult = WriteBackResult()
}
