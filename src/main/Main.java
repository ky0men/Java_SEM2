package main;

import dao.DBConnect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader;
        Parent root;
        Scene scene;

        loader = new FXMLLoader(getClass().getResource("/resources/views/Login.fxml"));
        root = loader.load();
        stage.setTitle("Login");
        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/hotel-icon.png")));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        //Check first setup application and database
        DBConnect dbConnect = new DBConnect();
        if (!dbConnect.readProperties()) {
            dbConnect.showAlert("The fisrt time config database","The fisrt time config database. \n \t1. Make sure you imported database to SQL Server\n \t2. Fill your database information to the next form ");
//            stage.hide();
            dbConnect.showSetupDB();
//            stage.show();
        }
//        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
