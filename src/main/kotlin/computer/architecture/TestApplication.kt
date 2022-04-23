package computer.architecture

class TestApplication {
}

fun main() {
    val num = 0xFFF3
    println(num)
    println(Integer.toBinaryString(zeroExtension32(num)))
    println(Integer.toBinaryString(signExtension32(num)))
}


private fun signExtension32(num: Int): Int = num shl 16 shr 16

private fun zeroExtension32(num: Int) = num shl 16 ushr 16
