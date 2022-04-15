package computer.architecture.single

operator fun Array<Int>.set(index: Int, value: Boolean) {
    this[index] = if (value) 1 else 0
}

operator fun Array<Int>.set(index: Int, value: String) {
    this[index] = Integer.decode(value)
}

class Registers(
    size: Int
) {
    var pc: Int = 0
    var r: Array<Int> = Array(size) { 0 }

    operator fun get(register: Int): Int {
        return r[register]
    }

    fun write(regWrite: Boolean, writeRegister: Int, writeData: Int) {
        if (regWrite) {
            r[writeRegister] = writeData
        }
    }
}
