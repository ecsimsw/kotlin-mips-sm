package computer.architecture.cpu.cache.replacement

class FIFOReplacementStrategy : CacheReplacementStrategy {

    private var setSize = 0
    private var lineSize = 0
    private var lastUsed = 0

    override fun init(setSize: Int, lineSize: Int) {
        this.setSize = setSize
        this.lineSize = lineSize
    }

    override fun use(setIndex: Int, lineIndex: Int) {
    }

    override fun nextVictim(lineIndex: Int): Int {
        lastUsed = (lastUsed + 1) % setSize
        return lastUsed
    }
}
