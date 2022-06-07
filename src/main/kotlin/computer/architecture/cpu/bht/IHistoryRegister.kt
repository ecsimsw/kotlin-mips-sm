package computer.architecture.cpu.bht

interface IHistoryRegister {

    fun update(pc: Int, isTaken: Boolean)

    fun valueOf(pc: Int): Int
}
