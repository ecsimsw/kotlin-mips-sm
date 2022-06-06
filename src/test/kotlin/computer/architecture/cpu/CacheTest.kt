package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.WriteBackDirectMappedCache
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CacheTest {

    @BeforeEach
    fun initLogger() {
        Logger.loggingSignal = LoggingSignal(result = true)
        Logger.init()
    }

    @Nested
    inner class AddressConvertingTest {

        private val directMappedCache = WriteBackDirectMappedCache(Memory(16), 4, 8)

        @Test
        fun tag() {
            val tag = directMappedCache.tag(0x7FFFFFFF)
            assertThat(tag).isEqualTo(0b11111111111111111)
        }

        @Test
        fun index() {
            val index = directMappedCache.index(0x7FFFFFFF)
            assertThat(index).isEqualTo(0b11111111)
        }

        @Test
        fun offset() {
            val offset = directMappedCache.offset(0x7FFFFFFF)
            assertThat(offset).isEqualTo(0b1111)
        }

        @Test
        fun address() {
            val address = directMappedCache.address(0b11111111111111111, 0b11111111, 0b1111)
            assertThat(address).isEqualTo(0x7FFFFFFC)
        }
    }
}
