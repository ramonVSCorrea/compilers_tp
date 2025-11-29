package codegen;

import java.util.HashSet;
import java.util.Set;

public class CodeGenMIPS {

    private final StringBuilder data = new StringBuilder();
    private final StringBuilder text = new StringBuilder();

    private int strCount   = 0;
    private int labelCount = 0;

    private final Set<String> declaradas = new HashSet<>();

    public CodeGenMIPS() {
        data.append(".data\n");
        // buffer para conversão de inteiro em string
        data.append("int_buffer: .space 32\n\n");

        text.append(".text\n");
        text.append(".globl main\n");
        text.append("main:\n");
    }

    // ============================================================
    //  GERENCIAMENTO DE VARIÁVEIS / LABELS / STRINGS
    // ============================================================

    public void declararVariavel(String nome) {
        if (declaradas.contains(nome)) return;
        declaradas.add(nome);
        data.append(nome).append(": .word 0\n");
    }

    // AGORA USA PREFIXO "M" PARA NÃO COLIDIR COM OS "L" DO PARSER
    private String novoLabel() {
        return "M" + (labelCount++);
    }

    private String novaStringLiteral(String conteudo) {
        String nome = "str_" + (strCount++);
        data.append(nome)
            .append(": .asciiz \"")
            .append(conteudo.replace("\\", "\\\\").replace("\"", "\\\""))
            .append("\"\n");
        return nome;
    }

    // ============================================================
    //  ATRIBUIÇÃO / MOVIMENTO
    // ============================================================

    public void setVar(String var, String valor) {
        if (valor.matches("-?\\d+")) {
            text.append("  li $t0, ").append(valor).append("\n");
        } else {
            text.append("  lw $t0, ").append(valor).append("\n");
        }
        text.append("  sw $t0, ").append(var).append("\n");
    }

    public void move(String dst, String src) {
        if (src.matches("-?\\d+")) {
            text.append("  li $t0, ").append(src).append("\n");
        } else {
            text.append("  lw $t0, ").append(src).append("\n");
        }
        text.append("  sw $t0, ").append(dst).append("\n");
    }

    // ============================================================
    //  STRINGS / CONCATENAÇÃO
    // ============================================================

    public String alocarStringLiteral(String conteudo) {
        return novaStringLiteral(conteudo);
    }

    public String concatStrings(String s1, String s2) {

        String dst = "str_concat_" + (strCount++);
        int tamanhoMax = 512;

        data.append(dst).append(": .space ").append(tamanhoMax).append("\n");

        String lCopia1 = novoLabel();
        String lFim1   = novoLabel();
        String lCopia2 = novoLabel();
        String lFim2   = novoLabel();

        text.append("  # concatStrings(").append(s1).append(", ").append(s2).append(")\n");
        text.append("  la $t0, ").append(dst).append("\n");   // ptr dst
        text.append("  la $t1, ").append(s1).append("\n");    // ptr s1
        text.append(lCopia1).append(":\n");
        text.append("  lb $t2, 0($t1)\n");
        text.append("  beq $t2, $zero, ").append(lFim1).append("\n");
        text.append("  sb $t2, 0($t0)\n");
        text.append("  addi $t0, $t0, 1\n");
        text.append("  addi $t1, $t1, 1\n");
        text.append("  j ").append(lCopia1).append("\n");
        text.append(lFim1).append(":\n");

        text.append("  la $t1, ").append(s2).append("\n");
        text.append(lCopia2).append(":\n");
        text.append("  lb $t2, 0($t1)\n");
        text.append("  beq $t2, $zero, ").append(lFim2).append("\n");
        text.append("  sb $t2, 0($t0)\n");
        text.append("  addi $t0, $t0, 1\n");
        text.append("  addi $t1, $t1, 1\n");
        text.append("  j ").append(lCopia2).append("\n");
        text.append(lFim2).append(":\n");

        text.append("  sb $zero, 0($t0)\n");

        return dst;
    }

    // ============================================================
    //  IMPRESSÃO
    // ============================================================

    public void printString(String label) {
        text.append("  la $a0, ").append(label).append("\n");
        text.append("  li $v0, 4\n");
        text.append("  syscall\n");
    }

    public void printInt(String var) {
        text.append("  lw $a0, ").append(var).append("\n");
        text.append("  li $v0, 1\n");
        text.append("  syscall\n");
    }

    // ============================================================
    //  OPERACOES ARITMÉTICAS / RELACIONAIS / LÓGICAS
    // ============================================================

