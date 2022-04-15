package computer.architecture.single.log

import computer.architecture.single.DecodeResult
import computer.architecture.single.Opcode

class Logger {

    companion object {

        fun decodeLog(
            op: Int,
            function: Int,
            rs: Int,
            rt: Int,
            rd: Int,
            shiftAmt: Int,
            immediate: Int,
            address: Int,
        ) {
            if (!LoggingSignal.decodeLogging) return
            if (op == 0 && function == 0 && shiftAmt == 0) return

            val opcode = Opcode.of(op, function)
            print("opcode : $opcode, ")

            if (opcode.type == Opcode.Type.R) println("rs : $rs, rt : $rt, rd : $rd, shamt : $shiftAmt, funct : $function")
            if (opcode.type == Opcode.Type.I) println(
                "rs : $rs, rt : $rt, immediate : ${
                    immediate.toString(16).uppercase()
                }"
            )
            if (opcode.type == Opcode.Type.J) println("addr ${address.toString(16)}")
        }

        fun decodeLog(
            decodeResult: DecodeResult
        ) {
            if (!LoggingSignal.decodeLogging) return
            if (decodeResult.opcode == Opcode.SLL && decodeResult.shiftAmt == 0) return

            val opcode = decodeResult.opcode
            print("opcode : ${opcode}, ")

            if (opcode.type == Opcode.Type.R) {
                println("readData1 : ${decodeResult.readData1}, readData2 : ${decodeResult.readData2}")
            }

            if (opcode.type == Opcode.Type.I) {
                println(
                    "readData1 : $${decodeResult.readData1}, immediate : ${decodeResult.immediate} / ${
                        decodeResult.immediate.toString(
                            16
                        ).uppercase()
                    }"
                )
            }

            if (opcode.type == Opcode.Type.J) {
                println("addr : ${decodeResult.address.toString(16)}")
            }
        }

        fun fetchLog(pc: Int, instruction: Int) {
            if (!LoggingSignal.fetchLogging) return
            println("pc : ${pc * 4}, instruction : 0x${instruction.toString(16)}")
        }
    }
}
