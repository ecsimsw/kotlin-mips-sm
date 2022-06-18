package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy
import computer.architecture.cpu.cache.replacement.FIFOReplacementStrategy
import computer.architecture.cpu.cache.replacement.RandomReplacementStrategy

class WriteThroughDirectMappedCache(
    memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
) : WriteThroughSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits = indexBits,
    setBits = 0
)
