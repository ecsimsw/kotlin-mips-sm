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

    fun read(readRegister1: Int, readRegister2: Int): RegisterReadData {
        return RegisterReadData(
            r[readRegister1],
            r[readRegister2]
        )
    }

    fun write(regWrite: Boolean, writeRegister: Int, writeData: Int) {
        if (regWrite) {
            r[writeRegister] = writeData
        }
    }
}

data class RegisterReadData(
    val readData1: Int,
    val readDate2: Int
)
