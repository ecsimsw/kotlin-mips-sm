package computer.architecture.cpu.register

class ScoreBoard(
    size: Int
) {
    private val valid: Array<Boolean> = Array(size) { true }
    private val tag: Array<Int> = Array(size) { 0 }

    fun book(regWrite: Boolean, writeRegister: Int, tag: Int) {
        if (regWrite && writeRegister != 0) {
            this.valid[writeRegister] = false
            this.tag[writeRegister] = tag
        }
    }

    fun release(writeRegister: Int, tag: Int) {
        if (this.tag[writeRegister] == tag) {
            this.valid[writeRegister] = true
        }
    }

    fun isValid(writeRegister: Int): Boolean {
        return this.valid[writeRegister]
    }
}