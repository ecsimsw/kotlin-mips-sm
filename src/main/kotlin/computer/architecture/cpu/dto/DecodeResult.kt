package computer.architecture.cpu.dto

import computer.architecture.cpu.ControlSignal

data class DecodeResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val shiftAmt: Int = 0,
    val immediate: Int = 0,
    val address: Int = 0,
    val readReg1: Int = 0,
    val readReg2: Int = 0,
    var readData1: Int = 0,
    var readData2: Int = 0,
    val writeReg: Int = 0,
    val jump: Boolean = false,
    val nextPc: Int = 0,
    var controlSignal: ControlSignal = ControlSignal.NONE,
    val pn: Int = -1
)