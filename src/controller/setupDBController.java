package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class setupDBController implements Initializable {
    @FXML
    private JFXTextField txtHostname;

    @FXML
    private JFXTextField txtDBName;

    @FXML
    private JFXTextField txtSQLUser;

    @FXML
    private JFXTextField txtSQLPass;

    @FXML
    private HBox loadingDB;

    @FXML
    private AnchorPane titleBar;

    @FXML
    private Button minimizeBtn;

    @FXML
    private FontIcon minimizeBtn1;

    @FXML
    private Button closeBtn;

    @FXML
    private FontIcon closeBtn1;

    @FXML
    private JFXButton setDBBtn;

    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Hide progress bar
        loadingDB.setVisible(false);

        //Window close, minimize and close action
        titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                titleBar.getScene().getWindow().setX(event.getScreenX() - x);
                titleBar.getScene().getWindow().setY(event.getScreenY() - y);
            }
        });
        closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage = (Stage) titleBar.getScene().getWindow();
                stage.close();
            }
        });
        minimizeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage = (Stage) titleBar.getScene().getWindow();
                stage.setIconified(true);
            }
        });


        //Set database button action
        setDBBtn.setOnAction(new EventHandler<ActionEvent> () {
            File fileConfig = new File("connection.properties");
            FileOutputStream fileOps = null;
            BufferedOutputStream bOut = null;
            @Override
            public void handle(ActionEvent actionEvent) {
                loadingDB.setVisible(true);
                try {
                    fileOps = new FileOutputStream(fileConfig);
                    bOut = new BufferedOutputStream(fileOps);
                    bOut.write(("Hostname=" + txtHostname.getText() + "\n").getBytes());
                    bOut.write(("DatabaseName=" + txtDBName.getText() + "\n").getBytes());
                    bOut.write(("SQLUser=" + txtSQLUser.getText() + "\n").getBytes());
                    bOut.write(("SQLPassword=" + txtSQLPass.getText() + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bOut != null) {
                        try {
                            bOut.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                    if (fileOps != null) {
                        try {
                            fileOps.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                }
//                DBConnect.readProperties();
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(2));
                pt.setOnFinished(e ->{
                    stage = (Stage) titleBar.getScene().getWindow();
                    stage.close();
                    //Show notifycation successful
                    TrayNotification tray = new TrayNotification();
                    tray.setTitle("Config database successfuly");
                    tray.setMessage("Now you can login to use the application");
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.setNotificationType(NotificationType.SUCCESS);
                    tray.showAndDismiss(Duration.seconds(3));
                });
                pt.play();


            }
        });
    }
}
