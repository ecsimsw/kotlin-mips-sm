package computer.architecture.cpu

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
}
