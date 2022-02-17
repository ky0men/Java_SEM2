package controller;

import com.jfoenix.controls.JFXButton;
import dao.DBConnect;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Room;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CheckoutController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private Label cusNameLabel;

    @FXML
    private Label cusIDLabel;

    @FXML
    private Label roomTypeLabel;

    @FXML
    private Label checkinTimeLabel;

    @FXML
    private Label roomPriceLabel;

    @FXML
    private Label checkoutTimeLabel;

    @FXML
    private JFXButton printBillBtn;

    @FXML
    private JFXButton paymentBtn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private Label roomNumberLabel;

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

        //Cancel button action
        cancelBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) titleBar.getScene().getWindow();
                stage.close();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);
            }
        });

        //Connect to database
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        roomNumberLabel.setText("Room "+getRoomName());
        roomTypeLabel.setText(getRoomType(conn, getRoomName()));
        roomPriceLabel.setText(getRoomPrice(conn, getRoomName()));
        cusIDLabel.setText(getCustomerID(conn, getRoomName()));
        cusNameLabel.setText(getCustomerName(conn, getCustomerID(conn, getRoomName())));
        checkinTimeLabel.setText(getCheckinTime(conn, getRoomName()));

        checkoutTimeLabel.setText(getCurrentTimeStamp());
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

    public String getRoomType(Connection conn, String roomNameChange) {
        ResultSet rs = null;
        CallableStatement cstm = null;
        String roomType = null;
        try {
            cstm = conn.prepareCall("{call getRoomType(?)}");
            cstm.setString(1, roomNameChange);
            rs = cstm.executeQuery();
            while (rs.next()) {
                roomType = rs.getString("roomTypeName");
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
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return roomType;
    }
    public String getRoomPrice(Connection conn, String roomName) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT R.roomPrice FROM Room R WHERE R.roomName = '"+roomName+"'");
            while(rs.next()){
                result = rs.getString("roomPrice");
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
        return result;
    }
    public String getCustomerID(Connection conn, String roomName) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT CI.cusIdentityNumber FROM Checkin CI WHERE CI.roomNumber = '"+roomName+"' AND CI.wasPayment = '0'");
            while(rs.next()){
                result = rs.getString("cusIdentityNumber");
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
        return result;
    }

    public String getCustomerName(Connection conn, String customerID) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT C.cusName FROM Customer C WHERE C.cusIdentityNumber = '"+customerID+"'");
            while(rs.next()){
                result = rs.getString("cusName");
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
        return result;
    }

    public String getCheckinTime(Connection conn, String roomNumber) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT CI.checkinDate FROM Checkin CI WHERE CI.roomNumber = '"+roomNumber+"' AND CI.wasPayment = '0'");
            while(rs.next()){
                result = rs.getString("checkinDate");
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
        return result;
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


}
