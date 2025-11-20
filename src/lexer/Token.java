package lexer;

public class Token {
    public enum Tipo {
        // Palavras-chave / tipos
        INTEIRO, LOGICO, CARACTERE,
        ENQUANTO, SE, SENAO, PARA, IMPRIMIR,

        // Literais e identificadores
        NUMERO, IDENTIFICADOR, STRING, VERDADE, MENTIRA,

        // Operadores
        OPERADOR_ARIT, OPERADOR_LOGICO, ATRIBUICAO,

        // Símbolos
        ABRE_CHAVE, FECHA_CHAVE, PONTO_VIRGULA,
        ABRE_PAREN, FECHA_PAREN, VIRGULA,

        // Fim de arquivo/entrada
        FIM
    }

    private final Tipo tipo;
    private final String valor;

    public Token(Tipo tipo, String valor) {
        this.tipo = tipo;
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
        return "|" + tipo + " = " + valor + "|";
    }
}
