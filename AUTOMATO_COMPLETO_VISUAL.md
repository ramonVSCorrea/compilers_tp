# Autômato Finito Determinístico Completo - Compilador CF

## 🎨 Desenho Completo do Autômato Principal

Este documento apresenta o desenho visual completo do autômato que reconhece todos os tokens da linguagem CF.

---

## 📊 AUTÔMATO PRINCIPAL - VISÃO GERAL

```
╔═══════════════════════════════════════════════════════════════════════════════════╗
║                        AUTÔMATO FINITO DETERMINÍSTICO COMPLETO                     ║
║                              COMPILADOR COMPILA FOFO (CF)                          ║
╚═══════════════════════════════════════════════════════════════════════════════════╝

                                    ┌──────────────┐
                                    │   INÍCIO     │
                                    │     (q0)     │
                                    └──────┬───────┘
                                           │
         ┌─────────────────────────────────┼─────────────────────────────────┐
         │                                 │                                 │
         │ [A-Z][a-z]                      │ [0-9]                          │ ["]
         │                                 │                                 │
         ↓                                 ↓                                 ↓
    ┌─────────┐                       ┌─────────┐                      ┌─────────┐
    │   q_id  │                       │  q_num  │                      │  q_str  │
    │ IDENT   │                       │ NUMERO  │                      │ STRING  │
    └────┬────┘                       └────┬────┘                      └────┬────┘
         │                                 │                                 │
         │ [A-Z][a-z]                      │ [0-9]                          │ [^"]
         │ ↺                               │ ↺                              │ ↺
         │                                 │                                 │
         │ [outros]                        │ [outros]                        │ ["]
         │                                 │                                 │
         ↓                                 ↓                                 ↓
    ┌─────────┐                       ┌─────────┐                      ┌─────────┐
    │CLASSIF. │                       │ TOKEN   │                      │ TOKEN   │
    │Palavra? │                       │ NUMERO  │                      │ STRING  │
    └────┬────┘                       └────┬────┘                      └────┬────┘
         │                                 │                                 │
    ┌────┴────┐                           │                                 │
    │ Sim│Não │                           │                                 │
    ↓    ↓    ↓                           │                                 │
  [KW] [IDENT]                            │                                 │
    │    │                                │                                 │
    └────┴────────────────────────────────┴─────────────────────────────────┘
                                          │
                                          ↓
                                     Adiciona Token
                                          │
                                          ↓
                              Volta para q0 (próximo char)


         ┌─────────────────────────────────┼─────────────────────────────────┐
         │                                 │                                 │
         │ [*]                             │ [<]                            │ [>]
         │                                 │                                 │
         ↓                                 ↓                                 ↓
    ┌─────────┐                       ┌─────────┐                      ┌─────────┐
    │  q_op1  │                       │  q_op2  │                      │  q_op3  │
    │   OP*   │                       │   OP<   │                      │   OP>   │
    └────┬────┘                       └────┬────┘                      └────┬────┘
         │                                 │                                 │
         │ [*]                             │ [-][>][=]                       │ [=]
         ↓                                 ↓                                 ↓
    ┌─────────┐                       ┌─────────┐                      ┌─────────┐
    │  q_pot  │                       │OP2CHARS │                      │  q_gte  │
    │ TOKEN** │                       │ <-<><== │                      │ TOKEN>= │
    └────┬────┘                       └────┬────┘                      └────┬────┘
         │                                 │                                 │
         └─────────────────────────────────┴─────────────────────────────────┘
                                          │
                                          ↓
                                     Adiciona Token
                                          │
                                          ↓
                              Volta para q0 (próximo char)


         ┌─────────────────────────────────────────────────────────────────┐
         │                                 │                                │
         │ [+][-][/][%][=][&][^]          │ [{][}][(][)][;][,]            │
         │                                 │                                │
         ↓                                 ↓                                ↓
    ┌─────────┐                       ┌─────────┐                      
    │  q_op   │                       │  q_sym  │                      
    │ OPERADOR│                       │ SIMBOLO │                      
    └────┬────┘                       └────┬────┘                      
         │                                 │                                
         │ TOKEN                           │ TOKEN                         
         │ OP_ARIT                         │ ABRE_CHAVE, etc               
         │ OP_LOG                          │                                
         │                                 │                                
         └─────────────────────────────────┴────────────────────────────────┘
                                          │
                                          ↓
                                     Adiciona Token
                                          │
                                          ↓
                              Volta para q0 (próximo char)


         ┌─────────────────────────────────────────────────────────────────┐
         │                                 │                                │
         │ [ ][\t][\n]                     │ EOF                           │
         │                                 │                                │
         ↓                                 ↓                                ↓
    ┌─────────┐                       ┌─────────┐                      
    │   q0    │◄──────────────────────│  q_fim  │                      
    │ IGNORA  │                       │ TOKEN   │                      
    └─────────┘                       │   FIM   │                      
                                      └─────────┘                      
                                          │
                                          ↓
                                    ANÁLISE COMPLETA
                                          │
                                          ↓
                                   Lista de Tokens
                                          │
                                          ↓
                                       PARSER

```

