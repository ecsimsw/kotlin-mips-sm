package computer.architecture.cpu.cache.replacement

import java.util.*

class RandomReplacementStrategy(
    val setSize : Int
) : CacheReplacementStrategy {

    private val random = Random()

    override fun use(setIndex: Int, lineIndex: Int) {

    }

    override fun nextVictim(lineIndex: Int): Int {
        return random.nextInt(setSize)
    }
}
