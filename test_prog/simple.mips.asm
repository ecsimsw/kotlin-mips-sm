
simple.mips.o:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <foo>:
   0:	27bdfff8 	addiu	sp,sp,-8
   4:	afbe0004 	sw	s8,4(sp)
   8:	03a0f021 	move	s8,sp
   c:	00000000 	nop
  10:	03c0e821 	move	sp,s8
  14:	8fbe0004 	lw	s8,4(sp)
  18:	27bd0008 	addiu	sp,sp,8
  1c:	03e00008 	jr	ra
  20:	00000000 	nop
	...
