package computer.architecture

import computer.architecture.utils.readAsBinary
import computer.architecture.utils.toBinaryString
import computer.architecture.utils.toHexString

class TestApplication {
}

fun main() {

    val pc = 0x4FFFFFFF
    val first4bit = pc.toInt() shr 28 and 0xF
    println(first4bit.toHexString())
}
