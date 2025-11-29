package test;

import lexer.*;
import java.util.List;

/**
 * Classe de testes para validar os autômatos do Lexer
 * Testa todos os tipos de tokens reconhecidos pelos AFDs
 */
public class TestAutomatos {

    private static int totalTestes = 0;
    private static int testesPassaram = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("     TESTES DE AUTOMATOS - COMPILADOR CF                  ");
        System.out.println("==========================================================\n");

        testarIdentificadores();
        testarNumeros();
        testarStrings();
        testarOperadores();
        testarSimbolos();
        testarComentarios();
        testarPalavrasChave();
        testarCasosComplexos();

        System.out.println("\n==========================================================");
        System.out.println("                    RESULTADO FINAL                       ");
        System.out.println("==========================================================");
        System.out.printf("  Total de Testes: %3d\n", totalTestes);
        System.out.printf("  Passaram:        %3d\n", testesPassaram);
        System.out.printf("  Falharam:        %3d\n", totalTestes - testesPassaram);
        System.out.printf("  Taxa de Sucesso: %.1f%%\n",
            (testesPassaram * 100.0 / totalTestes));
        System.out.println("==========================================================");
    }

    private static void testarIdentificadores() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: AFD de Identificadores");
        System.out.println("==========================================================");

        testar("x", Token.Tipo.IDENTIFICADOR, "x");
        testar("abc", Token.Tipo.IDENTIFICADOR, "abc");
        testar("variavel", Token.Tipo.IDENTIFICADOR, "variavel");
        testar("camelCase", Token.Tipo.IDENTIFICADOR, "camelcase");
        testar("nome", Token.Tipo.IDENTIFICADOR, "nome");

        System.out.println();
    }

    private static void testarNumeros() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: AFD de Numeros");
        System.out.println("==========================================================");

        testar("0", Token.Tipo.NUMERO, "0");
        testar("123", Token.Tipo.NUMERO, "123");
        testar("999", Token.Tipo.NUMERO, "999");
        testar("42", Token.Tipo.NUMERO, "42");
        testar("007", Token.Tipo.NUMERO, "007");

        System.out.println();
    }

    private static void testarStrings() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: AFD de Strings");
        System.out.println("==========================================================");

        testar("\"\"", Token.Tipo.STRING, "\"\"");
        testar("\"Hello\"", Token.Tipo.STRING, "\"Hello\"");
        testar("\"Ola Mundo\"", Token.Tipo.STRING, "\"Ola Mundo\"");
        testar("\"123\"", Token.Tipo.STRING, "\"123\"");
        testar("\"Teste!\"", Token.Tipo.STRING, "\"Teste!\"");

        System.out.println();
    }

    private static void testarOperadores() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: AFD de Operadores");
        System.out.println("==========================================================");

        // Operadores de 2 caracteres
        testar("**", Token.Tipo.OPERADOR_ARIT, "**");
        testar(">=", Token.Tipo.OPERADOR_LOGICO, ">=");
        testar("<=", Token.Tipo.OPERADOR_LOGICO, "<=");
        testar("<>", Token.Tipo.OPERADOR_LOGICO, "<>");
        testar("<-", Token.Tipo.ATRIBUICAO, "<-");

        // Operadores de 1 caractere
        testar("+", Token.Tipo.OPERADOR_ARIT, "+");
        testar("-", Token.Tipo.OPERADOR_ARIT, "-");
        testar("*", Token.Tipo.OPERADOR_ARIT, "*");
        testar("/", Token.Tipo.OPERADOR_ARIT, "/");
        testar("%", Token.Tipo.OPERADOR_ARIT, "%");
        testar("=", Token.Tipo.OPERADOR_LOGICO, "=");
        testar(">", Token.Tipo.OPERADOR_LOGICO, ">");
        testar("<", Token.Tipo.OPERADOR_LOGICO, "<");
        testar("&", Token.Tipo.OPERADOR_LOGICO, "&");
        testar("^", Token.Tipo.OPERADOR_LOGICO, "^");

        System.out.println();
    }

    private static void testarSimbolos() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: Simbolos");
        System.out.println("==========================================================");

        testar("{", Token.Tipo.ABRE_CHAVE, "{");
        testar("}", Token.Tipo.FECHA_CHAVE, "}");
        testar("(", Token.Tipo.ABRE_PAREN, "(");
        testar(")", Token.Tipo.FECHA_PAREN, ")");
        testar(";", Token.Tipo.PONTO_VIRGULA, ";");
        testar(",", Token.Tipo.VIRGULA, ",");

        System.out.println();
    }

    private static void testarComentarios() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: Remocao de Comentarios");
        System.out.println("==========================================================");

        // Comentario de linha - deve ser removido
        String codigo1 = "$ comentario\nInteiro x;";
        Lexer lex1 = new Lexer(codigo1);
        List<Token> tokens1 = lex1.analisar();
        boolean teste1 = tokens1.size() == 4; // INTEIRO, IDENTIFICADOR, PONTO_VIRGULA, FIM
        reportarTeste("Comentario de linha removido", teste1);

        // Comentario de bloco - deve ser removido
        String codigo2 = "$$ comentario\nmultilinha $$Inteiro y;";
        Lexer lex2 = new Lexer(codigo2);
        List<Token> tokens2 = lex2.analisar();
        boolean teste2 = tokens2.size() == 4; // INTEIRO, IDENTIFICADOR, PONTO_VIRGULA, FIM
        reportarTeste("Comentario de bloco removido", teste2);

        System.out.println();
    }

    private static void testarPalavrasChave() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: Palavras-Chave (case-insensitive)");
        System.out.println("==========================================================");

        testar("Inteiro", Token.Tipo.INTEIRO, "Inteiro");
        testar("INTEIRO", Token.Tipo.INTEIRO, "INTEIRO");
        testar("inteiro", Token.Tipo.INTEIRO, "inteiro");
        testar("Logico", Token.Tipo.LOGICO, "Logico");
        testar("Caractere", Token.Tipo.CARACTERE, "Caractere");
        testar("Enquanto", Token.Tipo.ENQUANTO, "Enquanto");
        testar("Se", Token.Tipo.SE, "Se");
        testar("Senao", Token.Tipo.SENAO, "Senao");
        testar("Para", Token.Tipo.PARA, "Para");
        testar("Imprimir", Token.Tipo.IMPRIMIR, "Imprimir");
        testar("Verdade", Token.Tipo.VERDADE, "Verdade");
        testar("Mentira", Token.Tipo.MENTIRA, "Mentira");

        System.out.println();
    }

    private static void testarCasosComplexos() {
        System.out.println("==========================================================");
        System.out.println("  TESTE: Casos Complexos");
        System.out.println("==========================================================");

        // Declaracao simples
        String codigo1 = "Inteiro x <- 10;";
        Lexer lex1 = new Lexer(codigo1);
        List<Token> tokens1 = lex1.analisar();
        boolean teste1 = tokens1.size() == 6 &&
                        tokens1.get(0).getTipo() == Token.Tipo.INTEIRO &&
                        tokens1.get(1).getTipo() == Token.Tipo.IDENTIFICADOR &&
                        tokens1.get(2).getTipo() == Token.Tipo.ATRIBUICAO &&
                        tokens1.get(3).getTipo() == Token.Tipo.NUMERO &&
                        tokens1.get(4).getTipo() == Token.Tipo.PONTO_VIRGULA;
        reportarTeste("Declaracao: Inteiro x <- 10;", teste1);

        // Expressao com potencia
        String codigo2 = "resultado <- x**2 + y;";
        Lexer lex2 = new Lexer(codigo2);
        List<Token> tokens2 = lex2.analisar();
        boolean teste2 = tokens2.get(3).getTipo() == Token.Tipo.OPERADOR_ARIT &&
                        tokens2.get(3).getValor().equals("**");
        reportarTeste("Operador ** reconhecido", teste2);

        // Comparacao
        String codigo3 = "Se (a >= b)";
        Lexer lex3 = new Lexer(codigo3);
        List<Token> tokens3 = lex3.analisar();
        boolean teste3 = tokens3.get(3).getTipo() == Token.Tipo.OPERADOR_LOGICO &&
                        tokens3.get(3).getValor().equals(">=");
        reportarTeste("Operador >= reconhecido", teste3);

        // Bloco de codigo
        String codigo4 = "{ Inteiro a; Inteiro b; }";
        Lexer lex4 = new Lexer(codigo4);
        List<Token> tokens4 = lex4.analisar();
        boolean teste4 = tokens4.size() == 9 &&
                        tokens4.get(0).getTipo() == Token.Tipo.ABRE_CHAVE &&
                        tokens4.get(7).getTipo() == Token.Tipo.FECHA_CHAVE;
        reportarTeste("Bloco com chaves", teste4);

        // String em Imprimir
        String codigo5 = "Imprimir(\"Resultado: \");";
        Lexer lex5 = new Lexer(codigo5);
        List<Token> tokens5 = lex5.analisar();
        boolean teste5 = tokens5.get(0).getTipo() == Token.Tipo.IMPRIMIR &&
                        tokens5.get(2).getTipo() == Token.Tipo.STRING;
        reportarTeste("Imprimir com string", teste5);

        System.out.println();
    }

    private static void testar(String codigo, Token.Tipo tipoEsperado, String valorEsperado) {
        totalTestes++;
        try {
            Lexer lexer = new Lexer(codigo);
            List<Token> tokens = lexer.analisar();

            if (tokens.isEmpty()) {
                System.out.printf("[X] FALHA: '%s' - nenhum token gerado\n", codigo);
                return;
            }

            Token token = tokens.get(0);
            boolean tipoCorreto = token.getTipo() == tipoEsperado;
            boolean valorCorreto = token.getValor().equals(valorEsperado);

            if (tipoCorreto && valorCorreto) {
                System.out.printf("[OK] '%s' -> %s(%s)\n",
                    codigo, token.getTipo(), token.getValor());
                testesPassaram++;
            } else {
                System.out.printf("[X] FALHA: '%s'\n", codigo);
                System.out.printf("    Esperado: %s(%s)\n", tipoEsperado, valorEsperado);
                System.out.printf("    Obtido:   %s(%s)\n", token.getTipo(), token.getValor());
            }
        } catch (Exception e) {
            System.out.printf("[X] ERRO: '%s' - %s\n", codigo, e.getMessage());
        }
    }

    private static void reportarTeste(String descricao, boolean passou) {
        totalTestes++;
        if (passou) {
            System.out.printf("[OK] %s\n", descricao);
            testesPassaram++;
        } else {
            System.out.printf("[X] FALHA: %s\n", descricao);
        }
    }
}

