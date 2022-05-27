package computer.architecture.utils

data class LoggingSignal(
    var cycle: Boolean = false,
    var cyclePrintPeriod: Int = 1,
    var fetch: Boolean = false,
    var decode: Boolean = false,
    var execute: Boolean = false,
    var memoryAccess: Boolean = false,
    var writeBack: Boolean = false,
    var result: Boolean = false,
    var sleepTime: Long = 0L,
    var from: Int = 0,
    var to: Int = 0
)
