package computer.architecture.cpu.cache.replacement

import java.util.*

class LruSecondChanceReplacementStrategy(
    private val setSize: Int,
    private val lineSize: Int
) : CacheReplacementStrategy {

    private val usedHistories: Array<LinkedList<Int>> = Array(lineSize) { LinkedList() }
    private val chanceHistories: Array<Array<Boolean>> = Array(lineSize) { Array(setSize) { false } }

    override fun use(setIndex: Int, lineIndex: Int) {
        val history = usedHistories[lineIndex]
        if (!history.contains(setIndex)) {
            history.addLast(setIndex)
            return
        }
        history.remove(setIndex)
        history.addLast(setIndex)
        chanceHistories[lineIndex][setIndex] = true
    }

    override fun nextVictim(lineIndex: Int): Int {
        val history = usedHistories[lineIndex]
        if (history.size < setSize) {
            return -1
        }

        for (victimSet in history) {
            if (!chanceHistories[lineIndex][victimSet]) {
                return victimSet
            }
        }

        val victimSet = history.first
        chanceHistories[lineIndex][victimSet] = false
        return victimSet
    }
}
