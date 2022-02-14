package controller;

import com.jfoenix.controls.JFXButton;
import dao.DBConnect;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Room;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class CleanedConfirmController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private Label roomLabel;

    @FXML
    private JFXButton confirmBtn;

    @FXML
    private JFXButton cancelBtn;

    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Window move action
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

        //Show room name
        roomLabel.setText("Room " + getRoomName() + " ");

        //Cancel button action
        cancelBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String position = getAccountPosition();
                if (position.equals("Employee")) {
                    showStaffDashboard();
                } else {
                    showAdminDashboard();
                }
            }
        });

        //Confirm button action
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String roomName = getRoomName();
                changeStatusCleanedRoom(roomName);
                //Show notification login successful
                TrayNotification tray = new TrayNotification();
                tray.setTitle("Change room status successful");
                tray.setMessage("Now room " + roomName + " is available and ready for checkin.");
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));

                String position = getAccountPosition();

                if (position.equals("Employee")) {
                    showStaffDashboard();
                } else {
                    showAdminDashboard();
                }

            }
        });
    }

    public String getRoomName() {

        //Get column and row index from roomview
        FXMLLoader roomViewLoader = new FXMLLoader(getClass().getResource("/resources/views/RoomView.fxml"));
        try {
            Parent roomViewRoot = roomViewLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RoomViewController roomViewcontroller = roomViewLoader.getController();
        int row = roomViewcontroller.getRow();
        int col = roomViewcontroller.getCol();
//        System.out.println(row);
//        System.out.println(col);
        String gridRoomType = roomViewcontroller.getGridRoomType();

        //Get room name from room map
        FXMLLoader roomMapLoader = new FXMLLoader(getClass().getResource("/resources/views/RoomMap.fxml"));
        try {
            Parent roomMapRoot = roomMapLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RoomMapController roomMapController = roomMapLoader.getController();
        List<Room> rooms = null;
        String roomName;
        int index = (row * 4) - (4 - col);
        if (gridRoomType.equals("gridAllRoom")) {
            rooms = roomMapController.getListAllRoom();

        } else if (gridRoomType.equals("gridDirtyRoom")) {
            rooms = roomMapController.getListDirtyRoom();

        }

        roomName = rooms.get(index).getRoomName();
        return roomName;
    }

    public void changeStatusCleanedRoom(String roomName) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call changeStatusRoom (?, ?)}");
            cstm.setString(1, roomName);
            cstm.setString(2, "Available");
            cstm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    public void showStaffDashboard() {
        Parent staffParent = null;
        FXMLLoader staffLoader = new FXMLLoader(getClass().getResource("/resources/views/StaffDashboard.fxml"));
        try {
            staffParent = staffLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene staffScene = new Scene(staffParent);
        staffScene.setFill(Color.TRANSPARENT);
        stage = (Stage) confirmBtn.getScene().getWindow();
        stage.close();
        stage.setScene(staffScene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/hotel-icon.png")));
        stage.setTitle("Hotel Management Application");
        stage.show();
    }

    public void showAdminDashboard() {
        Parent adminParent = null;
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/resources/views/AdminDashboard.fxml"));
        try {
            adminParent = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene adminScene = new Scene(adminParent);
        adminScene.setFill(Color.TRANSPARENT);
        stage = (Stage) confirmBtn.getScene().getWindow();
        stage.close();
        stage.setScene(adminScene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/hotel-icon.png")));
        stage.setTitle("Hotel Management Application");
        stage.show();
    }

    //Check account position
    public String getAccountPosition() {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT AC.position, EM.fullName FROM Account AC JOIN EmployeeInformation EM ON AC.id = EM.userID \n" +
                    "    WHERE AC.accountStatus = '1'");
            while (rs.next()) {
                result = rs.getString("position");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

}
