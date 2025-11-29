# Documentação Técnica – Compilador Compila Fofo (CF)

## Introdução

Este documento apresenta a documentação técnica completa do **Compila Fofo (CF)**, um compilador desenvolvido como trabalho prático da disciplina de Compiladores. O projeto implementa, de forma integrada, todas as principais etapas do processo de compilação de uma linguagem imperativa simples:

1. **Análise Léxica** – definição do conjunto de tokens da linguagem, construção dos autômatos finitos determinísticos (AFDs) e implementação do analisador léxico responsável por transformar o código-fonte em uma sequência de tokens válidos.  
2. **Análise Sintática** – definição e implementação da gramática da linguagem em um parser descendente recursivo (LL(1)), respeitando precedência e associatividade de operadores, e verificando se a sequência de tokens forma programas corretamente estruturados.  
3. **Análise Semântica** – verificação de tipos, uso correto de variáveis e regras semânticas da linguagem, por meio de uma tabela de símbolos com suporte a escopos aninhados, garantindo que programas sintaticamente corretos também façam sentido do ponto de vista semântico.  
4. **Geração de Código** – tradução do programa CF em código Assembly **MIPS 32 bits**, incluindo declaração de variáveis na seção `.data`, geração de instruções na seção `.text`, implementação de estruturas de controle (`Se`, `Enquanto`, `Para`) e suporte a operações aritméticas, lógicas e relacionais.  
5. **Execução e Otimizações** – execução do código gerado em simuladores como **MARS** e **SPIM**, e aplicação de otimizações simples, como *constant folding* e concatenação inteligente de strings, além da proposta de otimizações futuras.

A linguagem CF foi projetada com fins didáticos, buscando ao mesmo tempo ser expressiva o bastante para demonstrar conceitos fundamentais de compiladores e simples o suficiente para permitir uma implementação completa dentro do escopo da disciplina. Ela oferece tipos básicos (`Inteiro`, `Logico`/`Lógico`, `Caractere` e um tipo interno `STRING`), estruturas de controle usuais e um comando de saída (`Imprimir`) com suporte a concatenação de diferentes tipos.

Ao longo deste documento, cada etapa do compilador é descrita em detalhes:

- Na **Seção 1**, são apresentados a definição informal da linguagem, a gramática em alto nível e a construção dos autômatos que fundamentam o analisador léxico.  
- A **Seção 2** descreve o analisador sintático, a técnica utilizada, a implementação da precedência de operadores e o tratamento de erros sintáticos.  
- A **Seção 3** discute a análise semântica, a modelagem da tabela de símbolos, as regras de tipagem e os principais erros semânticos tratados.  
- A **Seção 4** explica a geração de código Assembly MIPS e mostra como o programa CF é traduzido e executado em um ambiente assembler online.  
- A **Seção 5** documenta as otimizações já implementadas e sugere aprimoramentos futuros.  
- Por fim, a **Seção 6** relaciona explicitamente cada parte do documento com os critérios de avaliação definidos no enunciado do trabalho.

Com isso, este texto serve tanto como guia de uso do compilador quanto como registro técnico das decisões de projeto, garantindo transparência na implementação das etapas léxica, sintática, semântica e de geração de código, além de demonstrar o alinhamento do projeto com os objetivos pedagógicos da disciplina.

## 1. Documentação e Léxico

### 1.1. Definição da Linguagem CF

A linguagem **Compila Fofo (CF)** é uma linguagem imperativa simples, projetada para fins didáticos na disciplina de Compiladores. Um programa CF é composto por:

- declarações de variáveis;
- comandos de atribuição;
- estruturas de controle (`Se`, `Senao`, `Enquanto`, `Para`);
- comando de saída (`Imprimir`);
- expressões aritméticas, relacionais e lógicas.

Não há funções definidas pelo usuário; o programa é um bloco único ("escopo global"), possivelmente com escopos internos delimitados por `{ }` (por exemplo, dentro de `Se`, `Enquanto`, `Para`).

#### 1.1.1. Tipos de Dados

A linguagem oferece os seguintes tipos:

- `Inteiro`  
  - Domínio: números inteiros (como `0`, `1`, `-5`, etc.).  
  - Suportam todas as operações aritméticas (`+`, `-`, `*`, `/`, `%`, `**`) e relacionais.

- `Logico` / `Lógico`  
  - Domínio: valores booleanos `Verdade` (1) e `Mentira` (0).  
  - Participam de operações lógicas (`&`, `^`) e de comparações quando aplicável.

