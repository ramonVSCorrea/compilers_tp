# Compilador - Compila Fofo (CF)

Implementação de um compilador para a linguagem **Compila Fofo (CF)**, desenvolvido como trabalho prático da disciplina de Compiladores.

## 📋 Descrição

Este projeto implementa um compilador completo para a linguagem CF, incluindo as seguintes etapas:

1. **Análise Léxica** - Tokenização do código-fonte
2. **Análise Sintática** - Verificação da estrutura gramatical
3. **Análise Semântica** - Validação de tipos e uso de variáveis através de uma Tabela de Símbolos

## 🎯 Características da Linguagem CF

A linguagem Compila Fofo possui as seguintes características:

### Tipos de Dados
- `Inteiro` - Números inteiros
- `Logico` / `Lógico` - Valores booleanos (Verdade/Mentira)
- `Caractere` - Strings de texto

### Palavras-Chave
- **Declaração:** `Inteiro`, `Logico`, `Caractere`
- **Controle de Fluxo:** `Se`, `Senao`/`Senão`, `Enquanto`, `Para`
- **Saída:** `Imprimir`
- **Literais Lógicos:** `Verdade`, `Mentira`

### Operadores
- **Aritméticos:** `+`, `-`, `*`, `/`, `%`, `**` (potenciação)
- **Relacionais:** `=`, `<>`, `<`, `>`, `<=`, `>=`
- **Lógicos:** `&` (E), `^` (OU)
- **Atribuição:** `<-`

### Comentários
- Linha única: `$ comentário até o fim da linha`
- Múltiplas linhas: `$$ comentário multilinha $$`

### Exemplo de Código
```cf
$ Declaração de variáveis
Inteiro i <- 1, j <- 5
Inteiro k <- i**2 + j

$ Saída
Imprimir("O valor de k é " + k)

$ Estrutura condicional
Se (k > 5) {
    Imprimir("k é maior que 5")
} Senao {
    Imprimir("k é menor ou igual a 5")
}

$ Laço
Enquanto (i < 10) {
    i <- i + 1
}
```

## 🔧 Requisitos

- **Java JDK 11** ou superior
- Sistema operacional: Windows, Linux ou macOS

## 📁 Estrutura do Projeto

```
compilers_tp/
├── src/                          # Código-fonte
│   ├── main/
│   │   └── Main.java            # Classe principal
│   ├── lexer/
│   │   ├── Lexer.java           # Analisador léxico
│   │   └── Token.java           # Definição de tokens
│   ├── parser/
│   │   └── Parser.java          # Analisador sintático
│   ├── TabelaDeSimbolos/
│   │   └── TabelaDeSimbolos.java # Gerenciamento de símbolos
│   └── Tipo_de_dados/
│       └── TipoDado.java        # Tipos de dados da linguagem
├── inputfiles/
│   └── exemplo.cf               # Arquivo de exemplo em CF
├── bin/                         # Arquivos compilados (.class)
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
javac -d bin src/main/*.java src/lexer/*.java src/parser/*.java src/TabelaDeSimbolos/*.java src/Tipo_de_dados/*.java
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
3. **Resultado da análise sintática** e semântica
4. **Mensagens de erro** (se houver problemas no código)

### Exemplo de Saída
```
=======================================
       COMPILADOR - COMPILA FOFO
=======================================

 CÓDIGO FONTE:
---------------------------------------
Inteiro i <- 1, j <- 5
Inteiro k <- i**2 + j
Imprimir("O valor de k é " + k)

---------------------------------------
 ETAPA 1 - ANÁLISE LÉXICA
---------------------------------------
|INTEIRO = Inteiro|
|IDENTIFICADOR = i|
|ATRIBUICAO = <-|
|NUMERO = 1|
...

---------------------------------------
 ETAPA 2 - ANÁLISE SINTÁTICA
---------------------------------------

Análise sintática + semântica concluídas sem erros!

 Compilação concluída com sucesso!
=======================================
```

## 🧪 Como Testar

1. Crie um arquivo `.cf` na pasta `inputfiles/`
2. Escreva código na linguagem CF seguindo a sintaxe descrita
3. Execute o compilador passando o caminho do arquivo
4. Verifique se há erros léxicos, sintáticos ou semânticos

## 🛠️ Funcionalidades Implementadas

- ✅ **Análise Léxica completa**
  - Reconhecimento de palavras-chave
  - Identificação de operadores
  - Suporte a comentários (linha única e múltiplas linhas)
  - Tratamento de strings, números e identificadores

- ✅ **Análise Sintática**
  - Declaração de variáveis com inicialização
  - Múltiplas declarações em uma linha
  - Estruturas de controle (Se/Senão, Enquanto, Para)
  - Blocos de código
  - Expressões aritméticas e lógicas

- ✅ **Análise Semântica**
  - Tabela de símbolos
  - Verificação de tipos
  - Detecção de variáveis não declaradas
  - Validação de operações entre tipos compatíveis

## 👥 Autor

Desenvolvido como trabalho prático da disciplina de Compiladores.

## 📄 Licença

Este projeto é de uso acadêmico.
