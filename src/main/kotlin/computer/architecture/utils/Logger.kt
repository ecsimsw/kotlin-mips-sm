package computer.architecture.utils

import computer.architecture.cpu.Opcode
import computer.architecture.cpu.dto.*

open class Logger {

    companion object {
        var loggingSignal = LoggingSignal()
        private var printControl = LoggingSignal()

        var cycleCount = 0
        private var numberOfExecutedMA = 0
        private var numberOfWriteBack = 0
        private var numberOfTakenBranches = 0
        private var executedOpcodes = mutableMapOf<Opcode, Int>()
        private var executedOpcodeType = mutableMapOf<Opcode.Type, Int>()
        private var executedInstructionSet = mutableSetOf<Int>()
        private var takenCount = 0
        private var predictionSucceedCount = 0
        private var predictionFailedCount = 0
        private var hitCount = 0
        private var missCount = 0
        private var memoryFetchCount = 0
        private var memoryWriteCount = 0
        private var indexUsage = mutableMapOf<Int, Int>()
        private var missedIndex = mutableMapOf<Int, Int>()
        private var victimSetIndex = mutableMapOf<Int, MutableMap<Int, Int>>()
        private var ffIndex = mutableMapOf<Int, Int>()
        private var fsIndex = mutableMapOf<Int, Int>()

        fun init() {
            printControl = loggingSignal.copy()
            cycleCount = 0
            numberOfExecutedMA = 0
            numberOfWriteBack = 0
            numberOfTakenBranches = 0
            executedOpcodes = mutableMapOf()
            executedOpcodeType = mutableMapOf()
            executedInstructionSet = mutableSetOf()
            takenCount = 0
            predictionSucceedCount = 0
            predictionFailedCount = 0
            hitCount = 0
            missCount = 0
            memoryFetchCount = 0
            memoryWriteCount = 0
            indexUsage = mutableMapOf()
            missedIndex = mutableMapOf()
            victimSetIndex = mutableMapOf()
        }

        fun log(
            fetchResult: FetchResult,
            decodeResult: DecodeResult,
            executionResult: ExecutionResult,
            memoryAccessResult: MemoryAccessResult,
            writeBackResult: WriteBackResult
        ) {
            collect(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
            printCycleLog(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
        }

        fun printCycleLog(
            fetchResult: FetchResult,
            decodeResult: DecodeResult,
            executionResult: ExecutionResult,
            memoryAccessResult: MemoryAccessResult,
            writeBackResult: WriteBackResult
        ) {
            printFetchResult(fetchResult)
            printDecodeResult(decodeResult)
            printExecutionResult(executionResult)
            printMemoryAccessResult(memoryAccessResult)
            printWriteBackResult(writeBackResult)
        }

        fun collect(
            fetchResult: FetchResult,
            decodeResult: DecodeResult,
            executionResult: ExecutionResult,
            memoryAccessResult: MemoryAccessResult,
            writeBackResult: WriteBackResult
        ) {
            executedInstructionSet.add(fetchResult.instruction)

            if (decodeResult.valid) {
                executedOpcodes[decodeResult.controlSignal.opcode] =
                    executedOpcodes.getOrDefault(decodeResult.controlSignal.opcode, 0) + 1
                executedOpcodeType[decodeResult.controlSignal.opcode.type] =
                    executedOpcodeType.getOrDefault(decodeResult.controlSignal.opcode.type, 0) + 1
            }

            if (executionResult.valid) {
                if (executionResult.controlSignal.branch && executionResult.aluValue == 1) {
                    numberOfTakenBranches++
                }
            }

            if (memoryAccessResult.valid) {
                if (memoryAccessResult.controlSignal.memRead || memoryAccessResult.controlSignal.memWrite) {
                    numberOfExecutedMA++
                }
            }

            if (writeBackResult.valid) {
                if (writeBackResult.controlSignal.regWrite) {
                    numberOfWriteBack++
                }
            }
        }

        fun printCycle(printOrNot: Boolean, cycleCount: Int) {
            checkPrintRange(cycleCount)
            if (!printOrNot) {
                return
            }
            this.cycleCount = cycleCount
            try {
                Thread.sleep(printControl.sleepTime)
                if (!printControl.cycle) return
                if (this.cycleCount % printControl.cyclePrintPeriod == 0) {
                    println("cycle : ${this.cycleCount}")
                }
            } catch (e: InterruptedException) {
            }
        }

        private fun checkPrintRange(cycleCount: Int) {
            if (cycleCount >= printControl.from && cycleCount <= printControl.to) {
                printControl.fetch = loggingSignal.fetch
                printControl.decode = loggingSignal.fetch
                printControl.execute = loggingSignal.fetch
                printControl.memoryAccess = loggingSignal.fetch
                printControl.writeBack = loggingSignal.fetch
            } else {
                printControl.fetch = false
                printControl.decode = false
                printControl.execute = false
                printControl.memoryAccess = false
                printControl.writeBack = false
            }
        }

        fun printCycle(cycleCount: Int) {
            printCycle(true, cycleCount)
        }

        fun predictTaken() {
            takenCount++
        }

        fun predictionSucceed() {
            predictionSucceedCount++
        }

        fun predictionFailed() {
            predictionFailedCount++
        }

        fun cacheHit() {
            hitCount++
        }

        fun cacheMiss() {
            missCount++
        }

        fun memoryFetch() {
            memoryFetchCount++
        }

        fun memoryWrite() {
            memoryWriteCount++
        }

        fun indexSet(index : Int) {
            indexUsage[index] = indexUsage.getOrDefault(index, 0) + 1
        }

        fun indexMiss(index : Int) {
            missedIndex[index] = missedIndex.getOrDefault(index, 0) + 1
        }

        fun victim(lineIndex : Int, setIndex: Int) {
            val victimMap : MutableMap<Int, Int> = victimSetIndex.getOrDefault(lineIndex, mutableMapOf())
            victimMap[setIndex] = victimMap.getOrDefault(setIndex, 0) +1
            victimSetIndex[lineIndex] = victimMap
        }

        fun ff(addr : Int) {
            ffIndex[addr] = ffIndex.getOrDefault(addr, 0) + 1
        }

        fun fs(addr : Int) {
            fsIndex[addr] = ffIndex.getOrDefault(addr, 0) + 1
        }

        private fun printFetchResult(result: FetchResult) {
            if (!printControl.fetch) return
            if (!result.valid) {
                printStep("IF", result.pc)
                printNop()
                return
            }

            printStep("IF", result.pc)
            print("pc : 0x${(result.pc).toHexString(2)}, ")
            print("instruction : 0x${result.instruction.toHexString(8)}")
            println()
        }

        private fun printDecodeResult(result: DecodeResult) {
            val opcode = result.controlSignal.opcode
            if (!printControl.decode) return
            if (!result.valid) {
                printStep("ID", result.pc)
                printNop()
                return
            }

            printStep("ID", result.pc)

            var msg = "opcode : ${opcode}, "
            msg += "rs : ${result.readReg1}, rt : ${result.readReg2} "
            if (opcode.type == Opcode.Type.R) {
                msg += "readData1 : ${result.readData1}, readData2 : ${result.readData2}"
            }

            if (opcode.type == Opcode.Type.I) {
                msg += "readData1 : ${result.readData1} [0x${result.readData2.toHexString()}], " +
                        "immediate : ${result.immediate} [0x${result.immediate.toHexString()}]"
            }

            if (opcode.type == Opcode.Type.J) {
                msg += "address : 0x${result.address.toHexString()}"
            }
            println(msg)
        }

        private fun printExecutionResult(result: ExecutionResult) {
            if (!printControl.execute) return
            if (!result.valid) {
                printStep("EX", result.pc)
                printNop()
                return
            }

            printStep("EX", result.pc)
            val msg = "result : ${result.aluValue} [0x${result.aluValue.toHexString()}], " +
                    "nextPc : 0x${result.nextPc.toHexString()}"
            println(msg)
        }

        private fun printMemoryAccessResult(result: MemoryAccessResult) {
            if (!printControl.memoryAccess) return
            if (!result.valid) {
                printStep("MA", result.pc)
                printNop()
                return
            }
            printStep("MA", result.pc)
            var msg = ""
            if (result.controlSignal.memRead) {
                msg =
                    "M[0x${result.address.toHexString()}] = ${result.memReadValue} [0x${result.memReadValue.toHexString()}]"
            }
            if (result.controlSignal.memWrite) {
                msg =
                    "M[0x${result.address.toHexString()}] = ${result.memWriteValue} [0x${result.memWriteValue.toHexString()}]"
            }
            println(msg)
        }

        private fun printWriteBackResult(result: WriteBackResult) {
            if (!printControl.writeBack) return
            if (!result.valid) {
                printStep("WB", result.pc)
                printNop()
                println()
                return
            }

            printStep("WB", result.pc)
            var msg = ""
            if (result.controlSignal.regWrite) {
                msg = "R[${result.writeReg}] = ${result.regWriteValue} [0x${result.regWriteValue.toHexString()}]"
            }
            println(msg + "\n")
        }

        fun printProcessResult(resultValue: Int) {
            if (!printControl.result) return

            println("=== Result === ")
            println("cycle count : $cycleCount")
            println("result value : $resultValue")
            println()

            println("=== Cache result === ")
            println("hit count : ${hitCount}")
            println("miss count : ${missCount}")
            println("memory write count : ${memoryWriteCount}")
            println("memory fetch count : ${memoryFetchCount}")
            if (hitCount + missCount == 0) {
                println("cache hit ratio : 0%")
            } else {
                println("cache hit ratio : ${hitCount.toFloat() / (hitCount + missCount) * 100}%")
            }
            println()

            println("=== Cache result === ")
            println("Line usage ratio")
            val indexLine = indexUsage.values.sum()
            indexUsage.forEach { index , value ->
                println("index -> ${index} : ${value.toFloat()/indexLine * 100}%")
            }
            println()

            println("Miss line ratio")
            val missedLine = missedIndex.values.sum()
            missedIndex.forEach { index , value ->
                println("index -> ${index} : ${value.toFloat()/missedLine * 100}%")
            }
            println()

            println("victim line ratio")
            victimSetIndex.forEach { index , value ->
                println("index -> ${index}")
                value.forEach { set, value ->
                    println("${set} : $value")
                }
            }
            println()

            println("55 line ratio")
            var sum = ffIndex.values.sum()
            ffIndex.forEach { index , value ->
                    println("${index} : ${value.toFloat()/sum * 100} ")
            }
            println()

            println("56 line ratio")
            sum = fsIndex.values.sum()
            fsIndex.forEach { index , value ->
                println("${index} : ${value.toFloat()/sum * 100} ")
            }
            println()

            println("=== Prediction result === ")
            val totalPredictedCount = predictionSucceedCount + predictionFailedCount
            println("total prediction count : ${totalPredictedCount}")
            println("taken count : $takenCount")
            if (totalPredictedCount == 0) {
                println("taken ratio : 0%")
            } else {
                println("taken ratio : ${takenCount / totalPredictedCount.toFloat() * 100} %")
            }
            println("succeed prediction count : $predictionSucceedCount")
            println("failed prediction count : $predictionFailedCount")

            if (totalPredictedCount == 0) {
                println("success ratio : 0%")
            } else {
                println("success ratio : ${predictionSucceedCount / totalPredictedCount.toFloat() * 100}%")
            }
            println()

            println("=== executed instructions ===")
            println("executed memory access : $numberOfExecutedMA")
            println("executed taken branches : $numberOfTakenBranches")
            println("executed write back : $numberOfWriteBack")
            println("kinds of instructions : ${executedInstructionSet.size}")
            println()

            println("=== executed opcode type ===")
            println("type R : ${executedOpcodeType.getOrDefault(Opcode.Type.R, 0)}")
            println("type I : ${executedOpcodeType.getOrDefault(Opcode.Type.I, 0)}")
            println("type J : ${executedOpcodeType.getOrDefault(Opcode.Type.J, 0)}")
            println()

            println("=== executed opcode === ")
            print("[] : ")
            executedOpcodes.forEach { print("$it ") }
            println()
        }

        private fun printStep(stepName: String, pc: Int) {
            print("[$stepName] [$pc] :: ")
        }

        private fun printNop() {
            println("[NOP]")
        }
        
        fun log(msg : String) {
            if (cycleCount >= printControl.from && cycleCount <= printControl.to) {
                println(msg)
            }
        }
    }
}