- `Caractere`  
  - Usado para strings de texto (literais entre aspas).  
  - Em nível de implementação, é utilizado para armazenar texto em memória, principalmente para impressão.

- `STRING`  
  - Tipo **interno** do compilador, não aparece como palavra-chave na linguagem.  
  - Representa literais string e resultados de concatenações com `+`.  
  - É usado para que o analisador semântico e o gerador de código saibam que determinado valor é textual.

#### 1.1.2. Palavras-chave

As principais palavras-chave da CF (case-insensitive) são:

- Tipos: `Inteiro`, `Logico`/`Lógico`, `Caractere`  
- Controle de fluxo: `Se`, `Senao`/`Senão`, `Enquanto`, `Para`, `em`  
- Entrada/Saída: `Imprimir`  
- Literais lógicos: `Verdade`, `Mentira`

Identificadores de variáveis são distintos de palavras-chave, mas a verificação é **case-insensitive**: `inteiro`, `INTEIRO` e `InTeIrO` são reconhecidos como a mesma palavra-chave.

#### 1.1.3. Operadores e Precedência

A linguagem suporta:

1. **Potenciação:**  
   - `**` – associatividade à **direita**.

2. **Unário:**  
   - `-` – negação numérica unária (por exemplo, `-x`).

3. **Multiplicativos:**  
   - `*`, `/`, `%` – multiplicação, divisão inteira, resto.

4. **Aditivos:**  
   - `+`, `-` – soma e subtração;  
   - `+` também concatena strings com inteiros e lógicos (conversão automática para string).

5. **Relacionais:**  
   - `=`, `<>`, `<`, `>`, `<=`, `>=` – retornam valores lógicos (`Logico`).

6. **Lógicos:**  
   - `&` – E lógico (AND);  
   - `^` – OU lógico (OR).

A precedência, do mais forte para o mais fraco, é:

1. `**`  
2. `-` unário  
3. `*`, `/`, `%`  
4. `+`, `-` binários  
5. `=`, `<>`, `<`, `>`, `<=`, `>=`  
6. `&`  
7. `^`

Essa precedência é refletida tanto na gramática quanto na estrutura de métodos do parser.

#### 1.1.4. Comentários

Dois tipos de comentários:

- **Linha única:**  
  ```cf
  $ Comentário até o fim da linha
  ```

- **Múltiplas linhas:**  
  ```cf
  $$ Comentário
     em várias linhas $$
  ```

O lexer remove comentários **antes** da tokenização usando expressões regulares:

- `(?s)\$\$.*?\$\$` – comentários multilinha  
- `(?m)\$.*$` – comentários de linha única

#### 1.1.5. Estruturas Suportadas (visão de linguagem)

- **Declaração de variáveis**  
  ```cf
  Inteiro x;
  Inteiro i <- 10;
  Inteiro a <- 1, b <- 2, c;
  Logico flag <- Verdade;
  Caractere texto <- "Olá";
  ```

- **Atribuição**  
  ```cf
  x <- 5;
  y <- x + 10;
  flag <- x > 5;
  ```

- **Condicional `Se`/`Senao`**  
  ```cf
  Se (x > 0) {
      Imprimir("Positivo");
  } Senao {
      Imprimir("Não positivo");
  }

  Se x = 5 {
      Imprimir("x é cinco");
  }
  ```

- **Laço `Enquanto`**  
  ```cf
  Enquanto (i < 10) {
      i <- i + 1;
      Imprimir("i = " + i);
  }
  ```

- **Laço `Para`**  
  ```cf
  Para i em (1, 10, 1) {
      Imprimir("Valor: " + i);
  }

  Para j em (10, 1, -1) {
      Imprimir("Decrescente: " + j);
  }
  ```

- **Impressão e concatenação**  
  ```cf
  Inteiro idade <- 25;
  Imprimir("Idade: " + idade);
  Imprimir("Resultado: " + (5 + 3));
  Logico teste <- Verdade;
  Imprimir("Teste: " + teste);
  ```

---

### 1.2. Gramática (visão BNF/EBNF de alto nível)

A gramática efetiva está embutida no parser recursivo descendente, mas em alto nível pode ser descrita assim (notação simplificada):

#### 1.2.1. Estrutura geral do programa

```ebnf
programa      ::= { declaracaoOuComando } EOF ;

declaracaoOuComando
              ::= declaracao
               | comando ;

declaracao    ::= tipo listaDeclaracoes ';' ;

tipo          ::= 'Inteiro'
               | 'Logico' | 'Lógico'
               | 'Caractere' ;

listaDeclaracoes
              ::= declaracaoVar { ',' declaracaoVar } ;

declaracaoVar ::= IDENTIFICADOR [ '<-' expressao ] ;
```

