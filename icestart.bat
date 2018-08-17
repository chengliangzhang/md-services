@echo off
:run
if "%2" == "" goto end
    echo start %2
    c:/windows/system32/taskkill /F /IM %2.exe >nul 2>nul
    c:/windows/system32/choice /t 1 /d y /n >nul
    if "%3" == "" goto last
    start /B %2 --Ice.Config=%1
    c:/windows/system32/choice /t 4 /d y /n >nul
    shift /2
goto run
:last
start /B /MIN /W %2 --Ice.Config=%1
:end
exit
