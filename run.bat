@echo off
REM Script de execução do Compilador Compila Fofo
REM Uso: run.bat [arquivo.cf]

if "%1"=="" (
    echo Executando com arquivo padrao: inputfiles/teste.cf
    java -cp bin main.Main inputfiles/teste.cf
) else (
    echo Executando com arquivo: %1
    java -cp bin main.Main %1
)

pause

