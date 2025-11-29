# Diagramas Visuais dos Autômatos - Compilador CF

Este documento contém diagramas em formato Mermaid que podem ser visualizados no GitHub, VSCode ou outras ferramentas compatíveis.

## 📊 Como Visualizar

- **GitHub/GitLab**: Os diagramas são renderizados automaticamente
- **VSCode**: Instale a extensão "Markdown Preview Mermaid Support"
- **Online**: Use https://mermaid.live/

---

## 1. Autômato Principal do Lexer

```mermaid
stateDiagram-v2
    [*] --> q0: Início
    
    q0 --> AFD_Ident: letra
    q0 --> AFD_Numero: dígito
    q0 --> AFD_String: "
    q0 --> AFD_Operador: operador
    q0 --> AFD_Simbolo: símbolo
    q0 --> q0: espaço (ignora)
    q0 --> [*]: EOF
    
    AFD_Ident --> TokenID: classificar
    AFD_Numero --> TokenNUM: aceitar
    AFD_String --> TokenSTR: aceitar
    AFD_Operador --> TokenOP: aceitar
    AFD_Simbolo --> TokenSYM: aceitar
    
    TokenID --> q0: próximo
    TokenNUM --> q0: próximo
    TokenSTR --> q0: próximo
    TokenOP --> q0: próximo
    TokenSYM --> q0: próximo
```

---

## 2. AFD para Identificadores

```mermaid
stateDiagram-v2
    [*] --> q0: início
    q0 --> q1: letra
    q1 --> q1: letra
    q1 --> [*]: outro (aceita)
    
    note right of q1
        Estado Final
        Pós-processa para
        verificar palavra-chave
    end note
```

---

## 3. AFD para Números Inteiros

```mermaid
stateDiagram-v2
    [*] --> q0: início
    q0 --> q1: dígito
    q1 --> q1: dígito
    q1 --> [*]: outro (aceita)
    
    note right of q1
        Estado Final
        Retorna NUMERO
    end note
```

---

## 4. AFD para Strings Literais

```mermaid
stateDiagram-v2
    [*] --> q0: início
    q0 --> q1: "
    q1 --> q1: c (c ≠ ")
    q1 --> q2: "
    q2 --> [*]: aceita STRING
    
    q1 --> qErro: EOF
    qErro --> [*]: erro (string não fechada)
    
    note right of q2
        String válida
        delimitada por aspas
    end note
```

---

## 5. AFD para Comentário de Linha ($)

```mermaid
stateDiagram-v2
    [*] --> q0: início
    q0 --> q1: $
    q1 --> q1: c (c ≠ \n)
    q1 --> q2: \n
    q2 --> [*]: descarta
    
    note right of q2
        Comentário removido
        no pré-processamento
    end note
```

---

## 6. AFD para Comentário de Bloco ($$)

```mermaid
stateDiagram-v2
    [*] --> q0: início
    q0 --> q1: $$
    q1 --> q1: c (c ≠ $)
    q1 --> q2: $
    q2 --> q3: $
    q3 --> [*]: descarta
    q2 --> q1: c (c ≠ $)
    
    note right of q3
        Comentário multilinha
        removido no pré-processamento
    end note
```

---

## 7. AFD para Operadores de 2 Caracteres

```mermaid
stateDiagram-v2
    [*] --> q0: início
    
    q0 --> q1_mult: *
    q1_mult --> q2_pot: *
    q2_pot --> [*]: aceita **
    
    q0 --> q1_menor: <
    q1_menor --> q2_atrib: -
    q2_atrib --> [*]: aceita <-
    q1_menor --> q2_dif: >
    q2_dif --> [*]: aceita <>
    q1_menor --> q2_menorIg: =
    q2_menorIg --> [*]: aceita <=
    
    q0 --> q1_maior: >
    q1_maior --> q2_maiorIg: =
    q2_maiorIg --> [*]: aceita >=
    
    note right of q2_pot: Potenciação
    note right of q2_atrib: Atribuição
    note right of q2_dif: Diferente
    note right of q2_menorIg: Menor ou Igual
    note right of q2_maiorIg: Maior ou Igual
```

---

## 8. Diagrama de Fluxo do Processo de Tokenização

```mermaid
flowchart TD
    A[Código Fonte] --> B[Pré-processamento]
    B --> C{Remove Comentários}
    C -->|$...| D[Remove linha]
    C -->|$$...$$ | E[Remove bloco]
    D --> F[Código Limpo]
    E --> F
    
    F --> G[Análise Léxica]
    G --> H{Próximo Caractere}
    
    H -->|Letra| I[AFD Identificador]
    H -->|Dígito| J[AFD Número]
    H -->|"| K[AFD String]
    H -->|Operador| L[AFD Operador]
    H -->|Símbolo| M[AFD Símbolo]
    H -->|Espaço| N[Ignora]
    H -->|EOF| O[Token FIM]
    
    I --> P[Classifica Token]
    J --> P
    K --> P
    L --> P
    M --> P
    N --> H
    
    P --> Q[Adiciona à Lista]
    Q --> H
    O --> R[Lista de Tokens]
    R --> S[Parser]
```

