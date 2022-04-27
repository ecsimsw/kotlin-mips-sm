## simple-computer-architecture

Define instruction set and implement a simple calculator that can execute instructions 

<br>

### How to run 

Quick start (sample code)
```
./gradlew process
```

- run your own code file written in my ISA
```
./gradlew process -PinputFile="${filePath}"
```

- you also can set multiple code files split by ,
```
./gradlew process -PinputFile="${filePath1, filePath2, filePath3}"
```

- run test code
```
./gradlew test
```

<br>

### Instruction set

1. Instruction format   
- Instruction consists of an operation code and two operands, and each of them is seperated by a delimiter (blank).
- Meaningless operands are ignored, if the operation doesn't need one or both of the operands.   

2. operand format
- There are two types of operand. 
- Numeric type operand is a hexadecimal number that prefixed with "0x"
- Register value type operand indicate value of register that prefixed with "R"

3. Operation code

|NAME|CODE|OPERATION|NOTE|
|:----:|:----:|:---------:|:----:|
|ADD|+|R[0]=o1+o2||
|MINUS|-|R[0]=o1-o2||
|MULTIPLY|*|R[0]=o1*o2||
|DIVIDE|/|R[0]=o1/o2||
|MOD|%|R[0]=o1%o2||
|SLL|<<|R[0]=o1<<o2||
|SRL|>>|R[0]=o1>>op2||
|AND|&&|R[0]=op1&op2||
|JUMP|J|pc=op1|op2 is ignored|
|JUMP_AND_LINK|JAL|R[op1]=pc, pc=op2|op1 should be register number|
|BRANCH|B|pc=(R[0]==1) ? op1 : pc|op2 is ignored|
|BRANCH_ON_EQUAL|BEQ|pc=(R[0]==op1) ? op2 : pc||
|BRANCH_ON_NOT_EQUAL|BEQ|pc=(R[0]!=op1) ? op2 : pc||
|CONDITION|C|R[0]=(op1<op2) ? 1 : 0||
|MOVE|M|R[op1]=op2|op1 should be register number|
|LOAD_WORD|LW|R[0]=M[op1]|op2 is ignored|
|STORE_WORD|SW|M[op2]=op1||
|ANNOTATION|//| |Do nothing|
|HALT|H|pc=INT.MAX_VALUE|End program|

<br>

### Sample code

1. GCD in my ISA
```
// MAIN 0x00
M R1 0x23       // SET INITIAL ARGUMENT_1
M R2 0x19       // SET INITIAL ARGUMENT_2
M R9 0xFF       // SET INITIAL STACK_POINTER
JAL R8 0x07     // JUMP to GCD
H 0x00 0x00

// GCD 0x07
- R9 0x04
M R9 R0         // SP = SP-4
SW R8 R9        // M[SP] = RA 
+ R9 0x01
SW R1 R0        // M[SP+1] = R1
+ R9 0x02
SW R2 R0        // M[SP+2] = R2
+ R9 0x03
M R7 R1         // V0 = R1
SW R7 R0        // M[SP+3] = V0
M R0 R2
BEQ 0x00 0x1D   // IF(R2 == 0) JUMP to EXIT
J 0x16 0x00     // JUMP to FALSE

// FALSE 0x16
% R1 R2
M R3 R0         // R3 = R1 % R2
M R1 R2         // R1 = R2
M R2 R3         // R2 = R3
JAL R8 0x08     // JUMP to GCD

// EXIT 0x1D
+ R9 0x04
M R9 R0         // SP = SP+4
- R9 0x02
LW R0 0x00
M R2 R0         // R2 = M[SP-2]
- R9 0x03
LW R0 0x00
M R1 R0         // R1 = M[SP-3]
- R9 0x04
LW R0 0x00
M R8 R0         // RA = M[SP-4]
J R8 0x00       // JUMP to RA
```

2. Register usage in sample code
```
R0 : Temporary space for execute result   
R1 : Argument1   
R2 : Argument2   
R3 ~ R6 : Temporaries   
R7 : Value for function result (V0)   
R8 : Return address   
R9 : Stack pointer
```

3. How to customize
- Change argument values and Stack pointer that are in 2~3 line
```
M R1 0x23       // SET INITIAL ARGUMENT_1
M R2 0x19       // SET INITIAL ARGUMENT_2
M R9 0xFF       // SET INITIAL STACK_POINTER
```
- If there is a lot of computation, you need to increase the memory size. Set memory size when instantiating Memory object.
``` kotlin
val memory = Memory(1000) // Change 1000 to size you want
```
- You can set register size when instantiating Register object 
``` kotlin
val register = Registers(32) // Set register size 32
```
- Or you can allocate register size when instantiating ControlUnit object
``` kotlin
val controlUnit = ControlUnit(memory) // Default size is 10
val controlUnit = ControlUnit(memory, 15) // Set register size 15
```
