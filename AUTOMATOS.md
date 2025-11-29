# Autômatos Finitos - Compilador Compila Fofo (CF)

Este documento apresenta os autômatos finitos determinísticos (AFD) e não-determinísticos (AFN) para o analisador léxico do compilador CF.

## 📑 Índice
- [1. Autômato Principal do Lexer](#1-autômato-principal-do-lexer)
- [2. Autômato para Identificadores](#2-autômato-para-identificadores)
- [3. Autômato para Números Inteiros](#3-autômato-para-números-inteiros)
- [4. Autômato para Strings Literais](#4-autômato-para-strings-literais)
- [5. Autômato para Comentários](#5-autômato-para-comentários)
- [6. Autômato para Operadores](#6-autômato-para-operadores)
- [7. Diagrama de Transição Completo](#7-diagrama-de-transição-completo)

---

## 1. Autômato Principal do Lexer

O autômato principal coordena o reconhecimento de todos os tokens.

```
┌─────────────────────────────────────────────────────────────┐
│                    AUTÔMATO PRINCIPAL                        │
└─────────────────────────────────────────────────────────────┘

Estado Inicial: q0
Estados Finais: {qPalavraChave, qIdentificador, qNumero, qString, 
                 qOperador, qSimbolo, qFim}

Transições de q0:
├─ letra       → AFD_Identificador    → qPalavraChave ou qIdentificador
├─ dígito      → AFD_Numero          → qNumero
├─ "           → AFD_String          → qString
├─ $ ou $$     → AFD_Comentario      → q0 (ignora)
├─ + - * / %   → AFD_Operador        → qOperador
├─ < > = & ^   → AFD_Operador        → qOperador
├─ { } ( ) ; , → qSimbolo            → qSimbolo
├─ espaço      → q0                   (ignora)
└─ EOF         → qFim                → ACEITA
```

---

## 2. Autômato para Identificadores

Reconhece identificadores e palavras-chave da linguagem CF.

```
┌────────────────────────────────────────────────────┐
│         AFD para IDENTIFICADORES                   │
└────────────────────────────────────────────────────┘

         letra
    ┌─────────┐
    │         │
    ↓         │
   (q0) ─→ ((q1))
           ↑  │
           └──┘
           letra

Estados:
- q0: Estado inicial
- q1: Estado final (aceita)

Alfabeto: Σ = {A-Z, a-z, À-ÿ}  // Unicode letters

Transições:
  δ(q0, letra) = q1
  δ(q1, letra) = q1

Classificação após aceitação:
  Verifica se o lexema é palavra-chave:
  ┌─────────────────────────────────────────┐
  │ "inteiro"    → Token.Tipo.INTEIRO       │
  │ "logico"     → Token.Tipo.LOGICO        │
  │ "caractere"  → Token.Tipo.CARACTERE     │
  │ "enquanto"   → Token.Tipo.ENQUANTO      │
  │ "se"         → Token.Tipo.SE            │
  │ "senao"      → Token.Tipo.SENAO         │
  │ "para"       → Token.Tipo.PARA          │
  │ "imprimir"   → Token.Tipo.IMPRIMIR      │
  │ "verdade"    → Token.Tipo.VERDADE       │
  │ "mentira"    → Token.Tipo.MENTIRA       │
  │ Outros       → Token.Tipo.IDENTIFICADOR │
  └─────────────────────────────────────────┘

Nota: Case-insensitive (converte para lowercase)
```

---

## 3. Autômato para Números Inteiros

Reconhece literais numéricos inteiros.

```
┌────────────────────────────────────────────────────┐
│         AFD para NÚMEROS INTEIROS                  │
└────────────────────────────────────────────────────┘

         dígito
    ┌─────────┐
    │         │
    ↓         │
   (q0) ─→ ((q1))
           ↑  │
           └──┘
          dígito

Estados:
- q0: Estado inicial
- q1: Estado final (aceita)

Alfabeto: Σ = {0-9}

Transições:
  δ(q0, dígito) = q1
  δ(q1, dígito) = q1

Exemplos:
  "0"     → ACEITA  (NUMERO)
  "123"   → ACEITA  (NUMERO)
  "45678" → ACEITA  (NUMERO)

Nota: Não suporta números negativos (o '-' é operador unário)
      Não suporta números decimais/float
```

---

## 4. Autômato para Strings Literais

Reconhece strings delimitadas por aspas duplas.

```
┌────────────────────────────────────────────────────┐
│         AFD para STRINGS LITERAIS                  │
└────────────────────────────────────────────────────┘

         "        qualquer char (exceto ")        "
   (q0) ──→ (q1) ───────────────────────→ (q1) ─→ ((q2))
                  ↑                           │
                  └───────────────────────────┘
                    qualquer char (exceto ")

Estados:
- q0: Estado inicial
- q1: Lendo conteúdo da string
- q2: Estado final (aceita)

Alfabeto: Σ = {qualquer caractere Unicode}

Transições:
  δ(q0, ") = q1
  δ(q1, c) = q1,  ∀c ≠ "
  δ(q1, ") = q2

Expressão Regular: "[^"]*"

Exemplos:
  ""              → ACEITA  (STRING vazia)
  "Olá Mundo"     → ACEITA  (STRING)
  "Valor: "       → ACEITA  (STRING)
  "abc            → REJEITA (falta aspas de fechamento)

Nota: Não suporta caracteres de escape (\n, \t, etc.)
```

---

## 5. Autômato para Comentários

Reconhece e descarta comentários de linha única e múltiplas linhas.

### 5.1. Comentário de Linha Única ($)

```
┌────────────────────────────────────────────────────┐
│      AFD para COMENTÁRIOS DE LINHA ÚNICA           │
└────────────────────────────────────────────────────┘

         $      qualquer (exceto \n)        \n ou EOF
   (q0) ─→ (q1) ──────────────────→ (q1) ───→ ((q2))
                ↑                       │
                └───────────────────────┘
                  qualquer (exceto \n)

Estados:
- q0: Estado inicial
- q1: Dentro do comentário
- q2: Estado final (descarta)

Transições:
  δ(q0, $) = q1
  δ(q1, c) = q1,  ∀c ≠ \n
  δ(q1, \n) = q2
  δ(q1, EOF) = q2

Expressão Regular: \$.*$  (modo multiline)

Exemplo:
  "$ Este é um comentário\n"  → ACEITA e DESCARTA
```

### 5.2. Comentário de Múltiplas Linhas ($$)

```
┌────────────────────────────────────────────────────┐
│     AFD para COMENTÁRIOS MÚLTIPLAS LINHAS          │
└────────────────────────────────────────────────────┘

        $$      qualquer    $      $        $$
  (q0) ─→ (q1) ────→ (q1) ─→ (q2) ─→ (q3) ──→ ((q4))
                ↑           │
                │           │ qualquer (exceto $)
                └───────────┘

Estados:
- q0: Estado inicial
- q1: Dentro do comentário
- q2: Leu primeiro $ do fechamento
- q3: Leu segundo $ do fechamento
- q4: Estado final (descarta)

Alfabeto: Σ = {qualquer caractere, incluindo \n}

Transições:
  δ(q0, $$) = q1
  δ(q1, c) = q1,  ∀c ≠ $
  δ(q1, $) = q2
  δ(q2, $) = q4
  δ(q2, c) = q1,  ∀c ≠ $

Expressão Regular: \$\$.*?\$\$  (modo dotall)

Exemplo:
  "$$ Comentário
     multilinha $$"  → ACEITA e DESCARTA
```

---

## 6. Autômato para Operadores

Reconhece operadores aritméticos, lógicos e relacionais.

### 6.1. Operadores de 2 Caracteres

```
┌────────────────────────────────────────────────────┐
│      AFD para OPERADORES DE 2 CARACTERES           │
└────────────────────────────────────────────────────┘

Operadores: ** >= <= <> <-

         *
   (q0) ─────→ (q1)
    │           │
    │           * 
    │           │
    │           ↓
    │         ((q2))  [**]
    │
    │     <
    ├─────→ (q3)
    │         │ ├─ - → ((q4))  [<-]
    │         │ ├─ > → ((q5))  [<>]
    │         │ └─ = → ((q6))  [<=]
    │
    │     >
    └─────→ (q7)
              │
              = → ((q8))  [>=]

Transições:
  δ(q0, *) = q1
  δ(q1, *) = q2  → ACEITA (**  - potenciação)
  
  δ(q0, <) = q3
  δ(q3, -) = q4  → ACEITA (<-  - atribuição)
  δ(q3, >) = q5  → ACEITA (<>  - diferente)
  δ(q3, =) = q6  → ACEITA (<=  - menor ou igual)
  
  δ(q0, >) = q7
  δ(q7, =) = q8  → ACEITA (>=  - maior ou igual)
```

### 6.2. Operadores de 1 Caractere

```
┌────────────────────────────────────────────────────┐
│      AFD para OPERADORES DE 1 CARACTERE            │
└────────────────────────────────────────────────────┘

Aritméticos: + - * / %
Lógicos/Relacionais: = > < & ^

         + → ((q1))  [adição]
         - → ((q2))  [subtração/negação]
   (q0)─ * → ((q3))  [multiplicação]
         / → ((q4))  [divisão]
         % → ((q5))  [módulo]
         = → ((q6))  [igual]
         > → ((q7))  [maior que]
         < → ((q8))  [menor que]
         & → ((q9))  [AND lógico]
         ^ → ((q10)) [OR lógico]

Todos são estados finais de aceitação.
```

---

## 7. Diagrama de Transição Completo

Visão geral do analisador léxico completo.

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                    DIAGRAMA DE ESTADOS COMPLETO DO LEXER                     │
└──────────────────────────────────────────────────────────────────────────────┘

                             ┌─────────────┐
                             │  INÍCIO (q0)│
                             └──────┬──────┘
                                    │
         ┌──────────────────────────┼──────────────────────────────┐
         │                          │                              │
         │ letra                    │ dígito                       │ "
         ↓                          ↓                              ↓
    ┌────────────┐          ┌──────────────┐              ┌──────────────┐
    │IDENTIFICADOR│          │    NÚMERO    │              │    STRING    │
    │   (AFD 2)  │          │   (AFD 3)    │              │   (AFD 4)    │
    └─────┬──────┘          └──────┬───────┘              └──────┬───────┘
          │                        │                              │
          ↓                        ↓                              ↓
    Palavra-Chave?          Token.NUMERO                   Token.STRING
    Sim → Token.{tipo}
    Não → Token.IDENTIFICADOR

         │                          │                              │
         │ $ ou $$                  │ Operador                     │ Símbolo
         ↓                          ↓                              ↓
    ┌────────────┐          ┌──────────────┐              ┌──────────────┐
    │ COMENTÁRIO │          │  OPERADOR    │              │   SÍMBOLO    │
    │  (AFD 5)   │          │   (AFD 6)    │              │  { } ( ) ; , │
    └─────┬──────┘          └──────┬───────┘              └──────┬───────┘
          │                        │                              │
          ↓                        ↓                              ↓
       DESCARTA              Token.OPERADOR_*              Token.{SIMBOLO}
                             ou Token.ATRIBUICAO


                             ┌─────────────┐
                             │ Espaço/Tab  │──→ q0 (ignora)
                             └─────────────┘

                             ┌─────────────┐
                             │     EOF     │──→ ((qFim))
                             └─────────────┘
```

---

## 8. Tabela de Transições Consolidada

Tabela resumida de todas as transições do estado inicial (q0).

| Entrada       | Autômato Ativado | Estado Destino | Token Gerado           |
|---------------|------------------|----------------|------------------------|
| A-Z, a-z, À-ÿ | AFD_Ident        | q_id          | PALAVRA_CHAVE ou IDENT |
| 0-9           | AFD_Numero       | q_num         | NUMERO                 |
| "             | AFD_String       | q_str         | STRING                 |
| $             | AFD_Coment       | q_com         | (descartado)           |
| $$            | AFD_Coment       | q_com         | (descartado)           |
| **            | AFD_Op2          | q_op          | OPERADOR_ARIT (**)     |
| >=            | AFD_Op2          | q_op          | OPERADOR_LOGICO        |
| <=            | AFD_Op2          | q_op          | OPERADOR_LOGICO        |
| <>            | AFD_Op2          | q_op          | OPERADOR_LOGICO        |
| <-            | AFD_Op2          | q_op          | ATRIBUICAO             |
| +, -, *, /, % | AFD_Op1          | q_op          | OPERADOR_ARIT          |
| =, >, <, &, ^ | AFD_Op1          | q_op          | OPERADOR_LOGICO        |
| {             | -                | q_sim         | ABRE_CHAVE             |
| }             | -                | q_sim         | FECHA_CHAVE            |
| (             | -                | q_sim         | ABRE_PAREN             |
| )             | -                | q_sim         | FECHA_PAREN            |
| ;             | -                | q_sim         | PONTO_VIRGULA          |
| ,             | -                | q_sim         | VIRGULA                |
| espaço, \t, \n| -                | q0            | (ignorado)             |
| EOF           | -                | q_fim         | FIM                    |

---

## 9. Expressões Regulares Equivalentes

Para cada tipo de token reconhecido:

```regex
┌─────────────────────────────────────────────────────────────┐
│              EXPRESSÕES REGULARES DOS TOKENS                │
└─────────────────────────────────────────────────────────────┘

Identificadores:       \p{L}+
                       (uma ou mais letras Unicode)

Números:               \d+
                       (um ou mais dígitos)

Strings:               "[^"]*"
                       (aspas, qualquer char exceto aspas, aspas)

Comentário linha:      \$.*$
                       ($ seguido de qualquer até fim de linha)

Comentário bloco:      \$\$.*?\$\$
                       (non-greedy match entre $$)

Operador 2 chars:      \*\*|>=|<=|<>|<-

Operador aritmético:   [+\-*/%]

Operador lógico:       [=><&\^]

Símbolos:              [{};,()]

Palavras-chave (case-insensitive):
  Inteiro|Logico|Lógico|Caractere|Enquanto|
  Senão|Senao|Para|Imprimir|Verdade|Mentira|Se
```

---

## 10. Autômato Completo com Prioridades

Ordem de verificação (crítica para reconhecimento correto):

```
┌────────────────────────────────────────────────────────────────┐
│            ORDEM DE PRIORIDADE DO LEXER                        │
└────────────────────────────────────────────────────────────────┘

Prioridade 1: PALAVRAS-CHAVE (mais específico)
              Inteiro, Logico, Caractere, Enquanto, Se, Senao,
              Para, Imprimir, Verdade, Mentira

Prioridade 2: OPERADORES DE 2 CARACTERES
              **, >=, <=, <>, <-
              (verifica antes dos de 1 char para evitar conflito)

Prioridade 3: NÚMEROS
              \d+

Prioridade 4: IDENTIFICADORES
              \p{L}+
              (após palavras-chave para não capturar reservadas)

Prioridade 5: OPERADORES ARITMÉTICOS
              +, -, *, /, %

Prioridade 6: OPERADORES LÓGICOS E RELACIONAIS
              =, >, <, &, ^

Prioridade 7: SÍMBOLOS
              {, }, (, ), ;, ,

Prioridade 8: STRINGS
              "[^"]*"

┌────────────────────────────────────────────────────────────────┐
│ NOTA: Comentários são removidos ANTES da tokenização          │
│       usando regex no pré-processamento.                       │
└────────────────────────────────────────────────────────────────┘
```

---

## 11. Exemplos de Execução

### Exemplo 1: Declaração Simples

**Entrada:**
```cf
Inteiro x <- 10;
```

**Sequência de Estados:**

```
┌──────────────────────────────────────────────────────────────┐
Posição 0: 'I'
  q0 --[letra]--> AFD_Ident
  "Inteiro" reconhecido
  → Token(INTEIRO, "Inteiro")

Posição 8: ' '
  q0 (ignora espaço)

Posição 9: 'x'
  q0 --[letra]--> AFD_Ident
  "x" reconhecido
  Não é palavra-chave
  → Token(IDENTIFICADOR, "x")

Posição 11: ' '
  q0 (ignora espaço)

Posição 12: '<'
  q0 --[<]--> q_op1
  Posição 13: '-'
  q_op1 --[-]--> q_op2
  "<-" reconhecido
  → Token(ATRIBUICAO, "<-")

Posição 15: ' '
  q0 (ignora espaço)

Posição 16: '1'
  q0 --[dígito]--> AFD_Numero
  "10" reconhecido
  → Token(NUMERO, "10")

Posição 18: ';'
  q0 --[;]--> q_sim
  → Token(PONTO_VIRGULA, ";")

Posição 19: EOF
  → Token(FIM, "")
└──────────────────────────────────────────────────────────────┘
```

### Exemplo 2: Expressão com Operadores

**Entrada:**
```cf
resultado <- x**2 + y;
```

**Tokens Gerados:**
```
1. Token(IDENTIFICADOR, "resultado")
2. Token(ATRIBUICAO, "<-")
3. Token(IDENTIFICADOR, "x")
4. Token(OPERADOR_ARIT, "**")      ← Operador de 2 chars reconhecido
5. Token(NUMERO, "2")
6. Token(OPERADOR_ARIT, "+")
7. Token(IDENTIFICADOR, "y")
8. Token(PONTO_VIRGULA, ";")
9. Token(FIM, "")
```

### Exemplo 3: Comentários

**Entrada:**
```cf
$ Comentário de linha
Inteiro a; $$ comentário
multilinha $$ Inteiro b;
```

**Pré-processamento (remoção de comentários):**
```cf
Inteiro a;  Inteiro b;
```

**Tokens Gerados:**
```
1. Token(INTEIRO, "Inteiro")
2. Token(IDENTIFICADOR, "a")
3. Token(PONTO_VIRGULA, ";")
4. Token(INTEIRO, "Inteiro")
5. Token(IDENTIFICADOR, "b")
6. Token(PONTO_VIRGULA, ";")
7. Token(FIM, "")
```

---

## 12. Diagramas de Estado Individuais (Formato Textual)

### 12.1. Palavra-Chave "Inteiro"

```
Estado: q0 → q1 → q2 → q3 → q4 → q5 → q6 → q7 → ((q8))
Entrada: I  → n  → t  → e  → i  → r  → o  → fim_palavra
```

### 12.2. Operador de Atribuição "<-"

```
Estado: q0 → q1 → ((q2))
Entrada: <  → -  → ACEITA
```

### 12.3. Número "123"

```
Estado: q0 → q1 → q1 → ((q1))
Entrada: 1  → 2  → 3  → fim_número
         ↑___________|
         (loop em q1 para cada dígito)
```

---

## 13. Representação Formal

### Autômato Finito Determinístico (AFD) do Lexer

**Definição Formal:**

```
M = (Q, Σ, δ, q0, F)

Onde:
  Q = {q0, q_id, q_num, q_str, q_op, q_sim, q_com, qFim}
      (conjunto de estados)
  
  Σ = {A-Z, a-z, 0-9, ", $, +, -, *, /, %, =, <, >, &, ^, {, }, (, ), ;, ,}
      (alfabeto de entrada)
  
  δ : Q × Σ → Q
      (função de transição - veja Tabela de Transições acima)
  
  q0 = estado inicial
  
  F = {q_id, q_num, q_str, q_op, q_sim, qFim}
      (estados finais de aceitação)
```

---

## 14. Notas de Implementação

1. **Regex vs AFD:** A implementação atual usa expressões regulares (Pattern/Matcher do Java) em vez de autômatos codificados manualmente. Isso é equivalente a AFDs gerados automaticamente.

2. **Lookahead:** Não há necessidade de lookahead explícito porque o regex matching é greedy por padrão.

3. **Backtracking:** O motor de regex do Java implementa backtracking automaticamente.

4. **Performance:** A ordem de prioridade no regex é crucial - operadores de 2 caracteres DEVEM vir antes dos de 1 caractere.

5. **Unicode:** Suporta identificadores com caracteres acentuados usando `\p{L}`.

---

## 📚 Referências

- **Introduction to Automata Theory, Languages, and Computation** - Hopcroft, Motwani, Ullman
- **Compilers: Principles, Techniques, and Tools** - Aho, Lam, Sethi, Ullman (Dragon Book)
- **Java Pattern API Documentation** - Oracle

---

---

## 15. Autômatos em Notação de Diagrama de Estados

### 15.1. AFD Completo para Identificadores e Palavras-Chave

```
                    ┌───────────────────────────────────────┐
                    │   RECONHECIMENTO DE IDENTIFICADORES   │
                    └───────────────────────────────────────┘

                           letra
                    ┌──────────────┐
                    │              ↓
    [INÍCIO] ──→  (q0) ────→ ((qIdent))
                            ↑       │
                            └───────┘
                             letra

    Estado Final qIdent → Pós-processamento:
    
    ┌─────────────────────────────────────────────────────┐
    │  lexema.toLowerCase() em {                          │
    │    "inteiro"   → INTEIRO                            │
    │    "logico"    → LOGICO                             │
    │    "caractere" → CARACTERE                          │
    │    "enquanto"  → ENQUANTO                           │
    │    "se"        → SE                                 │
    │    "senao"     → SENAO                              │
    │    "para"      → PARA                               │
    │    "imprimir"  → IMPRIMIR                           │
    │    "verdade"   → VERDADE                            │
    │    "mentira"   → MENTIRA                            │
    │  }                                                  │
    │  caso contrário → IDENTIFICADOR                     │
    └─────────────────────────────────────────────────────┘
```

### 15.2. AFD para Reconhecimento de Operadores Compostos

```
    ┌───────────────────────────────────────────────────────┐
    │      RECONHECIMENTO DE OPERADORES MULTI-CHAR          │
    └───────────────────────────────────────────────────────┘

                        *
    [q0] ────────────→ (q1)
      │                 │
      │                 │ *
      │                 ↓
      │               ((q_potencia))  [**]
      │
      │       <
      ├─────────────→ (q2)
      │                 │ 
      │                 ├── - ──→ ((q_atrib))    [<-]
      │                 ├── > ──→ ((q_dif))      [<>]
      │                 ├── = ──→ ((q_menorIg))  [<=]
      │                 └── outro → volta q0 (reconhece '<')
      │
      │       >
      └─────────────→ (q3)
                        │
                        ├── = ──→ ((q_maiorIg))  [>=]
                        └── outro → volta q0 (reconhece '>')

    Estratégia: Lookahead de 1 caractere
```

### 15.3. AFD para Strings com Tratamento de Erro

```
    ┌───────────────────────────────────────────────────────┐
    │         RECONHECIMENTO DE STRINGS LITERAIS            │
    └───────────────────────────────────────────────────────┘

                "                 c (c≠")              "
    [q0] ─────────→ (q1) ──────────────────→ (q1) ────────→ ((q2))
                     │         ↑       │                      ↑
                     │         └───────┘                      │
                     │        c (c≠")                         │
                     │                                        │
                     │                  EOF                   │
                     └────────────────────────────────────────┘
                                        │
                                        ↓
                                   [qErro_StringNaoFechada]

    Aceitação: q2 (string válida)
    Erro: qErro_StringNaoFechada (EOF antes de fechar aspas)
```

### 15.4. AFD para Números com Possível Extensão Futura

```
    ┌───────────────────────────────────────────────────────┐
    │       RECONHECIMENTO DE NÚMEROS (Atual: apenas INT)   │
    └───────────────────────────────────────────────────────┘

                dígito
         ┌──────────────┐
         │              ↓
    [q0] ├───────→ ((qInt))
         │          ↑   │
         │          └───┘
         │         dígito
         │
         │  Extensão futura para float:
         │          .
         │         ─────→ (qPonto) ─dígito─→ ((qFloat))
         │                            ↑  │
         │                            └──┘
         │                           dígito

    Estado Atual: Apenas qInt é implementado
```

---

## 16. Gramática Regular Equivalente

Os autômatos do lexer podem ser expressos como gramáticas regulares:

```
┌──────────────────────────────────────────────────────────┐
│            GRAMÁTICA REGULAR DO LEXER CF                 │
└──────────────────────────────────────────────────────────┘

<programa>      ::= <token>* EOF

<token>         ::= <palavra-chave>
                  | <identificador>
                  | <numero>
                  | <string>
                  | <operador>
                  | <simbolo>

<palavra-chave> ::= "Inteiro" | "Logico" | "Lógico" 
                  | "Caractere" | "Enquanto" | "Se" 
                  | "Senão" | "Senao" | "Para" 
                  | "Imprimir" | "Verdade" | "Mentira"

<identificador> ::= <letra> <letra>*

<numero>        ::= <digito> <digito>*

<string>        ::= '"' <char-nao-aspa>* '"'

<operador>      ::= <op-arit> | <op-logico> | <atribuicao>

<op-arit>       ::= '+' | '-' | '*' | '/' | '%' | "**"

<op-logico>     ::= '=' | '>' | '<' | '&' | '^' 
                  | ">=" | "<=" | "<>"

<atribuicao>    ::= "<-"

<simbolo>       ::= '{' | '}' | '(' | ')' | ';' | ','

<letra>         ::= 'A'..'Z' | 'a'..'z' | caracteres Unicode

<digito>        ::= '0'..'9'

<comentario>    ::= '$' <qualquer-exceto-newline>* '\n'
                  | "$$" <qualquer>* "$$"
```

---

## 17. Tabela de Transições Detalhada (Formato Matemático)

### Função de Transição δ para AFD de Identificadores

```
┌────────────────────────────────────────┐
│  δ_ident : Q × Σ → Q                   │
├────────────────────────────────────────┤
│  Estado │ Entrada │ Próximo Estado     │
├─────────┼─────────┼────────────────────┤
│   q0    │  letra  │      q1            │
│   q0    │  outro  │      ∅ (rejeita)   │
│   q1    │  letra  │      q1            │
│   q1    │  outro  │   ACEITA (q1)      │
└────────────────────────────────────────┘
```

### Função de Transição δ para AFD de Números

```
┌────────────────────────────────────────┐
│  δ_num : Q × Σ → Q                     │
├────────────────────────────────────────┤
│  Estado │ Entrada │ Próximo Estado     │
├─────────┼─────────┼────────────────────┤
│   q0    │ dígito  │      q1            │
│   q0    │  outro  │      ∅ (rejeita)   │
│   q1    │ dígito  │      q1            │
│   q1    │  outro  │   ACEITA (q1)      │
└────────────────────────────────────────┘
```

### Função de Transição δ para AFD de Operador '<-'

```
┌────────────────────────────────────────┐
│  δ_atrib : Q × Σ → Q                   │
├────────────────────────────────────────┤
│  Estado │ Entrada │ Próximo Estado     │
├─────────┼─────────┼────────────────────┤
│   q0    │   '<'   │      q1            │
│   q0    │  outro  │      ∅ (rejeita)   │
│   q1    │   '-'   │   ACEITA (q2)      │
│   q1    │  outro  │      ∅ (rejeita)   │
└────────────────────────────────────────┘
```

---

## 18. Análise de Complexidade

### Complexidade Temporal do Lexer

```
┌───────────────────────────────────────────────────────────┐
│           ANÁLISE DE COMPLEXIDADE TEMPORAL                │
└───────────────────────────────────────────────────────────┘

Operação: Tokenização completa de um programa

Entrada: String de tamanho n

Pré-processamento (remoção de comentários):
  - Regex para comentários de linha:    O(n)
  - Regex para comentários de bloco:    O(n)
  Total pré-processamento:              O(n)

Tokenização:
  - Pattern matching com regex:         O(n)
  - Cada token classificado em O(1):    O(k) onde k = número de tokens
  - k ≤ n (no pior caso, cada char é um token)
  Total tokenização:                    O(n)

Complexidade Total:                     O(n)

Espaço:
  - Lista de tokens:                    O(k) ≈ O(n)
  - String processada:                  O(n)
  Total:                                O(n)
```

---

## 19. Casos de Teste para Autômatos

### 19.1. Testes para Identificadores

```
┌──────────────────────────────────────────────────────────┐
│              CASOS DE TESTE - IDENTIFICADORES            │
└──────────────────────────────────────────────────────────┘

[✓] "x"           → ACEITA (IDENTIFICADOR)
[✓] "abc"         → ACEITA (IDENTIFICADOR)
[✓] "variável"    → ACEITA (IDENTIFICADOR com acento)
[✓] "Inteiro"     → ACEITA (INTEIRO - palavra-chave)
[✓] "InTeIrO"     → ACEITA (INTEIRO - case insensitive)
[✓] "x123"        → REJEITA (tem dígitos - não permitido)
[✓] "123x"        → REJEITA (começa com dígito)
[✓] "_var"        → REJEITA (underscore não é letra)
[✓] "camelCase"   → ACEITA (IDENTIFICADOR)
[✓] "se"          → ACEITA (SE - palavra-chave)
```

### 19.2. Testes para Números

```
┌──────────────────────────────────────────────────────────┐
│                 CASOS DE TESTE - NÚMEROS                 │
└──────────────────────────────────────────────────────────┘

[✓] "0"           → ACEITA (NUMERO)
[✓] "123"         → ACEITA (NUMERO)
[✓] "999999"      → ACEITA (NUMERO)
[✗] "-5"          → REJEITA (negativo não é reconhecido aqui)
[✗] "3.14"        → REJEITA (decimal não suportado)
[✗] "1e10"        → REJEITA (notação científica não suportada)
[✗] "0xFF"        → REJEITA (hexadecimal não suportado)
[✓] "007"         → ACEITA (NUMERO - zeros à esquerda ok)
```

### 19.3. Testes para Strings

```
┌──────────────────────────────────────────────────────────┐
│                 CASOS DE TESTE - STRINGS                 │
└──────────────────────────────────────────────────────────┘

[✓] ""            → ACEITA (STRING vazia)
[✓] "Hello"       → ACEITA (STRING)
[✓] "Olá Mundo!"  → ACEITA (STRING com acento)
[✓] "123"         → ACEITA (STRING com números)
[✗] "abc          → REJEITA (sem aspas de fechamento)
[✗] abc"          → REJEITA (sem aspas de abertura)
[✓] "a"b"         → PROBLEMA: captura apenas "a"
[✗] "linha1\nlinha2" → Não suporta escape sequences
```

### 19.4. Testes para Operadores

```
┌──────────────────────────────────────────────────────────┐
│               CASOS DE TESTE - OPERADORES                │
└──────────────────────────────────────────────────────────┘

Operadores de 2 caracteres:
[✓] "**"          → ACEITA (OPERADOR_ARIT - potência)
[✓] ">="          → ACEITA (OPERADOR_LOGICO)
[✓] "<="          → ACEITA (OPERADOR_LOGICO)
[✓] "<>"          → ACEITA (OPERADOR_LOGICO)
[✓] "<-"          → ACEITA (ATRIBUICAO)

Operadores de 1 caractere:
[✓] "+"           → ACEITA (OPERADOR_ARIT)
[✓] "-"           → ACEITA (OPERADOR_ARIT)
[✓] "*"           → ACEITA (OPERADOR_ARIT)
[✓] "/"           → ACEITA (OPERADOR_ARIT)
[✓] "%"           → ACEITA (OPERADOR_ARIT)
[✓] "="           → ACEITA (OPERADOR_LOGICO)
[✓] ">"           → ACEITA (OPERADOR_LOGICO)
[✓] "<"           → ACEITA (OPERADOR_LOGICO)
[✓] "&"           → ACEITA (OPERADOR_LOGICO - AND)
[✓] "^"           → ACEITA (OPERADOR_LOGICO - OR)

Sequências complexas:
[✓] "**2"         → ACEITA "**" depois "2"
[✓] "<=>"         → ACEITA "<=" depois ">"
[✓] "<--"         → ACEITA "<-" depois "-"
```

### 19.5. Testes para Comentários

```
┌──────────────────────────────────────────────────────────┐
│              CASOS DE TESTE - COMENTÁRIOS                │
└──────────────────────────────────────────────────────────┘

Comentário de linha:
[✓] "$ comentário\n"              → REMOVIDO
[✓] "$ até o fim da linha"        → REMOVIDO
[✓] "codigo $ comentário\n"       → "codigo "

Comentário de bloco:
[✓] "$$ comentário $$"            → REMOVIDO
[✓] "$$ linha1\nlinha2 $$"        → REMOVIDO
[✓] "$$ com $ dentro $$"          → REMOVIDO
[✓] "a $$ x $$ b"                 → "a  b"
[✗] "$$ sem fechar"               → Pode causar problema

Aninhamento:
[?] "$$ $$ aninhado $$ $$"        → Não suporta aninhamento
```

---

## 20. Limitações e Melhorias Futuras

```
┌──────────────────────────────────────────────────────────┐
│              LIMITAÇÕES ATUAIS DO LEXER                  │
└──────────────────────────────────────────────────────────┘

❌ Sem suporte a:
   • Números decimais/float (3.14)
   • Números em notação científica (1e10)
   • Números hexadecimais/octais (0xFF, 0o77)
   • Caracteres de escape em strings (\n, \t, \")
   • Strings multilinha
   • Identificadores com dígitos ou underscore
   • Comentários aninhados

❌ Tratamento de erros:
   • String sem fechamento não gera erro explícito
   • Caracteres inválidos causam RuntimeException genérica
   • Sem informação de linha/coluna nos tokens

┌──────────────────────────────────────────────────────────┐
│                  MELHORIAS FUTURAS                       │
└──────────────────────────────────────────────────────────┘

✓ Adicionar:
   • Números de ponto flutuante
   • Informação de posição (linha, coluna) nos tokens
   • Mensagens de erro mais descritivas
   • Suporte a escape sequences em strings
   • Identificadores com padrão [a-zA-Z_][a-zA-Z0-9_]*
   • Modo de recuperação de erros
   • Diretivas de compilação/preprocessador

✓ Otimizações:
   • Cache de tokens para expressões comuns
   • Análise incremental (re-lexer apenas trechos modificados)
   • Paralelização para arquivos grandes
```

---

## 👤 Autor

Documentação criada para o projeto de Compiladores - Compila Fofo (CF).

**Instituição:** Curso de Compiladores  
**Projeto:** Compilador para Linguagem CF (Compila Fofo)  
**Data:** 2025-11-29  
**Versão:** 1.0

---

## 📚 Referências

- **Introduction to Automata Theory, Languages, and Computation** - Hopcroft, Motwani, Ullman
- **Compilers: Principles, Techniques, and Tools** - Aho, Lam, Sethi, Ullman (Dragon Book)
- **Java Pattern API Documentation** - Oracle

---

*Fim da documentação de autômatos*