---

## 9. Diagrama de Classes Simplificado

```mermaid
classDiagram
    class Lexer {
        -String codigo
        -List~Token~ tokens
        +Lexer(String codigo)
        +List~Token~ analisar()
        -Token classificar(String v)
    }
    
    class Token {
        <<enumeration>> Tipo
        -Tipo tipo
        -String valor
        +Token(Tipo tipo, String valor)
        +Tipo getTipo()
        +String getValor()
        +String toString()
    }
    
    class Tipo {
        <<enumeration>>
        INTEIRO
        LOGICO
        CARACTERE
        ENQUANTO
        SE
        SENAO
        PARA
        IMPRIMIR
        VERDADE
        MENTIRA
        NUMERO
        STRING
        IDENTIFICADOR
        OPERADOR_ARIT
        OPERADOR_LOGICO
        ATRIBUICAO
        ABRE_CHAVE
        FECHA_CHAVE
        ABRE_PAREN
        FECHA_PAREN
        VIRGULA
        PONTO_VIRGULA
        FIM
    }
    
    Lexer "1" --> "*" Token : produz
    Token --> Tipo : usa
```

---

## 10. Sequência de Reconhecimento de Token

```mermaid
sequenceDiagram
    participant C as Código
    participant L as Lexer
    participant A as AFD
    participant T as Token
    
    C->>L: "Inteiro x <- 10;"
    L->>L: Remove comentários
    L->>A: Inicia análise
    
    A->>A: Reconhece "Inteiro"
    A->>T: Cria Token(INTEIRO, "Inteiro")
    T-->>L: Token criado
    
    A->>A: Reconhece "x"
    A->>T: Cria Token(IDENTIFICADOR, "x")
    T-->>L: Token criado
    
    A->>A: Reconhece "<-"
    A->>T: Cria Token(ATRIBUICAO, "<-")
    T-->>L: Token criado
    
    A->>A: Reconhece "10"
    A->>T: Cria Token(NUMERO, "10")
    T-->>L: Token criado
    
    A->>A: Reconhece ";"
    A->>T: Cria Token(PONTO_VIRGULA, ";")
    T-->>L: Token criado
    
    L->>T: Adiciona Token(FIM, "")
    L-->>C: Lista de Tokens
```

---

## 11. Máquina de Estados para Palavras-Chave "Inteiro"

```mermaid
stateDiagram-v2
    [*] --> q0
    q0 --> q1: I
    q1 --> q2: n
    q2 --> q3: t
    q3 --> q4: e
    q4 --> q5: i
    q5 --> q6: r
    q6 --> q7: o
    q7 --> qFinal: fim_palavra
    qFinal --> [*]: Token(INTEIRO)
    
    note right of qFinal
        Case-insensitive
        "inteiro", "INTEIRO", "InTeIrO"
        todos aceitos
    end note
```

---

## 12. Hierarquia de Reconhecimento de Operadores

```mermaid
graph TD
    A[Detecta Operador] --> B{1 ou 2 chars?}
    
    B -->|Tentar 2| C{Verifica lookahead}
    B -->|1 char| D[Operador Simples]
    
    C -->|*| E{Próximo = *?}
    C -->|<| F{Próximo = -, >, =?}
    C -->|>| G{Próximo = =?}
    
    E -->|Sim| H[Token OPERADOR ** ]
    E -->|Não| I[Token OPERADOR *]
    
    F -->|−| J[Token ATRIBUICAO <-]
    F -->|>| K[Token OPERADOR <>]
    F -->|=| L[Token OPERADOR <=]
    F -->|Outro| M[Token OPERADOR <]
    
    G -->|Sim| N[Token OPERADOR >=]
    G -->|Não| O[Token OPERADOR >]
    
    D --> P[+, -, *, /, %, =, <, >, &, ^]
```

---

## 13. Transições de Estado Completas - Visão Geral

```mermaid
graph LR
    subgraph "Estados Iniciais"
        q0[q0 - Início]
    end
    
    subgraph "Reconhecimento"
        id[Identificador]
        num[Número]
        str[String]
        op[Operador]
        sym[Símbolo]
    end
    
    subgraph "Estados Finais"
        tid[Token ID/Palavra-Chave]
        tnum[Token NUMERO]
        tstr[Token STRING]
        top[Token OPERADOR]
        tsym[Token SIMBOLO]
    end
    
    q0 -->|letra| id
    q0 -->|dígito| num
    q0 -->|"| str
    q0 -->|+ - * / % = < > & ^| op
    q0 -->|{ } ( ) ; ,| sym
    
    id --> tid
    num --> tnum
    str --> tstr
    op --> top
    sym --> tsym
    
    tid --> q0
    tnum --> q0
    tstr --> q0
    top --> q0
    tsym --> q0
```