---

## 🔍 AUTÔMATO DETALHADO - IDENTIFICADORES E PALAVRAS-CHAVE

```
╔════════════════════════════════════════════════════════════════════════╗
║            AFD PARA IDENTIFICADORES E PALAVRAS-CHAVE                   ║
╚════════════════════════════════════════════════════════════════════════╝

                              [A-Z, a-z, À-ÿ]
                         ┌─────────────────────┐
                         │                     │
                         ↓                     │
    ┌─────┐         ┌─────────┐               │
    │ q0  │────────→│   q1    │◄──────────────┘
    │     │  letra  │ (aceita)│     letra
    └─────┘         └────┬────┘
                         │
                         │ [outros caracteres]
                         │
                         ↓
                    ┌─────────────────────────────────────┐
                    │   CLASSIFICAÇÃO DO LEXEMA           │
                    │   (case-insensitive)                │
                    └────────────────┬────────────────────┘
                                     │
                 ┌───────────────────┼───────────────────┐
                 │                   │                   │
                 ↓                   ↓                   ↓
          ┌──────────┐        ┌──────────┐      ┌──────────┐
          │"inteiro" │        │"logico"  │      │"caractere"│
          │          │        │"lógico"  │      │          │
          └─────┬────┘        └─────┬────┘      └─────┬────┘
                │                   │                 │
                ↓                   ↓                 ↓
            [INTEIRO]           [LOGICO]         [CARACTERE]

                 ↓                   ↓                   ↓
          ┌──────────┐        ┌──────────┐      ┌──────────┐
          │"enquanto"│        │  "se"    │      │ "senao"  │
          │          │        │          │      │ "senão"  │
          └─────┬────┘        └─────┬────┘      └─────┬────┘
                │                   │                 │
                ↓                   ↓                 ↓
            [ENQUANTO]            [SE]             [SENAO]

                 ↓                   ↓                   ↓
          ┌──────────┐        ┌──────────┐      ┌──────────┐
          │  "para"  │        │"imprimir"│      │ "verdade"│
          │          │        │          │      │          │
          └─────┬────┘        └─────┬────┘      └─────┬────┘
                │                   │                 │
                ↓                   ↓                 ↓
              [PARA]            [IMPRIMIR]        [VERDADE]

                 ↓                   ↓
          ┌──────────┐        ┌──────────────────┐
          │ "mentira"│        │  Outros lexemas  │
          │          │        │                  │
          └─────┬────┘        └─────┬────────────┘
                │                   │
                ↓                   ↓
            [MENTIRA]         [IDENTIFICADOR]

                 │                   │
                 └───────────┬───────┘
                             │
                             ↓
                      [Retorna Token]
                             │
                             ↓
                      [Volta para q0]
```

---

## 🔢 AUTÔMATO DETALHADO - NÚMEROS INTEIROS

```
╔════════════════════════════════════════════════════════════════════════╗
║                    AFD PARA NÚMEROS INTEIROS                           ║
╚════════════════════════════════════════════════════════════════════════╝

                              [0-9]
                         ┌─────────────┐
                         │             │
                         ↓             │
    ┌─────┐         ┌─────────┐       │
    │ q0  │────────→│   q1    │◄──────┘
    │     │ dígito  │ (aceita)│  dígito
    └─────┘         └────┬────┘
     INÍCIO              │
                         │ [outros]
                         │
                         ↓
                   ┌──────────┐
                   │  TOKEN   │
                   │  NUMERO  │
                   └─────┬────┘
                         │
                         ↓
                   [Volta q0]

    EXEMPLOS:
    
    "0"     → q0 ──[0]──→ q1 ──[outros]──→ TOKEN(NUMERO, "0")
    
    "123"   → q0 ──[1]──→ q1 ──[2]──→ q1 ──[3]──→ q1 ──[outros]──→ TOKEN(NUMERO, "123")
    
    "4567"  → q0 ──[4]──→ q1 ──[5]──→ q1 ──[6]──→ q1 ──[7]──→ q1 ──[outros]──→ TOKEN(NUMERO, "4567")
```

