package computer.architecture.cpu.cache

data class CacheLine(
    var valid: Boolean = false,
    var tag: Int = 0,
    var datas: Array<Int>
) {
    companion object {
        fun listOf(lineSize: Int, blockSize: Int): Array<CacheLine> {
            return Array(lineSize) {
                CacheLine(false, 0, Array(blockSize) { 0 })
            }
        }
    }
}
