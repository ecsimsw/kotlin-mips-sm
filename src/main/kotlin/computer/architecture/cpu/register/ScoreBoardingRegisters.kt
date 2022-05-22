package computer.architecture.cpu.register

open class ScoreBoardingRegisters(
    size: Int
) {
    private val registers = Registers(size)
    private val valid: Array<Boolean> = Array(registers.registerSize) { true }
    private val tag: Array<Int> = Array(registers.registerSize) { 0 }

    operator fun get(register: Int) = registers[register]

    fun book(regWrite: Boolean, writeRegister: Int, tag: Int) {
        if (regWrite && writeRegister != 0) {
            this.valid[writeRegister] = false
            this.tag[writeRegister] = tag
        }
    }

    open fun write(regWrite: Boolean, writeRegister: Int, writeData: Int, tag: Int) {
        if (regWrite) {
            this.registers.write(writeRegister, writeData)
        }

        if (regWrite && this.tag[writeRegister] == tag) {
            this.valid[writeRegister] = true
        }
    }

    fun isValid(writeRegister: Int): Boolean {
        return this.valid[writeRegister]
    }
}
