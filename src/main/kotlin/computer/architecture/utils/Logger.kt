package computer.architecture.utils

import computer.architecture.cpu.*

class Logger {

    companion object {

        fun log(
            fetchResult: FetchResult,
            decodeResult: DecodeResult,
            executionResult: ExecutionResult,
            memoryAccessResult: MemoryAccessResult,
            writeBackResult: WriteBackResult
        ) {
            fetchLog(fetchResult)
            decodeLog(decodeResult)
            executeLog(executionResult)
            memoryAccessLog(memoryAccessResult)
            writeBackLog(writeBackResult)
        }

        fun fetchLog(fetchResult: FetchResult) {
            if (!LoggingSignal.fetchLogging) return

            printStep("IF")
            println(
                "pc: ${fetchResult.pc}, " + " origin : 0x${((fetchResult.pc) * 4).toHexString(2)}, instruction : 0x${
                    (fetchResult.instruction.toHexString(
                        8
                    ))
                }"
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
        }

        fun executeLog(executionResult: ExecutionResult) {
            if (!LoggingSignal.executeLogging) return
            printStep("EX")
            println(
                "result : ${executionResult.aluResult} [0x${executionResult.aluResult.toHexString()}], " +
                        "nextPc : ${executionResult.nextPc}"
            )
        }

        fun memoryAccessLog(memoryAccessResult: MemoryAccessResult) {
        }

        fun writeBackLog(writeBackResult: WriteBackResult) {
            printStep("WB")
            if (writeBackResult.regWrite) {
                println("R[${writeBackResult.writeRegister}] = ${writeBackResult.writeData}")
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
    }
}

class LoggingSignal {

    companion object {

        var decodeLogging = false
        var fetchLogging = false
        var executeLogging = false
        var writeBackLogging = false
        var finalValue = false

        fun init(
            decodeLogging: Boolean = false,
            fetchLogging: Boolean = false,
            executeLogging: Boolean = false,
            writeBackLogging: Boolean = false,
            finalValue: Boolean = false
        ) {
            this.decodeLogging = decodeLogging
            this.fetchLogging = fetchLogging
            this.executeLogging = executeLogging
            this.writeBackLogging = writeBackLogging
            this.finalValue = finalValue
        }
    }
}