---

## 14. Exemplo de Análise Passo a Passo

### Entrada: `x <- 5 + 3`

```mermaid
stateDiagram-v2
    direction LR
    [*] --> Estado1: Lê 'x'
    Estado1 --> Estado2: Token(IDENTIFICADOR, x)
    Estado2 --> Estado3: Lê '<-'
    Estado3 --> Estado4: Token(ATRIBUICAO, <-)
    Estado4 --> Estado5: Lê '5'
    Estado5 --> Estado6: Token(NUMERO, 5)
    Estado6 --> Estado7: Lê '+'
    Estado7 --> Estado8: Token(OPERADOR_ARIT, +)
    Estado8 --> Estado9: Lê '3'
    Estado9 --> Estado10: Token(NUMERO, 3)
    Estado10 --> [*]: Token(FIM)
```

---

## 15. Pipeline Completo do Compilador

```mermaid
flowchart LR
    A[Código Fonte .cf] --> B[Lexer]
    B --> C[Lista de Tokens]
    C --> D[Parser]
    D --> E[Árvore Sintática]
    E --> F[Análise Semântica]
    F --> G[Tabela de Símbolos]
    G --> H[Gerador de Código]
    H --> I[Código MIPS .asm]
    
    style B fill:#ff9
    style C fill:#9f9
    style D fill:#99f
    
    note1[AFDs<br/>Regex]
    note2[Gramática<br/>Recursiva]
    note3[Tipos<br/>Escopo]
    
    note1 -.-> B
    note2 -.-> D
    note3 -.-> F
```

---

## 16. Comparação de Poder Computacional

```mermaid
graph TB
    subgraph "Hierarquia de Chomsky"
        A[Tipo 3 - Regular<br/>AFD/AFN] --> B[Tipo 2 - Livre de Contexto<br/>PDA]
        B --> C[Tipo 1 - Sensível ao Contexto<br/>LBA]
        C --> D[Tipo 0 - Recursivamente Enumerável<br/>Máquina de Turing]
    end
    
    subgraph "Uso no Compilador CF"
        E[Lexer<br/>AFD] -.->|implementa| A
        F[Parser<br/>Recursivo] -.->|equivalente| B
    end
    
    style A fill:#9f9
    style B fill:#99f
    style E fill:#ff9
    style F fill:#f99
```

---

## 17. Autômato com Tratamento de Erros

```mermaid
stateDiagram-v2
    [*] --> q0: Início da análise
    
    q0 --> qValido: Caractere válido
    q0 --> qErro: Caractere inválido
    
    qValido --> qToken: Forma token
    qToken --> q0: Continua análise
    
    qErro --> qRecuperacao: Tenta recuperar
    qRecuperacao --> q0: Ignora e continua
    qRecuperacao --> [*]: Erro fatal
    
    qToken --> [*]: EOF
    
    note right of qErro
        Caractere não reconhecido
        por nenhum AFD
    end note
    
    note right of qRecuperacao
        Modo de recuperação
        não implementado
    end note
```

---

## 18. Grafo de Dependências entre Componentes

```mermaid
graph TD
    A[Main.java] --> B[Lexer.java]
    A --> C[Parser.java]
    A --> D[CodeGenMIPS.java]
    
    B --> E[Token.java]
    C --> E
    C --> F[TabelaDeSimbolos.java]
    C --> G[TipoDado.java]
    
    D --> F
    D --> G
    
    H[exemplo.cf] --> A
    A --> I[saida.asm]
    
    style A fill:#f96
    style B fill:#9f6
    style C fill:#69f
    style D fill:#f6f
```

---

## 📝 Notas sobre os Diagramas

### Legenda de Símbolos

- **`[*]`**: Estado inicial ou final
- **`(q0)`**: Estado normal
- **`((q1))`**: Estado de aceitação (final)
- **`qErro`**: Estado de erro
- **Setas**: Transições entre estados
- **Labels nas setas**: Condição para transição

### Convenções

- **AFD**: Autômato Finito Determinístico (uma transição por símbolo)
- **AFN**: Autômato Finito Não-determinístico (múltiplas transições possíveis)
- **PDA**: Autômato com Pilha (usado em parsers)

---

## 🔧 Ferramentas Recomendadas

1. **Mermaid Live Editor**: https://mermaid.live/
2. **VSCode Extension**: Markdown Preview Mermaid Support
3. **GitHub**: Renderização nativa de diagramas Mermaid
4. **Draw.io**: Para diagramas mais complexos

---

*Documentação gerada para o projeto Compilador CF - 2025*

