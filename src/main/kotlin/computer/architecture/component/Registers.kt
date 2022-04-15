package computer.architecture.component

operator fun Array<Int>.set(index: Int, value: Boolean) {
    this[index] = if (value) 1 else 0
}

class Registers(
    size: Int
) {
    var pc: Int = 0
    var r: Array<Int> = Array(size) { 0 }

    operator fun get(register: Int) = r[register]

    fun write(regWrite: Boolean, writeRegister: Int, writeData: Int) {
        if (regWrite) {
            r[writeRegister] = writeData
        }
    }
}
