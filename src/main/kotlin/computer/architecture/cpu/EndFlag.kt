package computer.architecture.cpu

class EndFlag {

    var isEnd = false

    fun check(pc: Int): Boolean {
        if(isEnd) {
            isEnd = true
            return isEnd
        } else {
            isEnd = pc == -1
            return isEnd
        }
    }
}