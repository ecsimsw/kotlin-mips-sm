package computer.architecture.cpu

data class ExecutionInfo(
    val opcode: Opcode,
    val operand1: Operand,
    val operand2: Operand
)
