package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class BookingServiceController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField roomNumber;

    @FXML
    private JFXComboBox<?> serviceTypeCombobox;

    @FXML
    private JFXComboBox<?> serviceCombobox;

    @FXML
    private JFXTextField unitServiceLabel;

    @FXML
    private JFXTextField serviceQty;

    @FXML
    private Label validateMessage;

    @FXML
    private JFXButton bookingServiceBtn;

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

        //Connect to database
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        roomNumber.setText(getRoomName());

        //Populate ServiceType
        potulateComboBox(conn, serviceTypeCombobox, "SELECT * FROM ServiceType", "ServiceType");

        serviceTypeCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {

            @Override
            public void changed(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
                //Populata service name
                potulateComboBox(conn, serviceCombobox, "SELECT * FROM Service S WHERE S.ServiceType = " + "'" +(String) newValue + "'", "ServiceName");
                validateMessage.setVisible(false);
            }
        });

        serviceCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
                validateMessage.setVisible(false);
                unitServiceLabel.setText(getServiceUnit(conn, String.valueOf(serviceCombobox.getValue())));
            }
        });

        serviceQty.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                validateMessage.setVisible(false);
                if(!isInteger(serviceQty.getText())){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Please input integer number!");
                }
            }
        });


        bookingServiceBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(serviceTypeCombobox.getValue() == null && serviceCombobox.getValue() == null && serviceQty.getText().equals("")){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Please chose service type, service name and quantity before search!");
                }else if(serviceTypeCombobox.getValue() == null){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Please chose service type before search!");
                }else if(serviceCombobox.getValue() == null){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Please chose service name before search!");
                }else if(serviceQty.getText().equals("")){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Please input service quantity!");
                }else if(!isInteger(serviceQty.getText())){
                    validateMessage.setVisible(true);
                    validateMessage.setText("Quantity is number, please input number!");
                }else if(serviceTypeCombobox.getValue() != null && serviceCombobox.getValue() != null && !serviceQty.getText().equals("") && isInteger(serviceQty.getText())){
                    CallableStatement cstm = null;
                    try {
                        cstm = conn.prepareCall("{call addUsedService(?, ?, ?)}");
                        cstm.setString(1, getCheckinID(conn, getRoomName()));
                        cstm.setString(2, getServiceID(conn, String.valueOf(serviceCombobox.getValue())));
                        cstm.setString(3, serviceQty.getText());
                        int rowEffect = cstm.executeUpdate();
                        if(rowEffect != 0){
                            TrayNotification tray = new TrayNotification();
                            tray.setTitle("Booking service successful");
                            tray.setNotificationType(NotificationType.SUCCESS);
                            tray.setAnimationType(AnimationType.POPUP);
                            tray.showAndDismiss(Duration.seconds(2));
                            //Close stage booking service
//                            GaussianBlur blur = new GaussianBlur(0);
//                            LoginController.stage.getScene().getRoot().setEffect(blur);
//                            Node node = (Node)actionEvent.getSource();
//                            Stage stage = (Stage)node.getScene().getWindow();
//                            stage.close();
                            String position = getAccountPosition();
                            if (position.equals("Employee")) {
                                showStaffDashboard();
                            } else {
                                showAdminDashboard();
                            }

                        }else{
                            TrayNotification tray = new TrayNotification();
                            tray.setTitle("Booking service failed!");
                            tray.setMessage("Something went wrong, please check again!");
                            tray.setNotificationType(NotificationType.ERROR);
                            tray.setAnimationType(AnimationType.POPUP);
                            tray.showAndDismiss(Duration.seconds(2));
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

    }

    //Get room number
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

        } else if (gridRoomType.equals("gridAvailableRoom")) {
            rooms = roomMapController.getListAvailableRoom();

        }

        roomName = rooms.get(index).getRoomName();
        return roomName;
    }

    public void potulateComboBox(Connection conn, JFXComboBox comboBox, String sqlString, String column) {

        ResultSet rs = null;
        Statement stm = null;
        ObservableList<String> serviceType = FXCollections.observableArrayList();
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(sqlString);
            while (rs.next()) {
                serviceType.add(rs.getString(column));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        comboBox.setItems(serviceType);
    }


    public boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getCheckinID(Connection conn, String roomNumber){
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT CI.checkinID FROM Checkin CI WHERE roomNumber = '"+ roomNumber +"' AND wasPayment = '0'");
            while (rs.next()){
                result = rs.getString("checkinID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public String getServiceID(Connection conn, String serviceName){
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT S.ID FROM Service S WHERE S.ServiceName = '"+ serviceName +"'");
            while (rs.next()){
                result = rs.getString("ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    //Get service Unit
    public String getServiceUnit(Connection conn, String serviceName){
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT S.Unit FROM Service S WHERE S.ServiceName = '"+ serviceName +"'");
            while (rs.next()){
                result = rs.getString("Unit");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
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
        stage = (Stage) bookingServiceBtn.getScene().getWindow();
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
        stage = (Stage) bookingServiceBtn.getScene().getWindow();
        stage.close();
        stage.setScene(adminScene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/hotel-icon.png")));
        stage.setTitle("Hotel Management Application");
        stage.show();
    }
}
