package main;

import lexer.*;
import parser.*;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;

/**
 * Programa principal do compilador da linguagem CF (Compila Fofo)
 * Etapas:
 * 1. Ler o código-fonte .cf
 * 2. Gerar tokens (analisador léxico)
 * 3. Executar a análise sintática
 */
public class Main {
    public static void main(String[] args) {
        // Caminho padrão do arquivo CF (pasta inputfiles na raiz do projeto)
        String caminho = "inputfiles/exemplo.cf";

        // Se o usuário passar um caminho por argumento, usa o dele
        if (args.length > 0) {
            caminho = args[0];
        }

        try {
            Path arquivo = Path.of(caminho);

            // Verifica se o arquivo existe
            if (!Files.exists(arquivo)) {
                System.err.println(" Arquivo não encontrado: " + caminho);
                return;
            }

            // Lê o conteúdo do arquivo
            String codigo = Files.readString(arquivo);

            System.out.println("=======================================");
            System.out.println("       COMPILADOR - COMPILA FOFO");
            System.out.println("=======================================");
            System.out.println("\n CÓDIGO FONTE:");
            System.out.println("---------------------------------------");
            System.out.println(codigo);

            // 1️⃣ Análise Léxica
            System.out.println("\n---------------------------------------");
            System.out.println(" ETAPA 1 - ANÁLISE LÉXICA");
            System.out.println("---------------------------------------");
            Lexer lexer = new Lexer(codigo);
            List<Token> tokens = lexer.analisar();
            tokens.forEach(System.out::println);

            // 2️⃣ Análise Sintática
            System.out.println("\n---------------------------------------");
            System.out.println(" ETAPA 2 - ANÁLISE SINTÁTICA");
            System.out.println("---------------------------------------");
            Parser parser = new Parser(tokens);
            parser.analisar();

            System.out.println("\n Compilação concluída com sucesso!");

            // 3️⃣ Código Intermediário
            parser.getGerador().imprimir();

            System.out.println("\n=======================================");

        } catch (IOException e) {
            System.err.println(" Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println(" Erro de compilação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
