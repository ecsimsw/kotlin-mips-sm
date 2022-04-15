package computer.architecture.component

class Mux {
    companion object {
        fun mux(signal: Boolean, trueResult: Int, falseResult: Int) =
            if (signal) trueResult
            else falseResult
    }
}
