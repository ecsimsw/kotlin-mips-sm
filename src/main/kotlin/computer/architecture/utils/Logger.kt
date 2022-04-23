package computer.architecture.utils

import computer.architecture.component.Memory
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

    fun cycleCount(cycleCount: Int) {
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

    fun fetchLog(cycleCount: Int, fetchResult: FetchResult) {
        executedInstructionSet.add(fetchResult.instruction)

        if (!loggingSignal.fetch) return

        printStep("IF")
        println(
            "cyl : $cycleCount" +
                    " , pc : 0x${(fetchResult.pc).toHexString(2)}, " +
                    "instruction : 0x${fetchResult.instruction.toHexString(8)}"
        )
    }

    fun decodeLog(decodeResult: DecodeResult) {
        val opCodeCount = executedOpcodes.getOrDefault(decodeResult.opcode, 0)
        executedOpcodes[decodeResult.opcode] = opCodeCount + 1

        val opCodeTypeCount = executedOpcodeType.getOrDefault(decodeResult.opcode.type, 0)
        executedOpcodeType[decodeResult.opcode.type] = opCodeTypeCount + 1

        if (!loggingSignal.decode) return

        printStep("ID")
        print("opcode : ${decodeResult.opcode}, ")

        if (decodeResult.opcode.type == Opcode.Type.R) {
            println("readData1 : ${decodeResult.readData1}, readData2 : ${decodeResult.readData2}")
        }

        if (decodeResult.opcode.type == Opcode.Type.I) {
            println(
                "readData1 : ${decodeResult.readData1} [0x${decodeResult.readData1.toHexString()}], " +
                        "immediate : ${decodeResult.immediate} [0x${decodeResult.immediate.toHexString()}]"
            )
        }

        if (decodeResult.opcode.type == Opcode.Type.J) {
            println("address : 0x${decodeResult.address.toHexString()}")
        }
    }

    fun executeLog(controlSignal: ControlSignal, executionResult: ExecutionResult) {
        if (controlSignal.branch && executionResult.aluValue == 1) {
            numberOfTakenBranches++
        }

        if (!loggingSignal.execute) return

        printStep("EX")
        println(
            "result : ${executionResult.aluValue} [0x${executionResult.aluValue.toHexString()}], " +
                    "nextPc : 0x${executionResult.nextPc.toHexString()}"
        )
    }

    fun memoryAccessLog(controlSignal: ControlSignal, memory: Memory, address: Int) {
        if (controlSignal.memRead || controlSignal.memWrite) {
            numberOfExecutedMA++
        }

        if (!loggingSignal.memoryAccess) return

        printStep("MA")
        if (controlSignal.memRead || controlSignal.memWrite) {
            println(
                "M[0x${address.toHexString()}] = " +
                        "${memory.read(address)} [0x${memory.read(address).toHexString()}]"
            )
        } else {
            println()
        }
    }

    fun writeBackLog(writeBackResult: WriteBackResult) {
        if (writeBackResult.regWrite) {
            numberOfWriteBack++
        }

        if (!loggingSignal.writeBack) return

        printStep("WB")
        if (writeBackResult.regWrite) {
            println("R[${writeBackResult.writeRegister}] = ${writeBackResult.writeData} [0x${writeBackResult.writeData.toHexString()}]")
        } else {
            println()
        }
        println()
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