---

## 📝 AUTÔMATO DETALHADO - STRINGS LITERAIS

```
╔════════════════════════════════════════════════════════════════════════╗
║                    AFD PARA STRINGS LITERAIS                           ║
╚════════════════════════════════════════════════════════════════════════╝

                 ["]              [qualquer ≠ "]           ["]
    ┌─────┐  abertura   ┌─────────┐  conteúdo   ┌─────────┐  fechamento  ┌─────────┐
    │ q0  │────────────→│   q1    │────────────→│   q1    │─────────────→│   q2    │
    │     │             │ lendo   │◄────────────│ lendo   │              │ (aceita)│
    └─────┘             └─────────┘  [qualquer  └─────────┘              └────┬────┘
     INÍCIO                 │           ≠ "]         ↑                         │
                            │                        │                         │
                            │          ┌─────────────┘                         │
                            │          │                                       │
                            │ EOF      │                                       │
                            │          │                                       │
                            ↓          │                                       ↓
                      ┌──────────┐    │                                 ┌──────────┐
                      │  ERRO!   │    │                                 │  TOKEN   │
                      │  String  │    │                                 │  STRING  │
                      │não fech. │    │                                 └─────┬────┘
                      └──────────┘    │                                       │
                                      │                                       ↓
                                      └──────────────────────────────→ [Volta q0]

    EXEMPLOS:
    
    ""           → q0 ──["]──→ q1 ──["]──→ q2 → TOKEN(STRING, "")
    
    "Hello"      → q0 ──["]──→ q1 ──[H]──→ q1 ──[e]──→ q1 ──[l]──→ q1 
                       ──[l]──→ q1 ──[o]──→ q1 ──["]──→ q2 → TOKEN(STRING, "Hello")
    
    "Olá Mundo"  → q0 ──["]──→ q1 ──[O]──→ q1 ──[l]──→ q1 ──[á]──→ q1 
                       ──[ ]──→ q1 ──[M]──→ q1 ──[u]──→ q1 ──[n]──→ q1 
                       ──[d]──→ q1 ──[o]──→ q1 ──["]──→ q2 → TOKEN(STRING, "Olá Mundo")
```

---

## ⚡ AUTÔMATO DETALHADO - OPERADORES

### Operadores de 2 Caracteres

```
╔════════════════════════════════════════════════════════════════════════╗
║              AFD PARA OPERADORES DE 2 CARACTERES                       ║
╚════════════════════════════════════════════════════════════════════════╝

                                [*]
    ┌─────┐                  ┌─────────┐            [*]           ┌─────────┐
    │ q0  │─────────────────→│   q1    │───────────────────────→  │   q2    │
    │     │                  │  leu *  │                          │ (aceita)│
    └──┬──┘                  └────┬────┘                          └────┬────┘
       │                          │                                    │
       │                          │ [outros]                           │
       │                          ↓                                    ↓
       │                    ┌──────────┐                         ┌──────────┐
       │                    │  TOKEN   │                         │  TOKEN   │
       │                    │OPERADOR_*│                         │   **     │
       │                    └──────────┘                         │POTENCIAÇÃO│
       │                                                         └──────────┘
       │
       │                      [<]
       │                  ┌─────────┐
       ├─────────────────→│   q3    │
       │                  │  leu <  │
       │                  └────┬────┘
       │                       │
       │         ┌─────────────┼─────────────┬─────────────┐
       │         │             │             │             │
       │       [-]           [>]           [=]         [outros]
       │         │             │             │             │
       │         ↓             ↓             ↓             ↓
       │    ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
       │    │   q4    │   │   q5    │   │   q6    │   │  TOKEN  │
       │    │(aceita) │   │(aceita) │   │(aceita) │   │    <    │
       │    └────┬────┘   └────┬────┘   └────┬────┘   └─────────┘
       │         │             │             │
       │         ↓             ↓             ↓
       │    ┌─────────┐   ┌─────────┐   ┌─────────┐
       │    │ TOKEN   │   │ TOKEN   │   │ TOKEN   │
       │    │   <-    │   │   <>    │   │   <=    │
       │    │ATRIBUIÇÃO  │DIFERENTE │   │MENOR_IG │
       │    └─────────┘   └─────────┘   └─────────┘
       │
       │                      [>]
       │                  ┌─────────┐
       └─────────────────→│   q7    │
                          │  leu >  │
                          └────┬────┘
                               │
                     ┌─────────┴─────────┐
                     │                   │
                   [=]               [outros]
                     │                   │
                     ↓                   ↓
                ┌─────────┐         ┌─────────┐
                │   q8    │         │  TOKEN  │
                │(aceita) │         │    >    │
                └────┬────┘         └─────────┘
                     │
                     ↓
                ┌─────────┐
                │ TOKEN   │
                │   >=    │
                │MAIOR_IG │
                └─────────┘
```

