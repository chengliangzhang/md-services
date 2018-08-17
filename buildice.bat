del /s /q "c:/work/ice/%1"
if not exist "c:/work/ice/%1" (mkdir "c:/work/ice/%1")
for %%i in (services/src/main/resources/zeroc/*.ice) do (slice2%1 -I services/src/main/resources/zeroc --output-dir "C:/work/ice/%1" services/src/main/resources/zeroc/%%i)