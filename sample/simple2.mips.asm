
simple2.o:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <foo>:
   0:	27bdffe8 	addiu	sp,sp,-24
   4:	afbe0014 	sw	s8,20(sp)
   8:	03a0f021 	move	s8,sp
   c:	24020064 	li	v0,100
  10:	afc20008 	sw	v0,8(s8)
  14:	8fc20008 	lw	v0,8(s8)
  18:	03c0e821 	move	sp,s8
  1c:	8fbe0014 	lw	s8,20(sp)
  20:	27bd0018 	addiu	sp,sp,24
  24:	03e00008 	jr	ra
  28:	00000000 	nop
  2c:	00000000 	nop