#### 1.2.2. Comandos

```ebnf
comando       ::= comandoAtribuicao
               | comandoSe
               | comandoEnquanto
               | comandoPara
               | comandoImprimir
               | bloco ;

comandoAtribuicao
              ::= IDENTIFICADOR '<-' expressao ';' ;

comandoSe     ::= 'Se' [ '(' ] expressao [ ')' ] bloco [ 'Senao' bloco ] ;

comandoEnquanto
              ::= 'Enquanto' [ '(' ] expressao [ ')' ] bloco ;

comandoPara   ::= 'Para' IDENTIFICADOR 'em'
                  '(' expressao ',' expressao ',' expressao ')' bloco ;

comandoImprimir
              ::= 'Imprimir' '(' expressao ')' ';' ;

bloco         ::= '{' { declaracaoOuComando } '}' ;
```

#### 1.2.3. Expressões (camadas por precedência)

```ebnf
expressao     ::= exprOu ;

exprOu        ::= exprE { '^' exprE } ;
exprE         ::= exprRel { '&' exprRel } ;

exprRel       ::= exprAdd [ ( '=' | '<>' | '<' | '>' | '<=' | '>=' ) exprAdd ] ;

exprAdd       ::= exprMul { ( '+' | '-' ) exprMul } ;

exprMul       ::= exprPow { ( '*' | '/' | '%' ) exprPow } ;

exprPow       ::= exprUn { '**' exprUn } ;   // associativo à direita

exprUn        ::= '-' exprUn
               | primario ;

primario      ::= IDENTIFICADOR
               | NUM_INTEIRO
               | STRING_LITERAL
               | 'Verdade'
               | 'Mentira'
               | '(' expressao ')' ;
```

#### 1.2.4. Tokens principais

- Identificadores:  
  - Regex típica: `[A-Za-z_][A-Za-z0-9_]*` (normalizados para lowercase internamente).
- Números:  
  - Sequência de dígitos decimais.
- Strings:  
  - Texto entre aspas `"..."` sem quebra de linha; trechos literais são armazenados na `.data` do MIPS.

---

### 1.3. Construção dos Autômatos e Analisador Léxico

#### 1.3.1. Abordagem

A análise léxica é descrita de forma teórica por meio de diversos documentos:

- `AUTOMATOS.md`: documentação técnica de AFDs;  
- `AUTOMATO_COMPLETO_VISUAL.md`: desenho visual completo de todos os autômatos;  
- `DIAGRAMAS_AUTOMATOS.md`: diagramas Mermaid dos autômatos;  
- `EXEMPLO_PRATICO_AUTOMATOS.md`: execução passo a passo em cima de um código real;  
- `RESUMO_AUTOMATOS.md`: visão geral dos autômatos e resultados de testes.

Esses documentos especificam, para cada classe de tokens, um **autômato finito determinístico (AFD)** com:

- conjunto de estados;  
- alfabeto;  
- função de transição;  
- estado inicial;  
- estados de aceitação.

#### 1.3.2. AFDs principais

Entre os autômatos definidos, podemos destacar:

- AFD para **identificadores/palavras-chave**:  
  - Estados para primeira letra e subsequentes;  
  - Aceitação em qualquer estado que tenha lido pelo menos um caractere válido;  
  - Após aceitar, o lexema é comparado com a tabela de palavras-chave.

- AFD para **números inteiros**:  
  - Sequência de dígitos;  
  - Proíbe letras logo após o número (para evitar identificadores mal formados).

- AFD para **strings**:  
  - Estado inicial ao ler aspas `"`;  
  - Consome caracteres até encontrar uma segunda aspas;  
  - Pode tratar caracteres especiais se definido na especificação.

- AFD para **operadores compostos**:  
  - `**`, `<-`, `>=`, `<=`, `<>`  
  - Leitura de primeiro caractere, transição condicional ao segundo;  
  - Se o segundo caractere não combinar, reverte para um token de um caractere (ex.: `<`).

- AFD para **comentários**:  
  - `$$ ... $$` – estados que acumulam até encontrar o delimitador de fechamento;  
  - `$ ... fim da linha` – leitura até `\n`.

- AFD para **símbolos especiais**:  
  - `{`, `}`, `(`, `)`, `;`, `,` etc., tokens de um único caractere.

##### 1.3.2.1. Construção formal dos AFDs (por token)

