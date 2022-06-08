package computer.architecture.cpu.cache.replacement

interface CacheReplacementStrategy {

    fun use(setIndex: Int, lineIndex: Int)

    fun nextVictim(lineIndex: Int): Int
}
