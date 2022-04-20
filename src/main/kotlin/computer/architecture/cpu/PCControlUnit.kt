package computer.architecture.cpu

import computer.architecture.utils.toBinaryString

class PCControlUnit {
    fun jump(
        controlSignal: ControlSignal,
        pc: Int,
        bcond : Boolean,
        rsValue: Int,
        address: Int,
        immediate: Int
    ): PCControlResult {
        if (controlSignal.jumpRegister) {
            return PCControlResult(rsValue)
        }

        if (controlSignal.jump) {
            return PCControlResult(jumpAddress(pc, address))
        }

        if(controlSignal.branch && !bcond) {
            println("BRANCH!!")
            val branchPc = pc + branchAddress(immediate)
            return PCControlResult(branchPc)
        }
        return PCControlResult(pc)
    }

    private fun jumpAddress(pc :Int, address: Int): Int {
        val first4bit = pc shr 26
        val last28bit = address shl 2
        return first4bit + last28bit
    }

    private fun branchAddress(immediate: Int) :Int {
        val first = immediate.toBinaryString(16).first()
        var binNum = ""
        for(i:Int in 1..14) {
            binNum += first
        }
        binNum += immediate.toBinaryString(16)
        binNum += "00"
        return binNum.toLong(2).toInt()
    }
}

data class PCControlResult(
    val pc: Int
)
