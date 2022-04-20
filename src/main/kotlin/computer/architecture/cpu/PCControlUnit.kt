package computer.architecture.cpu

class PCControlUnit {
    fun jump(controlSignal: ControlSignal, pc: Int, readData1: Int): PCControlResult {
        if (controlSignal.jumpRegister) {
            return PCControlResult(readData1)
        }
        return PCControlResult(pc)
    }
}

data class PCControlResult(
    val pc: Int
)
