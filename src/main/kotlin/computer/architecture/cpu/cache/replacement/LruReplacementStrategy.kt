package computer.architecture.cpu.cache.replacement

import java.util.*

class LruReplacementStrategy(

) : CacheReplacementStrategy {

    private var setSize: Int = 0
    private var lineSize: Int = 0
    private lateinit var usedHistories: Array<LinkedList<Int>>

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
        this.lineSize = lineSize
        this.usedHistories = Array(lineSize) { LinkedList() }
    }

    override fun use(setIndex: Int, lineIndex: Int) {
        val history = usedHistories[lineIndex]
        if (!history.contains(setIndex)) {
            history.addLast(setIndex)
            return
        }
        history.remove(setIndex)
        history.addLast(setIndex)
    }

    override fun nextVictim(lineIndex: Int): Int {
        val history = usedHistories[lineIndex]
        if (history.size < setSize) {
            return -1
        }
        return history.first
    }
}
