package computer.architecture.cpu

import computer.architecture.cpu.dto.DecodeResult
import computer.architecture.cpu.dto.WriteBackResult
import computer.architecture.cpu.register.Scoreboard

class DataDependencyUnit(
    registerSize: Int
) {
    private val scoreBoard: Scoreboard = Scoreboard(registerSize)

    fun book(nextIdEx: DecodeResult) {
        scoreBoard.book(nextIdEx.controlSignal.regWrite, nextIdEx.writeReg, nextIdEx.pc)
    }

    fun release(wbResult: WriteBackResult) {
        if (wbResult.valid && wbResult.controlSignal.regWrite) {
            scoreBoard.release(wbResult.writeReg, wbResult.pc)
        }
    }

    fun hasHazard(readReg1: Int, readReg2: Int): Boolean {
        val valid1 = scoreBoard.isValid(readReg1)
        val valid2 = scoreBoard.isValid(readReg2)
        return !(valid1 && valid2)
    }
}
