package computer.architecture.cpu.utils

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.ICache
import computer.architecture.cpu.cache.NoCachedMemory
import computer.architecture.cpu.cache.WriteBackSetAssociativeMappedCache
import computer.architecture.cpu.cache.replacement.LruReplacementStrategy
import computer.architecture.utils.Logger
import org.assertj.core.api.Assertions.assertThat

class Utils {
    companion object {
        fun checkProcessResult(processResult: Int, expected: Int) {
            Logger.printProcessResult(processResult)
            assertThat(processResult).isEqualTo(expected)
        }

        fun cache(memory : Memory) : ICache{
            return NoCachedMemory(memory)
        }
    }
}
