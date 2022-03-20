package computer.architecture.memory

import computer.architecture.cpu.Registers

class Results {

    private val logs: MutableList<String> = mutableListOf()

    fun log(instruction: String, registers: Registers) {
        println("Inst : $instruction")
        println("PC   : ${registers.pc}")
        println("REGs : ${registers.r.joinToString(" ")} \n")
        Thread.sleep(1000)

        addLine("Inst : $instruction")
        addLine("PC   : ${registers.pc}")
        addLine("REGs : ${registers.r.joinToString(" ")} \n")
    }

    fun printLogs() {
        logs.forEach { println(it) }
    }

    private fun addLine(log: String) {
        logs.add(log)
    }
}
