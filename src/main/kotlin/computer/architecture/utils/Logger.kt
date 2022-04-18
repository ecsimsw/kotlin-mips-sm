package computer.architecture.utils

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.Opcode

class Logger {

    companion object {

        fun decodeLog(decodeResult: DecodeResult) {
            if (!LoggingSignal.decodeLogging) return
            if (decodeResult.opcode == Opcode.SLL && decodeResult.shiftAmt == 0) return

            val opcode = decodeResult.opcode
            print("opcode : ${opcode}, ")

            if (opcode.type == Opcode.Type.R) {
                println("readData1 : ${decodeResult.readData1}, readData2 : ${decodeResult.readData2}")
            }

            if (opcode.type == Opcode.Type.I) {
                println(
                    "readData1 : ${decodeResult.readData1}, " +
                            "immediate : ${decodeResult.immediate.toString(16).uppercase()} [${decodeResult.immediate}]"
                )
            }

            if (opcode.type == Opcode.Type.J) {
                println("address : ${decodeResult.address.toString(16)}")
            }
        }

        fun fetchLog(pc: Int, instruction: Int) {
            if (!LoggingSignal.fetchLogging) return
            println("pc : ${pc * 4}, instruction : 0x${Integer.toHexString(instruction).uppercase()}")
        }
    }
}

class LoggingSignal {

    companion object {

        var decodeLogging = false
        var fetchLogging = false

        fun init(
            decodeLogging: Boolean = false,
            fetchLogging: Boolean = false
        ) {
            this.decodeLogging = decodeLogging
            this.fetchLogging = fetchLogging
        }
    }
}
