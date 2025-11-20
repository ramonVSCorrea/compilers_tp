package codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerador de Código Intermediário (Código de Três Endereços)
 * Gera instruções no formato: resultado = operando1 operador operando2
 */
public class GeradorCodigo {
    private final List<String> codigo = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;

    /**
     * Gera um novo temporário (t0, t1, t2, ...)
     */
    public String novoTemp() {
        return "t" + (tempCount++);
    }

    /**
     * Gera um novo label (L0, L1, L2, ...)
     */
    public String novoLabel() {
        return "L" + (labelCount++);
    }

    /**
     * Adiciona uma instrução ao código intermediário
     */
    public void emitir(String instrucao) {
        codigo.add(instrucao);
    }

    /**
     * Emite uma operação binária: resultado = op1 operador op2
     */
    public String emitirBinario(String op1, String operador, String op2) {
        String temp = novoTemp();
        emitir(temp + " = " + op1 + " " + operador + " " + op2);
        return temp;
    }

    /**
     * Emite uma operação unária: resultado = operador op
     */
    public String emitirUnario(String operador, String operando) {
        String temp = novoTemp();
        emitir(temp + " = " + operador + " " + operando);
        return temp;
    }

    /**
     * Emite uma atribuição: destino = origem
     */
    public void emitirAtribuicao(String destino, String origem) {
        emitir(destino + " = " + origem);
    }

    /**
     * Emite um label
     */
    public void emitirLabel(String label) {
        emitir(label + ":");
    }

    /**
     * Emite um salto incondicional: goto label
     */
    public void emitirGoto(String label) {
        emitir("goto " + label);
    }

    /**
     * Emite um salto condicional: if condição goto label
     */
    public void emitirIfGoto(String condicao, String label) {
        emitir("if " + condicao + " goto " + label);
    }

    /**
     * Emite um salto condicional negado: ifFalse condição goto label
     */
    public void emitirIfFalseGoto(String condicao, String label) {
        emitir("ifFalse " + condicao + " goto " + label);
    }

    /**
     * Emite uma chamada de impressão: print valor
     */
    public void emitirPrint(String valor) {
        emitir("print " + valor);
    }

    /**
     * Emite uma declaração de variável
     */
    public void emitirDeclaracao(String tipo, String variavel) {
        emitir("declare " + tipo + " " + variavel);
    }

    /**
     * Emite um comentário no código intermediário
     */
    public void emitirComentario(String comentario) {
        emitir("# " + comentario);
    }

    /**
     * Retorna todo o código gerado
     */
    public List<String> getCodigo() {
        return codigo;
    }

    /**
     * Imprime o código intermediário formatado
     */
    public void imprimir() {
        System.out.println("\n---------------------------------------");
        System.out.println(" ETAPA 3 - CÓDIGO INTERMEDIÁRIO");
        System.out.println("---------------------------------------");
        for (int i = 0; i < codigo.size(); i++) {
            String linha = codigo.get(i);
            // Labels não têm numeração de linha
            if (linha.contains(":") && !linha.contains("=")) {
                System.out.println(linha);
            } else {
                System.out.printf("%3d: %s%n", i, linha);
            }
        }
    }

    /**
     * Retorna o código intermediário como string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String instrucao : codigo) {
            sb.append(instrucao).append("\n");
        }
        return sb.toString();
    }

    /**
     * Limpa todo o código gerado
     */
    public void limpar() {
        codigo.clear();
        tempCount = 0;
        labelCount = 0;
    }
}

