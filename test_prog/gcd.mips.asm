
gcd.o:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <main>:
   0:	27bdffd0 	addiu	sp,sp,-48
   4:	afbf002c 	sw	ra,44(sp)
   8:	afbe0028 	sw	s8,40(sp)
   c:	03a0f021 	move	s8,sp
  10:	24021298 	li	v0,4760
  14:	afc20018 	sw	v0,24(s8)
  18:	34029387 	li	v0,0x9387
  1c:	afc2001c 	sw	v0,28(s8)
  20:	8fc40018 	lw	a0,24(s8)
  24:	8fc5001c 	lw	a1,28(s8)
  28:	0c000000 	jal	0 <main>
  2c:	00000000 	nop
  30:	afc20020 	sw	v0,32(s8)
  34:	03c0e821 	move	sp,s8
  38:	8fbf002c 	lw	ra,44(sp)
  3c:	8fbe0028 	lw	s8,40(sp)
  40:	27bd0030 	addiu	sp,sp,48
  44:	03e00008 	jr	ra
  48:	00000000 	nop

0000004c <gcd>:
  4c:	27bdffe0 	addiu	sp,sp,-32
  50:	afbf001c 	sw	ra,28(sp)
  54:	afbe0018 	sw	s8,24(sp)
  58:	03a0f021 	move	s8,sp
  5c:	afc40020 	sw	a0,32(s8)
  60:	afc50024 	sw	a1,36(s8)
  64:	8fc30020 	lw	v1,32(s8)
  68:	8fc20024 	lw	v0,36(s8)
  6c:	00000000 	nop
  70:	14620004 	bne	v1,v0,84 <gcd+0x38>
  74:	00000000 	nop
  78:	8fc20020 	lw	v0,32(s8)
  7c:	08000039 	j	e4 <gcd+0x98>
  80:	00000000 	nop
  84:	8fc30020 	lw	v1,32(s8)
  88:	8fc20024 	lw	v0,36(s8)
  8c:	00000000 	nop
  90:	0043102a 	slt	v0,v0,v1
  94:	1040000b 	beqz	v0,c4 <gcd+0x78>
  98:	00000000 	nop
  9c:	8fc30020 	lw	v1,32(s8)
  a0:	8fc20024 	lw	v0,36(s8)
  a4:	00000000 	nop
  a8:	00621023 	subu	v0,v1,v0
  ac:	00402021 	move	a0,v0
  b0:	8fc50024 	lw	a1,36(s8)
  b4:	0c000000 	jal	0 <main>
  b8:	00000000 	nop
  bc:	08000039 	j	e4 <gcd+0x98>
  c0:	00000000 	nop
  c4:	8fc30024 	lw	v1,36(s8)
  c8:	8fc20020 	lw	v0,32(s8)
  cc:	00000000 	nop
  d0:	00621023 	subu	v0,v1,v0
  d4:	00402021 	move	a0,v0
  d8:	8fc50020 	lw	a1,32(s8)
  dc:	0c000000 	jal	0 <main>
  e0:	00000000 	nop
  e4:	03c0e821 	move	sp,s8
  e8:	8fbf001c 	lw	ra,28(sp)
  ec:	8fbe0018 	lw	s8,24(sp)
  f0:	27bd0020 	addiu	sp,sp,32
  f4:	03e00008 	jr	ra
  f8:	00000000 	nop
  fc:	00000000 	nop
