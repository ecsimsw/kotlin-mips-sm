package computer.architecture.cpu.pc

import computer.architecture.cpu.bht.GlobalHistoryRegister

class TwoLevelGlobalHistoryPredictionPcUnit(
    size : Int = 16
) : HistoryBufferedBranchPredictionPcUnit(size, GlobalHistoryRegister())
