@echo off
echo Building ImageLab Pro for Windows...
mkdir build 2>nul
javac -encoding UTF-8 -d build src\*.java
echo Build complete! Run with: java -cp build Main
echo.
echo Creating launcher...
echo @echo off > run.bat
echo java -cp "build" Main %%* >> run.bat
echo java -cp "build" Main %%* > ImageLabPro.bat
echo.
echo To run: double-click run.bat or ImageLabPro.bat
pause