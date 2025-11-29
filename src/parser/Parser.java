package parser;

import lexer.Token;
import lexer.Token.Tipo;
import codegen.CodeGenMIPS;
import TabelaDeSimbolos.TabelaDeSimbolos;
import Tipo_de_dados.TipoDado;

import java.util.List;

/**
 * Parser do Compila Fofo (CF)
 * - Análise sintática
 * - Análise semântica
 * - Geração de código MIPS
 *
 * Suporta:
 *   • Declarações: Inteiro, Logico, Caractere
 *   • Atribuições com <- 
 *   • SE / SENAO (com ou sem parênteses)
 *   • ENQUANTO
 *   • PARA i em (ini, fim, passo)
 *   • IMPRIMIR(...)
 *   • Operadores aritméticos: + - * / % **
 *   • Operadores lógicos/relacionais: = <> > < >= <= & ^
 *   • Concatenação de strings com inteiros/lógicos
 */
public class Parser {

    private final List<Token> tokens;
    private int atual = 0;

    private final TabelaDeSimbolos ts = new TabelaDeSimbolos();
    private final CodeGenMIPS mips = new CodeGenMIPS();

    private int tempCount  = 0;
    private int labelCount = 0;

    // Resultado de uma expressão: (nome da variável/temporário, tipo)
    private static class Res {
        final String nome;
        final TipoDado tipo;

