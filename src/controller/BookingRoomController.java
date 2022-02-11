package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Room;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BookingRoomController implements Initializable {
    @FXML
    private HBox titleBar;

    @FXML
    private JFXButton checkinBtn;

    @FXML
    private JFXButton bookingBtn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXTextField identityNumber;

    @FXML
    private JFXTextField customerName;

    @FXML
    private JFXComboBox<Room> roomNameComboBox;

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

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        identityNumber.getValidators().add(validator);
        customerName.getValidators().add(validator);

        identityNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    validator.setMessage("Customer ID is required!");
                    identityNumber.validate();
                    String cusName = getCustomerNameFromID(identityNumber.getText(), conn);
                    customerName.setText(cusName);
                }
            }
        });

        customerName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    validator.setMessage("Customer name is required!");
                    customerName.validate();
                }
            }
        });

        //Populate data to room name combobox
//        potulateRoomName(conn, roomNameComboBox);



    }

    public String getCustomerNameFromID(String id, Connection conn){
        ResultSet rs = null;
        CallableStatement cstm = null;
        String result = null;
        try {
            cstm = conn.prepareCall("{call getCusNameFromIDNumber(?)}");
            cstm.setString(1, id);
            rs = cstm.executeQuery();
            if(rs.next()){
                result = rs.getString("cusName");

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

//    public void potulateRoomName(Connection conn, JFXComboBox comboBox){
//        ResultSet rs = null;
//        Statement stm = null;
//        ObservableList<Room> listRoomName = FXCollections.observableArrayList();
//        try {
//            stm = conn.createStatement();
//            rs = stm.executeQuery("SELECT Room.roomName FROM Room");
//            if(rs.next()){
//                listRoomName.add(new Room(rs.getString("roomName")));
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        comboBox.setItems(listRoomName);
//        comboBox.setConverter(new StringConverter<Room>() {
//            @Override
//            public String toString(Room room) {
//                return room.getRoomName();
//            }
//
//            @Override
//            public Room fromString(String s) {
//                return null;
//            }
//        });
//    }
}
