package lexer;

public class Token {

    public enum Tipo {
        // Palavras-chave
        INTEIRO, LOGICO, CARACTERE,
        ENQUANTO, SE, SENAO, PARA, IMPRIMIR,
        VERDADE, MENTIRA,

        // Literais
        NUMERO, STRING,

        // Identificadores
        IDENTIFICADOR,

        // Operadores
        OPERADOR_ARIT,
        OPERADOR_LOGICO,
        ATRIBUICAO,   // <-

        // Símbolos
        ABRE_CHAVE, FECHA_CHAVE,
        ABRE_PAREN, FECHA_PAREN,
        VIRGULA, PONTO_VIRGULA,

        // Final
        FIM
    }

    private final Tipo tipo;
    private final String valor;

    public Token(Tipo tipo, String valor) {
        this.tipo  = tipo;
        this.valor = valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return tipo + " = " + valor;
    }
}
