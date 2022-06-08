package computer.architecture.cpu.cache.replacement

interface CacheReplacementStrategy {

    fun init(setSize : Int, lineSize : Int)

    fun use(setIndex: Int, lineIndex: Int)

    fun nextVictim(lineIndex: Int): Int
}