### Operadores de 1 Caractere

```
╔════════════════════════════════════════════════════════════════════════╗
║              AFD PARA OPERADORES DE 1 CARACTERE                        ║
╚════════════════════════════════════════════════════════════════════════╝

                        [+]              [−]              [*]
    ┌─────┐      ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │ q0  │─────→│ TOKEN(+)    │  │ TOKEN(−)    │  │ TOKEN(*)    │
    │     │      │OPERADOR_ARIT│  │OPERADOR_ARIT│  │OPERADOR_ARIT│
    └──┬──┘      └─────────────┘  └─────────────┘  └─────────────┘
       │
       │            [/]              [%]
       ├────→  ┌─────────────┐  ┌─────────────┐
       │       │ TOKEN(/)    │  │ TOKEN(%)    │
       │       │OPERADOR_ARIT│  │OPERADOR_ARIT│
       │       └─────────────┘  └─────────────┘
       │
       │            [=]              [>]              [<]
       ├────→  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
       │       │ TOKEN(=)    │  │ TOKEN(>)    │  │ TOKEN(<)    │
       │       │OPERADOR_LOG │  │OPERADOR_LOG │  │OPERADOR_LOG │
       │       └─────────────┘  └─────────────┘  └─────────────┘
       │
       │            [&]              [^]
       └────→  ┌─────────────┐  ┌─────────────┐
               │ TOKEN(&)    │  │ TOKEN(^)    │
               │OPERADOR_LOG │  │OPERADOR_LOG │
               │    AND      │  │     OR      │
               └─────────────┘  └─────────────┘

    NOTA: Todos são estados finais de aceitação imediata
```

---

## 💬 AUTÔMATO DETALHADO - COMENTÁRIOS

### Comentário de Linha Única ($)

```
╔════════════════════════════════════════════════════════════════════════╗
║              AFD PARA COMENTÁRIO DE LINHA ÚNICA                        ║
╚════════════════════════════════════════════════════════════════════════╝

                 [$]           [qualquer ≠ \n]         [\n] ou EOF
    ┌─────┐  início    ┌─────────┐  leitura    ┌─────────┐  fim      ┌─────────┐
    │ q0  │───────────→│   q1    │────────────→│   q1    │──────────→│   q2    │
    │     │            │ dentro  │◄────────────│ dentro  │           │(descarta)│
    └─────┘            │coment.  │  [qualquer  │coment.  │           └────┬────┘
                       └─────────┘    ≠ \n]    └─────────┘                │
                                                                           │
                                                                           ↓
                                                                    [Volta q0]
                                                                    [SEM Token]

    EXEMPLO:
    
    "$ Este é um comentário\n"
    
    → q0 ──[$]──→ q1 ──[ ]──→ q1 ──[E]──→ q1 ──[s]──→ q1 ──[t]──→ q1 
         ──[e]──→ q1 ──[ ]──→ q1 ... ──[\n]──→ q2 → DESCARTA
```

### Comentário de Múltiplas Linhas ($$)

