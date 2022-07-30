# Computer architecture
MIPS binary file을 읽어 명령어를 해석하고 이를 실행시킬 수 있는 프로세서를 설계한다.   
   
Pipeline : Single-cycle에서 Pipelining, BranchPrediction을 적용한 Multi-cycle으로 설계를 수정하며 각 단계별 사이클 수 변화를 테스트한다.   
Cache : Cache를 적용하여 쓰기 정책, 교체 전략에 따른 Memory 접근 횟수 변화를 테스트한다.   
</br>

## 1) Pipeline

### 구현 내용

ControlUnit
 - 단일 프로그램을 싱글사이클로 처리할 수 있다. 
 - 단일 프로그램을 멀티사이클 / 파이프라인으로 처리할 수 있다.
 - 다중 프로그램을 멀티사이클/파이프라인으로 처리할 수 있는 멀티 프로세서를 구현한다.
 
ProgramCounter
 - 분기 예측 없이 Stalling만으로 분기 명령어를 처리할 수 있다.
 - 분기 예측 전략에 따른 정적 분기 예측을 수행할 수 있다.
 - 분기 예측 전략에 따른 동적 분기 예측을 수행할 수 있다.
   - Local history register와 Pattern history register를 이용한 분기 예측을 수행할 수 있다.
   - Global history register와 Pattern history register를 이용한 분기 예측을 수행한다.
 
BranchPredictionStrategy
  - Always taken으로 예측하는 전략을 정의한다.
  - Always not taken 으로 예측하는 전략을 정의한다.
  - Backward taken, Forward not taken 예측하는 전략을 정의한다.
 
BitStateMachine
  - 단일 비트로 예측하는 상태 머신을 정의한다.
  - Saturation two bit counter 예측을 위한 2 bit 상태 머신을 정의한다.
  - Hysteresis two bit counter 예측을 위한 2 bit 상태 머신을 정의한다.
 
HistoryRegister / Branch Target Buffer
  - 최근 n번의 지역 분기 결과를 저장할 수 있는 Local history register를 정의한다.
  - 최근 n번의 전역 분기 결과를 저장할 수 있는 Global history register를 정의한다.
  - 최근 분기 결과마다의 예측 결과 패턴을 저장할 수 있는 Pattern history register를 정의한다.
  - pc로 인덱싱 된 Branch target buffer를 사용하여 분기 내용을 캐싱할 수 있다.
 
 </br>
 
### Class diagram

