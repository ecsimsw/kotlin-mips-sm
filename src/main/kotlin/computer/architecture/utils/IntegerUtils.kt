package computer.architecture.utils

import kotlin.math.absoluteValue

fun Int.toBinaryString(digits: Int) : String {
    val toBinaryString = Integer.toBinaryString(this.absoluteValue)
    return toBinaryString.fillDigitsWith(digits, 0)
}

fun String.fillDigitsWith(digits: Int, num: Int) : String {
    var newBinary = this
    if (newBinary.length < digits) {
        for (i in 0 until digits - newBinary.length) {
            newBinary = num.toString() + newBinary
        }
    }
    return newBinary
}

class IntegerUtils {

}