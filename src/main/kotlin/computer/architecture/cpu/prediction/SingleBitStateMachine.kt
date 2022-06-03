package computer.architecture.cpu.prediction

class SingleBitStateMachine : IBitStateMachine {

    var state = ONE_BIT_STATE.NOT_TAKEN

    override fun taken(): Boolean {
        return state.taken
    }

    override fun update(isTaken: Boolean) {
        if (!isTaken) {
            state = when (state) {
                ONE_BIT_STATE.TAKEN -> ONE_BIT_STATE.NOT_TAKEN
                ONE_BIT_STATE.NOT_TAKEN -> ONE_BIT_STATE.TAKEN
            }
        }
    }
}
