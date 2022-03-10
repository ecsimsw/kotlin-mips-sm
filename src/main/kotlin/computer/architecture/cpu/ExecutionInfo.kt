package computer.architecture.cpu

data class ExecutionInfo(
    val opcode: Opcode,
    val operand1: Int,
    val operand2: Int
)
