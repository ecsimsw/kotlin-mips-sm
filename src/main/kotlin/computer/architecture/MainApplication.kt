package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal

/*
 covered :
 simple.bin
 simple2.bin
 simple3.bin
 simple4.bin
 gcd.bin
 fib.bin
 */

fun main() {
    initLoggingSignal()

    val instructionMemory = Memory(160000000)
    instructionMemory.loadFile("test_prog/input4.bin", 0)

    val controlUnit = ControlUnit(instructionMemory)
    controlUnit.process()

//    for( (i, value) in instructionMemory.memory.withIndex()) {
//        if(i < 530) {
//            println("0x${(i*4).toHexString()}" + " : 0x" + value.toHexString())
//        }
//    }
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        cycleLogging = true,
        fetchLogging = false,
        decodeLogging = false,
        executeLogging = false,
        memoryAccessLogging = false,
        writeBackLogging = false,
        finalValue = true,
        sleepTime = 0
    )
}