Para tornar explícita a *construção dos autômatos* exigida no enunciado da disciplina, a linguagem CF foi decomposta em classes de lexemas, e para **cada classe de token** foi projetado um AFD específico. A seguir descrevemos, em termos formais, alguns dos principais AFDs.

**AFD para identificadores/palavras-chave**

- Conjunto de estados: `Q = { q0, q1 }`  
- Alfabeto: letras (`A–Z`, `a–z`), dígitos (`0–9`) e sublinhado (`_`).  
- Estado inicial: `q0`  
- Estados de aceitação: `{ q1 }`  
- Função de transição `δ`:
  - `δ(q0, letra_ou_underscore) = q1`  
  - `δ(q0, outro_caractere) = erro`  
  - `δ(q1, letra_ou_digito_ou_underscore) = q1`  
  - `δ(q1, outro_caractere) = parada` (retorna lexema formado)  

Ao aceitar em `q1`, o lexema acumulado é comparado com a tabela de palavras-chave; se houver correspondência, gera-se um token de palavra-chave, caso contrário gera-se um token `IDENTIFICADOR`.

**AFD para números inteiros**

- Conjunto de estados: `Q = { q0, q1 }`  
- Alfabeto: dígitos (`0–9`).  
- Estado inicial: `q0`  
- Estados de aceitação: `{ q1 }`  
- Função de transição `δ`:
  - `δ(q0, digito) = q1`  
  - `δ(q0, outro_caractere) = erro`  
  - `δ(q1, digito) = q1`  
  - `δ(q1, outro_caractere) = parada` (token `NUM_INTEIRO`)  

Se imediatamente após um número aparecer um caractere inválido para essa classe (por exemplo, uma letra), o lexer sinaliza erro léxico conforme especificado.

**AFD para strings literais**

- Conjunto de estados: `Q = { q0, q1, q2 }`  
- Alfabeto: aspas `"` e qualquer caractere de texto permitido.  
- Estado inicial: `q0`  
- Estados de aceitação: `{ q2 }`  
- Função de transição `δ`:
  - `δ(q0, '"') = q1`  
  - `δ(q0, outro) = erro`  
  - `δ(q1, '"') = q2`  
  - `δ(q1, caractere_texto) = q1`  
  - `δ(q2, qualquer) = parada` (token `STRING_LITERAL`)  

Caso o fim do arquivo seja alcançado ainda em `q1` (sem encontrar a segunda aspas), temos um erro léxico de *string não terminada*.

**AFD para operadores compostos (`<-`, `**`, `>=`, `<=`, `<>`)**

- Conjunto de estados: `Q = { q0, q1, q2 }`  
- Alfabeto: símbolos como `<`, `>`, `=`, `-`, `*`.  
- Estado inicial: `q0`  
- Estados de aceitação: `{ q1, q2 }` (dependendo do operador).  
- Ideia geral de `δ` (exemplos):
  - Ao ler `<` em `q0`, transita para `q1`:
    - se próximo caractere for `-` → aceita token de atribuição (`<-`), estado `q2`;  
    - se próximo caractere for `=` → aceita `<=`;  
    - se próximo caractere for `>` → aceita `<>`;  
    - se próximo caractere não formar operador de 2 caracteres → retrocede um caractere e aceita `<` como operador relacional simples.
  - Ao ler `>` em `q0`, transita para `q1`:
    - se próximo caractere for `=` → aceita `>=`;  
    - senão, aceita `>` simples.
  - Ao ler `*` em `q0`, transita para `q1`:
    - se próximo caractere for `*` → aceita `**` (potenciação);  
    - senão, aceita `*` (multiplicação).

Esse AFD garante que operadores de dois caracteres sejam priorizados, mantendo compatibilidade com os operadores de um caractere.

**AFD para comentários de linha e multilinha**

- Comentário de linha (`$ ... \n`):  
  - Estados: `Q = { q0, q1 }`;  
  - `q0` lê `$` e vai para `q1`;  
  - em `q1`, qualquer caractere diferente de `\n` mantém o estado;  
  - ao ler `\n` ou fim de arquivo, o comentário termina (nenhum token é emitido).

- Comentário multilinha (`$$ ... $$`):  
  - Estados: `Q = { q0, q1, q2 }`;  
  - `q0` lê um `$` e vai para `q1`;  
  - `q1` lê outro `$` e vai para `q2` (dentro do comentário);  
  - em `q2`, lê caracteres livremente até encontrar a sequência de fechamento `$$`, que o leva de volta ao fluxo normal sem produzir tokens.

