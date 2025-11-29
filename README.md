# Compilador - Compila Fofo (CF)

Implementação de um compilador completo para a linguagem **Compila Fofo (CF)**, desenvolvido como trabalho prático da disciplina de Compiladores.

## 📑 Índice
- [Descrição](#-descrição)
- [Quick Start](#-quick-start)
- [Requisitos](#-requisitos)
- [Características da Linguagem](#-características-da-linguagem-cf)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Compilar](#-como-compilar)
- [Como Executar](#-como-executar)
- [Saída do Compilador](#-saída-do-compilador)
- [Como Testar](#-como-testar)
- [Executando Código MIPS](#-executando-o-código-mips-gerado)
- [Funcionalidades Implementadas](#-funcionalidades-implementadas)
- [Detalhes Técnicos](#-detalhes-técnicos-da-implementação)

## 📋 Descrição

Este projeto implementa um compilador completo para a linguagem CF, incluindo as seguintes etapas:

1. **Análise Léxica** - Tokenização do código-fonte
2. **Análise Sintática** - Verificação da estrutura gramatical com precedência de operadores
3. **Análise Semântica** - Validação de tipos e uso de variáveis através de uma Tabela de Símbolos
4. **Geração de Código** - Geração de código Assembly MIPS executável

## ⚡ Quick Start

```powershell
# Clone ou baixe o projeto
cd C:\Users\1210499\Desktop\compilers_tp

# Compile o projeto
javac -d bin src/main/*.java src/lexer/*.java src/parser/*.java src/codegen/*.java src/TabelaDeSimbolos/*.java src/Tipo_de_dados/*.java

# Execute com o arquivo de exemplo
java -cp bin main.Main

# Ou execute com seu próprio arquivo
java -cp bin main.Main inputfiles/meu_codigo.cf

# O código MIPS gerado estará em outputfiles/saida.asm
```

## 🔧 Requisitos

- **Java JDK 11** ou superior
- Sistema operacional: Windows, Linux ou macOS

## 🎯 Características da Linguagem CF

A linguagem Compila Fofo possui as seguintes características:

### Tipos de Dados
- `Inteiro` - Números inteiros (suporta operações aritméticas e relacionais)
- `Logico` / `Lógico` - Valores booleanos (`Verdade`/`Mentira`)
- `Caractere` - Tipo para strings de texto
- `STRING` - Literais entre aspas e resultado de concatenações (tipo interno)

### Palavras-Chave
- **Declaração de Tipos:** `Inteiro`, `Logico`/`Lógico`, `Caractere`
- **Estruturas de Controle:** `Se`, `Senao`/`Senão`, `Enquanto`, `Para`
- **Entrada/Saída:** `Imprimir`
- **Literais Lógicos:** `Verdade` (1), `Mentira` (0)

### Operadores (com precedência)
**Precedência dos operadores (do maior para o menor):**
1. **Potenciação:** `**` (direita para esquerda)
2. **Unário:** `-` (negação)
3. **Multiplicativos:** `*`, `/`, `%`
4. **Aditivos:** `+`, `-` (também usado para concatenação de strings)
5. **Relacionais:** `=`, `<>`, `<`, `>`, `<=`, `>=`
6. **Lógico E:** `&`
7. **Lógico OU:** `^`

### Comentários
- **Linha única:** `$ comentário até o fim da linha`
- **Múltiplas linhas:** `$$ comentário multilinha $$`

### Estruturas Suportadas

#### 1. Declaração de Variáveis
```cf
Inteiro x;                    $ declaração simples
Inteiro i <- 10;              $ declaração com inicialização
Inteiro a <- 1, b <- 2, c;    $ múltiplas declarações
Logico flag <- Verdade;
Caractere texto <- "Olá";
```

#### 2. Atribuição
```cf
x <- 5;
y <- x + 10;
flag <- x > 5;
```

#### 3. Estrutura Condicional (Se/Senão)
```cf
$ Com parênteses na condição
Se (x > 0) {
    Imprimir("Positivo");
} Senao {
    Imprimir("Não positivo");
}

$ Sem parênteses também funciona
Se x = 5 {
    Imprimir("x é cinco");
}
```

#### 4. Laço Enquanto
```cf
Enquanto (i < 10) {
    i <- i + 1;
    Imprimir("i = " + i);
}
```

#### 5. Laço Para
```cf
$ Sintaxe: Para variavel em (inicio, fim, passo)
Para i em (1, 10, 1) {
    Imprimir("Valor: " + i);
}

$ Laço decrescente
Para j em (10, 1, -1) {
    Imprimir("Decrescente: " + j);
}
```

#### 6. Impressão com Concatenação
```cf
Inteiro idade <- 25;
Imprimir("Idade: " + idade);              $ concatena string + inteiro
Imprimir("Resultado: " + (5 + 3));        $ concatena string + expressão
Logico teste <- Verdade;
Imprimir("Teste: " + teste);              $ concatena string + lógico
```

### Exemplo Completo de Código CF
```cf
$$ 
  Programa de exemplo demonstrando
  as principais características da linguagem CF
$$

$ Declaração de variáveis
Inteiro i <- 1, j <- 5;
Inteiro k <- i**2 + j;           $ potenciação: 1^2 + 5 = 6

$ Operações lógicas
Logico a <- Verdade;
Logico b <- Mentira;
Logico resultado <- a & b;       $ AND lógico
Logico outraOp <- a ^ b;         $ OR lógico

$ Saída com concatenação
Imprimir("O valor de k é: " + k);
Imprimir("Resultado AND: " + resultado);
Imprimir("Resultado OR: " + outraOp);

$ Estrutura condicional
Se (k > 5) {
    Imprimir("k é maior que 5");
} Senao {
    Imprimir("k é menor ou igual a 5");
}

$ Laço Enquanto
Inteiro contador <- 0;
Enquanto (contador < 3) {
    Imprimir("Contador: " + contador);
    contador <- contador + 1;
}

$ Laço Para
Para x em (1, 5, 1) {
    Imprimir("Iteração: " + x);
}

$ Expressões complexas
Inteiro calc <- (10 + 5) * 2 - 8 / 2;    $ resultado: 26
Logico comparacao <- calc >= 20 & calc <= 30;
Imprimir("Cálculo: " + calc);
Imprimir("Está entre 20 e 30: " + comparacao);
```

## 🔧 Requisitos

- **Java JDK 11** ou superior
- Sistema operacional: Windows, Linux ou macOS

## 📁 Estrutura do Projeto

```
compilers_tp/
├── src/                          # Código-fonte
│   ├── main/
│   │   └── Main.java            # Classe principal (orquestra todas as etapas)
│   ├── lexer/
│   │   ├── Lexer.java           # Analisador léxico
│   │   └── Token.java           # Definição de tokens
│   ├── parser/
│   │   └── Parser.java          # Analisador sintático e semântico
│   ├── codegen/
│   │   └── CodeGenMIPS.java     # Gerador de código Assembly MIPS
│   ├── TabelaDeSimbolos/
│   │   └── TabelaDeSimbolos.java # Gerenciamento de símbolos
│   └── Tipo_de_dados/
│       └── TipoDado.java        # Enumeração de tipos de dados
├── bin/                         # Arquivos compilados (.class)
├── inputfiles/
│   └── exemplo.cf               # Arquivo de exemplo em CF
├── outputfiles/
│   └── saida.asm                # Código MIPS gerado
└── README.md                    # Este arquivo
```

## 🚀 Como Compilar

### Opção 1: Usando linha de comando (PowerShell/CMD)

1. Navegue até a pasta raiz do projeto:
```powershell
cd C:\Users\1210499\Desktop\compilers_tp
```

2. Compile todos os arquivos Java:
```powershell
javac -d bin src/main/*.java src/lexer/*.java src/parser/*.java src/codegen/*.java src/TabelaDeSimbolos/*.java src/Tipo_de_dados/*.java
```

### Opção 2: Usando IDE (VS Code, IntelliJ, Eclipse)

- Abra o projeto na IDE de sua preferência
- A IDE deve compilar automaticamente os arquivos para a pasta `bin/`

## ▶️ Como Executar

### Executar com arquivo padrão (exemplo.cf)

```powershell
java -cp bin main.Main
```

### Executar com arquivo personalizado

```powershell
java -cp bin main.Main caminho/para/seu_arquivo.cf
```

Exemplo:
```powershell
java -cp bin main.Main inputfiles/exemplo.cf
```

## 📤 Saída do Compilador

O compilador exibe as seguintes informações:

1. **Código-fonte** lido do arquivo .cf
2. **Tokens gerados** pela análise léxica
3. **Resultado da análise sintática e semântica**
4. **Confirmação da geração de código MIPS** (salvo em `outputfiles/saida.asm`)
5. **Mensagens de erro** detalhadas (se houver problemas no código)

### Exemplo de Saída
```
=======================================
       COMPILADOR - COMPILA FOFO
=======================================

CÓDIGO FONTE:
---------------------------------------
Logico a <- Verdade;
Logico b <- Mentira;
Logico r <- a & b;
Imprimir("a & b = " + r);

---------------------------------------
ETAPA 1 - ANÁLISE LÉXICA
---------------------------------------
LOGICO = Logico
IDENTIFICADOR = a
ATRIBUICAO = <-
VERDADE = Verdade
PONTO_VIRGULA = ;
LOGICO = Logico
IDENTIFICADOR = b
...

---------------------------------------
ETAPA 2 - ANÁLISE SINTÁTICA / SEMÂNTICA 

 ANÁLISE SINTÁTICA REALIZADA COM SUCESSO.
 ANÁLISE SEMÂNTICA REALIZADA COM SUCESSO.
---------------------------------------
ETAPA 3 - GERAÇÃO DE CÓDIGO (MIPS)
---------------------------------------
 Código salvo em: outputfiles/saida.asm

 Compilação concluída com sucesso!
=======================================
```

## 🧪 Como Testar

1. Crie um arquivo `.cf` na pasta `inputfiles/`
2. Escreva código na linguagem CF seguindo a sintaxe descrita
3. Execute o compilador passando o caminho do arquivo
4. Verifique se há erros léxicos, sintáticos ou semânticos
5. Se a compilação for bem-sucedida, o código MIPS será gerado em `outputfiles/saida.asm`

### Exemplos de Testes Disponíveis

O arquivo `inputfiles/exemplo.cf` contém exemplos de código CF que demonstram:
- Declarações de variáveis lógicas
- Operações lógicas (AND `&` e OR `^`)
- Concatenação de strings com valores lógicos
- Impressão de resultados

## 🖥️ Executando o Código MIPS Gerado

O código Assembly MIPS gerado pode ser executado em simuladores como:

### MARS (MIPS Assembler and Runtime Simulator)
1. Baixe o MARS em: http://courses.missouristate.edu/KenVollmar/mars/
2. Abra o arquivo `outputfiles/saida.asm` no MARS
3. Clique em "Assemble" (ou F3)
4. Clique em "Run" (ou F5)
5. A saída aparecerá no console do MARS

### SPIM
Alternativa ao MARS, também simula código MIPS:
```powershell
spim -file outputfiles/saida.asm
```

### Características do Código MIPS Gerado
- **Seção .data:** Contém todas as variáveis e strings literais
- **Seção .text:** Contém o código executável
- **Entry point:** `main`
- **Syscalls utilizadas:**
  - `li $v0, 1` + `syscall` - Impressão de inteiros
  - `li $v0, 4` + `syscall` - Impressão de strings
  - Conversão int→string implementada em MIPS
  - Concatenação de strings implementada em MIPS

## 🛠️ Funcionalidades Implementadas

### ✅ Análise Léxica Completa
- ✓ Reconhecimento de palavras-chave (case-insensitive)
- ✓ Identificação de operadores (aritméticos, lógicos, relacionais)
- ✓ Suporte a comentários de linha única (`$`) e múltiplas linhas (`$$ ... $$`)
- ✓ Tratamento de strings literais entre aspas
- ✓ Reconhecimento de números inteiros
- ✓ Identificadores (variáveis)
- ✓ Símbolos especiais: `{ } ( ) ; ,`
- ✓ Operadores especiais: `<-`, `**`, `>=`, `<=`, `<>`

### ✅ Análise Sintática
- ✓ Declaração de variáveis com ou sem inicialização
- ✓ Múltiplas declarações em uma única linha (separadas por vírgula)
- ✓ Atribuições com operador `<-`
- ✓ Estrutura condicional `Se`/`Senão` (com ou sem parênteses na condição)
- ✓ Laço `Enquanto` com expressões booleanas
- ✓ Laço `Para` com sintaxe `Para var em (inicio, fim, passo)`
- ✓ Comando `Imprimir` com concatenação automática
- ✓ Blocos de código delimitados por `{ }`
- ✓ Expressões aritméticas completas com precedência de operadores
- ✓ Expressões lógicas e relacionais
- ✓ Suporte a parênteses para agrupamento de expressões

### ✅ Análise Semântica
- ✓ Tabela de símbolos com suporte a escopos aninhados
- ✓ Verificação de tipos em todas as operações
- ✓ Detecção de variáveis não declaradas
- ✓ Validação de compatibilidade de tipos em atribuições
- ✓ Verificação de tipos em operações aritméticas (INTEIRO)
- ✓ Verificação de tipos em operações lógicas (LOGICO)
- ✓ Verificação de tipos em operações relacionais (requerem INTEIRO, retornam LOGICO)
- ✓ Suporte a concatenação de strings com inteiros e lógicos
- ✓ Conversão implícita de tipos para concatenação

### ✅ Geração de Código MIPS
- ✓ Geração de código Assembly MIPS 32-bit
- ✓ Declaração de variáveis na seção `.data`
- ✓ Alocação de strings literais
- ✓ Operações aritméticas: `+`, `-`, `*`, `/`, `%`
- ✓ Operação de potenciação `**` (implementada com loop)
- ✓ Operadores relacionais: `=`, `<>`, `>`, `<`, `>=`, `<=`
- ✓ Operadores lógicos: `&` (AND), `^` (OR)
- ✓ Estruturas de controle (if/else, while, for)
- ✓ Geração de labels únicos
- ✓ Gerenciamento de variáveis temporárias
- ✓ Concatenação de strings em tempo de execução
- ✓ Conversão de inteiros para string (para impressão e concatenação)
- ✓ Syscalls para impressão de strings e inteiros
- ✓ Otimização de constantes em tempo de compilação (constant folding)

### 🎯 Características Avançadas
- **Precedência de Operadores:** Implementação completa da precedência correta dos operadores
- **Associatividade:** Potenciação associa à direita, demais operadores à esquerda
- **Operador Unário:** Suporte ao operador `-` unário para negação
- **Constant Folding:** Otimização de expressões constantes em tempo de compilação
- **Concatenação Inteligente:** Conversão automática de tipos para concatenação com `+`
- **Escopos Aninhados:** Suporte a declaração de variáveis em diferentes escopos
- **Mensagens de Erro Detalhadas:** Erros léxicos, sintáticos e semânticos com informações precisas

## 🔬 Detalhes Técnicos da Implementação

### Análise Léxica (Lexer.java)
- **Técnica:** Análise baseada em expressões regulares (regex)
- **Ordem de Prioridade:** Tokens de 2 caracteres (`**`, `>=`, `<=`, `<>`, `<-`) são verificados antes dos de 1 caractere
- **Tratamento de Comentários:** Remoção de comentários antes da tokenização usando regex
  - `(?s)\$\$.*?\$\$` - Comentários multilinha
  - `(?m)\$.*$` - Comentários de linha única
- **Case-Insensitive:** Palavras-chave podem ser escritas em qualquer combinação de maiúsculas/minúsculas
- **Identificadores:** Armazenados em lowercase para facilitar comparações

### Análise Sintática (Parser.java)
- **Técnica:** Parser descendente recursivo (Recursive Descent Parser)
- **Precedência de Operadores:** Implementada através de métodos encadeados
  ```
  expressao → exprOr → exprAnd → exprRel → exprAdd → exprMul → exprPow → exprUn → primario
  ```
- **Gramática LL(1):** Utiliza lookahead de 1 token
- **Método Predictivo:** Decisões baseadas no token atual usando `check()` e `match()`

### Análise Semântica
- **Tabela de Símbolos:** Estrutura de pilha de escopos (lista de mapas)
  - Cada escopo é um `HashMap<String, TipoDado>`
  - Busca do escopo mais interno para o mais externo
- **Verificação de Tipos:** Validação em tempo de compilação
  - Operações aritméticas requerem `INTEIRO`
  - Operações lógicas requerem `LOGICO`
  - Operações relacionais aceitam `INTEIRO` e retornam `LOGICO`
  - Concatenação aceita `STRING`, `INTEIRO` e `LOGICO`

### Geração de Código (CodeGenMIPS.java)
- **Arquitetura:** MIPS 32-bit
- **Registradores Utilizados:**
  - `$t0-$t7` - Temporários para operações
  - `$a0` - Argumento para syscalls
  - `$v0` - Código de syscall
- **Estruturas de Dados:**
  - `.data` - Variáveis globais (word), strings (asciiz), buffer de conversão
  - `.text` - Código executável
- **Otimizações:**
  - **Constant Folding:** Expressões com literais são calculadas em tempo de compilação
  - **Labels únicos:** Prefixos diferentes para labels do Parser (`L`) e CodeGen (`M`)
- **Implementações Especiais:**
  - **Potenciação:** Loop multiplicativo (sem instrução nativa)
  - **Conversão int→string:** Algoritmo de divisão por 10 + reversão
  - **Concatenação:** Cópia byte-a-byte de strings na memória

## 📚 Referências

- **MIPS Assembly Language Programming** - Robert Britton
- **Compilers: Principles, Techniques, and Tools** - Aho, Lam, Sethi, Ullman (Dragon Book)
- **MARS MIPS Simulator:** [http://courses.missouristate.edu/KenVollmar/mars/](https://github.com/dpetersanderson/MARS/releases/tag/v.4.5.1)

## 👥 Autor

Desenvolvido como trabalho prático da disciplina de Compiladores.

## 📄 Licença

Este projeto é de uso acadêmico.
