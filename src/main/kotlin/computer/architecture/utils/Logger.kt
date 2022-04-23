package computer.architecture.utils

import computer.architecture.component.Memory
import computer.architecture.cpu.*

class Logger {
    private var cycleCount = 0
    private var numberOfExecutedMA = 0
    private var numberOfWriteBack = 0
    private var numberOfTakenBranches = 0
    private val executedOpcodes = mutableMapOf<Opcode, Int>()
    private val executedOpcodeType = mutableMapOf<Opcode.Type, Int>()
    private val executedInstructionSet = mutableSetOf<Int>()

    fun cycleCount(cycleCount: Int) {
        this.cycleCount = cycleCount

        try {
            Thread.sleep(LoggingSignal.sleepTime)
            if (!LoggingSignal.cycle) return
            if (this.cycleCount % 1000000 == 0) {
                println("cycle : ${this.cycleCount}")
            }
        } catch (e: InterruptedException) {
        }
    }

    fun fetchLog(cycleCount: Int, fetchResult: FetchResult) {
        executedInstructionSet.add(fetchResult.instruction)

        if (!LoggingSignal.fetch) return

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

        if (!LoggingSignal.decode) return

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

    fun instructionDecode(result: DecodedInstruction) {
        if (!LoggingSignal.decode) return
        println("[ID] :: rs : ${result.rs}, rt : ${result.rt}, rd : ${result.rd}")
    }

    fun executeLog(executionResult: ExecutionResult) {
        if (executionResult.branchCondition) {
            numberOfTakenBranches++
        }

        if (!LoggingSignal.execute) return

        printStep("EX")
        println(
            "result : ${executionResult.aluResultValue} [0x${executionResult.aluResultValue.toHexString()}], " +
                    "nextPc : 0x${executionResult.nextPc.toHexString()}"
        )
    }

    fun memoryAccessLog(memRead: Boolean, memWrite: Boolean, memory: Memory, address: Int) {
        if (memRead || memWrite) {
            numberOfExecutedMA++
        }

        if (!LoggingSignal.memoryAccess) return

        printStep("MA")
        if (memRead || memWrite) {
            println(
                "M[0x${address.toHexString()}] = " +
                        "${memory.readInt(address)} [0x${memory.readInt(address).toHexString()}]"
            )
        } else {
            println()
        }
    }

    fun writeBackLog(writeBackResult: WriteBackResult) {
        if (writeBackResult.regWrite) {
            numberOfWriteBack++
        }

        if (!LoggingSignal.writeBack) return

        printStep("WB")
        if (writeBackResult.regWrite) {
            println("R[${writeBackResult.writeRegister}] = ${writeBackResult.writeData} [0x${writeBackResult.writeData.toHexString()}]")
        } else {
            println()
        }
        println()
    }

    fun printProcessResult(resultValue: Int) {
        if (!LoggingSignal.resultInformation) return

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

class LoggingSignal {
    companion object {
        var cycle = false
        var fetch = false
        var decode = false
        var execute = false
        var memoryAccess = false
        var writeBack = false
        var finalValue = false
        var resultInformation = false
        var sleepTime = 0L

        fun init(
            cycle: Boolean = false,
            fetch: Boolean = false,
            decode: Boolean = false,
            execute: Boolean = false,
            memoryAccess: Boolean = false,
            writeBack: Boolean = false,
            resultInformation: Boolean = false,
            sleepTime: Long = 0L
        ) {
            this.cycle = cycle
            this.fetch = fetch
            this.decode = decode
            this.execute = execute
            this.memoryAccess = memoryAccess
            this.writeBack = writeBack
            this.resultInformation = resultInformation
            this.sleepTime = sleepTime
        }
    }
}
