package computer.architecture.utils

import computer.architecture.component.Memory
import computer.architecture.cpu.*

class Logger(
    private val memory: Memory
) {
    fun cycleCount(cycleCount: Int) {
        sleep()
        if (!LoggingSignal.cycleLogging) return
        if (cycleCount % 100000 == 0) {
            println("cycle : $cycleCount")
        }
    }

    fun fetchLog(cycleCount: Int, fetchResult: FetchResult) {
        if (!LoggingSignal.fetchLogging) return

        printStep("IF")
        println(
            "cyl : $cycleCount" +
                    " , pc : 0x${(fetchResult.pc).toHexString(2)}, " +
                    "instruction : 0x${fetchResult.instruction.toHexString(8)}"
        )
    }

    fun decodeLog(decodeResult: DecodeResult) {
        if (!LoggingSignal.decodeLogging) return

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
        if (!LoggingSignal.decodeLogging) return
        println("[ID] :: rs : ${result.rs}, rt : ${result.rt}, rd : ${result.rd}")
    }

    fun executeLog(executionResult: ExecutionResult) {
        if (!LoggingSignal.executeLogging) return
        printStep("EX")
        println(
            "result : ${executionResult.aluResult} [0x${executionResult.aluResult.toHexString()}], " +
                    "nextPc : 0x${executionResult.nextPc.toHexString()}"
        )
    }

    fun memoryAccessLog(controlSignal: ControlSignal, address: Int) {
        if (!LoggingSignal.memoryAccessLogging) return

        printStep("MA")
        if (controlSignal.memRead || controlSignal.memWrite) {
            println(
                "M[0x${address.toHexString()}] = ${memory.readInt(address)} [0x${
                    memory.readInt(address).toHexString()
                }]"
            )
        } else {
            println()
        }
    }

    fun writeBackLog(writeBackResult: WriteBackResult) {
        if (!LoggingSignal.writeBackLogging) return

        printStep("WB")
        if (writeBackResult.regWrite) {
            println("R[${writeBackResult.writeRegister}] = ${writeBackResult.writeData} [0x${writeBackResult.writeData.toHexString()}]")
        } else {
            println()
        }
        println()
    }

    private fun sleep(sleepTime: Long = LoggingSignal.sleepTime) {
        try {
            Thread.sleep(sleepTime)
        } catch (e: InterruptedException) {

        }
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
        var cycleLogging = false
        var fetchLogging = false
        var decodeLogging = false
        var executeLogging = false
        var memoryAccessLogging = false
        var writeBackLogging = false
        var finalValue = false
        var sleepTime = 0L

        fun init(
            cycleLogging: Boolean = false,
            fetchLogging: Boolean = false,
            decodeLogging: Boolean = false,
            executeLogging: Boolean = false,
            memoryAccessLogging: Boolean = false,
            writeBackLogging: Boolean = false,
            finalValue: Boolean = false,
            sleepTime: Long = 0L
        ) {
            this.cycleLogging = cycleLogging
            this.fetchLogging = fetchLogging
            this.decodeLogging = decodeLogging
            this.executeLogging = executeLogging
            this.memoryAccessLogging = memoryAccessLogging
            this.writeBackLogging = writeBackLogging
            this.finalValue = finalValue
            this.sleepTime = sleepTime
        }
    }
}
