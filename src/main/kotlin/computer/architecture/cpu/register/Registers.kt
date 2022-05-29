package computer.architecture.cpu.register

open class Registers(
    size: Int
) {
    val size = if (size < 32) 32 else size
    private val r: Array<Int> = Array(this.size) { 0 }

    init {
        r[29] = 0x1000000
        r[31] = -1
    }

    operator fun get(register: Int) = r[register]

    fun write(register: Int, data: Int) {
        this.r[register] = data
    }
}
