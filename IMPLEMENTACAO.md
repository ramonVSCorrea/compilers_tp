# Resumo da Implementação - Geração de Código Intermediário

## ✅ O que foi implementado

### 1. Classe GeradorCodigo (src/codegen/GeradorCodigo.java)

Implementa a geração de **código de três endereços** com os seguintes recursos:

#### Funcionalidades:
- **Geração de temporários**: t0, t1, t2, ... para armazenar resultados intermediários
- **Geração de labels**: L0, L1, L2, ... para controle de fluxo
- **Instruções suportadas**:
  - Declaração de variáveis: `declare TIPO variavel`
  - Atribuições: `variavel = valor`
  - Operações binárias: `temp = op1 operador op2`
  - Operações unárias: `temp = operador op`
  - Labels: `L0:`
  - Saltos: `goto L0`, `if cond goto L0`, `ifFalse cond goto L0`
  - Impressão: `print valor`

### 2. Integração com o Parser

O Parser foi modificado para gerar código durante a análise sintática:

#### Métodos atualizados:

**Declarações e Atribuições:**
- `declItem()`: Gera `declare` e atribuição inicial
- `atribuicaoSimples()`: Gera código de atribuição

**Estruturas de Controle:**
- `seSenaoResto()`: Gera labels e saltos condicionais para if-else
- `enquantoResto()`: Gera labels e saltos para loops while
- `paraResto()`: Gera inicialização, condição e incremento para loops for

**Expressões:**
- Novos métodos `*ComCodigo()` que retornam o nome do temporário
- Hierarquia completa de precedência de operadores mantida
- Suporte a operadores aritméticos, relacionais e lógicos

**Comandos:**
- `imprimirResto()`: Gera instrução `print`

### 3. Exemplos de Código Gerado

#### Exemplo 1: Declaração e Atribuição
```cf
Inteiro x <- 5;
Inteiro y <- x + 3;
```
```
0: declare INTEIRO x
1: x = 5
2: declare INTEIRO y
3: t0 = x + 3
4: y = t0
```

#### Exemplo 2: Estrutura Condicional
```cf
Se (x > 5) {
    Imprimir("Maior");
} Senao {
    Imprimir("Menor");
}
```
```
0: t0 = x > 5
1: ifFalse t0 goto L0
2: print "Maior"
3: goto L1
L0:
5: print "Menor"
L1:
```

#### Exemplo 3: Laço Enquanto
```cf
Enquanto (i < 10) {
    i <- i + 1;
}
```
```
L0:
1: t0 = i < 10
2: ifFalse t0 goto L1
3: t1 = i + 1
4: i = t1
5: goto L0
L1:
```

#### Exemplo 4: Laço Para
```cf
Para i em (1, 10, 1) {
    soma <- soma + i;
}
```
```
0: declare INTEIRO i
1: i = 1
2: t0 = 10
3: t1 = 1
L0:
5: t2 = i <= t0
6: ifFalse t2 goto L1
7: t3 = soma + i
8: soma = t3
9: t4 = i + t1
10: i = t4
11: goto L0
L1:
```

## 🎯 Características Técnicas

### Código de Três Endereços
- Cada instrução tem no máximo 3 operandos
- Facilita otimização e geração de código objeto
- Formato padrão em compiladores

### Geração Durante o Parsing
- Usa estratégia de "dupla passagem" local
- Primeira passagem: verifica tipos (análise semântica)
- Segunda passagem: gera código intermediário
- Mantém sincronização entre análise e geração

### Estruturas de Dados
- Lista sequencial de instruções
- Contadores para temporários e labels
- Métodos auxiliares para cada tipo de instrução

## 📊 Estatísticas

- **Arquivos criados**: 1 (GeradorCodigo.java)
- **Arquivos modificados**: 2 (Parser.java, Main.java)
- **Métodos adicionados**: ~15
- **Linhas de código**: ~250 novas linhas

## 🧪 Testes Realizados

Foram criados 4 arquivos de teste:

1. **teste.cf**: Exemplo completo com todas as estruturas
2. **teste_para.cf**: Demonstração do laço Para
3. **teste_logico.cf**: Operadores lógicos (&, ^)
4. **teste_aritmetica.cf**: Todos os operadores aritméticos

Todos os testes passaram com sucesso! ✅

## 📝 Notas de Implementação

### Decisões de Projeto:
1. **Dupla passagem local**: Necessária para manter análise semântica e geração separadas
2. **Temporários explícitos**: Cada operação gera um novo temporário
3. **Labels numéricos**: Simplifica geração e debuging
4. **Formato legível**: Numeração de linhas facilita depuração

### Possíveis Melhorias Futuras:
- Otimização de temporários (reutilização)
- Eliminação de código morto
- Propagação de constantes
- Otimização de saltos
- Geração de código objeto (assembly/bytecode)

## ✅ Conclusão

A geração de código intermediário foi implementada com sucesso, cobrindo:
- ✅ Todas as estruturas da linguagem CF
- ✅ Todas as expressões (aritméticas, relacionais, lógicas)
- ✅ Estruturas de controle (Se/Senão, Enquanto, Para)
- ✅ Declarações e atribuições
- ✅ Comando Imprimir
- ✅ Geração de código de três endereços padrão

O compilador agora realiza todas as 4 etapas principais:
1. Análise Léxica ✅
2. Análise Sintática ✅
3. Análise Semântica ✅
4. Geração de Código Intermediário ✅