Essas máquinas são equivalentes às expressões regulares usadas para remoção de comentários no `Lexer.java`.

**AFD para símbolos especiais** (`{`, `}`, `(`, `)`, `;`, `,`, etc.)

- Esses tokens são reconhecidos por AFDs muito simples, com um único estado de aceitação:  
  - `Q = { q0, q1 }`, estado inicial `q0`, aceitação em `q1`;  
  - `δ(q0, simbolo_especifico) = q1`, qualquer outro caractere leva a erro.

Em todos os casos acima, após o autômato alcançar um estado de aceitação, o lexema reconhecido é convertido para um objeto `Token` com o tipo apropriado do enum `Token.Tipo`, e o lexer continua a varredura a partir do próximo caractere de entrada.

#### 1.3.3. Implementação do Lexer (`lexer/Lexer.java`)

Apesar de toda fundamentação em AFDs, a implementação concreta usa **expressões regulares (regex)** e processamento sequencial, respeitando a mesma lógica de reconhecimento:

- Remoção prévia de comentários via regex;
- Verificação prioritária de tokens de **dois caracteres** (`**`, `>=`, `<=`, `<>`, `<-`), depois de **um caractere**;
- Reconhecimento de:
  - palavras-chave (case-insensitive);
  - identificadores;
  - inteiros;
  - strings;
  - símbolos especiais.

O lexer produz objetos `Token`, cujo tipo é dado pelo enum `Token.Tipo`. Esses tokens são consumidos pelo parser.

#### 1.3.4. Testes do Lexer

O arquivo `src/test/TestAutomatos.java` implementa uma **suite de 55 testes** que:

- verificam o reconhecimento correto de tokens;  
- validam o comportamento de todos os AFDs;  
- garantem 100% de sucesso nos casos definidos.

Isso assegura a corretude do analisador léxico com base na especificação teórica.

---

## 2. Analisador Sintático

### 2.1. Técnica Utilizada

O analisador sintático, implementado em `parser/Parser.java`, é um **parser descendente recursivo (Recursive Descent Parser)**. Características:

- Estilo **LL(1)** (um token de lookahead);
- Cada não-terminal principal da gramática é modelado como um método Java;
- A precedência de operadores é implementada pela **estratificação** de métodos:
  ```text
  expressao → exprOr → exprAnd → exprRel → exprAdd → exprMul → exprPow → exprUn → primario
  ```

O parser controla o fluxo com funções auxiliares como:

- `check(tipoToken)` – verifica se o token atual é de um determinado tipo;  
- `match(tipoToken)` – consome o token atual se corresponder, senão lança erro sintático;  
- métodos que avançam o token e retornam estruturas intermediárias (nós da AST ou anotações necessárias para geração de código).

### 2.2. Estrutura Geral do Parser

Métodos principais (nomes ilustrativos):

- `parsePrograma()` – ponto de entrada;  
- `parseDeclaracaoOuComando()` – decide entre declaração e comando;  
- `parseDeclaracao()` – trata declarações `Inteiro`, `Logico`, `Caractere`;  
- `parseComandoSe()`, `parseComandoEnquanto()`, `parseComandoPara()`, `parseComandoImprimir()`;  
- `parseExpressao()`, com submétodos para cada nível de precedência.

O parser:

1. Consome tokens produzidos por `Lexer`;
2. Verifica se a sequência pertence à linguagem definida pela gramática;
3. Já integra **checagens semânticas** (tipos, tabela de símbolos) e **ação de geração de código** durante a descida recursiva.

### 2.3. Precedência e Associatividade

A precedência é codificada em camadas de métodos:

- `parseExprOu()` lida com `^`;  
- `parseExprE()` lida com `&`;  
- `parseExprRel()` lida com `=`, `<`, `>`, `<=`, `>=`, `<>`;  
- `parseExprAdd()` lida com `+`, `-`;  
- `parseExprMul()` lida com `*`, `/`, `%`;  
- `parseExprPow()` lida com `**` (associatividade à direita);  
- `parseExprUn()` lida com `-` unário;  
- `parsePrimario()` lida com identificadores, literais, parênteses, etc.

A associatividade da potenciação `**` é explicitamente implementada como **direita para esquerda**, por exemplo:

- `a ** b ** c` é interpretado como `a ** (b ** c)`.

### 2.4. Tratamento de Erros Sintáticos

O parser detecta erros quando:

- um token esperado não aparece (`match` falha);  
- a estrutura dos comandos não segue a gramática (por exemplo, falta de `{`, `}`, `;`, ou parênteses, quando obrigatórios).

