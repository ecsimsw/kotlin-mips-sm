package computer.architecture.cpu.cache.replacement

import java.util.*

class LruReplacementStrategy(

) : CacheReplacementStrategy {

    private var setSize: Int = 0
    private var lineSize: Int = 0
    lateinit var usedHistories: Array<MutableList<Int>>

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
        this.lineSize = lineSize
        this.usedHistories = Array(lineSize) { MutableList(setSize) { it } }
    }

    override fun use(setIndex: Int, lineIndex: Int) {
        val history = usedHistories[lineIndex]
        history.remove(setIndex)
        history.add(setIndex)
    }

    override fun nextVictim(lineIndex: Int): Int {
        val history = usedHistories[lineIndex]
        return history[0]
    }
}