```
╔════════════════════════════════════════════════════════════════════════╗
║            AFD PARA COMENTÁRIO DE MÚLTIPLAS LINHAS                     ║
╚════════════════════════════════════════════════════════════════════════╝

              [$$]          [qualquer ≠ $]      [$]            [$]
    ┌─────┐ abertura  ┌─────────┐  leitura ┌─────────┐ 1º$ ┌─────────┐ 2º$ ┌─────────┐
    │ q0  │──────────→│   q1    │─────────→│   q1    │────→│   q2    │────→│   q3    │
    │     │           │ dentro  │◄─────────│ dentro  │     │ leu 1º$ │     │(descarta)│
    └─────┘           │coment.  │[qualquer │coment.  │     └────┬────┘     └────┬────┘
                      └─────────┘   ≠ $]   └─────────┘          │                │
                                        ↑                        │                │
                                        │                        │                │
                                        │      [qualquer ≠ $]    │                │
                                        └────────────────────────┘                │
                                                                                  │
                                                                                  ↓
                                                                           [Volta q0]
                                                                           [SEM Token]

    EXEMPLO:
    
    "$$ Comentário
       multilinha $$"
    
    → q0 ──[$$]──→ q1 ──[ ]──→ q1 ──[C]──→ q1 ──[o]──→ q1 ... 
         ──[\n]──→ q1 ... ──[$]──→ q2 ──[$]──→ q3 → DESCARTA
```

---

## 🔤 AUTÔMATO DETALHADO - SÍMBOLOS

```
╔════════════════════════════════════════════════════════════════════════╗
║                    AFD PARA SÍMBOLOS                                   ║
╚════════════════════════════════════════════════════════════════════════╝

                        [{]              [}]              
    ┌─────┐      ┌─────────────┐  ┌─────────────┐  
    │ q0  │─────→│TOKEN({)     │  │TOKEN(})     │  
    │     │      │ABRE_CHAVE   │  │FECHA_CHAVE  │  
    └──┬──┘      └─────────────┘  └─────────────┘  
       │
       │            [(]              [)]
       ├────→  ┌─────────────┐  ┌─────────────┐
       │       │TOKEN(()     │  │TOKEN())     │
       │       │ABRE_PAREN   │  │FECHA_PAREN  │
       │       └─────────────┘  └─────────────┘
       │
       │            [;]              [,]
       └────→  ┌─────────────┐  ┌─────────────┐
               │TOKEN(;)     │  │TOKEN(,)     │
               │PONTO_VIRGULA│  │   VIRGULA   │
               └─────────────┘  └─────────────┘

    NOTA: Reconhecimento direto, sem estados intermediários
```

---

## 🌊 FLUXO COMPLETO DE PROCESSAMENTO

```
╔════════════════════════════════════════════════════════════════════════╗
║                  FLUXO COMPLETO DO ANALISADOR LÉXICO                   ║
╚════════════════════════════════════════════════════════════════════════╝

    ┌───────────────────────────────────────────┐
    │      CÓDIGO FONTE (String)                │
    │   "Inteiro x <- 10; $ comentário"         │
    └────────────────┬──────────────────────────┘
                     │
                     ↓
    ┌────────────────────────────────────────────┐
    │     PRÉ-PROCESSAMENTO                      │
    │  • Remove comentários de linha ($)         │
    │  • Remove comentários de bloco ($$)        │
    └────────────────┬───────────────────────────┘
                     │
                     ↓
    ┌────────────────────────────────────────────┐
    │      CÓDIGO LIMPO                          │
    │   "Inteiro x <- 10; "                      │
    └────────────────┬───────────────────────────┘
                     │
                     ↓
         ┌───────────────────────┐
         │   ANÁLISE LÉXICA      │
         │   Estado q0 (inicial) │
         └───────────┬───────────┘
                     │
         ┌───────────┴───────────┐
         │  LÊ PRÓXIMO CARACTERE │
         └───────────┬───────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
    ↓                ↓                ↓
┌────────┐      ┌────────┐      ┌────────┐
│ Letra? │      │Dígito? │      │  "?    │
│  SIM   │      │  SIM   │      │  SIM   │
└───┬────┘      └───┬────┘      └───┬────┘
    │               │               │
    ↓               ↓               ↓
┌────────┐      ┌────────┐      ┌────────┐
│AFD_Ident      │AFD_Num │      │AFD_Str │
└───┬────┘      └───┬────┘      └───┬────┘
    │               │               │
    └───────────────┼───────────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ↓               ↓               ↓
┌────────┐      ┌────────┐      ┌────────┐
│Operador│      │Símbolo?│      │Espaço? │
│  SIM   │      │  SIM   │      │  SIM   │
└───┬────┘      └───┬────┘      └───┬────┘
    │               │               │
    ↓               ↓               ↓
┌────────┐      ┌────────┐      ┌────────┐
│AFD_Op  │      │Direto  │      │Ignora  │
└───┬────┘      └───┬────┘      └───┬────┘
    │               │               │
    └───────────────┼───────────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │   TOKEN RECONHECIDO  │
         │  Adiciona à Lista    │
         └──────────┬───────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │   Volta para q0      │
         │   (próximo caractere)│
         └──────────┬───────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │      EOF?            │
         └──────┬───────┬───────┘
                │       │
              NÃO      SIM
                │       │
                │       ↓
                │  ┌────────────┐
                │  │ Adiciona   │
                │  │ Token(FIM) │
                │  └─────┬──────┘
                │        │
                └────────┼───────┐
                         │       │
                         ↓       ↓
                  ┌──────────────────┐
                  │ LISTA DE TOKENS  │
                  │  COMPLETA        │
                  └────────┬─────────┘
                           │
                           ↓
                  ┌──────────────────┐
                  │     PARSER       │
                  │ (Análise Sintática)
                  └──────────────────┘
```

