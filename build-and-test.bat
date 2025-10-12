@echo off
REM Compile sources
javac -cp "lib/junit-platform-console-standalone-1.14.0.jar" -d build\classes src\main\java\cardgame\*.java src\test\cardgame\*.java
IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b %ERRORLEVEL%
)

REM Run tests
java -jar lib\junit-platform-console-standalone-1.14.0.jar -cp "build\classes" --scan-classpath

pause
