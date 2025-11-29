package main;

import lexer.*;
import parser.Parser;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String caminho = "inputfiles/exemplo.cf";
        if (args.length > 0) caminho = args[0];

        try {
            Path arquivo = Path.of(caminho);

            if (!Files.exists(arquivo)) {
                System.err.println("Arquivo não encontrado: " + caminho);
                return;
            }

            String codigo = Files.readString(arquivo);

            System.out.println("=======================================");
            System.out.println("       COMPILADOR - COMPILA FOFO");
            System.out.println("=======================================\n");

            System.out.println("CÓDIGO FONTE:");
            System.out.println("---------------------------------------");
            System.out.println(codigo + "\n");

            // 1. LÉXICO
            System.out.println("---------------------------------------");
            System.out.println("ETAPA 1 - ANÁLISE LÉXICA");
            System.out.println("---------------------------------------");

            Lexer lexer = new Lexer(codigo);
            List<Token> tokens = lexer.analisar();
            tokens.forEach(System.out::println);

            // 2. SINTÁTICO + SEMÂNTICO + MIPS
            System.out.println("\n---------------------------------------");
            System.out.println("ETAPA 2 - ANÁLISE SINTÁTICA / SEMÂNTICA \n");

            Parser parser = new Parser(tokens);
            parser.analisar();
             System.out.println(" ANÁLISE SINTÁTICA REALIZADA COM SUCESSO.");
             System.out.println(" ANÁLISE SEMÂNTICA REALIZADA COM SUCESSO.");
           

            // 3. GERAR ARQUIVO .ASM (SEM EXIBIR NO TERMINAL)
            String codigoMIPS = parser.getCodigoGeradoMIPS();
            String saidaAsm = "outputfiles/saida.asm";

            Files.writeString(Path.of(saidaAsm), codigoMIPS);

            System.out.println("---------------------------------------");
            System.out.println("ETAPA 3 - GERAÇÃO DE CÓDIGO (MIPS)");
            System.out.println("---------------------------------------");
            System.out.println(" Código salvo em: " + saidaAsm);
            

            
            System.out.println("\n Compilação concluída com sucesso!");
            System.out.println("=======================================\n");

        } catch (RuntimeException e) {
            System.err.println("\n Erro de compilação: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("\n Erro ao ler/escrever arquivos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
