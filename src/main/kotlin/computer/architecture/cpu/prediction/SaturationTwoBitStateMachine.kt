package computer.architecture.cpu.prediction

class SaturationTwoBitStateMachine : BitStateMachine {

    var state = TWO_BIT_STATE.WEAKLY_NOT_TAKEN

    override fun taken() : Boolean {
        return state.taken
    }

    override fun change(isTaken: Boolean) {
        if(!isTaken) {
            state = when(state) {
                TWO_BIT_STATE.STRONGLY_TAKEN -> TWO_BIT_STATE.WEAKLY_TAKEN
                TWO_BIT_STATE.WEAKLY_TAKEN -> TWO_BIT_STATE.WEAKLY_NOT_TAKEN
                TWO_BIT_STATE.WEAKLY_NOT_TAKEN -> TWO_BIT_STATE.STRONGLY_NOT_TAKEN
                TWO_BIT_STATE.STRONGLY_NOT_TAKEN -> TWO_BIT_STATE.STRONGLY_NOT_TAKEN
            }
        }

        if(isTaken) {
            state = when(state) {
                TWO_BIT_STATE.STRONGLY_TAKEN -> TWO_BIT_STATE.STRONGLY_TAKEN
                TWO_BIT_STATE.WEAKLY_TAKEN -> TWO_BIT_STATE.STRONGLY_TAKEN
                TWO_BIT_STATE.WEAKLY_NOT_TAKEN -> TWO_BIT_STATE.WEAKLY_TAKEN
                TWO_BIT_STATE.STRONGLY_NOT_TAKEN -> TWO_BIT_STATE.WEAKLY_NOT_TAKEN
            }
        }
    }
}
