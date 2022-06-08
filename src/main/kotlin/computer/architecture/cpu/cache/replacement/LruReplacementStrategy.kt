package computer.architecture.cpu.cache.replacement

import java.util.*

class LruReplacementStrategy(
    private val setSize: Int,
    private val lineSize: Int
) : CacheReplacementStrategy {
    private val usedHistories: Array<LinkedList<Int>> = Array(lineSize) { LinkedList() }

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
