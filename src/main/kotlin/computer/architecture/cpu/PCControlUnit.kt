package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux
import computer.architecture.utils.toBinaryString

class PCControlUnit {

    fun next(
        pc: Int,
        pcSrc1: Boolean,
        pcSrc2: Boolean,
        pcSrc3: Boolean,
        address: Int,
        immediate: Int,
        rsValue: Int
    ): Int {
        var nextPc = pc
        nextPc = mux(pcSrc1, jumpAddress(pc, address), nextPc)
        nextPc = mux(pcSrc2, pc + branchAddress(immediate), nextPc)
        nextPc = mux(pcSrc3, rsValue, nextPc)
        return nextPc
    }

    private fun jumpAddress(pc: Int, address: Int): Int {
        val first4bit = (pc shr 28 and 0xF).toBinaryString(4)
        val last28bit = address.toBinaryString(26) + "00"
        return (first4bit + last28bit).toLong(2).toInt()
    }

    private fun branchAddress(immediate: Int): Int {
        val first = immediate.toBinaryString(16).first()
        var binNum = ""
        for (i: Int in 1..14) {
            binNum += first
        }
        binNum += immediate.toBinaryString(16)
        binNum += "00"
        return binNum.toLong(2).toInt()
    }
}
