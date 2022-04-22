package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux

class ProgramCounterUnit {
    fun next(
        pc: Int,
        jump: Boolean,
        branch: Boolean,
        jr: Boolean,
        address: Int,
        immediate: Int,
        rsValue: Int
    ): Int {
        var nextPc = pc
        nextPc = mux(jump, jumpAddress(pc, address), nextPc)
        nextPc = mux(branch, pc + branchAddress(immediate), nextPc)
        nextPc = mux(jr, rsValue, nextPc)
        return nextPc
    }

    private fun jumpAddress(pc: Int, address: Int): Int {
        val first4bit = (pc shr 28 and 0xF).toBinaryString(4)
        val last28bit = address.toBinaryString(26) + "00"
        return (first4bit + last28bit).toLong(2).toInt()
    }

    private fun branchAddress(immediate: Int): Int {
        val binaryImmediate = immediate.toBinaryString(16)
        return if (binaryImmediate.first() == '1') {
            ("11111111111111" + binaryImmediate + "00").toLong(2).toInt()
        } else {
            ("00000000000000" + binaryImmediate + "00").toLong(2).toInt()
        }
    }
}

private fun Int.toBinaryString(digits: Int): String {
    val originNumber = Integer.toBinaryString(this)
    var newBinary = originNumber
    if (newBinary.length < digits) {
        for (i in 0 until digits - originNumber.length) {
            newBinary = "0$newBinary"
        }
    }
    return newBinary
}