    public void operacao(String dst, String a, String op, String b) {

        if (a.matches("-?\\d+"))
            text.append("  li $t1, ").append(a).append("\n");
        else
            text.append("  lw $t1, ").append(a).append("\n");

        if (b.matches("-?\\d+"))
            text.append("  li $t2, ").append(b).append("\n");
        else
            text.append("  lw $t2, ").append(b).append("\n");

        switch (op) {
            case "+":
                text.append("  add $t3, $t1, $t2\n");
                break;
            case "-":
                text.append("  sub $t3, $t1, $t2\n");
                break;
            case "*":
                text.append("  mul $t3, $t1, $t2\n");
                break;
            case "/":
                text.append("  div $t1, $t2\n");
                text.append("  mflo $t3\n");
                break;
            case "%":
                text.append("  div $t1, $t2\n");
                text.append("  mfhi $t3\n");
                break;

            case "=":
                text.append("  seq $t3, $t1, $t2\n");
                break;
            case "<>":
                text.append("  sne $t3, $t1, $t2\n");
                break;
            case ">":
                text.append("  sgt $t3, $t1, $t2\n");
                break;
            case "<":
                text.append("  slt $t3, $t1, $t2\n");
                break;
            case ">=":
                text.append("  sge $t3, $t1, $t2\n");
                break;
            case "<=":
                text.append("  sle $t3, $t1, $t2\n");
                break;

            case "&":
                text.append("  and $t3, $t1, $t2\n");
                break;
            case "^":
                text.append("  or  $t3, $t1, $t2\n");
                break;

            case "**":
                String loop = novoLabel();
                String end  = novoLabel();

                text.append("  li $t3, 1\n");
                text.append(loop).append(":\n");
                text.append("  beq $t2, $zero, ").append(end).append("\n");
                text.append("  mul $t3, $t3, $t1\n");
                text.append("  addi $t2, $t2, -1\n");
                text.append("  j ").append(loop).append("\n");
                text.append(end).append(":\n");
                break;

            default:
                break;
        }

        text.append("  sw $t3, ").append(dst).append("\n");
    }

    // ============================================================
    //  intToString: converte inteiro em string (int_buffer)
    // ============================================================

    public String intToString(String valor) {

        String buf      = "int_buffer";
        String Lloop    = novoLabel();
        String Lrev     = novoLabel();
        String LrevEnd  = novoLabel();
        String Lzero    = novoLabel();
        String Lfim     = novoLabel();

        text.append("  # intToString(").append(valor).append(") -> ").append(buf).append("\n");

        if (valor.matches("-?\\d+")) {
            text.append("  li $t0, ").append(valor).append("\n");
        } else {
            text.append("  lw $t0, ").append(valor).append("\n");
        }

        text.append("  la $t1, ").append(buf).append("\n");
        text.append("  li $t2, 0\n");

        text.append("  beq $t0, $zero, ").append(Lzero).append("\n");

        text.append(Lloop).append(":\n");
        text.append("  li $t3, 10\n");
        text.append("  div $t0, $t3\n");
        text.append("  mfhi $t4\n");
        text.append("  mflo $t0\n");
        text.append("  addi $t4, $t4, 48\n");
        text.append("  sb $t4, 0($t1)\n");
        text.append("  addi $t1, $t1, 1\n");
        text.append("  addi $t2, $t2, 1\n");
        text.append("  bne $t0, $zero, ").append(Lloop).append("\n");

        text.append("  sb $zero, 0($t1)\n");

        text.append("  la $t1, ").append(buf).append("\n");
        text.append("  move $t4, $t1\n");
        text.append("  addu $t5, $t1, $t2\n");
        text.append("  addi $t5, $t5, -1\n");

        text.append(Lrev).append(":\n");
        text.append("  blt $t5, $t4, ").append(LrevEnd).append("\n");
        text.append("  lb $t6, 0($t4)\n");
        text.append("  lb $t7, 0($t5)\n");
        text.append("  sb $t7, 0($t4)\n");
        text.append("  sb $t6, 0($t5)\n");
        text.append("  addi $t4, $t4, 1\n");
        text.append("  addi $t5, $t5, -1\n");
        text.append("  j ").append(Lrev).append("\n");

        text.append(LrevEnd).append(":\n");
        text.append("  j ").append(Lfim).append("\n");

        text.append(Lzero).append(":\n");
        text.append("  li $t4, 48\n");
        text.append("  sb $t4, 0($t1)\n");
        text.append("  addi $t1, $t1, 1\n");
        text.append("  sb $zero, 0($t1)\n");

        text.append(Lfim).append(":\n");

        return buf;
    }

    // ============================================================
    //  IF / GOTO / LABEL (usados pelo Parser)
    // ============================================================

    public void ifZero(String var, String label) {
        text.append("  lw $t0, ").append(var).append("\n");
        text.append("  beq $t0, $zero, ").append(label).append("\n");
    }

    public void label(String L) {
        text.append(L).append(":\n");
    }

    public void goTo(String L) {
        text.append("  j ").append(L).append("\n");
    }

    // ============================================================
    //  FINAL
    // ============================================================

    public String gerarCodigo() {
        return data.toString() + "\n" + text.toString();
    }
}
