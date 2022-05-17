package computer.architecture.utils

fun Int.toHexString(): String {
    return Integer.toHexString(this).uppercase()
}

fun Int.toHexString(digits: Int): String {
    val hexString = Integer.toHexString(this)
    var newBinary = hexString
    if (newBinary.length < digits) {
        for (i in 0 until digits - hexString.length) {
            newBinary = "0$newBinary"
        }
    }
    return newBinary.uppercase()
}

class NumberFormatUtils {

}