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
        // Comentários
        codigo = codigo.replaceAll("(?s)\\$\\$.*?\\$\\$", "");   // $$ ... $$ (multilinha)
        codigo = codigo.replaceAll("(?m)\\$[^\\n]*", "");        // $ ... (linha)

        // IMPORTANTE:
        // - (?iu): case-insensitive + unicode
        // - tokens de 2 chars antes dos de 1 char
        // - Senão/Senao ANTES de Se
        String regex =
            "(?iu)" +
            "(" + "Inteiro|Logico|Lógico|Caractere|Enquanto|Sen[ãa]o|Senao|Para|Imprimir|Verdade|Mentira|Se" + ")" + // keywords (longos antes!)
            "|" + "(\\*\\*|>=|<=|<>|<-)" +        // operadores 2 chars
            "|" + "(\\d+)" +                      // números
            "|" + "(\\p{L}+)" +                   // identificadores (todas as letras unicode)
            "|" + "([+\\-/%*])" +                 // aritméticos 1 char
            "|" + "(=|>|<|&|\\^)" +               // lógicos 1 char
            "|" + "([{};(),])" +                  // símbolos
            "|" + "(\"[^\"]*\")";                 // strings

        Matcher matcher = Pattern.compile(regex).matcher(codigo);
        while (matcher.find()) {
            String lexema = matcher.group();
            tokens.add(classificar(lexema));
        }

        tokens.add(new Token(Token.Tipo.FIM, ""));
        return tokens;
    }

    private Token classificar(String valor) {
        // Strings
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            return new Token(Token.Tipo.STRING, valor);
        }

        // Números
        if (valor.matches("\\d+")) return new Token(Token.Tipo.NUMERO, valor);

        // Símbolos
        switch (valor) {
            case "{": return new Token(Token.Tipo.ABRE_CHAVE, valor);
            case "}": return new Token(Token.Tipo.FECHA_CHAVE, valor);
            case ";": return new Token(Token.Tipo.PONTO_VIRGULA, valor);
            case "(": return new Token(Token.Tipo.ABRE_PAREN, valor);
            case ")": return new Token(Token.Tipo.FECHA_PAREN, valor);
            case ",": return new Token(Token.Tipo.VIRGULA, valor);
        }

        // Operadores compostos / atribuição
        if (valor.equals("<-")) return new Token(Token.Tipo.ATRIBUICAO, valor);
        if (valor.equals("**")) return new Token(Token.Tipo.OPERADOR_ARIT, valor);
        if (valor.equals(">=") || valor.equals("<=") || valor.equals("<>"))
            return new Token(Token.Tipo.OPERADOR_LOGICO, valor);

        // Operadores 1 char
        if (valor.matches("[+\\-/%*]"))  return new Token(Token.Tipo.OPERADOR_ARIT, valor);
        if (valor.matches("=|>|<|&|\\^")) return new Token(Token.Tipo.OPERADOR_LOGICO, valor);

        // Palavras-chave (case-insensitive + acentos)
        String low = valor.toLowerCase(Locale.ROOT);
        switch (low) {
            case "inteiro":   return new Token(Token.Tipo.INTEIRO, valor);
            case "logico":
            case "lógico":    return new Token(Token.Tipo.LOGICO, valor);
            case "caractere": return new Token(Token.Tipo.CARACTERE, valor);
            case "enquanto":  return new Token(Token.Tipo.ENQUANTO, valor);
            case "senão":
            case "senao":     return new Token(Token.Tipo.SENAO, valor);
            case "para":      return new Token(Token.Tipo.PARA, valor);
            case "imprimir":  return new Token(Token.Tipo.IMPRIMIR, valor);
            case "verdade":   return new Token(Token.Tipo.VERDADE, valor);
            case "mentira":   return new Token(Token.Tipo.MENTIRA, valor);
            case "se":        return new Token(Token.Tipo.SE, valor);
        }

        // Identificador (normaliza para minúsculas p/ case-insensitive)
        if (valor.matches("\\p{L}+")) {
            String id = valor.toLowerCase(Locale.ROOT);
            return new Token(Token.Tipo.IDENTIFICADOR, id);
        }

        throw new RuntimeException("Token inválido: " + valor);
    }
}
