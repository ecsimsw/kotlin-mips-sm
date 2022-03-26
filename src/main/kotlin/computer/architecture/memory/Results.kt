package computer.architecture.memory

import computer.architecture.cpu.Registers

class Results(
    private val sleepTime:Long
) {
    private val logs: MutableList<String> = mutableListOf()

    fun log(instruction: String, registers: Registers, memory: Memory) {
        println("Inst : $instruction")
        println("PC   : ${registers.pc}")
        println("SP   : ${registers.r[9]}")
        println("RA   : ${registers.r[8]}")
        println("REGs : ${registers.r.joinToString(" ")} \n")
        Thread.sleep(sleepTime)

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