O compilador:

- exibe mensagens claras sobre o **tipo de erro** e, quando possível, a linha ou contexto;  
- interrompe a compilação ao encontrar erros sintáticos graves;
- apresenta, na saída do compilador, uma seção `ETAPA 2 - ANÁLISE SINTÁTICA / SEMÂNTICA` com o status (sucesso ou falha).

### 2.5. Exemplos de Análise Sintática

Exemplo correto (do `README.md`):

```cf
Logico a <- Verdade;
Logico b <- Mentira;
Logico r <- a & b;
Imprimir("a & b = " + r);
```

O parser reconhece:

- três declarações de variáveis (`a`, `b`, `r`);  
- uma atribuição com expressão lógica (`a & b`);  
- um comando `Imprimir` com concatenação de string + lógico.

Se escrevermos, por exemplo:

```cf
Inteiro x
x <- 5;
```

A falta de `;` na declaração gera um erro sintático informado ao usuário.

---

## 3. Analisador Semântico

### 3.1. Tabela de Símbolos (`TabelaDeSimbolos.java`) e Tipos (`TipoDado.java`)

A análise semântica é baseada em:

- `Tipo_de_dados/TipoDado.java` – enumeração dos tipos internos (INTEIRO, LOGICO, CARACTERE, STRING etc.);
- `TabelaDeSimbolos/TabelaDeSimbolos.java` – gerenciamento dos símbolos (variáveis) e seus tipos, com **escopos aninhados**.

#### 3.1.1. Escopos

A tabela de símbolos é implementada como uma **pilha de mapas**:

- Cada escopo é um `HashMap<String, TipoDado>`;
- Ao entrar em um bloco `{ ... }`, o compilador abre um novo escopo;
- Ao sair do bloco, o escopo é removido;
- A busca por um identificador é feita do escopo mais interno para o mais externo.

Isso permite, por exemplo, declarar uma variável com o mesmo nome dentro de um bloco interno, sombreamento a variável do escopo externo (se essa funcionalidade foi permitida pela implementação).

### 3.2. Regras Semânticas

As principais verificações semânticas (descritas no `README`):

- **Declaração e uso de variáveis**  
  - Estrutura detecta **variáveis não declaradas**;  
  - Impede uso de variáveis sem tipo conhecido.

- **Tipos em operações aritméticas**  
  - Operações `+`, `-`, `*`, `/`, `%`, `**` exigem operandos `INTEIRO`;  
  - O resultado é do tipo `INTEIRO`.

- **Tipos em operações lógicas**  
  - `&`, `^` exigem operandos `LOGICO`;  
  - O resultado é `LOGICO`.

- **Operações relacionais**  
  - `=`, `<>`, `<`, `>`, `<=`, `>=`:  
    - operam sobre inteiros (e, eventualmente, outros tipos que a implementação suportar);  
    - retornam `LOGICO`.

- **Atribuições**  
  - O tipo da expressão do lado direito deve ser compatível com o tipo da variável à esquerda;  
  - Erros de incompatibilidade são reportados (por exemplo, atribuir `Verdade` a uma variável `Inteiro`).

- **Concatenação de strings**  
  - `+` suporta concatenação de `STRING` com `INTEIRO` e `LOGICO`;  
  - Há conversão implícita de `INTEIRO` e `LOGICO` para `STRING` quando em contexto de concatenação.

- **Condições de controle de fluxo**  
  - As condições de `Se`, `Enquanto` e testes em laços devem ser do tipo `LOGICO`;  
  - Se a expressão não for lógica, é gerado um erro semântico.

### 3.3. Integração com o Parser

A análise semântica é feita durante a própria análise sintática:

- No momento em que uma variável é declarada, é inserida na `TabelaDeSimbolos` com seu tipo (`TipoDado`);
- Quando uma variável é usada:
  - Verifica-se se ela foi declarada;
  - Recupera-se o tipo para verificar operações envolvendo essa variável.

Ao construir uma expressão, cada operação recebe os tipos de seus operandos e:

- valida a compatibilidade;
- determina o tipo resultante;
- se a expressão for uma constante pura (`2 + 3 * 4`), pode ativar **constant folding** (ver Seção 5).

Erros semânticos são exibidos na mesma etapa do relatório do compilador:

```text
ETAPA 2 - ANÁLISE SINTÁTICA / SEMÂNTICA
 ANÁLISE SINTÁTICA REALIZADA COM SUCESSO.
 ANÁLISE SEMÂNTICA REALIZADA COM SUCESSO.
```

