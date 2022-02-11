package dao;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect {
    private String url_db;
    private String user;
    private String password;

    //Read setup database properties file
    public boolean readProperties() {
        Properties prop = new Properties();
        boolean flag = false;
        //For Windows Link
//        String fileConfig = "\\connection.properties";
        //For Macos link
        String fileConfig = "/connection.properties";
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + fileConfig);
//            System.out.println(currentDir + fileConfig);
            prop.load(inputStream);
            url_db = "jdbc:sqlserver://" + prop.getProperty("Hostname") + ":1433;databaseName = " + prop.getProperty("DatabaseName");
            user = prop.getProperty("SQLUser");
            password = prop.getProperty("SQLPassword");
            flag = true;
        } catch (IOException e) {
            System.out.println("Not setup database yet!");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    //Connect to database
    public Connection getDBConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url_db, user, password);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Cannot connect database");
        }
        return conn;
    }

    //Show alert
    public void showAlert(String title, String mess) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(mess);

        alert.showAndWait();
    }

    //Show setup database form if fisrt setup or wrong setup database
    public void showSetupDB() {
        Stage dbConnectStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/resources/views/setupDB.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        Scene scene = new Scene(root);
//        dbConnectStage.hide();
        scene.setFill(Color.TRANSPARENT);
        dbConnectStage.setScene(scene);
        dbConnectStage.initStyle(StageStyle.TRANSPARENT);
        dbConnectStage.showAndWait();
    }
}
