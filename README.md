## Single cycle

Designed a single-cycle architecture that can run the MIPS program

</br>

## How to run 

run sample code : "sample/simple.bin"
```
./gradlew run
```

test : run 7 sample files and check result values are all correct
```
./gradlew cleanTest test  
```

</br>

## Features

1. Full data path
![image](https://user-images.githubusercontent.com/46060746/165592387-ad8009d7-b367-4db6-817d-edc98ec9de69.png)

2. Memory usage
- 32bit architecture
- Byte addressable
- Using little endian

3. Handling Mips Opcode

![image](https://user-images.githubusercontent.com/46060746/165592961-7648d246-8ea6-43fc-80c8-67238fd831d4.png)

4. Control Signals
- regDest : Determine the register to store the processing results
- jal : Determine the register to store the processing results (31)
- aluSrc : Determine the argument to be calculated
- shift : Determine the argument to be calculated
- upperImm : Determine the argument to be calculated
- memToReg : Determine the data to store in register
- regWrite : Whether register is written
- memRead : Whether memory is read
- memWrite : Whether memory is written
- jump : Determine next program counter
- branch : Determine next program counter
- jr : Determine next program counter
- aluOp : Determine operation that alu execute

5. ALU Operations (aluOp)

```
ADDITION : src1 + scr2
SUBTRACTION : scr1 - src2
OR : scr1 or scr2
SHIFT_LEFT : src1 << scr2
SET_LESS_THAN : scr1 < scr2 ? 1 : 0
EQUAL : src1 == scr2 ? 1 : 0
NOT_EQUAL : src1 != src2 ? 1 : 0
```

</br>

## Sample MIPS program

List
```
simple.bin : Simple return
simple2.bin : Return with value 100 
simple3.bin : Return the sum from 1 to 100 
simple4.bin : Return the sum from 1 to 10 using recursion 
gcd.bin : Calculate the GCD of 0x1298 and 0x9387
fib.bin : Calculate 10th Fibonacci number 
input4.bin : Return 102-th smallest number from 10000 random numbers. 
```

Binary file example in hexadecimal representation (gcd.bin)
```
00000000: 27bd ffd0 afbf 002c afbe 0028 03a0 f021  '......,...(...!
00000010: 2402 1298 afc2 0018 3402 9387 afc2 001c  $.......4.......
00000020: 8fc4 0018 8fc5 001c 0c00 0013 0000 0000  ................
00000030: afc2 0020 03c0 e821 8fbf 002c 8fbe 0028  ... ...!...,...(
00000040: 27bd 0030 03e0 0008 0000 0000 27bd ffe0  '..0........'...
00000050: afbf 001c afbe 0018 03a0 f021 afc4 0020  ...........!... 
00000060: afc5 0024 8fc3 0020 8fc2 0024 0000 0000  ...$... ...$....
00000070: 1462 0004 0000 0000 8fc2 0020 0800 0039  .b......... ...9
00000080: 0000 0000 8fc3 0020 8fc2 0024 0000 0000  ....... ...$....
00000090: 0043 102a 1040 000b 0000 0000 8fc3 0020  .C.*.@......... 
000000a0: 8fc2 0024 0000 0000 0062 1023 0040 2021  ...$.....b.#.@ !
000000b0: 8fc5 0024 0c00 0013 0000 0000 0800 0039  ...$...........9
000000c0: 0000 0000 8fc3 0024 8fc2 0020 0000 0000  .......$... ....
000000d0: 0062 1023 0040 2021 8fc5 0020 0c00 0013  .b.#.@ !... ....
000000e0: 0000 0000 03c0 e821 8fbf 001c 8fbe 0018  .......!........
000000f0: 27bd 0020 03e0 0008 0000 0000 0000 0000  '.. ............
00000100: 0a                                       .                         
```

Mips assembly file example (gcd.mips.asm)

```
00000000 <main>:
   0:   27bdffd0        addiu   sp,sp,-48
   4:   afbf002c        sw      ra,44(sp)
   8:   afbe0028        sw      s8,40(sp)
   c:   03a0f021        move    s8,sp
  10:   24021298        li      v0,4760
  14:   afc20018        sw      v0,24(s8)
  18:   34029387        li      v0,0x9387
  1c:   afc2001c        sw      v0,28(s8)
  20:   8fc40018        lw      a0,24(s8)
  24:   8fc5001c        lw      a1,28(s8)
  28:   0c000000        jal     0 <main>
  2c:   00000000        nop
  30:   afc20020        sw      v0,32(s8)
  34:   03c0e821        move    sp,s8
  38:   8fbf002c        lw      ra,44(sp)
  3c:   8fbe0028        lw      s8,40(sp)
  40:   27bd0030        addiu   sp,sp,48
  44:   03e00008        jr      ra
  48:   00000000        nop

0000004c <gcd>:
  4c:   27bdffe0        addiu   sp,sp,-32
  50:   afbf001c        sw      ra,28(sp)
  54:   afbe0018        sw      s8,24(sp)
  58:   03a0f021        move    s8,sp
  5c:   afc40020        sw      a0,32(s8)
  60:   afc50024        sw      a1,36(s8)
  64:   8fc30020        lw      v1,32(s8)
  68:   8fc20024        lw      v0,36(s8)
  6c:   00000000        nop
  70:   14620004        bne     v1,v0,84 <gcd+0x38>
  74:   00000000        nop
  78:   8fc20020        lw      v0,32(s8)
  7c:   08000039        j       e4 <gcd+0x98>
  80:   00000000        nop
  84:   8fc30020        lw      v1,32(s8)
  88:   8fc20024        lw      v0,36(s8)
  8c:   00000000        nop
  90:   0043102a        slt     v0,v0,v1
  94:   1040000b        beqz    v0,c4 <gcd+0x78>
  98:   00000000        nop
  9c:   8fc30020        lw      v1,32(s8)
  a0:   8fc20024        lw      v0,36(s8)
  a4:   00000000        nop
  a8:   00621023        subu    v0,v1,v0
  ac:   00402021        move    a0,v0
  b0:   8fc50024        lw      a1,36(s8)
  b4:   0c000000        jal     0 <main>
  b8:   00000000        nop
  bc:   08000039        j       e4 <gcd+0x98>
  c0:   00000000        nop
  c4:   8fc30024        lw      v1,36(s8)
  c8:   8fc20020        lw      v0,32(s8)
  cc:   00000000        nop
  d0:   00621023        subu    v0,v1,v0
  d4:   00402021        move    a0,v0
  d8:   8fc50020        lw      a1,32(s8)
  dc:   0c000000        jal     0 <main>
  e0:   00000000        nop
  e4:   03c0e821        move    sp,s8
  e8:   8fbf001c        lw      ra,28(sp)
  ec:   8fbe0018        lw      s8,24(sp)
  f0:   27bd0020        addiu   sp,sp,32
  f4:   03e00008        jr      ra
  f8:   00000000        nop
  fc:   00000000        nop
```

</br>

## Logs (gcd.bin)

Cycle log
```
[IF] :: cyl : 1, pc : 0x00, instruction : 0x27BDFFD0
[ID] :: opcode : ADDIU, readData1 : 16777216 [0x1000000], immediate : -48 [0xFFFFFFD0]
[EX] :: result : 16777168 [0xFFFFD0], nextPc : 0x4
[MA] :: 
[WB] :: R[29] = 16777168 [0xFFFFD0]

[IF] :: cyl : 2, pc : 0x04, instruction : 0xAFBF002C
[ID] :: opcode : SW, readData1 : 16777168 [0xFFFFD0], immediate : 44 [0x2C]
[EX] :: result : 16777212 [0xFFFFFC], nextPc : 0x8
[MA] :: M[0xFFFFFC] = -1 [0xFFFFFFFF]
[WB] :: 

[IF] :: cyl : 3, pc : 0x08, instruction : 0xAFBE0028
[ID] :: opcode : SW, readData1 : 16777168 [0xFFFFD0], immediate : 40 [0x28]
[EX] :: result : 16777208 [0xFFFFF8], nextPc : 0xC
[MA] :: M[0xFFFFF8] = 0 [0x0]
[WB] :: 

[IF] :: cyl : 4, pc : 0x0C, instruction : 0x03A0F021
[ID] :: opcode : ADDU, readData1 : 16777168, readData2 : 0
[EX] :: result : 16777168 [0xFFFFD0], nextPc : 0x10
[MA] :: 
[WB] :: R[30] = 16777168 [0xFFFFD0]

[IF] :: cyl : 5, pc : 0x10, instruction : 0x24021298
[ID] :: opcode : ADDIU, readData1 : 0 [0x0], immediate : 4760 [0x1298]
[EX] :: result : 4760 [0x1298], nextPc : 0x14
[MA] :: 
[WB] :: R[2] = 4760 [0x1298]

...
```

Result log
```
=== Result === 
cycle count : 1061
result value : 1

=== executed instructions ===
executed memory access : 486
executed taken branches : 45
executed write back : 769
kinds : 38
[] : 0x27BDFFD0 0xAFBF002C 0xAFBE0028 0x03A0F021 0x24021298 0xAFC20018 0x34029387 0xAFC2001C 
0x8FC40018 0x8FC5001C 0x0C000013 0x27BDFFE0 0xAFBF001C 0xAFBE0018 0xAFC40020 0xAFC50024 
0x8FC30020 0x8FC20024 0x00000000 0x14620004 0x0043102A 0x1040000B 0x8FC30024 0x8FC20020 
0x00621023 0x00402021 0x8FC50020 0x8FC50024 0x08000039 0x03C0E821 0x8FBF001C 0x8FBE0018 
0x27BD0020 0x03E00008 0xAFC20020 0x8FBF002C 0x8FBE0028 0x27BD0030 

=== executed opcode type ===
type R : 359
type I : 637
type J : 65

=== executed opcode === 
kinds : 13
[] : ADDIU=77 SW=153 ADDU=112 ORI=1 LW=333 JAL=37 SLL=137 BNE=37 SLT=36 BEQ=36 SUBU=36 J=28 JR=38 
