@echo off
:check
    c:/windows/system32/choice /t 1 /d y /n >nul
:restart
    echo restart program
    pause
    exit