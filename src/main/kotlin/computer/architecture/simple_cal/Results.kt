package computer.architecture.simple_cal

class Results(
    private val registers: Registers
) {
    private val logs: MutableList<String> = mutableListOf()

    fun saveLog(instruction: String) {
        logs.add("Inst : $instruction")
        logs.add("PC   : ${registers.pc}")
        logs.add("REGs : ${registers.r.joinToString(" ")} \n")
    }

    fun printLogs() {
        logs.forEach { println(it) }
    }

    fun printResultValue() {
        println("Result value : ${registers.r[7]}")
    }
}
