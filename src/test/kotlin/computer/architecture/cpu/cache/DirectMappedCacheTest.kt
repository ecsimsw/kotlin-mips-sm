package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test

internal class DirectMappedCacheTest {

    @Test
    fun tag() {
        val directMappedCache = DirectMappedCache(Memory(16), 4, 8)
        val tag = directMappedCache.tag(0x7FFFFFFF)
        assertThat(tag).isEqualTo(0b11111111111111111)
    }

    @Test
    fun index() {
        val directMappedCache = DirectMappedCache(Memory(16), 4, 8)
        val index = directMappedCache.index(0x7FFFFFFF)
        assertThat(index).isEqualTo(0b11111111)
    }

    @Test
    fun offset() {
        val directMappedCache = DirectMappedCache(Memory(16), 4, 8)
        val offset = directMappedCache.offset(0x7FFFFFFF)
        assertThat(offset).isEqualTo(0b1111)
    }

    @Test
    fun address() {
        val directMappedCache = DirectMappedCache(Memory(16), 4, 8)
        val address = directMappedCache.address(0b11111111111111111, 0b11111111, 0b1111)
        assertThat(address).isEqualTo(0x7FFFFFFC)
    }
}