---

## 📊 TABELA DE ESTADOS E TRANSIÇÕES

```
╔════════════════════════════════════════════════════════════════════════╗
║              TABELA COMPLETA DE ESTADOS E TRANSIÇÕES                   ║
╚════════════════════════════════════════════════════════════════════════╝

┌───────────┬─────────────────────────┬────────────────┬────────────────────┐
│  ESTADO   │       ENTRADA           │ PRÓXIMO ESTADO │   AÇÃO/TOKEN       │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│    q0     │  [A-Z][a-z][À-ÿ]        │     q_id       │ Inicia leitura ID  │
│ (INICIAL) │  [0-9]                  │     q_num      │ Inicia leitura NUM │
│           │  ["]                    │     q_str      │ Inicia leitura STR │
│           │  [$]                    │     q_com1     │ Inicia comentário  │
│           │  [$$]                   │     q_com2     │ Inicia coment bloc │
│           │  [*]                    │     q_op_mult  │ Verifica **        │
│           │  [<]                    │     q_op_men   │ Verifica <-,<>,<=  │
│           │  [>]                    │     q_op_mai   │ Verifica >=        │
│           │  [+−/%=&^]              │     ACEITA     │ Token OPERADOR     │
│           │  [{](;,})]              │     ACEITA     │ Token SIMBOLO      │
│           │  [ \t\n]                │     q0         │ IGNORA             │
│           │  EOF                    │     q_fim      │ Token FIM          │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│   q_id    │  [A-Z][a-z][À-ÿ]        │     q_id       │ Continua leitura   │
│ (IDENT)   │  [outros]               │     ACEITA     │ Classifica Token   │
│           │                         │                │ PALAVRA_CHAVE ou   │
│           │                         │                │ IDENTIFICADOR      │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│   q_num   │  [0-9]                  │     q_num      │ Continua leitura   │
│ (NUMERO)  │  [outros]               │     ACEITA     │ Token NUMERO       │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│   q_str   │  [qualquer ≠ "]         │     q_str      │ Continua leitura   │
│ (STRING)  │  ["]                    │     ACEITA     │ Token STRING       │
│           │  EOF                    │     ERRO       │ String não fechada │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│ q_op_mult │  [*]                    │     ACEITA     │ Token ** (potência)│
│ (leu *)   │  [outros]               │     ACEITA     │ Token * (mult.)    │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│ q_op_men  │  [-]                    │     ACEITA     │ Token <- (atrib.)  │
│ (leu <)   │  [>]                    │     ACEITA     │ Token <> (dif.)    │
│           │  [=]                    │     ACEITA     │ Token <= (menor=)  │
│           │  [outros]               │     ACEITA     │ Token < (menor)    │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│ q_op_mai  │  [=]                    │     ACEITA     │ Token >= (maior=)  │
│ (leu >)   │  [outros]               │     ACEITA     │ Token > (maior)    │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│  q_com1   │  [qualquer ≠ \n]        │     q_com1     │ Continua leitura   │
│(coment. $)│  [\n] ou EOF            │     DESCARTA   │ Remove comentário  │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│  q_com2   │  [qualquer ≠ $]         │     q_com2     │ Continua leitura   │
│(coment.$$)│  [$]                    │     q_com2_1   │ Leu primeiro $     │
│           │                         │                │                    │
│ q_com2_1  │  [$]                    │     DESCARTA   │ Remove comentário  │
│(leu 1º $) │  [qualquer ≠ $]         │     q_com2     │ Volta a ler        │
├───────────┼─────────────────────────┼────────────────┼────────────────────┤
│           │                         │                │                    │
│  q_fim    │  -                      │     -          │ Token FIM          │
│  (FINAL)  │                         │                │ Fim da análise     │
└───────────┴─────────────────────────┴────────────────┴────────────────────┘
```

