package computer.architecture.cpu.prediction

class SingleBitState : BitStateMachine {

    var state = ONE_BIT_STATE.NOT_TAKEN

    override fun taken() : Boolean {
        return state.taken
    }

    override fun change(wasWrong: Boolean) {
        if(wasWrong) {
            state = when(state) {
                ONE_BIT_STATE.TAKEN -> ONE_BIT_STATE.NOT_TAKEN
                ONE_BIT_STATE.NOT_TAKEN -> ONE_BIT_STATE.TAKEN
            }
        }
    }
}
