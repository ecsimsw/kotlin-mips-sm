package computer.architecture.cpu

operator fun Array<Int>.set(index: Int, value: Boolean) {
    this[index] = if (value) 1 else 0
}

class Registers(
    size: Int = 10
) {
    var pc: Int = 0
    var r: Array<Int> = Array(size) { 0 }
}
