package computer.architecture

import computer.architecture.utils.toHexString

class TestApplication {
}

fun main() {
//    println((0x400000 + 0xFFFFFFFF8).toInt())

    val num = 0xFFF8
    println(num.toHexString())

    val num2 = (num shl 16) shr 16
    println(num2.toHexString())

    println(10 + num2)
}

//4194304
//4194296