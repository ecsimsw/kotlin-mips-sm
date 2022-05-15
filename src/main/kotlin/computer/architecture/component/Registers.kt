package computer.architecture.component

operator fun Array<Int>.set(index: Int, value: Boolean) {
    this[index] = if (value) 1 else 0
}

class Registers(
    size: Int
) {
    private val registerSize = if (size < 32) 32 else size
    private val r: Array<Int> = Array(registerSize) { 0 }
    private val valid: Array<Boolean> = Array(registerSize) { true }

    init {
        r[29] = 0x1000000
        r[31] = -1
    }

    var pc: Int = 0

    operator fun get(register: Int) = r[register]

    fun book(regWrite: Boolean, writeRegister: Int) {
        if(regWrite) {
            valid[writeRegister] = false
        }
    }

    fun write(regWrite: Boolean, writeRegister: Int, writeData: Int) {
        if (regWrite) {
            r[writeRegister] = writeData
            valid[writeRegister] = true
        }
    }

    fun isValid(writeRegister: Int): Boolean {
        return valid[writeRegister]
    }
}
