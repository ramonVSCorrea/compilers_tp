# Exemplo Prático de Execução dos Autômatos

Este documento mostra passo a passo como os autômatos processam código real.

## 📝 Código de Exemplo

```cf
Inteiro resultado <- 0;
```

---

## 🔄 Processamento Passo a Passo

### **PASSO 1: Pré-processamento**

```
Entrada: "Inteiro resultado <- 0;"
```

**Ação:** Verificar e remover comentários
- Regex `\$\$.*?\$\$` → Nenhum match
- Regex `\$.*$` → Nenhum match
- **Resultado:** String permanece inalterada

```
Saída: "Inteiro resultado <- 0;"
```

---

### **PASSO 2: Tokenização - Primeira Palavra**

#### Posição 0-6: "Inteiro"

```
Caractere: 'I'
├─ q0 (estado inicial)
└─ Análise: É uma letra?
   └─ SIM → Ativa AFD_Identificador
```

**AFD de Identificadores em ação:**

```
Estado   | Entrada | Próximo Estado | Caracteres Lidos
---------|---------|----------------|------------------
q0       | 'I'     | q1 (aceita)    | "I"
q1       | 'n'     | q1 (aceita)    | "In"
q1       | 't'     | q1 (aceita)    | "Int"
q1       | 'e'     | q1 (aceita)    | "Inte"
q1       | 'i'     | q1 (aceita)    | "Intei"
q1       | 'r'     | q1 (aceita)    | "Inteir"
q1       | 'o'     | q1 (aceita)    | "Inteiro"
q1       | ' '     | FIM (espaço)   | "Inteiro"
```

**Pós-processamento:**
```java
lexema = "Inteiro"
lexema.toLowerCase() = "inteiro"

Verificação:
if (lexema.equals("inteiro"))
    return new Token(INTEIRO, "Inteiro");
```

**Token Gerado:** `Token(INTEIRO, "Inteiro")` ✅

---

### **PASSO 3: Espaço em Branco**

#### Posição 7: ' '

```
Caractere: ' ' (espaço)
├─ q0
└─ Ação: Ignora e continua
```

**Nenhum token gerado**

---

### **PASSO 4: Identificador**

#### Posição 8-16: "resultado"

```
Caractere: 'r'
├─ q0
└─ É letra? SIM → AFD_Identificador
```

**AFD de Identificadores:**

```
Estado   | Entrada | Caracteres Lidos
---------|---------|------------------
q0       | 'r'     | "r"
q1       | 'e'     | "re"
q1       | 's'     | "res"
q1       | 'u'     | "resu"
q1       | 'l'     | "resul"
q1       | 't'     | "result"
q1       | 'a'     | "resulta"
q1       | 'd'     | "resultad"
q1       | 'o'     | "resultado"
q1       | ' '     | FIM
```

**Pós-processamento:**
```java
lexema = "resultado"
lexema.toLowerCase() = "resultado"

Verificação de palavras-chave:
"resultado" NÃO está na lista

return new Token(IDENTIFICADOR, "resultado");
```

**Token Gerado:** `Token(IDENTIFICADOR, "resultado")` ✅

---

### **PASSO 5: Espaço em Branco**

#### Posição 17: ' '

**Ação:** Ignora

---

### **PASSO 6: Operador de Atribuição**

#### Posição 18-19: "<-"

```
Caractere: '<'
├─ q0
└─ É operador? SIM → AFD_Operador
```

**AFD de Operadores (2 caracteres):**

```
Estado   | Entrada | Ação
---------|---------|---------------------------
q0       | '<'     | Vai para q1_menor
q1_menor | '-'     | MATCH! Operador "<-"
         |         | Tipo: ATRIBUICAO
```

**Token Gerado:** `Token(ATRIBUICAO, "<-")` ✅

---

### **PASSO 7: Espaço em Branco**

#### Posição 20: ' '

**Ação:** Ignora

---

### **PASSO 8: Número**

#### Posição 21: "0"

```
Caractere: '0'
├─ q0
└─ É dígito? SIM → AFD_Numero
```

**AFD de Números:**

```
Estado   | Entrada | Caracteres Lidos
---------|---------|------------------
q0       | '0'     | "0"
q1       | ';'     | FIM (não é dígito)
```

**Token Gerado:** `Token(NUMERO, "0")` ✅

---

### **PASSO 9: Ponto e Vírgula**

#### Posição 22: ";"

```
Caractere: ';'
├─ q0
└─ É símbolo? SIM
```

**Reconhecimento direto:**
```java
switch (';') {
    case ';': 
        return new Token(PONTO_VIRGULA, ";");
}
```

**Token Gerado:** `Token(PONTO_VIRGULA, ";")` ✅

---

### **PASSO 10: Fim do Arquivo**

```
EOF detectado
→ Adiciona Token(FIM, "")
```

**Token Gerado:** `Token(FIM, "")` ✅

---

## 📋 Lista Final de Tokens

```java
[0] Token(INTEIRO, "Inteiro")
[1] Token(IDENTIFICADOR, "resultado")
[2] Token(ATRIBUICAO, "<-")
[3] Token(NUMERO, "0")
[4] Token(PONTO_VIRGULA, ";")
[5] Token(FIM, "")
```