---

## 🎯 EXEMPLO COMPLETO DE EXECUÇÃO

```
╔════════════════════════════════════════════════════════════════════════╗
║          EXEMPLO: Processamento de "Inteiro x <- 10;"                  ║
╚════════════════════════════════════════════════════════════════════════╝

    ENTRADA: "Inteiro x <- 10;"
    
    ┌────────────────────────────────────────────────────────────────┐
    │ PASSO 1: Pré-processamento                                     │
    │ • Verifica comentários: NENHUM                                 │
    │ • String permanece: "Inteiro x <- 10;"                         │
    └────────────────────────────────────────────────────────────────┘
    
    ┌────────────────────────────────────────────────────────────────┐
    │ PASSO 2: Análise Léxica - Caractere por Caractere             │
    └────────────────────────────────────────────────────────────────┘
    
    Pos 0: 'I'
      q0 ──[letra]──→ q_id
      
    Pos 1-6: 'n', 't', 'e', 'i', 'r', 'o'
      q_id ──[letra]──→ q_id (loop)
      
    Pos 7: ' ' (espaço)
      q_id ──[espaço]──→ ACEITA
      Lexema acumulado: "Inteiro"
      Classificação: "inteiro" (lowercase) → É PALAVRA-CHAVE!
      ✅ Token(INTEIRO, "Inteiro")
      Volta para q0
      
    Pos 7: ' ' (espaço)
      q0 ──[espaço]──→ q0 (IGNORA)
      
    Pos 8: 'x'
      q0 ──[letra]──→ q_id
      
    Pos 9: ' ' (espaço)
      q_id ──[espaço]──→ ACEITA
      Lexema acumulado: "x"
      Classificação: "x" (lowercase) → NÃO é palavra-chave
      ✅ Token(IDENTIFICADOR, "x")
      Volta para q0
      
    Pos 9: ' ' (espaço)
      q0 ──[espaço]──→ q0 (IGNORA)
      
    Pos 10: '<'
      q0 ──[<]──→ q_op_men
      
    Pos 11: '-'
      q_op_men ──[-]──→ ACEITA
      Lexema: "<-"
      ✅ Token(ATRIBUICAO, "<-")
      Volta para q0
      
    Pos 12: ' ' (espaço)
      q0 ──[espaço]──→ q0 (IGNORA)
      
    Pos 13: '1'
      q0 ──[dígito]──→ q_num
      
    Pos 14: '0'
      q_num ──[dígito]──→ q_num
      
    Pos 15: ';'
      q_num ──[;]──→ ACEITA
      Lexema acumulado: "10"
      ✅ Token(NUMERO, "10")
      Volta para q0
      
    Pos 15: ';'
      q0 ──[;]──→ ACEITA
      ✅ Token(PONTO_VIRGULA, ";")
      Volta para q0
      
    Pos 16: EOF
      q0 ──[EOF]──→ q_fim
      ✅ Token(FIM, "")
    
    ┌────────────────────────────────────────────────────────────────┐
    │ RESULTADO: Lista de Tokens Gerada                             │
    ├────────────────────────────────────────────────────────────────┤
    │ [0] Token(INTEIRO, "Inteiro")                                  │
    │ [1] Token(IDENTIFICADOR, "x")                                  │
    │ [2] Token(ATRIBUICAO, "<-")                                    │
    │ [3] Token(NUMERO, "10")                                        │
    │ [4] Token(PONTO_VIRGULA, ";")                                  │
    │ [5] Token(FIM, "")                                             │
    └────────────────────────────────────────────────────────────────┘
    
    ✅ ANÁLISE LÉXICA COMPLETA - 6 TOKENS GERADOS
```

