# Java_SEM2
This project use IDE IntelliJ and framework JavaFX, tool SceneBuilder 15, JDK 15.0.1
How to run project:
  1. Import project to IDE
  2. Run SQL file to setup database to Sql Server on your computer
  3. Add libary in the folder lib
  4. Add VM Option:
  
    --module-path  $PATH_TO_YOUR_JAVAFX_SDK$
    --add-modules
    javafx.controls,javafx.fxml
    --add-exports
    javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
    --add-exports
    javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
    --add-exports
    javafx.base/com.sun.javafx.binding=ALL-UNNAMED
    --add-exports
    javafx.base/com.sun.javafx.event=ALL-UNNAMED
    --add-exports
    javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED
    --add-exports
    javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
    --add-exports
    javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
    --add-exports
    javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
    --illegal-access=warn
    
For example: replace $PATH_TO_YOUR_JAVAFX_SDK$ to C:/javafx-sdk-15.0.1/lib

You can download javafx-sdk-15.0.1 here: https://drive.google.com/file/d/1_EB8l6xNwQJOwR6zk_EKzJyRtVL0f4LW/view?usp=sharing

