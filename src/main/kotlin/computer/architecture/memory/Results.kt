package computer.architecture.memory

import computer.architecture.cpu.Registers

class Results {

    private val lines: MutableList<String> = mutableListOf()

    fun log(instruction: String, registers: Registers) {
        addLine("instruction : $instruction")
        addLine("pc : $registers.pc")
        addLine("registers : ${registers.r.joinToString(" ")} \n")
    }

    fun printAll() {
        lines.forEach { println(it) }
    }

    private fun addLine(line: String) {
        lines.add(line)
    }
}
