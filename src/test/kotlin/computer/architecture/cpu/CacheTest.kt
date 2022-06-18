package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.*
import computer.architecture.cpu.cache.replacement.FIFOReplacementStrategy
import computer.architecture.cpu.cache.replacement.LruReplacementStrategy
import computer.architecture.cpu.cache.replacement.SecondChanceReplacementStrategy
import computer.architecture.cpu.cache.replacement.RandomReplacementStrategy
import computer.architecture.cpu.cu.ForwardingPipelineControlUnit
import computer.architecture.cpu.pc.TwoLevelLocalHistoryPredictionPcUnit
import computer.architecture.cpu.utils.Utils.Companion.checkProcessResult
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CacheTest {

    @BeforeEach
    fun initLogger() {
        Logger.loggingSignal = LoggingSignal(result = true)
        Logger.init()
    }

    private val pcUnit = TwoLevelLocalHistoryPredictionPcUnit()
    private val replacementStrategy = FIFOReplacementStrategy()

    @DisplayName("FullyAssociativeMapped, SetAssociativeMapped, DirectMapped cache 성능을 비교한다.")
    @Nested
    inner class Cache {

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBackFullyAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackFullyAssociativeMappedCache(memory, 4, 8, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack2waySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 7, 1, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBackDirectMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackDirectMappedCache(memory, 4, 8)
            testResult(cache, expected)
        }
    }

    @DisplayName("WriteThrough와 WriteBack의 MemoryWrite 횟수를 비교한다.")
    @Nested
    inner class WritePolicyTest {

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun noCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = NoCachedMemory(memory)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeThroughDirectedMappedCached(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteThroughDirectMappedCache(memory, 4, 8)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBackDirectedMappedCached(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackDirectMappedCache(memory, 4, 8)
            testResult(cache, expected)
        }
    }

    @DisplayName("Set 수에 변화에 따른 성능 변화를 확인한다. (2,4,16,32,128,256)")
    @Nested
    inner class SetWayTest {

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack2WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 7, 1, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack4WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack16WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 4, 4, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack32WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 3, 5, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack128WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 3, 5, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun writeBack256WaySetAssociativeMappedCache(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 2, 6, replacementStrategy)
            testResult(cache, expected)
        }
    }

    @DisplayName("교체 알고리즘 성능을 비교한다. (LRU, LRU_SecondChance, Random)")
    @Nested
    inner class ReplacementTest {

        private val fifo = FIFOReplacementStrategy()
        private val secondChance = SecondChanceReplacementStrategy()
        private val random = RandomReplacementStrategy()
        private val lru = LruReplacementStrategy()

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun fifo_4way(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun secondChance_4way(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2,  secondChance)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun random4way(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2,  random)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun lru4way(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2, lru)
            testResult(cache, expected)
        }
    }

    @DisplayName("Write - miss 시, memory fetch 여부에 hit률을 비교한다.")
    @Nested
    inner class WriteBackMiss {

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun lru4wayMemoryFetch(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2, replacementStrategy)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun lru4wayNoneFetch(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = object : WriteBackSetAssociativeMappedCache(memory, 4, 6, 2, replacementStrategy) {
                override fun write(address: Int, value: Int) {
                    val tag = tag(address)
                    val lineIndex = index(address)
                    val offset = offset(address)
                    val setIndex = setIndex(tag, lineIndex)
                    if (setIndex != -1) {
                        Logger.cacheHit()
                        replacementStrategy.use(setIndex, lineIndex)

                        dirties[setIndex][lineIndex] = true
                        lineSets[setIndex][lineIndex].datas[offset] = value
                    } else {
                        Logger.memoryWrite()
                        memory.write(address, value)
                    }
                }
            }
            testResult(cache, expected)
        }
    }

    @DisplayName("Block size에 따른 hit률을 비교한다.")
    @Nested
    inner class BlockSizeTest {

        private val fifo = FIFOReplacementStrategy()

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_4(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 2, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_16(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_32(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 6, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_128(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 8, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_1024(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 10, 6, 2,  fifo)
            testResult(cache, expected)
        }

        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_4096(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 12, 6, 2,  fifo)
            testResult(cache, expected)
        }


        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_16384(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 14, 6, 2,  fifo)
            testResult(cache, expected)
        }


        @ParameterizedTest
        @CsvSource(
            "sample/simple.bin,0",
            "sample/simple2.bin,100",
            "sample/simple3.bin,5050",
            "sample/simple4.bin,55",
            "sample/gcd.bin,1",
            "sample/fib.bin,55",
            "sample/input4.bin,85"
        )
        fun block_65536(path: String, expected: Int) {
            val memory = Memory.load(20000000, path)
            val cache = WriteBackSetAssociativeMappedCache(memory, 16, 6, 2,  fifo)
            testResult(cache, expected)
        }
    }

    private fun testResult(
        cache: ICache,
        expected: Int
    ) {
        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }
}