**Total:** 6 tokens gerados

---

## 🔍 Diagrama de Transições Completo

```
┌─────────────────────────────────────────────────────────┐
│          PROCESSAMENTO: "Inteiro resultado <- 0;"       │
└─────────────────────────────────────────────────────────┘

Posição:  0       7       8        17 18  20 21 22
Entrada:  I n t e i r o   r e ... o    <  -    0  ;
          └─────┬─────┘   └────┬────┘  └┬─┘   │  │
                │              │        │     │  │
          AFD_Ident       AFD_Ident  AFD_Op AFD  │
                │              │        │    Num  │
                ↓              ↓        ↓     ↓   ↓
           [INTEIRO]     [IDENTIFICADOR] [ATRIB] [NUM] [;]
```

---

## 🧪 Exemplo Complexo: Expressão Aritmética

### Código: `x**2 + y`

#### Processamento:

```
┌──────────┬──────────┬────────────────┬───────────────────┐
│ Posição  │ Entrada  │ AFD Ativado    │ Token Gerado      │
├──────────┼──────────┼────────────────┼───────────────────┤
│ 0        │ x        │ AFD_Ident      │ IDENTIFICADOR(x)  │
│ 1-2      │ **       │ AFD_Op2        │ OPERADOR_ARIT(**) │
│ 3        │ 2        │ AFD_Numero     │ NUMERO(2)         │
│ 4        │ (espaço) │ (ignora)       │ -                 │
│ 5        │ +        │ AFD_Op1        │ OPERADOR_ARIT(+)  │
│ 6        │ (espaço) │ (ignora)       │ -                 │
│ 7        │ y        │ AFD_Ident      │ IDENTIFICADOR(y)  │
│ EOF      │ -        │ -              │ FIM()             │
└──────────┴──────────┴────────────────┴───────────────────┘
```

**Tokens Gerados:**
1. `IDENTIFICADOR(x)`
2. `OPERADOR_ARIT(**)`  ← Note: ** reconhecido como UM token
3. `NUMERO(2)`
4. `OPERADOR_ARIT(+)`
5. `IDENTIFICADOR(y)`
6. `FIM()`

---

## 🎯 Prioridades de Reconhecimento

### Por que "**" não é reconhecido como dois '*'?

**Resposta:** Ordem de prioridade no regex!

```java
String regex =
    "(?iu)" +
    "(" + "Inteiro|Logico|..." + ")" +  // 1. Palavras-chave
    "|" + "(\\*\\*|>=|<=|<>|<-)" +     // 2. Operadores 2 chars ← PRIMEIRO
    "|" + "(\\d+)" +                   // 3. Números
    "|" + "(\\p{L}+)" +                // 4. Identificadores
    "|" + "([+\\-/%*])" +              // 5. Op. aritméticos 1 char
    ...
```

**Matching:**
```
Tentativa 1: Palavras-chave? "**" → NÃO
Tentativa 2: Operador 2 chars? "**" → SIM! ✅
    → Para aqui, não tenta operador de 1 char
```

Se a ordem fosse invertida:
```
Tentativa: Operador 1 char? "*" → SIM (captura apenas o primeiro)
    → Resultado incorreto: dois tokens '*' e '*'
```

---

## 💡 Exemplo com Comentários

### Código: 
```cf
$ Isto é um comentário
Inteiro x <- 5;
```

#### Pré-processamento:

```
Entrada original:
"$ Isto é um comentário\nInteiro x <- 5;"

Regex aplicado: \$.*$
Match encontrado: "$ Isto é um comentário"

Remoção:
codigo = codigo.replaceAll("(?m)\\$.*$", "");

Resultado:
"\nInteiro x <- 5;"
```

#### Tokenização da string limpa:

```
\n        → Espaço em branco (ignora)
Inteiro   → Token(INTEIRO, "Inteiro")
x         → Token(IDENTIFICADOR, "x")
<-        → Token(ATRIBUICAO, "<-")
5         → Token(NUMERO, "5")
;         → Token(PONTO_VIRGULA, ";")
EOF       → Token(FIM, "")
```

**Comentário foi removido antes da tokenização!**

---

## 📊 Estatísticas de Processamento

### Análise de: `Inteiro resultado <- 0;`

- **Caracteres totais:** 23
- **Espaços ignorados:** 3
- **Caracteres processados:** 20
- **Tokens gerados:** 6
- **AFDs ativados:**
  - AFD_Identificador: 2 vezes
  - AFD_Operador: 1 vez
  - AFD_Numero: 1 vez
  - Símbolos diretos: 1 vez
- **Complexidade:** O(n) = O(23)
- **Tempo teórico:** Linear ao tamanho da entrada

---

## 🎓 Conclusão

Os autômatos do compilador CF funcionam em conjunto para:

1. ✅ Remover comentários (pré-processamento)
2. ✅ Identificar tipo de cada caractere
3. ✅ Ativar AFD apropriado
4. ✅ Reconhecer tokens completos
5. ✅ Classificar tokens (palavra-chave vs identificador)
6. ✅ Gerar lista de tokens para o parser

**Todos os processos são determinísticos e executam em tempo linear O(n).**

---

*Documentação criada para fins didáticos - Compilador CF - 2025*

