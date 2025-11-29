.data
int_buffer: .space 32

a: .word 0
b: .word 0
c: .word 0
r: .word 0
t0: .word 0
s: .word 0
t1: .word 0
t: .word 0
t2: .word 0
t3: .word 0
str_0: .asciiz "TESTE 6:"
str_1: .asciiz "a = "
str_concat_2: .space 512
str_3: .asciiz "b = "
str_concat_4: .space 512
str_5: .asciiz "c = "
str_concat_6: .space 512
str_7: .asciiz "a & b = "
str_concat_8: .space 512
str_9: .asciiz "a ^ b = "
str_concat_10: .space 512
str_11: .asciiz "a ^ b ^ c = "
str_concat_12: .space 512

.text
.globl main
main:
  li $t0, 1
  sw $t0, a
  li $t0, 0
  sw $t0, b
  li $t0, 0
  sw $t0, c
  lw $t1, a
  lw $t2, b
  and $t3, $t1, $t2
  sw $t3, t0
  lw $t0, t0
  sw $t0, r
  lw $t1, a
  lw $t2, b
  or  $t3, $t1, $t2
  sw $t3, t1
  lw $t0, t1
  sw $t0, s
  lw $t1, a
  lw $t2, b
  or  $t3, $t1, $t2
  sw $t3, t2
  lw $t1, t2
  lw $t2, c
  or  $t3, $t1, $t2
  sw $t3, t3
  lw $t0, t3
  sw $t0, t
  la $a0, str_0
  li $v0, 4
  syscall
  # intToString(a) -> int_buffer
  lw $t0, a
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M3
M0:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M0
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M1:
  blt $t5, $t4, M2
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M1
M2:
  j M4
M3:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M4:
  # concatStrings(str_1, int_buffer)
  la $t0, str_concat_2
  la $t1, str_1
M5:
  lb $t2, 0($t1)
  beq $t2, $zero, M6
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M5
M6:
  la $t1, int_buffer
M7:
  lb $t2, 0($t1)
  beq $t2, $zero, M8
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M7
M8:
  sb $zero, 0($t0)
  la $a0, str_concat_2
  li $v0, 4
  syscall
  # intToString(b) -> int_buffer
  lw $t0, b
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M12
M9:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M9
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M10:
  blt $t5, $t4, M11
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M10
M11:
  j M13
M12:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M13:
  # concatStrings(str_3, int_buffer)
  la $t0, str_concat_4
  la $t1, str_3
M14:
  lb $t2, 0($t1)
  beq $t2, $zero, M15
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M14
M15:
  la $t1, int_buffer
M16:
  lb $t2, 0($t1)
  beq $t2, $zero, M17
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M16
M17:
  sb $zero, 0($t0)
  la $a0, str_concat_4
  li $v0, 4
  syscall
  # intToString(c) -> int_buffer
  lw $t0, c
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M21
M18:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M18
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M19:
  blt $t5, $t4, M20
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M19
M20:
  j M22
M21:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M22:
  # concatStrings(str_5, int_buffer)
  la $t0, str_concat_6
  la $t1, str_5
M23:
  lb $t2, 0($t1)
  beq $t2, $zero, M24
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M23
M24:
  la $t1, int_buffer
M25:
  lb $t2, 0($t1)
  beq $t2, $zero, M26
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M25
M26:
  sb $zero, 0($t0)
  la $a0, str_concat_6
  li $v0, 4
  syscall
  # intToString(r) -> int_buffer
  lw $t0, r
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M30
M27:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M27
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M28:
  blt $t5, $t4, M29
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M28
M29:
  j M31
M30:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M31:
  # concatStrings(str_7, int_buffer)
  la $t0, str_concat_8
  la $t1, str_7
M32:
  lb $t2, 0($t1)
  beq $t2, $zero, M33
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M32
M33:
  la $t1, int_buffer
M34:
  lb $t2, 0($t1)
  beq $t2, $zero, M35
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M34
M35:
  sb $zero, 0($t0)
  la $a0, str_concat_8
  li $v0, 4
  syscall
  # intToString(s) -> int_buffer
  lw $t0, s
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M39
M36:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M36
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M37:
  blt $t5, $t4, M38
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M37
M38:
  j M40
M39:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M40:
  # concatStrings(str_9, int_buffer)
  la $t0, str_concat_10
  la $t1, str_9
M41:
  lb $t2, 0($t1)
  beq $t2, $zero, M42
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M41
M42:
  la $t1, int_buffer
M43:
  lb $t2, 0($t1)
  beq $t2, $zero, M44
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M43
M44:
  sb $zero, 0($t0)
  la $a0, str_concat_10
  li $v0, 4
  syscall
  # intToString(t) -> int_buffer
  lw $t0, t
  la $t1, int_buffer
  li $t2, 0
  beq $t0, $zero, M48
M45:
  li $t3, 10
  div $t0, $t3
  mfhi $t4
  mflo $t0
  addi $t4, $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  addi $t2, $t2, 1
  bne $t0, $zero, M45
  sb $zero, 0($t1)
  la $t1, int_buffer
  move $t4, $t1
  addu $t5, $t1, $t2
  addi $t5, $t5, -1
M46:
  blt $t5, $t4, M47
  lb $t6, 0($t4)
  lb $t7, 0($t5)
  sb $t7, 0($t4)
  sb $t6, 0($t5)
  addi $t4, $t4, 1
  addi $t5, $t5, -1
  j M46
M47:
  j M49
M48:
  li $t4, 48
  sb $t4, 0($t1)
  addi $t1, $t1, 1
  sb $zero, 0($t1)
M49:
  # concatStrings(str_11, int_buffer)
  la $t0, str_concat_12
  la $t1, str_11
M50:
  lb $t2, 0($t1)
  beq $t2, $zero, M51
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M50
M51:
  la $t1, int_buffer
M52:
  lb $t2, 0($t1)
  beq $t2, $zero, M53
  sb $t2, 0($t0)
  addi $t0, $t0, 1
  addi $t1, $t1, 1
  j M52
M53:
  sb $zero, 0($t0)
  la $a0, str_concat_12
  li $v0, 4
  syscall
