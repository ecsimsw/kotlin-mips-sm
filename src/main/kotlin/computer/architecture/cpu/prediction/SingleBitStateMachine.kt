package computer.architecture.cpu.prediction

class SingleBitStateMachine : IBitStateMachine {

    var state = ONE_BIT_STATE.TAKEN

    override fun taken(): Boolean {
        return state.taken
    }

    override fun update(isTaken: Boolean) {
        if (isTaken) {
            state = ONE_BIT_STATE.TAKEN
        }

        if (!isTaken) {
            state = ONE_BIT_STATE.NOT_TAKEN
        }
    }
}
