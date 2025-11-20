@echo off
REM Script de compilação do Compilador Compila Fofo
REM Uso: compile.bat

echo =========================================
echo   Compilando Compilador Compila Fofo
echo =========================================

echo.
echo Limpando pasta bin...
if exist bin rmdir /s /q bin
mkdir bin

echo.
echo Compilando arquivos Java...
javac -encoding UTF-8 -d bin src/codegen/*.java src/Tipo_de_dados/*.java src/TabelaDeSimbolos/*.java src/lexer/*.java src/parser/*.java src/main/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo =========================================
    echo   Compilacao concluida com sucesso!
    echo =========================================
    echo.
    echo Para executar:
    echo   java -cp bin main.Main [arquivo.cf]
    echo.
) else (
    echo.
    echo =========================================
    echo   Erro na compilacao!
    echo =========================================
    echo.
)

pause

