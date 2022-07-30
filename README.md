# Computer architecture
MIPS binary file을 읽어 명령어를 해석하고 이를 실행시킬 수 있는 프로세서를 설계한다.   
   
Pipeline : Single-cycle에서 Pipelining, BranchPrediction을 적용한 Multi-cycle으로 설계를 수정하며 각 단계별 사이클 수 변화를 테스트한다.   
Cache : Cache를 적용하여 쓰기 정책, 교체 전략에 따른 Memory 접근 횟수 변화를 테스트한다.   

</br>

## 1) Pipeline

### Implementation

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
 
</br>
 
## 2) Cache

### Implementation
 
Set size, Cache line 수, Block size를 조절할 수 있는 캐시를 구현한다.
   - Direct mapped cache를 구현한다.
   - N way set associative mapped cache를 구현한다.
   - Fully associative mapped cache를 구현한다.
 
쓰기 정책을 지정할 수 있다.
   - Write through cache 정책을 구현한다.
   - Write back cache 정책을 구현한다.
 
다양한 교체 전략을 선택할 수 있다.
   - FIFO 교체 전략을 구현한다.
   - Random 교체 전략을 구현한다.
   - Second chance 알고리즘 교체 전략을 구현한다.
   - LRU 알고리즘 교체 전략을 구현한다.
 
</br>

### Class diagram 
 
구현한 Cache와 교체 정책에 대한 Class 다이어그램은 다음과 같다.

