package computer.architecture.single

class Mux {

    companion object {
        fun mux(signal: Boolean, trueResult: Int, falseResult: Int): Int {
            if (signal) {
                return trueResult
            }
            return falseResult
        }
    }
}
