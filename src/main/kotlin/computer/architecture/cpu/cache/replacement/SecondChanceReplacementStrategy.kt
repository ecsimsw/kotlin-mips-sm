package computer.architecture.cpu.cache.replacement

class SecondChanceReplacementStrategy : CacheReplacementStrategy {

    private var setSize = 0
    private var lineSize = 0
    private var lastUsed = 0
    private lateinit var chanceHistories: Array<Array<Boolean>>

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
        this.lineSize = lineSize
        this.chanceHistories = Array(lineSize) { Array(setSize) { false } }
    }

    override fun use(setIndex: Int, lineIndex: Int) {
        chanceHistories[lineIndex][setIndex] = true
    }

    override fun nextVictim(lineIndex: Int): Int {
        while (true) {
            lastUsed = (lastUsed + 1) % setSize
            if (!chanceHistories[lineIndex][lastUsed]) {
                return lastUsed
            }
            chanceHistories[lineIndex][lastUsed] = false
        }
    }
}
