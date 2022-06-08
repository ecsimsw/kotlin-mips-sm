package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy

class WriteBackFullyAssociativeMappedCache(
    memory: Memory,
    offsetBits: Int = 4,
    lineBits: Int = 8,
    replacementStrategy: CacheReplacementStrategy
) : WriteBackSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits =  0,
    setBits = lineBits,
    replacementStrategy = replacementStrategy
)
