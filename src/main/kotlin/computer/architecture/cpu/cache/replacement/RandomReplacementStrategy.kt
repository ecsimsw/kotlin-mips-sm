package computer.architecture.cpu.cache.replacement

import java.util.*

class RandomReplacementStrategy : CacheReplacementStrategy {

    private val random = Random()
    private var setSize = 0

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
    }

    override fun use(setIndex: Int, lineIndex: Int) {

    }

    override fun nextVictim(lineIndex: Int): Int {
        return random.nextInt(setSize)
    }
}
