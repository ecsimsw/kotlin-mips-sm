package computer.architecture.single.log

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
            if(op == 0 && function == 0 && shiftAmt == 0) return

            val opcode = Opcode.of(op, function)
            print("opcode : $opcode, ")

            if (opcode.type == Opcode.Type.R) println("rs : $rs, rt : $rt, rd : $rd, shamt : $shiftAmt, funct : $function")
            if (opcode.type == Opcode.Type.I) println("rs : $rs, rt : $rt, immediate : ${immediate.toString(16).uppercase()}")
            if (opcode.type == Opcode.Type.J) println("addr ${address.toString(16)}")
        }
    }
}
