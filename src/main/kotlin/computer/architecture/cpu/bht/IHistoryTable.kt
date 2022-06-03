package computer.architecture.cpu.bht

import computer.architecture.cpu.prediction.IBitStateMachine

interface IHistoryTable {

    fun isHit(pc : Int) : Boolean

    fun update(branchAddress:Int, target: Int, isTaken: Boolean)

    fun target(pc : Int) : Int

    fun state(pc: Int): IBitStateMachine
}
