package computer.architecture.utils

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.Opcode

class Logger {

    companion object {

        fun fetchLog(pc: Int, instruction: Int) {
            if (!LoggingSignal.fetchLogging) return

            printStep("IF")
            println(
                "pc: ${pc}, " + " origin : 0x${(pc * 4).toHexString(2)}, instruction : 0x${(instruction.toHexString(8))}"
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
                    "readData1 : ${decodeResult.readData1}, immediate : ${decodeResult.immediate} [0x${decodeResult.immediate.toHexString()}]"
                )
            }

            if (decodeResult.opcode.type == Opcode.Type.J) {
                println("address : 0x${decodeResult.address.toHexString()}")
            }

//            println("address : " + decodeResult.address)
//            println("address as binary : " + decodeResult.address.toBinaryString(32))
//            println("address as hex : " + Integer.toHexString(decodeResult.address))
        }

        fun executeLog(executionResult: ExecutionResult) {
            if (!LoggingSignal.executeLogging) return
            printStep("EXE")
            println(
                "result : ${executionResult.aluResult} [0x${executionResult.aluResult.toHexString()}], " +
                        "nextPc : ${executionResult.nextPc}"
            )
        }

        fun breakLine() {
            if (!LoggingSignal.breakLine) return
            println()
        }

        private fun printStep(stepName: String) {
            print("[$stepName] :: ")
        }
    }
}

class LoggingSignal {

    companion object {

        var decodeLogging = false
        var fetchLogging = false
        var executeLogging = false
        var breakLine = false

        fun init(
            decodeLogging: Boolean = false,
            fetchLogging: Boolean = false,
            executeLogging: Boolean = false,
            breakLine: Boolean = false
        ) {
            this.decodeLogging = decodeLogging
            this.fetchLogging = fetchLogging
            this.executeLogging = executeLogging
            this.breakLine = breakLine
        }
    }
}
