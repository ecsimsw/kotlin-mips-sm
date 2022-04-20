package computer.architecture.cpu

class PCControlUnit {
    fun jump(
        controlSignal: ControlSignal,
        pc: Int,
        readData1: Int,
        address: Int
    ): PCControlResult {
        if (controlSignal.jumpRegister) {
            return PCControlResult(readData1)
        }
        if (controlSignal.jump) {
            return PCControlResult(jumpAddress(pc, address))
        }
        return PCControlResult(pc)
    }

    private fun jumpAddress(pc :Int, address: Int): Int {
        val addressWithZero = address shl 2
        val pcWith4bit = pc shr 26
        return pcWith4bit + addressWithZero
    }
}

data class PCControlResult(
    val pc: Int
)
