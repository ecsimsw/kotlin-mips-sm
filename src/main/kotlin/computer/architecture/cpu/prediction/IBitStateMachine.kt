package computer.architecture.cpu.prediction

enum class ONE_BIT_STATE(
    val taken : Boolean
) {
    TAKEN(true),
    NOT_TAKEN(false);
}

enum class TWO_BIT_STATE(
    val taken : Boolean
) {
    STRONGLY_TAKEN(true),
    WEAKLY_TAKEN(true),
    WEAKLY_NOT_TAKEN(false),
    STRONGLY_NOT_TAKEN(false);
}

interface IBitStateMachine {

    fun taken() : Boolean

    fun change(isTaken: Boolean)
}
