package computer.architecture.cpu.pc

import computer.architecture.cpu.bht.LocalHistoryRegister

class TwoLevelLocalHistoryPredictionPcUnit(
    size: Int = 16
) : HistoryBufferedBranchPredictionPcUnit(size, LocalHistoryRegister())
