# 🚀 GUIA RÁPIDO - Compilador Compila Fofo

## ⚡ Início Rápido

### 1. Compilar o Projeto
```powershell
.\compile.bat
```

### 2. Executar com Arquivo de Teste
```powershell
.\run.bat inputfiles/teste.cf
```

### 3. Executar com Seu Próprio Arquivo
```powershell
java -cp bin main.Main seu_arquivo.cf
```

## 📂 Arquivos de Teste Disponíveis

- `inputfiles/teste.cf` - Exemplo geral
- `inputfiles/teste_para.cf` - Laço Para
- `inputfiles/teste_logico.cf` - Operadores lógicos
- `inputfiles/teste_aritmetica.cf` - Operações aritméticas
- `inputfiles/teste_completo.cf` - Teste abrangente

## 📝 Sintaxe Básica da Linguagem CF

### Declaração de Variáveis
```cf
Inteiro x <- 10;
Logico ativo <- Verdade;
Caractere msg <- "Ola mundo";
```

### Estrutura Condicional
```cf
Se (x > 5) {
    Imprimir("Maior que 5");
} Senao {
    Imprimir("Menor ou igual a 5");
}
```

### Laços
```cf
$ Enquanto
Enquanto (x < 10) {
    x <- x + 1;
}

$ Para
Para i em (1, 10, 1) {
    Imprimir(i);
}
```

### Operadores

**Aritméticos**: `+`, `-`, `*`, `/`, `%`, `**`  
**Relacionais**: `=`, `<>`, `<`, `>`, `<=`, `>=`  
**Lógicos**: `&` (E), `^` (OU)  
**Atribuição**: `<-`

### Comentários
```cf
$ Comentário de linha

$$ 
   Comentário
   de múltiplas
   linhas
$$
```

## 🔍 Saída do Compilador

O compilador exibe:

1. **Código-fonte** lido
2. **Tokens** gerados (análise léxica)
3. **Resultado** da análise sintática/semântica
4. **Código intermediário** (3 endereços)

## 🛠️ Comandos Úteis

### Compilar Manualmente
```powershell
javac -encoding UTF-8 -d bin src/codegen/*.java src/Tipo_de_dados/*.java src/TabelaDeSimbolos/*.java src/lexer/*.java src/parser/*.java src/main/*.java
```

### Executar Manualmente
```powershell
java -cp bin main.Main inputfiles/teste.cf
```

### Limpar e Recompilar
```powershell
rmdir /s /q bin
.\compile.bat
```

## 📖 Documentação Completa

- `README.md` - Documentação principal
- `IMPLEMENTACAO.md` - Detalhes técnicos
- `GERACAO_CODIGO_CONCLUIDA.md` - Conclusão do projeto
- `RESUMO_FINAL.md` - Resumo executivo

## ❓ Solução de Problemas

### Erro de compilação
- Verifique se o JDK está instalado: `java -version`
- Use encoding UTF-8: `javac -encoding UTF-8 ...`

### Erro ao executar
- Compile novamente: `.\compile.bat`
- Verifique o classpath: `-cp bin`

### Arquivo não encontrado
- Use caminho relativo: `inputfiles/arquivo.cf`
- Ou caminho absoluto: `C:\caminho\completo\arquivo.cf`

## ✅ Checklist de Verificação

- [ ] JDK 11+ instalado
- [ ] Projeto compilado (pasta `bin/` existe)
- [ ] Arquivo `.cf` com sintaxe correta
- [ ] Ponto-e-vírgula `;` ao final das instruções

## 🎯 Dicas

1. **Use os scripts**: `compile.bat` e `run.bat` facilitam muito!
2. **Teste incrementalmente**: Comece com código simples
3. **Verifique os exemplos**: Use os arquivos em `inputfiles/`
4. **Leia as mensagens de erro**: Elas indicam linha e tipo do erro

## 📞 Estrutura de um Programa CF

```cf
$ Comentário explicativo

$ 1. Declarações
Inteiro num <- 42;
Logico ativo <- Verdade;

$ 2. Processamento
Se (num > 0) {
    num <- num * 2;
}

$ 3. Saída
Imprimir("Resultado: ");
Imprimir(num);
```

## 🏆 Pronto para Usar!

O compilador está **100% funcional** e pronto para uso!

Experimente os arquivos de teste e crie seus próprios programas em CF! 🚀