Caso haja erro semântico, a mensagem é apresentada e a compilação é abortada antes da geração de código MIPS.

---

## 4. Geração de Código e Execução

### 4.1. Arquitetura do Gerador de Código (`codegen/CodeGenMIPS.java`)

A etapa de geração de código recebe como entrada a estrutura produzida pelo parser (árvore sintática e informação de tipos) e gera como saída um arquivo MIPS:

- **Entrada:**  
  - programa tipado (variáveis com tipos, expressões anotadas);  
  - tabela de símbolos;  
  - informações auxiliares (labels únicos, temporários).

- **Saída:**  
  - arquivo `outputfiles/saida.asm` em Assembly MIPS 32 bits.

O gerador divide o código em:

- seção `.data` – variáveis e strings;  
- seção `.text` – código executável, começando em `main`.

### 4.2. Convenções MIPS

#### 4.2.1. Seção `.data`

- Todas as variáveis globais são alocadas com diretivas como `.word` para inteiros e lógicos;
- Strings literais recebem labels únicos e são declaradas com `.asciiz`;
- Buffers auxiliares (por exemplo, para conversão int→string) também são declarados aqui.

#### 4.2.2. Seção `.text`

- `main:` é o ponto de entrada;
- Cada comando CF é traduzido em uma sequência de instruções MIPS:
  - Atribuições → `lw`, `sw`, operações aritméticas (`add`, `sub`, `mul`, `div`, etc.);
  - Estruturas de controle → labels (`L1`, `L2` etc.) e saltos condicionais (`beq`, `bne`, `blt`, `bgt`, etc.);
  - Impressão → syscalls.

- Registradores usados:
  - `$t0-$t7` – temporários para cálculos intermediários;
  - `$a0` – registro de argumento para syscalls (endereço de string ou valor inteiro);
  - `$v0` – número da syscall e também registro de retorno de algumas operações internas.

### 4.3. Implementação de Operações

- **Aritmética:**
  - `+`, `-`, `*`, `/`, `%` traduzidos para instruções MIPS padrão (e combinações para `%` se necessário);
  - `**` (potenciação) implementado com um **loop**:
    - repetidas multiplicações ou uma rotina auxiliar específica.

- **Relacionais e lógicas:**
  - Comparações são traduzidas em instruções de comparação + set/branch, resultando em `0` ou `1` em um registrador;
  - Operadores lógicos `&` e `^` podem ser traduzidos como AND/OR bit a bit ou combinações de comparações com 0/1.

- **Controle de fluxo:**
  - `Se`/`Senao`:  
    - gera labels para o bloco `then`, o bloco `else` (se existir) e o fim do `if`;  
    - condicional traduzida em salto condicional para pular o bloco quando a condição é falsa.

  - `Enquanto`:  
    - label para o início do laço;  
    - avaliação da condição;  
    - salto para o fim quando a condição é falsa;  
    - corpo do laço;  
    - salto de volta ao início.

  - `Para`:  
    - inicialização do iterador;  
    - teste de condição (comparação com limite final);  
    - corpo;  
    - atualização do iterador (incremento ou decremento, de acordo com o passo);  
    - saltos e labels análogos ao `Enquanto`.

### 4.4. Impressão, Conversão e Concatenação

A impressão e concatenação utilizam syscalls padrão do MIPS:

- `li $v0, 1` + `syscall` – impressão de inteiros (com `$a0` contendo o valor);  
- `li $v0, 4` + `syscall` – impressão de strings (`$a0` contém o endereço da string em memória).

#### 4.4.1. Conversão int→string

O compilador implementa rotinas em MIPS para:

- Converter inteiros para strings (por exemplo, para `Imprimir("Idade: " + idade)`);  
- O algoritmo clássico de dividir por 10, pegar o dígito, converter para caractere e construir a string invertendo no final.

#### 4.4.2. Concatenação de strings

Para `Imprimir("Texto: " + x)`, o gerador:

1. Garante que `x` esteja representado como string (convertendo se for inteiro ou lógico);  
2. Usa um buffer de concatenação na `.data`;  
3. Copia caractere a caractere o primeiro operando para o buffer;  
4. Em seguida copia o segundo operando para a sequência;  
5. Finaliza com `0` (terminador de string) para `.asciiz`.

### 4.5. Execução em Ambiente Assembler Online (MARS/SPIM)

#### 4.5.1. Geração e localização do arquivo

Ao final da compilação bem-sucedida:

- É exibida a mensagem de sucesso na etapa 3 (Geração de Código);  
- O arquivo `outputfiles/saida.asm` é criado/atualizado com o Assembly MIPS.

