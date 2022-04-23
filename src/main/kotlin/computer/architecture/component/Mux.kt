package computer.architecture.component

class Mux {
    companion object {
        fun mux(signal: Boolean, trueResult: Int, falseResult: Int) =
            if (signal)
                trueResult
            else
                falseResult

        fun mux(signal: Boolean, trueResult: Boolean, falseResult: Boolean) =
            if (signal)
                trueResult
            else
                falseResult
    }
}
