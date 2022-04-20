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
            val jumpAddress = jumpAddress(pc, address)
            return PCControlResult(jumpAddress)
        }

        if(controlSignal.branch && bcond) {
            val branchPc = pc + branchAddress(immediate)
            return PCControlResult(branchPc)
        }
        return PCControlResult(pc)
    }

    private fun jumpAddress(pc :Int, address: Int): Int {
        val first4bit = (pc shr 28 and 0xF).toBinaryString(4)
        val last28bit = address.toBinaryString(26) + "00"
        return (first4bit + last28bit).toLong(2).toInt()
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
