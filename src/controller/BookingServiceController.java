package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Room;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private JFXTextField serviceQty;

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

        roomNumber.setText(getRoomName());

        //Populate ServiceType
        potulateComboBox(conn, serviceTypeCombobox, "SELECT * FROM ServiceType", "ServiceType");

        serviceTypeCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {

            @Override
            public void changed(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
                //Populata service name
                potulateComboBox(conn, serviceCombobox, "SELECT * FROM Service S WHERE S.ServiceType = " + "'" +(String) newValue + "'", "ServiceName");
                System.out.println("SELECT * FROM Service S WHERE S.ServiceType = " + "'" +(String) newValue + "'");
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
}
