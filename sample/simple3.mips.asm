
simple3.o:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <foo>:
   0:	27bdffe8 	addiu	sp,sp,-24
   4:	afbe0014 	sw	s8,20(sp)
   8:	03a0f021 	move	s8,sp
   c:	afc00008 	sw	zero,8(s8)
  10:	afc0000c 	sw	zero,12(s8)
  14:	afc00008 	sw	zero,8(s8)
  18:	08000011 	j	44 <foo+0x44>
  1c:	00000000 	nop
  20:	8fc3000c 	lw	v1,12(s8)
  24:	8fc20008 	lw	v0,8(s8)
  28:	00000000 	nop
  2c:	00621021 	addu	v0,v1,v0
  30:	afc2000c 	sw	v0,12(s8)
  34:	8fc20008 	lw	v0,8(s8)
  38:	00000000 	nop
  3c:	24420001 	addiu	v0,v0,1
  40:	afc20008 	sw	v0,8(s8)
  44:	8fc20008 	lw	v0,8(s8)
  48:	00000000 	nop
  4c:	28420065 	slti	v0,v0,101
  50:	1440fff3 	bnez	v0,20 <foo+0x20>
  54:	00000000 	nop
  58:	8fc2000c 	lw	v0,12(s8)
  5c:	03c0e821 	move	sp,s8
  60:	8fbe0014 	lw	s8,20(sp)
  64:	27bd0018 	addiu	sp,sp,24
  68:	03e00008 	jr	ra
  6c:	00000000 	nop
