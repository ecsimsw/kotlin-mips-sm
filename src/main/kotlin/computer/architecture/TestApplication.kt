package computer.architecture

import computer.architecture.utils.toHexString

class TestApplication {
}

fun main() {

    val num = 0b00100111
    println(num.toByte())

//    val pc = 0x4FFFFFFF
//    val first4bit = pc.toInt() shr 28 and 0xF
//    println(first4bit.toHexString())
}
