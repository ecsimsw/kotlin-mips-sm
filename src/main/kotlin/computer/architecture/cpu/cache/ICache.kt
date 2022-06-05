package computer.architecture.cpu.cache

interface ICache {

    fun read(address: Int) : Int

    fun write(address: Int, value: Int)
}