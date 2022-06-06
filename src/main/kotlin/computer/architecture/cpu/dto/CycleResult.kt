package computer.architecture.cpu.dto

data class CycleResult(
    val nextPc: Int = 0,
    val valid: Boolean = false,
    val value: Int = 0,
    val isEnd: Boolean = false,
    val lastCycle: Boolean = false,
    val pn: Int = -1
)