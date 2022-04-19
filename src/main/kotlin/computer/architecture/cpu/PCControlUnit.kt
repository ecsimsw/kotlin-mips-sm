package computer.architecture.cpu

import computer.architecture.component.Registers

class PCControlUnit(
    private val registers: Registers
) {
    fun jump(controlSignal: ControlSignal, readData1: Int) {
        if(controlSignal.jumpRegister) {
            registers.pc = readData1/4
        }
    }
}
