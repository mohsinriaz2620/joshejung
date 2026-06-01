@echo off
set JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-25.0.2\lib
set PROJECT_LIB=D:\BSCS - 15 - NUST\Semester 2\Object Oriented Programming\JoshEJung\lib

javac --module-path "%JAVAFX_PATH%" ^
      --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
      -cp "%PROJECT_LIB%\*" ^
      -d out ^
      src\*.java src\characters\*.java src\interfaces\*.java src\items\*.java src\game\*.java

java --module-path "%JAVAFX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
     -cp "out;%PROJECT_LIB%\*" ^
     Main

pause