package computer.architecture.cpu

class ForwardingUnit {

    fun execute(readReg: Int, exmaRd: Int, exmaValue: Int, mawbRd: Int, mawbValue: Int): ForwardingResult {
        if ((exmaRd != 0) && (readReg == exmaRd)) {
            return ForwardingResult(true, exmaValue)
        }
        if ((mawbRd != 0) && (readReg == mawbRd)) {
            return ForwardingResult(true, mawbValue)
        }
        return ForwardingResult.DO_NOTHING
    }
}

data class ForwardingResult(
    val isTarget: Boolean = false,
    val value: Int = 0
) {
    companion object {
        val DO_NOTHING = ForwardingResult()
    }
}