![image](https://user-images.githubusercontent.com/46060746/182002679-a8910197-39e8-4c61-b472-2302626dc721.png)

</br>

### ControlUnit

#### SingleProcessingPipelineControlUnit
 
단일 프로그램을 파이프라인으로 처리하는 방식의 5단계를 추상 클래스로 묶는다. Data Hazard, Control Hazard를 처리하는 방식의 다양함과는 상관없이 기본 fetch, decode, execute, memoryAccess, writeBack 단계의 내용은 동일하다. 이후 단일 프로그램을 파이프라인으로 처리하는 구현체는 이를 상속하고 앞선 5단계를 어떻게 수행할지 결정하는 cycleExecution()만을 재정의하는 것으로 파이프라인 ControlUnit을 정의할 수 있다.

``` kotlin
abstract class SingleProcessingPipeLineControlUnit(
    private val memory: Memory,
) : IControlUnit {
    protected val registers: Registers = Registers(32)
    protected val stallUnit = StallUnit()
    protected val latches = Latches()
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()

    abstract fun cycleExecution(valid: Boolean, pc: Int): CycleResult

    override fun process(): List<Int> { .. }
    fun fetch(valid: Boolean, pc: Int): FetchResult { .. }
    fun decode(ifResult: FetchResult): DecodeResult { .. }
    fun execute(idResult: DecodeResult): ExecutionResult { .. }
    fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult { .. }
    fun writeBack(maResult: MemoryAccessResult): WriteBackResult { .. }
}
```
</br>

#### ForwardingPipeLineControlUnit
 
forwarding을 이용하여 DataHazard를 피하도록 설계한 ControlUnit이다. 한 사이클에서 다섯 단계가 동시에 처리되면서, Latch를 이용하여 이전 사이클의 결과를 다음 사이클의 다음 단계로 넘기는 구조를 기본으로 한다. 여기에 ForwardingUnit이 execution 단계 전에 이전 사이클의 출력 값을 확인하여 dataFowarding을 처리한다.
 
pcUnit은 ProgramCounterUnit 인터페이스를 타입으로 하여, 어떤 PcUnit 구현체로도 쉽게 바꿀 수 있도록 하였다. 예를 들어 pcUnit을  NonePrediction으로 한다면 branchPrediction 없이 stalling만으로 controlHazard를 처리하도록, DynamicBranchPredictionPcUnit을 구현체로 한다면 동적 분기 예측 전략을 다시 선택하여 해당 전략으로 pc를 얻을 수 있도록 구현하여 변화와 전략에 유연한 코드를 만들고자 하였다.

``` kotlin
class ForwardingPipelineControlUnit(
    memory: Memory,
    private val pcUnit: IProgramCounterUnit = NonePredictionPcUnit()
) : SingleProcessingPipelineControlUnit(memory) {
    private val forwardingUnit = ForwardingUnit()

    override fun cycleExecution(valid: Boolean, pc: Int): CycleResult {
        val prevIfId = latches.ifId()
        val prevIdEx = latches.idEx()
        val prevExMa = latches.exMa()
        val prevMaWb = latches.maWb()

        val wbResult = writeBack(prevMaWb)
        val nextMaWb = memoryAccess(prevExMa)
        forwardingUnit.forward(prevIdEx, prevExMa, prevMaWb)
        val nextExMa = execute(prevIdEx)
        val nextIdEx = decode(prevIfId)
        val nextIfId = fetch(valid, pc)
        val nextPc = pcUnit.findNext(pc, nextIfId, nextIdEx, nextExMa)

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)

        return CycleResult(
            nextPc = nextPc,
            value = registers[2],
            valid = wbResult.valid,
            isEnd = nextPc == -1,
            lastCycle = wbResult.controlSignal.isEnd
        )
    }
}
```
</br>

#### MultiProcessingPipelineControlUnit
 
Pipeline에서 각 단계에 서로 다른 프로그램이 처리된다면, 그리고 register와 memory가 각 프로세스마다 분리되어 있다면 dataHazard와 controlHazard를 고민하지 않아도 되겠다는 아이디어에서 다중 프로그램을 처리할 수 있는 pipelined control unit을 간단히 구현해보았다.
 
구현한 MultiProcessingPipelineControlUnit은 프로그램마다 서로 다른 ControlRegister를 갖고 있고, SchedulingUnit이 사이클 시작 시 처리할 프로그램 번호와 pc, 끝나면 해당 사이클의 writeBack 결과를 프로그램 번호에 해당하는 controlRegister에 반영하도록 하였다.

``` kotlin
class MultiProcessingPipelineControlUnit(
    private val memories: List<Memory>
) : IControlUnit {
    private val registers: List<Registers> = List(memories.size) { Registers(32) }
    private val schedulingUnit = SchedulingUnit(memories.size)

    override fun process(): List<Int> {
        var cycle = 0

        Logger.init()
        while (!schedulingUnit.isAllEnd()) {
            val programInfo = schedulingUnit.next()
            val isProcessEnd = programInfo.processEnd
            val cycleResult = cycleExecution(!isProcessEnd, programInfo.pn, programInfo.nextPc)
            
            schedulingUnit.update(cycleResult)

            latches.flushAll()
            cycle++
        }
        return registers.map { it[2] }.toList()
    }

    ...
}
```
</br>
 
### ProgramCounterUnit

pc를 계산하는 방식에서, Branch Prediction을 진행하지 않는 NonePredictionPcUnit, 정적 예측을 진행하는 StaticBranchPredictionUnit, 동적 예측을 진행하는 DynamicBranchPredictionUnit, 그 아래 HistoryBuffer를 사용한 경우와 사용하지 않은 경우를 또 따로 분리하여 구현하였다.
 
이를 통해 ControlUnit에 원하는 ProgramCounter 방식과 예측 전략을 조립할 수 있었다. 아래는 DataHazard와 ControlHazard를 해결하는 방식을 각각 달리 조합하여 ControlUnit을 조합한 예시이다.

``` kotlin
// DataHazard = forwarding, ControlHazard = stalling
val pcUnit = NonePredictionPcUnit()
val controlUnit = ForwardingPipelineControlUnit(memory, pcUnit)

// DataHazard = stalling, ControlHazard = twoLevelGlobalHistoryBranchPrediction
val pcUnit = TwoLevelGlobalHistoryPredictionPcUnit()
val controlUnit = StallingPipelineControlUnit(memory, pcUnit)

// DataHazard = forwarding, ControlHazard = BTFNT_branchPrediction
val predictionStrategy = BTFNTStrategy()
val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
val controlUnit = ForwardingPipelineControlUnit(memory, pcUnit)
```
</br>

#### Branch Target Buffer
 
Branch Target Buffer은 valid bit, tag bit, targetAddress로 구성하였다. pc의 가장 마지막 2비트를 제외하고 buffer의 line 사이즈만큼의 하위 비트를 index, 나머지 상위 비트를 tag로 한다. 
 
pc의 index부로 buffer line을 찾고 해당 line의 tag와 pc의 tag부가 일치하는 경우를 hit로 한다. valid bit는 해당 line의 tag 값이 더미 값인지 아닌지를 구분하기 위해서 사용한다. 예를 들어 tag 초기 값이 모두 0인 경우에 pc의 tag가 0이라면 이 경우 hit -> targetAddress를 믿을 수 있는 것인지, 혹은 tag가 초기 값이라 targetAddress 역시 초기 값으로 hit가 아닌 상황인지 모른다. 이 상황을 피하기 위해 valid bit를 두고 초기 값을 false로 하였다.
 
``` kotlin
class BranchTargetBuffer(
    val size: Int
) {
    private val valid = Array(size) { false }
    private val tags = Array(size) { 0 }
    private val targetAddresses = Array(size) { 0 }

    fun update(pc: Int, targetAddress: Int) {
        val index = index(pc)
        valid[index] = true
        tags[index] = tag(pc)
        targetAddresses[index] = targetAddress
    }

    fun targetAddress(pc: Int): Int {
        val index = index(pc)
        return targetAddresses[index]
    }

    fun isHit(pc: Int): Boolean {
        val index = index(pc)
        return valid[index] && tags[index] == tag(pc)
    }

    private fun index(pc: Int): Int {
        return (pc / 4) % size
    }

    private fun tag(pc: Int): Int {
        return (pc / 4) / size
    }
}
```
</br>

### Test Result

0. 대상 프로그램은 Simple3, Simple4, GCD, Fib, Input4로 하였다.
1. Multi-Cycle : Single-cycle의 처리 명령어 종류와 개수를 바탕으로 Multi-cycle로 실행하였을 경우의 cycle count를 계산하였다.
2. Branch Target Buffer : buffer 라인을 16으로 하는 944bit 크기의 buffer를 사용하였다.
3. Local History Register : 최근 4개의 지역 Branch 여부 history를 저장하고, Buffer 라인은 16으로 하였다.
4. Global History Register : 최근 4개의 전역 Branch 여부 History를 저장하였다.
5. 2 Level History Prediction의 PatternHistory 전략은 Saturation 2bit counter를 사용하였다.
6. Multi Processing n process avg : 동일한 프로그램을 n개 동시 처리하고 각 cycle count를 평균 내었다.

![R1280x0](https://user-images.githubusercontent.com/46060746/182002844-b1561722-71a3-437b-8ef8-fd0ff7105ffc.png)

multi-cycle에 비해 pipelining으로 얻은 성능 증가 순위와 사이클 개선율은 다음과 같다.
 
1. Multi-Processing (317.373123%) 
2. Forwarding-2LevelLocalHistory (317.355177%)
3. Forwarding-2LevelGlobalHistory (317.354266%)
4. Fowarding-alwaysTaken (284.022638%)
 
prediction도 없고, 스케줄링도 제대로 안 되는 간단히 만들어 본 Multi-processing control unit이었지만, 처리하는 프로세스의 개수가 일정 개수 (input4 기준, 5개) 이상인 상황에서 다른 방식들보다 월등한 성능 차이를 내었다. 
 
alwaysTaken, alwaysNotTaken, BFTNT, 1bit counter, 2bit counter 등, 예제 프로그램의 개수가 적고, 내용이 짧아 BranchPrediction 전략에 따른 성능 차이를 명확하게 보긴 어려웠다. 프로그램이 좀 더 길고 다양한 분기 상황들이 더 많아야 예측 전략 비교가 가능할 것이라고 생각한다. 다만 Pipelining, Forwarding, BranchPrediction, Branch target buffer 등 구조적인 부분에서의 발전과 성능 차이는 명확히 확인할 수 있었던 테스트였다고 생각한다.
 
 
