package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux

class ForwardingUnit {

    fun forward(
        prevIdEx: DecodeResult,
        prevExMa: ExecutionResult,
        prevMaWb: MemoryAccessResult
    ) {
        val fwSignal1 = ForwardingSignal.of(
            readReg = prevIdEx.readReg1,
            exmaRegWrite = prevExMa.controlSignal.regWrite,
            exmaRd = prevExMa.writeReg,
            mawbRegWrite = prevMaWb.controlSignal.regWrite,
            mawbRd = prevMaWb.writeReg,
        )

        val fwSignal2 = ForwardingSignal.of(
            readReg = prevIdEx.readReg2,
            exmaRegWrite = prevExMa.controlSignal.regWrite,
            exmaRd = prevExMa.writeReg,
            mawbRegWrite = prevMaWb.controlSignal.regWrite,
            mawbRd = prevMaWb.writeReg,
        )

        prevIdEx.readData1 = mux(fwSignal1 == ForwardingSignal.SRC_EX_MA, prevExMa.aluValue, prevIdEx.readData1)
        prevIdEx.readData1 = mux(fwSignal1 == ForwardingSignal.SRC_MA_WB, prevMaWb.regWriteValue, prevIdEx.readData1)

        prevIdEx.readData2 = mux(fwSignal2 == ForwardingSignal.SRC_EX_MA, prevExMa.aluValue, prevIdEx.readData2)
        prevIdEx.readData2 = mux(fwSignal2 == ForwardingSignal.SRC_MA_WB, prevMaWb.regWriteValue, prevIdEx.readData2)
    }
}

enum class ForwardingSignal {
    SRC_ID_EX, SRC_EX_MA, SRC_MA_WB;

    companion object {
        fun of(
            readReg: Int,
            exmaRegWrite: Boolean,
            exmaRd: Int,
            mawbRegWrite: Boolean,
            mawbRd: Int
        ): ForwardingSignal {
            if ((exmaRd != 0) && exmaRegWrite && (readReg == exmaRd)) {
                return SRC_EX_MA
            }
            if ((mawbRd != 0) && mawbRegWrite && (readReg == mawbRd)) {
                return SRC_MA_WB
            }
            return SRC_ID_EX
        }
    }
}