---

## 📐 REPRESENTAÇÃO MATEMÁTICA FORMAL

```
╔════════════════════════════════════════════════════════════════════════╗
║          DEFINIÇÃO FORMAL DO AUTÔMATO FINITO DETERMINÍSTICO            ║
╚════════════════════════════════════════════════════════════════════════╝

    M = (Q, Σ, δ, q₀, F)

    Onde:

    Q = { q0, q_id, q_num, q_str, q_op_mult, q_op_men, q_op_mai,
          q_com1, q_com2, q_com2_1, q_fim }
        
        Conjunto finito de estados

    Σ = { A-Z, a-z, À-ÿ, 0-9, +, -, *, /, %, =, <, >, &, ^,
          {, }, (, ), ;, ,, ", $, espaço, \t, \n, EOF }
        
        Alfabeto de entrada (símbolos aceitos)

    δ : Q × Σ → Q
        
        Função de transição (veja tabela de transições acima)
        
        Exemplos:
        δ(q0, 'I') = q_id
        δ(q_id, 'n') = q_id
        δ(q0, '5') = q_num
        δ(q_num, '3') = q_num
        δ(q0, '<') = q_op_men
        δ(q_op_men, '-') = ACEITA com token ATRIBUICAO

    q₀ = q0
        
        Estado inicial (onde a análise começa)

    F = { q_id, q_num, q_str, q_op_mult, q_op_men, q_op_mai,
          q_com1, q_com2, q_fim }
        
        Conjunto de estados finais (aceita tokens)

    ┌─────────────────────────────────────────────────────────────┐
    │ PROPRIEDADES DO AUTÔMATO:                                   │
    ├─────────────────────────────────────────────────────────────┤
    │ • Determinístico: Para cada (estado, entrada) existe        │
    │   exatamente uma transição                                  │
    │                                                             │
    │ • Completo: Todas as entradas possíveis são tratadas       │
    │                                                             │
    │ • Minimal: Não há estados redundantes ou equivalentes      │
    │                                                             │
    │ • Reconhece Linguagem Regular: L(M) = linguagem CF         │
    │                                                             │
    │ • Complexidade Temporal: O(n) onde n = tamanho da entrada  │
    │                                                             │
    │ • Complexidade Espacial: O(k) onde k = número de tokens    │
    └─────────────────────────────────────────────────────────────┘
```

---

## 🏁 RESUMO VISUAL

```
╔════════════════════════════════════════════════════════════════════════╗
║                    RESUMO DO AUTÔMATO COMPLETO                         ║
╚════════════════════════════════════════════════════════════════════════╝

    ┌─────────────────────────────────────────────────────────────┐
    │                   AUTÔMATO PRINCIPAL                        │
    │                                                             │
    │   Estado Inicial: q0                                        │
    │                                                             │
    │   Sub-Autômatos:                                           │
    │   ├─ AFD Identificadores (10 palavras-chave)              │
    │   ├─ AFD Números Inteiros                                 │
    │   ├─ AFD Strings Literais                                 │
    │   ├─ AFD Operadores 2 chars (5 operadores)                │
    │   ├─ AFD Operadores 1 char (10 operadores)                │
    │   ├─ AFD Comentário Linha ($)                             │
    │   ├─ AFD Comentário Bloco ($$)                            │
    │   └─ Símbolos Diretos (6 símbolos)                        │
    │                                                             │
    │   Total de Tokens Reconhecidos: 29 tipos                   │
    │                                                             │
    │   Complexidade: O(n) linear                                │
    │                                                             │
    │   Implementação: Regex (equivalente a AFD gerado)          │
    └─────────────────────────────────────────────────────────────┘
```

---

**Documentação Criada por:** GitHub Copilot  
**Data:** 2025-11-29  
**Versão:** 1.0  
**Projeto:** Compilador Compila Fofo (CF)

---

*Este é o desenho completo e detalhado do autômato finito determinístico que implementa o analisador léxico do compilador CF.*

