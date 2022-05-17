package computer.architecture.cpu

class EndFlag {

    var isEnd = false

    fun update(lastInstruction: Boolean) {
        if(!isEnd) {
            this.isEnd = lastInstruction
        }
    }
}
