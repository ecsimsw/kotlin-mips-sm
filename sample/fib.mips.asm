
fib.o:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <main>:
   0:	27bdffd8 	addiu	sp,sp,-40
   4:	afbf0024 	sw	ra,36(sp)
   8:	afbe0020 	sw	s8,32(sp)
   c:	03a0f021 	move	s8,sp
  10:	2402000a 	li	v0,10
  14:	afc20018 	sw	v0,24(s8)
  18:	8fc40018 	lw	a0,24(s8)
  1c:	0c000000 	jal	0 <main>
  20:	00000000 	nop
  24:	afc2001c 	sw	v0,28(s8)
  28:	03c0e821 	move	sp,s8
  2c:	8fbf0024 	lw	ra,36(sp)
  30:	8fbe0020 	lw	s8,32(sp)
  34:	27bd0028 	addiu	sp,sp,40
  38:	03e00008 	jr	ra
  3c:	00000000 	nop

00000040 <fib>:
  40:	27bdffd0 	addiu	sp,sp,-48
  44:	afbf002c 	sw	ra,44(sp)
  48:	afbe0028 	sw	s8,40(sp)
  4c:	afb00024 	sw	s0,36(sp)
  50:	03a0f021 	move	s8,sp
  54:	afc40030 	sw	a0,48(s8)
  58:	8fc20030 	lw	v0,48(s8)
  5c:	00000000 	nop
  60:	28420003 	slti	v0,v0,3
  64:	10400004 	beqz	v0,78 <fib+0x38>
  68:	00000000 	nop
  6c:	24020001 	li	v0,1
  70:	0800002e 	j	b8 <fib+0x78>
  74:	00000000 	nop
  78:	8fc20030 	lw	v0,48(s8)
  7c:	00000000 	nop
  80:	2442ffff 	addiu	v0,v0,-1
  84:	00402021 	move	a0,v0
  88:	0c000000 	jal	0 <main>
  8c:	00000000 	nop
  90:	00408021 	move	s0,v0
  94:	8fc20030 	lw	v0,48(s8)
  98:	00000000 	nop
  9c:	2442fffe 	addiu	v0,v0,-2
  a0:	00402021 	move	a0,v0
  a4:	0c000000 	jal	0 <main>
  a8:	00000000 	nop
  ac:	02021021 	addu	v0,s0,v0
  b0:	afc20018 	sw	v0,24(s8)
  b4:	8fc20018 	lw	v0,24(s8)
  b8:	03c0e821 	move	sp,s8
  bc:	8fbf002c 	lw	ra,44(sp)
  c0:	8fbe0028 	lw	s8,40(sp)
  c4:	8fb00024 	lw	s0,36(sp)
  c8:	27bd0030 	addiu	sp,sp,48
  cc:	03e00008 	jr	ra
  d0:	00000000 	nop
	...