![R1280x0-2](https://user-images.githubusercontent.com/46060746/182003006-5b3dd47c-1a15-4889-b51d-be281c4cffd4.png)

</br>

### AbstractAssociativeMappedCache
 
캐시는 주소 비트, byte offset 비트, 세트 수, cache line 수, 블록 사이즈 수를 변수로 하여 유동적으로 캐시를 구성할 수 있도록 하였다. 주소 비트와 byte offset 비트의 기본 값은 MIPS를 기준으로 하여 각각 32, 2비트이고, 이렇게 입력받은 주소 체계를 기준으로 나머지 indexBits, setBits, offsetBits로 캐시 구성이 가능한지 유효 여부를 확인한다. tag로 사용되는 비트 수는 이때 함께 계산된다.

``` kotlin
abstract class AbstractAssociativeMappedCache(
    private val addressBits: Int = 32,
    private val byteOffsetBits: Int = 2,
    private val offsetBits: Int,
    private val indexBits: Int,
    private val setBits: Int,
    protected val replacementStrategy: LruReplacementStrategy
) : ICache {

    open fun read(address: Int): Int { .. }

    abstract fun memoryFetch(tag: Int, lineIndex: Int): Int
 }
```
 
</br>
 
### Direct mapped cache, N way set associative cache, Fully associative cache
 
위 추상 클래스를 기반으로 하여 주소에서 표현될 비트 수를 달리하는 것만으로 Direct mapped cache, N way set associative cache, Fully associative cache를 표현할 수 있었다. 아래는 Set associative cache의 set 수를 0으로 하여 표현한 DirectMappedCache의 전체 코드이다. 
```kotlin
class WriteBackDirectMappedCache(
    memory: Memory,
    offsetBits: Int,
    indexBits: Int
) : WriteBackSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits = indexBits,
    setBits = 0
)
```

반대로 FullyAssociativeMappedCache는 set 수가 최대이고, index 수를 0으로 하여 다음과 같이 표현할 수 있다. DirectMappedCache와의 차이점은 교체 전략이 불필요했던 Direct와 달리 FullyAssociative는 교체 전략이 필요하여 생성 시 주입을 받는다는 점뿐이다.

```kotlin
class WriteBackFullyAssociativeMappedCache(
    memory: Memory,
    offsetBits: Int,
    lineBits: Int,
    replacementStrategy: CacheReplacementStrategy
) : WriteBackSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits =  0,
    setBits = lineBits,
    replacementStrategy = replacementStrategy
)
``` 

</br>

### Cache write 
 
Cache의 쓰기 정책으로 Write through(바로 쓰기), Write back (나중 쓰기)을 구현하였다. 먼저 WriteThrough의 경우 hit여부와 상관없이 우선 memory에 데이터를 쓰기 한다. 이후에 setIndex가 존재함에 따라(동일 tag가 존재하는 set의 인덱스 확인), hit와 miss여부를 확인한 후에 hit일 경우 캐시에 요청 데이터를 업데이트, miss일 경우 memory fetch를 하는 것으로 캐시 라인을 메모리와 동기화한다.

``` kotlin
override fun write(address: Int, value: Int) {
    val tag = tag(address)
    val lineIndex = index(address)
    val offset = offset(address)

    memory.write(address, value)

    val setIndex = setIndex(tag, lineIndex)
    if (setIndex != -1) {
        replacementStrategy.use(setIndex, lineIndex)
        lineSets[setIndex][lineIndex].datas[offset] = value
    } else {
        memoryFetch(tag, lineIndex)
    }
}
```
 
Write Through와 마찬가지로 쓰기 요청이 왔을 때 해당 index-tag 값을 갖고 있는 세트 여부를 확인하여 hit, miss여부를 확인한다.  차이점은 Write back에선 hit 시 캐시에만 데이터를 쓰고 해당 캐시 라인에 dirty 임을 표시한다. miss 시 memory fetch로 캐시 라인을 업데이트하고 해당 라인에 쓰기 요청을 반영, 마찬가지로 dirty 임을 표시한다. 

``` kotlin
override fun write(address: Int, value: Int) {
    val tag = tag(address)
    val lineIndex = index(address)
    val offset = offset(address)

    val setIndex = setIndex(tag, lineIndex)
    if (setIndex != -1) {
        replacementStrategy.use(setIndex, lineIndex)
        dirties[setIndex][lineIndex] = true
        lineSets[setIndex][lineIndex].datas[offset] = value
    } else {
        val newSetIndex = memoryFetch(tag, lineIndex)
        dirties[newSetIndex][lineIndex]= true
        lineSets[newSetIndex][lineIndex].datas[offset] = value
    }
}
```

이렇게 dirty로 표시된 캐시라인은 memory fetch가 이뤄지면서 교체 알고리즘에 의해 해당 라인이 교체 대상이 되는 경우에 메모리에 반영된다. 아래는 WriteBack에서 재정의된 memory fetch 코드이다. 교체 알고리즘에 의해 lineIndex의 교체되어야 하는 set가 결정되면 해당 라인이 dirty인지 확인하여 그때서야 memory write가 일어난다. 이후 dirty 여부를 다시 false로 초기화하고 캐시 라인에 메모리 동기화가 일어나게 된다.

``` kotlin
override fun memoryFetch(tag: Int, lineIndex: Int): Int {
    for (setIndex in 0 until setSize) {
        //cacheLine의 valid가 fale인 경우 해당 set를 바로 반환
    }

    val victimSet = replacementStrategy.nextVictim(lineIndex)
    updateDirties(victimSet, lineIndex)
    dirties[victimSet][lineIndex] = false
    lineSets[victimSet][lineIndex].fetch(tag, readBlockLine(tag, lineIndex))
    return victimSet
}
```
</br>
 
#### Replacement strategy
 
교체 전략은 FIFO(first in, first out), Random, SecondChance, LRU 교체 정책 네 가지를 구현하였다. 그리고 이들을 CacheReplacementStragy이라는 인터페이스로 묶어 Cache에서 교체 전략을 자유롭게 선택할 수 있도록 구성하였다. 아래는 CacheReplacementStrategy의 구현 관계와 이 인터페이스와 AbstractAssociativeMappedCache의 의존성 관계를 보여주는 클래스 다이어그램이다. 

</br>

#### 정적 교체 전략 / FIFO, Random

FIFO의 경우 마지막으로 교체된 index를 기억하고 여기에 매 교체시마다 1을 더하여 반환하게 된다. (기존 값+1)을 setSize로 나눈 값을 저장하게 되어 인덱스 오버플로우를 방지한다.

``` kotlin
override fun nextVictim(lineIndex: Int): Int {
    lastUsed = (lastUsed + 1) % setSize
    return lastUsed
}
```

Random의 경우 0~setSize-1 까지의 인덱스를 반환하여 교체될 setIndex를 결정하게 된다.

```kotlin
override fun nextVictim(lineIndex: Int): Int {
    return random.nextInt(setSize)
}
```

</br>

#### 동적 교체 전략 / SecondChance, LRU
 
동적 교체 전략은 hit시 사용된 set를 기억하고, 다음 교체 대상에 이를 사용한다. SecondChance의 경우 (set * set당 cache line 수)만큼의 chance를 담는 배열을 만들어 사용한다. hit 되는 경우 이 set에 chance를 부여하고, 교체 대상으로 해당 set가 지정되는 경우 chance를 제거하는 전략을 구현하였다. 

``` kotlin
override fun use(setIndex: Int, lineIndex: Int) {
    chanceHistories[lineIndex][setIndex] = true
}

override fun nextVictim(lineIndex: Int): Int {
    while (true) {
        lastUsed = (lastUsed + 1) % setSize
        if (!chanceHistories[lineIndex][lastUsed]) {
            return lastUsed
        }
        chanceHistories[lineIndex][lastUsed] = false
    }
}
```

LRU는 hit시 사용을 기록하고 사용에 가장 오래된 set를 교체 대상으로 하는 정책이다. 각 cacheLine 마다 사용된 set 인덱스를 기록하는 리스트를 선언하고 교체 대상 구하기에 이를 사용한다. hit 시 리스트에서 사용된 setIndex의 값을 리스트의 가장 마지막으로 순서를 이동하고, 교체 대상을 확인할 때는 리스트의 첫 요소를 반환하는 것으로 사용에 가장 오래된 set를 구할 수 있었다.

``` kotlin
override fun use(setIndex: Int, lineIndex: Int) {
    val history = usedHistories[lineIndex]
    history.remove(setIndex)
    history.add(setIndex)
}

override fun nextVictim(lineIndex: Int): Int {
    val history = usedHistories[lineIndex]
    return history[0]
}
``` 

</br>

### Test result
 
1. 쓰기 방식에 따른 Memory write 횟수를 비교한다. (Write back, Write through)

![image](https://user-images.githubusercontent.com/46060746/182003243-48055f60-92ff-4e86-b91d-74550176ac50.png)

- Block size : 16, Cache line : 256, Direct mapped cache
 
 
2. 교체 Set 수에 따른 Hit률을 비교한다. (Direct mapped, 2way, 4way, 16way, 32way, 128way, 256way, Fully associative)

![R1280x0-4](https://user-images.githubusercontent.com/46060746/182003256-903f9670-ad44-4677-9262-e672bccbcc9a.png)

- block size : 16, Cache line : 256, Replacement strategy : FIFO, Write policy : write back
 
 
3. 교체 알고리즘에 따른 Hit률을 비교한다. (FIFO, Random, Second chance, LRU)

![image](https://user-images.githubusercontent.com/46060746/182003261-5d107e7e-3884-4ede-81be-23afd51e1852.png)

- block size : 16, Cache line : 256, Set size : 4, Write policy : write back
 
 
4. Block 사이즈에 따른 Hit률을 비교한다. (4, 16, 64, 256, 1024)

![image](https://user-images.githubusercontent.com/46060746/182003266-0e97459c-cb54-4268-acc3-2eb4598c817f.png)

- Cache line : 4096 / blockSize, DirectMapped, Replacement strategy : FIFO, Write policy : write back
 
 
5. 캐시를 사용의 성능 향상률을 확인한다.

![image](https://user-images.githubusercontent.com/46060746/182003269-8c3e56bf-d0d2-4f4e-bb40-7dac01811dd6.png)

- Block size : 16, Cache line : 256, Set size : 4, Replacement strategy : Random, Write policy : write back
- Input4 기준, 99.76%의 hit율로 메모리 쓰기에선 99.76%의, 메모리 읽기에선 99.75%의 접근 횟수 감소율을 얻을 수 있었다.
