package computer.architecture.cpu.dto

data class FetchResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val instruction: Int = 0,
    val pn: Int = -1
)
