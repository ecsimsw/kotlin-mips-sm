## simple-computer-architecture

Define instruction set and implement a simple calculator that can execute instructions 

<br>

### How to run 

Quick start (sample code)
```
git clone https://github.com/ecsimsw/simple-computer-architecture ecsimsw-ca
cd ecsimsw-ca
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

