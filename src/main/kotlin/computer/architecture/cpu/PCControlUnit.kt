package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux
import computer.architecture.utils.toBinaryString

class PCControlUnit {

    fun next(
        pc: Int,
        jType: Boolean,
        branch: Boolean,
        jr: Boolean,
        address: Int,
        immediate: Int,
        rsValue: Int
    ): Int {
        var nextPc = pc
        nextPc = mux(jType, jumpAddress(pc, address), nextPc)
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
