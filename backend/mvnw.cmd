@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
set "SCRIPT_DIR=%~dp0"
if exist "%SCRIPT_DIR%..\.tools\apache-maven-3.9.9\bin\mvn.cmd" (
    "%SCRIPT_DIR%..\.tools\apache-maven-3.9.9\bin\mvn.cmd" %*
) else (
    mvn %*
)
