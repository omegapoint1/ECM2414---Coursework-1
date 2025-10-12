@echo off
REM Compile main sources only
javac -d build/classes src\main\java\cardgame\*.java
IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b %ERRORLEVEL%
)

REM Run the main program
java -cp build\classes cardgame.CardGame

pause
