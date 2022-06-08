package computer.architecture.cpu.cache.replacement

import java.util.*

class LruSecondChanceReplacementStrategy : CacheReplacementStrategy {

    private var setSize = 0
    private var lineSize = 0
    private lateinit var usedHistories: Array<LinkedList<Int>>
    private lateinit var chanceHistories: Array<Array<Boolean>>

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
        this.lineSize = lineSize
        this.usedHistories = Array(lineSize) { LinkedList() }
        this.chanceHistories = Array(lineSize) { Array(setSize) { false } }
    }

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
