package computer.architecture.utils

import computer.architecture.cpu.*

class Logger {

    companion object {

        fun fetchLog(fetchResult: FetchResult) {
            if (!LoggingSignal.fetchLogging) return

            printStep("IF")
            println(
                "pc: ${fetchResult.pc}, " + " origin : 0x${(fetchResult.pc).toHexString(2)}, " +
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

        fun executeLog(executionResult: ExecutionResult) {
            if (!LoggingSignal.executeLogging) return
            printStep("EX")
            println(
                "result : ${executionResult.aluResult} [0x${executionResult.aluResult.toHexString()}], " +
                        "nextPc : ${executionResult.nextPc.toHexString()}"
            )
        }

        fun memoryAccessLog(executionResult: ExecutionResult) {
            if (!LoggingSignal.memoryAccessLogging) return

            printStep("MA")
        }


        fun memoryRead(address: Int, value: Int) {
            if (!LoggingSignal.memoryAccessLogging) return
            printStep("MA")
            println("M[0x${address.toHexString()}] = $value [0x${value.toHexString()}]")
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

        fun finalValue(vo: Int) {
            printStep("RESULT")
            println("V0 : $vo [0x${vo.toHexString()}]")
        }

        private fun printStep(stepName: String) {
            print("[$stepName] :: ")
        }

        fun instructionDecode(result: InstructionDecodeResult) {
            if (!LoggingSignal.decodeLogging) return
            println("[ID] :: rs : ${result.rs}, rt : ${result.rt}, rd : ${result.rd}")
        }

        fun sleep() {
            try {
                Thread.sleep(LoggingSignal.sleepTime)
            }catch (e : InterruptedException) {

            }
        }
    }
}

class LoggingSignal {

    companion object {

        var decodeLogging = false
        var fetchLogging = false
        var executeLogging = false
        var memoryAccessLogging = false
        var writeBackLogging = false
        var finalValue = false
        var sleepTime = 0L

        fun init(
            decodeLogging: Boolean = false,
            fetchLogging: Boolean = false,
            executeLogging: Boolean = false,
            memoryAccessLogging: Boolean = false,
            writeBackLogging: Boolean = false,
            finalValue: Boolean = false,
            sleepTime: Long = 1000L
        ) {
            this.decodeLogging = decodeLogging
            this.fetchLogging = fetchLogging
            this.executeLogging = executeLogging
            this.memoryAccessLogging = memoryAccessLogging
            this.writeBackLogging = writeBackLogging
            this.finalValue = finalValue
            this.sleepTime = sleepTime
        }
    }
}
