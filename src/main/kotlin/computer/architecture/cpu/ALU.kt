package computer.architecture.cpu

import computer.architecture.memory.Memory

class ALU(
    private val registers: Registers,
    private val memory: Memory
) {
    private val operations: MutableMap<Opcode, (Operand, Operand) -> Unit> = mutableMapOf()

    init {
        operations[Opcode.ADD] = { op1, op2 -> registers.r[0] = value(op1) + value(op2) }
        operations[Opcode.MINUS] = { op1, op2 -> registers.r[0] = value(op1) - value(op2) }
        operations[Opcode.MULTIPLY] = { op1, op2 -> registers.r[0] = value(op1) * value(op2) }
        operations[Opcode.DIVIDE] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
        operations[Opcode.MOD] = { op1, op2 -> registers.r[0] = value(op1) % value(op2) }
        operations[Opcode.SLL] = { op1, op2 -> registers.r[0] = value(op1) shl value(op2) }
        operations[Opcode.SRL] = { op1, op2 -> registers.r[0] = value(op1) shr value(op2) }
        operations[Opcode.AND] = { op1, op2 -> registers.r[0] = value(op1) and value(op2) }
        operations[Opcode.OR] = { op1, op2 -> registers.r[0] = value(op1) or value(op2) }
        operations[Opcode.JUMP] = { op1, op2 -> registers.pc = value(op1) }
        operations[Opcode.JUMP_AND_LINK] = { op1, op2 ->
            registers.pc = value(op2)
            registers.r[op1.registerNumber()] = registers.pc + 1
        }
        operations[Opcode.BRANCH] = { op1, op2 -> if (registers.r[0] == 1) registers.pc = value(op1) }
        operations[Opcode.BRANCH_ON_EQUAL] = { op1, op2 -> if (registers.r[0] == value(op1)) registers.pc = value(op2) }
        operations[Opcode.BRANCH_ON_NOT_EQUAL] = { op1, op2 -> if (registers.r[0] != value(op1)) registers.pc = value(op2) }
        operations[Opcode.CONDITION] = { op1, op2 -> registers.r[0] = value(op1) < value(op2) }
        operations[Opcode.MOVE] = { op1, op2 -> registers.r[op1.registerNumber()] = value(op2) }
        operations[Opcode.LOAD_WORD] = { op1, op2 -> registers.r[0] = Integer.decode(memory[value(op1)]) }
        operations[Opcode.STORE_WORD] = { op1, op2 -> memory[value(op2)] = "0x" + Integer.toHexString(value(op1)).uppercase() }
        operations[Opcode.HALT] = { op1, op2 -> registers.pc = Int.MAX_VALUE }
    }

    fun process(executionInfo: ExecutionInfo) {
        process(executionInfo.opcode, executionInfo.operand1, executionInfo.operand2)
    }

    fun process(opcode: Opcode, operand1: Operand, operand2: Operand) {
        val operation = operations[opcode] ?: throw IllegalArgumentException("Opcodes that cannot be computed")
        operation.invoke(operand1, operand2)
    }

    private fun value(operand: Operand): Int {
        if (operand.type == OperandType.REGISTER_NUMBER) {
            return registers.r[operand.number]
        }
        if (operand.type == OperandType.HEXADECIMAL_NUMBER) {
            return operand.number
        }
        throw IllegalArgumentException("This is not the format of operand")
    }
}
