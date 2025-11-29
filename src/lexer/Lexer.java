package lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer {

    private String codigo;
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String codigo) {
        this.codigo = codigo;
    }

    public List<Token> analisar() {

        // Remoção de comentários
        codigo = codigo.replaceAll("(?s)\\$\\$.*?\\$\\$", "");  // $$ ... $$
        codigo = codigo.replaceAll("(?m)\\$.*$", "");           // $ ...

        // Ordem de prioridade é crucial!
        String regex =
            "(?iu)" +
            "(" + "Inteiro|Logico|Lógico|Caractere|Enquanto|Sen[ãa]o|Para|Imprimir|Verdade|Mentira|Se" + ")" + // palavras-chave
            "|" + "(\\*\\*|>=|<=|<>|<-)" +     // operadores 2 caracteres
            "|" + "(\\d+)" +                   // números
            "|" + "(\\p{L}+)" +                // identificadores
            "|" + "([+\\-/%*])" +              // operadores aritméticos
            "|" + "(=|>|<|&|\\^)" +            // operadores lógicos
            "|" + "([{};,()])" +               // <<< parênteses incluídos aqui
            "|" + "(\"[^\"]*\")";              // strings

        Matcher m = Pattern.compile(regex).matcher(codigo);

        while (m.find()) {
            tokens.add(classificar(m.group()));
        }

        tokens.add(new Token(Token.Tipo.FIM, ""));
        return tokens;
    }

    private Token classificar(String v) {

        // Strings
        if (v.startsWith("\"") && v.endsWith("\""))
            return new Token(Token.Tipo.STRING, v);

        // Números
        if (v.matches("\\d+"))
            return new Token(Token.Tipo.NUMERO, v);

        // Símbolos e parênteses
        switch (v) {
            case "{": return new Token(Token.Tipo.ABRE_CHAVE, v);
            case "}": return new Token(Token.Tipo.FECHA_CHAVE, v);
            case "(": return new Token(Token.Tipo.ABRE_PAREN, v);
            case ")": return new Token(Token.Tipo.FECHA_PAREN, v);
            case ";": return new Token(Token.Tipo.PONTO_VIRGULA, v);
            case ",": return new Token(Token.Tipo.VIRGULA, v);
        }

        // Operadores especiais
        switch (v) {
            case "<-": return new Token(Token.Tipo.ATRIBUICAO, v);
            case "**": return new Token(Token.Tipo.OPERADOR_ARIT, v);
            case ">=": case "<=": case "<>":
                return new Token(Token.Tipo.OPERADOR_LOGICO, v);
        }

        // Operadores simples
        if (v.matches("[+\\-/%*]"))
            return new Token(Token.Tipo.OPERADOR_ARIT, v);

        if (v.matches("=|>|<|&|\\^"))
            return new Token(Token.Tipo.OPERADOR_LOGICO, v);

        // Palavras-chave (case insensitive)
        String low = v.toLowerCase(Locale.ROOT);

        switch (low) {
            case "inteiro": return new Token(Token.Tipo.INTEIRO, v);
            case "logico":
            case "lógico": return new Token(Token.Tipo.LOGICO, v);
            case "caractere": return new Token(Token.Tipo.CARACTERE, v);
            case "enquanto": return new Token(Token.Tipo.ENQUANTO, v);
            case "senão":
            case "senao": return new Token(Token.Tipo.SENAO, v);
            case "para": return new Token(Token.Tipo.PARA, v);
            case "imprimir": return new Token(Token.Tipo.IMPRIMIR, v);
            case "verdade": return new Token(Token.Tipo.VERDADE, v);
            case "mentira": return new Token(Token.Tipo.MENTIRA, v);
            case "se": return new Token(Token.Tipo.SE, v);
        }

        // Identificador
        if (v.matches("\\p{L}+"))
            return new Token(Token.Tipo.IDENTIFICADOR, v.toLowerCase());

        throw new RuntimeException("Token inválido: " + v);
    }
}
