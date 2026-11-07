@echo off
rem Usage: run.bat <MainClass> [args...]      e.g.  run.bat JavaSerializationLab
setlocal
cd /d "%~dp0"
chcp 65001 >nul
call mvn -q compile dependency:build-classpath -Dmdep.outputFile=target\cp.txt
if errorlevel 1 exit /b 1
set /p DEPS=<target\cp.txt
java -Dfile.encoding=UTF-8 -cp "target\classes;%DEPS%" %*