#### 4.5.2. Execução no MARS

1. Baixar MARS em:  
   http://courses.missouristate.edu/KenVollmar/mars/
2. Abrir o arquivo `outputfiles/saida.asm` no MARS;
3. Clicar em **Assemble** (F3);
4. Clicar em **Run** (F5);
5. Ver a saída no console de saída do MARS.

#### 4.5.3. Execução no SPIM

Em linha de comando:

```powershell
spim -file outputfiles/saida.asm
```

A saída será mostrada no terminal. O comportamento é equivalente ao do MARS, com pequenas diferenças de mensagens de log.

---

## 5. Otimizações

### 5.1. Otimizações Implementadas

O compilador já implementa algumas otimizações, conforme descrito em `README.md`:

- **Constant Folding (otimização de constantes em tempo de compilação)**  
  - Expressões cujos operandos são todos literais (por exemplo, `10 + 5 * 2 - 8 / 2`) são avaliadas **no compilador**;  
  - Em vez de gerar diversas instruções MIPS, gera-se apenas o valor constante final.

- **Concatenação Inteligente**  
  - O compilador detecta contextos de concatenação de string e evita conversões desnecessárias;  
  - Mantém um tratamento unificado de strings, inteiros e lógicos na hora de gerar o MIPS para `+` em contexto de texto.

- **Labels únicos**  
  - A geração automática de labels (como `L...` no Parser e `M...` no CodeGen) evita colisões e simplifica o fluxo, o que reduz a complexidade de debugging e chances de saltos errados.

Essas otimizações melhoram tanto a legibilidade quanto a eficiência do código Assembly gerado.

### 5.2. Possíveis Otimizações Futuras (sugestões para “até 3 pontos extras”)

Algumas otimizações que poderiam ser implementadas para incrementar o trabalho:

1. **Propagação de Constantes**  
   - Se uma variável recebe um valor constante e não é modificada posteriormente, essa constante pode ser usada diretamente nas expressões, evitando load redundante.

2. **Eliminação de Código Morto**  
   - Analisar comandos que nunca são alcançados (por exemplo, código após um desvio incondicional até o fim de um bloco);  
   - Remover instruções que não afetam o resultado final (variáveis nunca usadas).

3. **Otimização de Controle de Fluxo**  
   - Simplificar sequências de saltos (`bne` seguido de `j`) em apenas um salto quando possível;  
   - Introduzir transformações equivalentes em `if`/`else` para reduzir o número de labels.

4. **Melhor Gerenciamento de Registradores**  
   - Rastrear variáveis temporárias vivas para reutilizar melhor `$t0-$t7`;  
   - Reduzir saves/loads redundantes na memória.

5. **Peephole Optimization (janela de instruções)**  
   - Examinar pequenas janelas de 2–3 instruções e aplicar regras de simplificação, por exemplo:  
     - sequência `li $t0, 0; add $t1, $t0, $t2` → pode ser simplificada.

6. **Otimizações de Alto Nível na Gramática**  
   - Simplificar expressões booleanas (`x & Verdade` → `x`, `x & Mentira` → `Mentira`) em tempo de compilação.

---

## 6. Relação com os Itens de Avaliação

- **Documentação e Léxico (5 pontos)**  
  - Definição informal da linguagem CF;  
  - Gramática de alto nível (BNF/EBNF simplificada);  
  - Explicação da construção e do uso dos AFDs;  
  - Descrição detalhada do analisador léxico, tokens e testes.

- **Sintático (5 pontos)**  
  - Explicação da técnica de parser descendente recursivo (LL(1));  
  - Especificação da ordem de precedência e associatividade;  
  - Tratamento de erros sintáticos e exemplos.

- **Semântico (5 pontos)**  
  - Descrição da tabela de símbolos com escopos aninhados;  
  - Regras de tipagem e compatibilidade;  
  - Checagens de variáveis não declaradas, tipos em operações, atribuições, condições.

- **Geração de Código e Execução (5 pontos)**  
  - Funcionamento do gerador de código MIPS;  
  - Convenções de uso de registradores e seções `.data`/`.text`;  
  - Tradução de estruturas de controle, expressões e impressão;  
  - Passo a passo de execução em MARS/SPIM.

- **Otimizações (até 3 pontos extras)**  
  - Documentação do **constant folding** já implementado;  
  - Características avançadas como concatenação inteligente e tratamento de tipos;  
  - Propostas claras de otimizações futuras (propagação de constantes, código morto, melhorias de fluxo e registradores).
