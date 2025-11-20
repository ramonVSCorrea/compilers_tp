package parser;

import lexer.*;
import java.util.List;

import TabelaDeSimbolos.TabelaDeSimbolos;
import Tipo_de_dados.TipoDado;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    // Semântica
    private final TabelaDeSimbolos ts = new TabelaDeSimbolos();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analisar() {
        while (!isAtEnd()) {
            comando();
        }
        System.out.println("\nAnálise sintática + semântica concluídas sem erros!");
    }

    // --------- Comandos ----------
    private void comando() {
        if (match(Token.Tipo.PONTO_VIRGULA)) return;

        // Declarações por tipo
        if (match(Token.Tipo.INTEIRO))   { declaracaoResto(TipoDado.INTEIRO);   return; }
        if (match(Token.Tipo.LOGICO))    { declaracaoResto(TipoDado.LOGICO);    return; }
        if (match(Token.Tipo.CARACTERE)) { declaracaoResto(TipoDado.CARACTERE); return; }

        if (match(Token.Tipo.IMPRIMIR)) { imprimirResto(); return; }
        if (match(Token.Tipo.ENQUANTO)) { enquantoResto(); return; }
        if (match(Token.Tipo.SE))       { seSenaoResto();  return; }
        if (match(Token.Tipo.PARA))     { paraResto();     return; }

        if (match(Token.Tipo.ABRE_CHAVE)) { blocoResto(); return; }

        // Atribuição simples: id <- expr ;
        if (check(Token.Tipo.IDENTIFICADOR)) {
            atribuicaoSimples();
            return;
        }

        erro("Comando desconhecido: " + atual().getValor());
    }

    // Inteiro/Logico/Caractere id <- expr [, id <- expr]* ;
    private void declaracaoResto(TipoDado tipoDecl) {
        declItem(tipoDecl);
        while (match(Token.Tipo.VIRGULA)) declItem(tipoDecl);
        consumir(Token.Tipo.PONTO_VIRGULA, "Esperado ';' ao final da declaração.");
    }

    private void declItem(TipoDado tipoDecl) {
        Token id = consumir(Token.Tipo.IDENTIFICADOR, "Esperado identificador após tipo.");
        if (!ts.declarar(id.getValor(), tipoDecl)) {
            erro("Identificador já declarado neste escopo: " + id.getValor());
        }
        consumir(Token.Tipo.ATRIBUICAO, "Esperado '<-' após identificador em declaração.");
        TipoDado t = expressao();
        verificarAtribuicao(tipoDecl, t, "Atribuição incompatível para '" + id.getValor() + "'");
    }

    // id <- expr ;
    private void atribuicaoSimples() {
        Token id = consumir(Token.Tipo.IDENTIFICADOR, "Esperado identificador no início da atribuição.");
        TipoDado tId = tipoDe(id.getValor());
        if (tId == null) erro("Variável não declarada: " + id.getValor());
        consumir(Token.Tipo.ATRIBUICAO, "Esperado '<-' após identificador.");
        TipoDado tExpr = expressao();
        verificarAtribuicao(tId, tExpr, "Atribuição incompatível para '" + id.getValor() + "'");
        consumir(Token.Tipo.PONTO_VIRGULA, "Esperado ';' ao final da atribuição.");
    }

    // Imprimir ( expr ) ;
    private void imprimirResto() {
        consumir(Token.Tipo.ABRE_PAREN, "Esperado '(' após 'Imprimir'.");
        expressao(); // qualquer tipo é permitido para imprimir (STRING resultará de concatenação, se houver)
        consumir(Token.Tipo.FECHA_PAREN, "Esperado ')' após expressão em 'Imprimir'.");
        consumir(Token.Tipo.PONTO_VIRGULA, "Esperado ';' após 'Imprimir(...)'.");
    }

    // Enquanto <expr(logica)> { ... }
    private void enquantoResto() {
        TipoDado tCond = expressao();
        exigir(tCond == TipoDado.LOGICO, "Condição de 'Enquanto' deve ser lógica (booleana).");
        bloco();
    }

    // Se <expr(logica)> { ... } [Senão { ... }]
    private void seSenaoResto() {
        TipoDado tCond = expressao();
        exigir(tCond == TipoDado.LOGICO, "Condição de 'Se' deve ser lógica (booleana).");
        bloco();
        if (match(Token.Tipo.SENAO)) {
            bloco();
        }
    }

    // Para id em (ini, fim, passo) { ... }
    // >>> AJUSTE: declara automaticamente a variável de controle como INTEIRO se ainda não existir.
    private void paraResto() {
        Token id = consumir(Token.Tipo.IDENTIFICADOR, "Esperado identificador após 'Para'.");
        TipoDado tId = tipoDe(id.getValor());
        if (tId == null) {
            // variável de controle do laço: declare como Inteiro neste escopo
            ts.declarar(id.getValor(), TipoDado.INTEIRO);
            tId = TipoDado.INTEIRO;
        }
        exigir(tId == TipoDado.INTEIRO, "Variável de 'Para' deve ser Inteiro.");

        Token em = consumir(Token.Tipo.IDENTIFICADOR, "Esperado 'em' após identificador em 'Para'.");
        if (!em.getValor().equalsIgnoreCase("em")) {
            erro("Esperado 'em' após identificador em 'Para'. Encontrado: '" + em.getValor() + "'");
        }

        consumir(Token.Tipo.ABRE_PAREN, "Esperado '(' após 'em'.");
        exigir(expressao() == TipoDado.INTEIRO, "Início do 'Para' deve ser Inteiro.");
        consumir(Token.Tipo.VIRGULA, "Esperado ',' após expressão de início.");
        exigir(expressao() == TipoDado.INTEIRO, "Fim do 'Para' deve ser Inteiro.");
        consumir(Token.Tipo.VIRGULA, "Esperado ',' após expressão de fim.");
        exigir(expressao() == TipoDado.INTEIRO, "Passo do 'Para' deve ser Inteiro.");
        consumir(Token.Tipo.FECHA_PAREN, "Esperado ')' após parâmetros do 'Para'.");
        bloco();
    }

    // --------- Blocos / escopos ----------
    private void bloco() {
        consumir(Token.Tipo.ABRE_CHAVE, "Esperado '{' para iniciar bloco.");
        ts.abrirEscopo();
        blocoResto();
        ts.fecharEscopo();
    }

    private void blocoResto() {
        while (!check(Token.Tipo.FECHA_CHAVE) && !isAtEnd()) {
            comando();
        }
        consumir(Token.Tipo.FECHA_CHAVE, "Esperado '}' para encerrar bloco.");
    }

    // --------- Expressões tipadas ----------
    private TipoDado expressao() { return exprOr(); }                 // ^

    private TipoDado exprOr() {
        TipoDado t = exprAnd();
        while (matchOpLog("^")) {
            TipoDado r = exprAnd();
            exigir(t == TipoDado.LOGICO && r == TipoDado.LOGICO, "Operador '^' exige tipos lógicos.");
            t = TipoDado.LOGICO;
        }
        return t;
    }

    private TipoDado exprAnd() {
        TipoDado t = exprRel();
        while (matchOpLog("&")) {
            TipoDado r = exprRel();
            exigir(t == TipoDado.LOGICO && r == TipoDado.LOGICO, "Operador '&' exige tipos lógicos.");
            t = TipoDado.LOGICO;
        }
        return t;
    }

    // Relacionais (=, <>, >, <, >=, <=) -> LOGICO (comparando INTEIRO; simplificação)
    private TipoDado exprRel() {
        TipoDado t = exprAdd();
        while (matchRel()) {
            TipoDado r = exprAdd();
            exigir(t == TipoDado.INTEIRO && r == TipoDado.INTEIRO,
                   "Operadores relacionais exigem Inteiro.");
            t = TipoDado.LOGICO;
        }
        return t;
    }

    private TipoDado exprAdd() {
        TipoDado t = exprMul();
        while (matchOpArit("+", "-")) {
            String op = anterior().getValor();
            TipoDado r = exprMul();
            if (op.equals("+")) {
                // concatenação de strings se qualquer lado for STRING
                if (t == TipoDado.STRING || r == TipoDado.STRING) {
                    t = TipoDado.STRING;
                } else {
                    exigir(t == TipoDado.INTEIRO && r == TipoDado.INTEIRO,
                           "Operador '+' exige Inteiro (ou concatena com String).");
                    t = TipoDado.INTEIRO;
                }
            } else { // '-'
                exigir(t == TipoDado.INTEIRO && r == TipoDado.INTEIRO,
                       "Operador '-' exige Inteiro.");
                t = TipoDado.INTEIRO;
            }
        }
        return t;
    }

    private TipoDado exprMul() {
        TipoDado t = exprPow();
        while (matchOpArit("*", "/", "%")) {
            TipoDado r = exprPow();
            exigir(t == TipoDado.INTEIRO && r == TipoDado.INTEIRO,
                   "Operadores '*', '/', '%' exigem Inteiro.");
            t = TipoDado.INTEIRO;
        }
        return t;
    }

    // ** (direita-associativa)
    private TipoDado exprPow() {
        TipoDado t = exprUn();
        if (matchOpArit("**")) {
            TipoDado r = exprPow();
            exigir(t == TipoDado.INTEIRO && r == TipoDado.INTEIRO,
                   "Operador '**' exige Inteiro.");
            t = TipoDado.INTEIRO;
        }
        return t;
    }

    private TipoDado exprUn() {
        if (matchOpArit("-")) {
            TipoDado t = exprUn();
            exigir(t == TipoDado.INTEIRO, "Sinal unário '-' exige Inteiro.");
            return TipoDado.INTEIRO;
        }
        return primario();
    }

    private TipoDado primario() {
        if (match(Token.Tipo.NUMERO))  return TipoDado.INTEIRO;
        if (match(Token.Tipo.STRING))  return TipoDado.STRING;
        if (match(Token.Tipo.VERDADE)) return TipoDado.LOGICO;
        if (match(Token.Tipo.MENTIRA)) return TipoDado.LOGICO;

        if (match(Token.Tipo.IDENTIFICADOR)) {
            String nome = anterior().getValor();
            TipoDado t = tipoDe(nome);
            if (t == null) erro("Variável não declarada: " + nome);
            return t;
        }

        if (match(Token.Tipo.ABRE_PAREN)) {
            TipoDado t = expressao();
            consumir(Token.Tipo.FECHA_PAREN, "Esperado ')' após expressão.");
            return t;
        }

        erro("Expressão inválida perto de: " + atual().getValor());
        return TipoDado.DESCONHECIDO;
    }

    // --------- Helpers de tipos ----------
    private void verificarAtribuicao(TipoDado alvo, TipoDado valor, String msg) {
        if (alvo == valor) return;
        throw new RuntimeException("Erro semântico: " + msg + " (esperado " + alvo + ", obtido " + valor + ").");
    }

    private void exigir(boolean cond, String msg) {
        if (!cond) throw new RuntimeException("Erro semântico: " + msg);
    }

    private TipoDado tipoDe(String id) {
        return ts.buscar(id);
    }

    // --------- Utilitários de parsing ----------
    private boolean match(Token.Tipo... tipos) {
        for (Token.Tipo t : tipos) if (check(t)) { avancar(); return true; }
        return false;
    }

    private boolean matchOpArit(String... ops) {
        if (!check(Token.Tipo.OPERADOR_ARIT)) return false;
        String v = atual().getValor();
        for (String s : ops) if (v.equals(s)) { avancar(); return true; }
        return false;
    }

    private boolean matchOpLog(String op) {
        if (!check(Token.Tipo.OPERADOR_LOGICO)) return false;
        if (atual().getValor().equals(op)) { avancar(); return true; }
        return false;
    }

    private boolean matchRel() {
        if (!check(Token.Tipo.OPERADOR_LOGICO)) return false;
        String v = atual().getValor();
        if (v.equals("=") || v.equals("<>") || v.equals(">") || v.equals("<") || v.equals(">=") || v.equals("<=")) {
            avancar();
            return true;
        }
        return false;
    }

    private Token consumir(Token.Tipo tipo, String msg) {
        if (check(tipo)) return avancar();
        Token t = atual();
        erro(msg + " Encontrado: " + t.getTipo() + "('" + t.getValor() + "').");
        return null;
    }

    private boolean check(Token.Tipo tipo) {
        if (isAtEnd()) return false;
        return atual().getTipo() == tipo;
    }

    private Token avancar() {
        if (!isAtEnd()) pos++;
        return anterior();
    }

    private boolean isAtEnd() { return atual().getTipo() == Token.Tipo.FIM; }

    private Token atual()    { return tokens.get(pos); }
    private Token anterior() { return tokens.get(pos - 1); }

    private void erro(String msg) {
        throw new RuntimeException("Erro sintático: " + msg);
    }
}
