package computer.architecture.cpu.prediction

class SingleBitStateMachine : BitStateMachine {

    var state = ONE_BIT_STATE.NOT_TAKEN

    override fun taken() : Boolean {
        return state.taken
    }

    override fun change(isTaken: Boolean) {
        if(!isTaken) {
            state = when(state) {
                ONE_BIT_STATE.TAKEN -> ONE_BIT_STATE.NOT_TAKEN
                ONE_BIT_STATE.NOT_TAKEN -> ONE_BIT_STATE.TAKEN
            }
        }
    }
}