        Res(String nome, TipoDado tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ============================================================
    //                      ENTRADA PRINCIPAL
    // ============================================================

    public void analisar() {
        while (!isAtEnd()) {
            comando();
        }
    }

    public String getCodigoGeradoMIPS() {
        return mips.gerarCodigo();
    }

    // ============================================================
    //                          COMANDOS
    // ============================================================

    private void comando() {

        if (match(Tipo.INTEIRO)) {
            declaracao(TipoDado.INTEIRO);
            return;
        }
        if (match(Tipo.LOGICO)) {
            declaracao(TipoDado.LOGICO);
            return;
        }
        if (match(Tipo.CARACTERE)) {
            declaracao(TipoDado.CARACTERE);
            return;
        }

        if (match(Tipo.SE)) {
            cmdSeSenao();
            return;
        }

        if (match(Tipo.ENQUANTO)) {
            cmdEnquanto();
            return;
        }

        if (match(Tipo.PARA)) {
            cmdPara();
            return;
        }

        if (match(Tipo.IMPRIMIR)) {
            cmdImprimir();
            return;
        }

        if (check(Tipo.IDENTIFICADOR)) {
            cmdAtribuicao();
            return;
        }

        // Se chegou aqui e não é FIM, provavelmente é lixo de sintaxe
        if (!check(Tipo.FIM)) {
            erroSintatico("Comando inválido ou inesperado.");
        }
    }

    // ------------------------------------------------------------
    //                   DECLARAÇÃO DE VARIÁVEIS
    // ------------------------------------------------------------

    private void declaracao(TipoDado tipoBase) {

        // Pelo menos um identificador
        while (true) {
            Token id = consumir(Tipo.IDENTIFICADOR, "Esperado identificador na declaração");

            if (!ts.declarar(id.getValor(), tipoBase)) {
                erroSemantico("Variável já declarada neste escopo: " + id.getValor());
            }

            mips.declararVariavel(id.getValor());

            // Atribuição opcional: id <- expr
            if (match(Tipo.ATRIBUICAO)) {
                Res expr = expressao();
                verificarAtribuicao(tipoBase, expr.tipo,
                        "Tipo incompatível na declaração de " + id.getValor());
                mips.move(id.getValor(), expr.nome);
            }

            // Se houver vírgula, continua declarando mais variáveis
            if (!match(Tipo.VIRGULA)) break;
        }

        // Fecha a declaração com ';'
        consumir(Tipo.PONTO_VIRGULA, "Esperado ';' ao final da declaração");
    }

    // ------------------------------------------------------------
    //                   ATRIBUIÇÃO
    // ------------------------------------------------------------

    private void cmdAtribuicao() {
        Token id = consumir(Tipo.IDENTIFICADOR, "Esperado identificador");

        TipoDado tipoVar = tipoDe(id.getValor());
        if (tipoVar == null) {
            erroSemantico("Variável não declarada: " + id.getValor());
        }

        consumir(Tipo.ATRIBUICAO, "Esperado '<-'");

        Res expr = expressao();
        verificarAtribuicao(tipoVar, expr.tipo, "Tipo incompatível na atribuição");

        mips.move(id.getValor(), expr.nome);

        consumir(Tipo.PONTO_VIRGULA, "Esperado ';'");
    }

    // ------------------------------------------------------------
    //                   IMPRIMIR(...)
    // ------------------------------------------------------------

    private void cmdImprimir() {

        consumir(Tipo.ABRE_PAREN, "Esperado '(' após Imprimir");

        // Estratégia simples: Imprimir(expr)
        Res expr = expressao();

        if (expr.tipo == TipoDado.STRING || expr.tipo == TipoDado.CARACTERE) {
            mips.printString(expr.nome);
        } else if (expr.tipo == TipoDado.INTEIRO || expr.tipo == TipoDado.LOGICO) {
            mips.printInt(expr.nome);
        } else {
            erroSemantico("Tipo incompatível em Imprimir");
        }

        consumir(Tipo.FECHA_PAREN, "Esperado ')' após argumentos de Imprimir");
        consumir(Tipo.PONTO_VIRGULA, "Esperado ';' após Imprimir");
    }

    // ------------------------------------------------------------
    //                   ENQUANTO
    // ------------------------------------------------------------

    private void cmdEnquanto() {

        String Linicio = novoLabel();
        String Lfim    = novoLabel();

        mips.label(Linicio);

        Res cond = expressao();
        if (cond.tipo != TipoDado.LOGICO) {
            erroSemantico("Condição do Enquanto deve ser lógica");
        }

        mips.ifZero(cond.nome, Lfim);

        bloco();

        mips.goTo(Linicio);
        mips.label(Lfim);
    }

    // ------------------------------------------------------------
    //                   SE / SENAO
    // ------------------------------------------------------------

    private void cmdSeSenao() {

        Res cond;

        // Aceita "Se (expressao)" OU "Se expressao"
        if (match(Tipo.ABRE_PAREN)) {
            cond = expressao();
            if (cond.tipo != TipoDado.LOGICO) {
                erroSemantico("Condição do Se deve ser lógica");
            }
            consumir(Tipo.FECHA_PAREN, "Esperado ')' após condição do Se");
        } else {
            cond = expressao();
            if (cond.tipo != TipoDado.LOGICO) {
                erroSemantico("Condição do Se deve ser lógica");
            }
        }

        String Lelse = novoLabel();
        String Lfim  = novoLabel();

        mips.ifZero(cond.nome, Lelse);

        bloco();

        if (match(Tipo.SENAO)) {
            mips.goTo(Lfim);
            mips.label(Lelse);
            bloco();
            mips.label(Lfim);
        } else {
            mips.label(Lelse);
        }
    }

    // ------------------------------------------------------------
    //                   PARA i em (ini, fim, passo)
    // ------------------------------------------------------------

    private void cmdPara() {

        Token id = consumir(Tipo.IDENTIFICADOR,
                "Esperado identificador após 'Para'");

        TipoDado tipo = tipoDe(id.getValor());
        if (tipo == null) {
            // se não declarado, declara como inteiro
            ts.declarar(id.getValor(), TipoDado.INTEIRO);
            tipo = TipoDado.INTEIRO;
            mips.declararVariavel(id.getValor());
        }

        Token em = consumir(Tipo.IDENTIFICADOR,
                "Esperado palavra 'em'");

        if (!em.getValor().equalsIgnoreCase("em")) {
            erroSintatico("Esperado palavra-chave 'em' após identificador em 'Para'");
        }

        consumir(Tipo.ABRE_PAREN, "Esperado '(' após 'em'");

        Res ini = expressao();
        consumir(Tipo.VIRGULA, "Esperado ',' após expressão inicial");

        Res fim = expressao();
        consumir(Tipo.VIRGULA, "Esperado ',' após expressão de fim");

        Res passo = expressao();

        consumir(Tipo.FECHA_PAREN,
                "Esperado ')' após parâmetros do Para");

        if (ini.tipo != TipoDado.INTEIRO ||
                fim.tipo != TipoDado.INTEIRO ||
                passo.tipo != TipoDado.INTEIRO) {
            erroSemantico("Parâmetros de Para devem ser INTEIRO");
        }

        // i <- ini
        mips.setVar(id.getValor(), ini.nome);

        String Lloop = novoLabel();
        String Lend  = novoLabel();

        mips.label(Lloop);

        // condição depende do sinal do passo (se for constante)
        String tcmp = novoTemp();

        if (isInt(passo.nome)) {
            int vPasso = Integer.parseInt(passo.nome);

            if (vPasso > 0) {
                // laço crescente: enquanto i <= fim
                mips.operacao(tcmp, id.getValor(), "<=", fim.nome);
            } else if (vPasso < 0) {
                // laço decrescente: enquanto i >= fim
                mips.operacao(tcmp, id.getValor(), ">=", fim.nome);
            } else {
                erroSemantico("Passo do comando 'Para' não pode ser zero");
            }
        } else {
            // passo não constante: fallback simples (laço crescente)
            mips.operacao(tcmp, id.getValor(), "<=", fim.nome);
        }

        mips.ifZero(tcmp, Lend);

        // corpo do laço
        bloco();

        // i <- i + passo
        String tsum = novoTemp();
        mips.operacao(tsum, id.getValor(), "+", passo.nome);
        mips.move(id.getValor(), tsum);

        mips.goTo(Lloop);
        mips.label(Lend);
    }

    // ------------------------------------------------------------
    //                   BLOCO {...}
    // ------------------------------------------------------------

    private void bloco() {
        consumir(Tipo.ABRE_CHAVE, "Esperado '{'");
        ts.abrirEscopo();
        while (!check(Tipo.FECHA_CHAVE) && !isAtEnd()) {
            comando();
        }
        consumir(Tipo.FECHA_CHAVE, "Esperado '}'");
        ts.fecharEscopo();
    }

    // ============================================================
    //                        EXPRESSÕES
    // ============================================================

    private Res expressao() {
        return exprOr();
    }

    // exprOr -> exprAnd ( "^" exprAnd )*
    private Res exprOr() {
        Res left = exprAnd();
        while (matchOpLog("^")) {
            Res right = exprAnd();
            verificarLogico(left, right);
            left = foldOr(left, right);
        }
        return left;
    }

    // exprAnd -> exprRel ( "&" exprRel )*
    private Res exprAnd() {
        Res left = exprRel();
        while (matchOpLog("&")) {
            Res right = exprRel();
            verificarLogico(left, right);
            left = foldAnd(left, right);
        }
        return left;
    }

    // exprRel -> exprAdd ( relOp exprAdd )*
    private Res exprRel() {
        Res left = exprAdd();

        // Só entra se o token atual for operador lógico relacional (=, <>, >, <, >=, <=)
        while (check(Tipo.OPERADOR_LOGICO) && isRelOp(atual().getValor())) {
            String op = atual().getValor(); // pega o operador atual
            avancar();                      // consome o operador

            Res right = exprAdd();

            if (left.tipo != TipoDado.INTEIRO || right.tipo != TipoDado.INTEIRO) {
                erroSemantico("Operadores relacionais requerem inteiros");
            }

            if (isInt(left.nome) && isInt(right.nome)) {
                int a = Integer.parseInt(left.nome);
                int b = Integer.parseInt(right.nome);
                int r = switch (op) {
                    case "="  -> (a == b ? 1 : 0);
                    case "<>" -> (a != b ? 1 : 0);
                    case ">"  -> (a >  b ? 1 : 0);
                    case "<"  -> (a <  b ? 1 : 0);
                    case ">=" -> (a >= b ? 1 : 0);
                    case "<=" -> (a <= b ? 1 : 0);
                    default   -> 0;
                };
                left = new Res(Integer.toString(r), TipoDado.LOGICO);
            } else {
                String t = novoTemp();
                mips.operacao(t, left.nome, op, right.nome);
                left = new Res(t, TipoDado.LOGICO);
            }
        }

        return left;
    }

    // exprAdd -> exprMul ( ("+"|"-") exprMul )*
    private Res exprAdd() {
        Res left = exprMul();
        while (matchOpArit("+", "-")) {
            String op = previous().getValor();
            Res right = exprMul();

            // CASO 1: soma numérica normal
            // CASO 2: concatenação, quando houver STRING envolvida
            if (op.equals("+") && (left.tipo == TipoDado.STRING || right.tipo == TipoDado.STRING)) {

                // Se um dos lados não for string, converte (int/logico -> string)
                left  = toStringRes(left);
                right = toStringRes(right);

                String s = mips.concatStrings(left.nome, right.nome);
                left = new Res(s, TipoDado.STRING);

            } else {
                if (left.tipo != TipoDado.INTEIRO || right.tipo != TipoDado.INTEIRO) {
                    erroSemantico("Operador '" + op + "' requer inteiros");
                }

                if (isInt(left.nome) && isInt(right.nome)) {
                    int a = Integer.parseInt(left.nome);
                    int b = Integer.parseInt(right.nome);
                    int r = op.equals("+") ? (a + b) : (a - b);
                    left = new Res(Integer.toString(r), TipoDado.INTEIRO);
                } else {
                    String t = novoTemp();
                    mips.operacao(t, left.nome, op, right.nome);
                    left = new Res(t, TipoDado.INTEIRO);
                }
            }
        }
        return left;
    }

    // exprMul -> exprPow ( ("*"|"/" "%") exprPow )*
    private Res exprMul() {
        Res left = exprPow();
        while (matchOpArit("*", "/", "%")) {
            String op = previous().getValor();
            Res right = exprPow();

            if (left.tipo != TipoDado.INTEIRO || right.tipo != TipoDado.INTEIRO) {
                erroSemantico("Operador '" + op + "' requer inteiros");
            }

            if (isInt(left.nome) && isInt(right.nome)) {
                int a = Integer.parseInt(left.nome);
                int b = Integer.parseInt(right.nome);
                int r = switch (op) {
                    case "*" -> a * b;
                    case "/" -> a / b;
                    case "%" -> a % b;
                    default  -> 0;
                };
                left = new Res(Integer.toString(r), TipoDado.INTEIRO);
            } else {
                String t = novoTemp();
                mips.operacao(t, left.nome, op, right.nome);
                left = new Res(t, TipoDado.INTEIRO);
            }
        }
        return left;
    }

    // exprPow -> exprUn ( "**" exprUn )*
    private Res exprPow() {
        Res left = exprUn();

        // Só trata potência se o operador atual for exatamente "**"
        while (check(Tipo.OPERADOR_ARIT) && atual().getValor().equals("**")) {
            avancar(); // consome o "**"

            Res right = exprUn();
            if (left.tipo != TipoDado.INTEIRO || right.tipo != TipoDado.INTEIRO) {
                erroSemantico("Potência requer inteiros");
            }

            if (isInt(left.nome) && isInt(right.nome)) {
                int base = Integer.parseInt(left.nome);
                int exp  = Integer.parseInt(right.nome);
                int r = 1;
                for (int i = 0; i < exp; i++) r *= base;
                left = new Res(Integer.toString(r), TipoDado.INTEIRO);
            } else {
                String t = novoTemp();
                mips.operacao(t, left.nome, "**", right.nome);
                left = new Res(t, TipoDado.INTEIRO);
            }
        }
        return left;
    }

    // exprUn -> "-" exprUn | primario
    private Res exprUn() {
        if (matchOpArit("-")) {
            Res r = exprUn();
            if (r.tipo != TipoDado.INTEIRO) {
                erroSemantico("Sinal unário '-' requer inteiro");
            }
            if (isInt(r.nome)) {
                int v = Integer.parseInt(r.nome);
                return new Res(Integer.toString(-v), TipoDado.INTEIRO);
            } else {
                String t = novoTemp();
                mips.operacao(t, "0", "-", r.nome);
                return new Res(t, TipoDado.INTEIRO);
            }
        }
        return primario();
    }

    // primario -> NUMERO | STRING | VERDADE | MENTIRA | IDENT | "(" expressao ")"
    private Res primario() {
        if (match(Tipo.NUMERO)) {
            return new Res(previous().getValor(), TipoDado.INTEIRO);
        }
        if (match(Tipo.STRING)) {
            String nome = mips.alocarStringLiteral(previous().getValor().substring(
                    1, previous().getValor().length() - 1));
            return new Res(nome, TipoDado.STRING);
        }
        if (match(Tipo.VERDADE)) {
            return new Res("1", TipoDado.LOGICO);
        }
        if (match(Tipo.MENTIRA)) {
            return new Res("0", TipoDado.LOGICO);
        }
        if (match(Tipo.IDENTIFICADOR)) {
            Token id = previous();
            TipoDado tipo = tipoDe(id.getValor());
            if (tipo == null) {
                erroSemantico("Variável não declarada: " + id.getValor());
            }
            return new Res(id.getValor(), tipo);
        }
        if (match(Tipo.ABRE_PAREN)) {
            Res r = expressao();
            consumir(Tipo.FECHA_PAREN, "Esperado ')' após expressão");
            return r;
        }

        erroSintatico("Expressão inválida");
        return new Res("0", TipoDado.DESCONHECIDO);
    }

    // ============================================================
    //                 FOLD DE & / ^ COM CÓDIGO MIPS
    // ============================================================

    private Res foldOr(Res a, Res b) {
        // constante?
        if (isInt(a.nome) && isInt(b.nome)) {
            int x = Integer.parseInt(a.nome);
            int y = Integer.parseInt(b.nome);
            int r = ((x != 0) || (y != 0)) ? 1 : 0;
            return new Res(Integer.toString(r), TipoDado.LOGICO);
        }
        // caso geral: gera código MIPS usando "^" (OR lógico)
        String t = novoTemp();
        mips.operacao(t, a.nome, "^", b.nome);
        return new Res(t, TipoDado.LOGICO);
    }

    private Res foldAnd(Res a, Res b) {
        // constante?
        if (isInt(a.nome) && isInt(b.nome)) {
            int x = Integer.parseInt(a.nome);
            int y = Integer.parseInt(b.nome);
            int r = ((x != 0) && (y != 0)) ? 1 : 0;
            return new Res(Integer.toString(r), TipoDado.LOGICO);
        }
        // caso geral: gera código MIPS usando "&" (AND lógico)
        String t = novoTemp();
        mips.operacao(t, a.nome, "&", b.nome);
        return new Res(t, TipoDado.LOGICO);
    }

    // ============================================================
    //        AJUDANTE: converter Res em STRING (para concat)
    // ============================================================

    private Res toStringRes(Res r) {
        if (r.tipo == TipoDado.STRING) {
            return r;
        }
        if (r.tipo == TipoDado.INTEIRO || r.tipo == TipoDado.LOGICO) {
            String nomeStr = mips.intToString(r.nome);
            return new Res(nomeStr, TipoDado.STRING);
        }
        erroSemantico("Só é possível concatenar strings, inteiros ou lógicos (recebido: " + r.tipo + ")");
        return r; // nunca chega aqui por causa do erroSemantico
    }

    // ============================================================
    //                       FUNÇÕES DE TIPO
    // ============================================================

    private TipoDado tipoDe(String id) {
        return ts.buscar(id);
    }

    private void verificarAtribuicao(TipoDado esperado, TipoDado obtido, String msg) {
        if (esperado != obtido) {
            erroSemantico(msg + " (esperado=" + esperado + ", obtido=" + obtido + ")");
        }
    }

    private void verificarLogico(Res a, Res b) {
        if (a.tipo != TipoDado.LOGICO || b.tipo != TipoDado.LOGICO) {
            erroSemantico("Operação lógica requer operandos lógicos");
        }
    }

    private boolean isRelOp(String op) {
        return op.equals("=") || op.equals("<>") || op.equals(">") ||
                op.equals("<") || op.equals(">=") || op.equals("<=");
    }

    private boolean isInt(String s) {
        return s != null && s.matches("-?\\d+");
    }

    // ============================================================
    //                       PARSER UTILS
    // ============================================================

    private boolean match(Tipo... tipos) {
        for (Tipo t : tipos) {
            if (check(t)) {
                avancar();
                return true;
            }
        }
        return false;
    }

    private boolean matchOpArit(String... ops) {
        if (!check(Tipo.OPERADOR_ARIT)) return false;
        String v = atual().getValor();
        for (String s : ops) {
            if (v.equals(s)) {
                avancar();
                return true;
            }
        }
        return false;
    }

    private boolean matchOpLog(String op) {
        if (!check(Tipo.OPERADOR_LOGICO)) return false;
        String v = atual().getValor();
        if (v.equals(op)) {
            avancar();
            return true;
        }
        return false;
    }

    private Token consumir(Tipo tipo, String mensagem) {
        if (check(tipo)) return avancar();
        erroSintatico(mensagem + " Encontrado: " + atual().getTipo() + " '" + atual().getValor() + "'");
        return null; // nunca chega aqui
    }

    private boolean check(Tipo tipo) {
        if (isAtEnd()) return false;
        return atual().getTipo() == tipo;
    }

    private Token avancar() {
        if (!isAtEnd()) atual++;
        return previous();
    }

    private boolean isAtEnd() {
        return atual().getTipo() == Tipo.FIM;
    }

    private Token atual() {
        return tokens.get(atual);
    }

    private Token previous() {
        return tokens.get(atual - 1);
    }

    private String novoTemp() {
        String t = "t" + (tempCount++);
        mips.declararVariavel(t);
        return t;
    }

    private String novoLabel() {
        return "L" + (labelCount++);
    }

    private void erroSintatico(String msg) {
        throw new RuntimeException("Erro sintático: " + msg);
    }

    private void erroSemantico(String msg) {
        throw new RuntimeException("Erro semântico: " + msg);
    }
}
