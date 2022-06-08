package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy

class WriteThroughDirectMappedCache(
    memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
    replacementStrategy: CacheReplacementStrategy
) : WriteThroughSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits = indexBits,
    setBits = 0,
    replacementStrategy = replacementStrategy
)
