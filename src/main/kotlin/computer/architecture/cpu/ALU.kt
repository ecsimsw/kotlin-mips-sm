package computer.architecture.cpu

class ALU(
    private val registers: Registers
) {

    fun process(executionInfo: ExecutionInfo) {
        val opcode = executionInfo.opcode
        val operand1 = executionInfo.operand1
        val operand2 = executionInfo.operand2

        if (opcode == Opcode.ADD) {
            registers.r[0] = operand1 + operand2
            return
        }

        if (opcode == Opcode.MINUS) {
            registers.r[0] = operand1 - operand2
            return
        }

        if (opcode == Opcode.MULTIPLY) {
            registers.r[0] = operand1 * operand2
            return
        }

        if (opcode == Opcode.DIVIDE) {
            registers.r[0] = operand1 / operand2
            return
        }

        if (opcode == Opcode.CONDITION) {
            registers.r[0] = operand1 < operand2
            return
        }

        if (opcode == Opcode.MOVE) {
            registers.r[operand1] = registers.r[operand2]
            return
        }
    }
}
