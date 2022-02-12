package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
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
import javafx.scene.Parent;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import models.Room;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class BookingRoomController implements Initializable {
    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField identityNumber;

    @FXML
    private JFXTextField customerName;

    @FXML
    private JFXComboBox<?> roomNameComboBox;

    @FXML
    private DatePicker checkinDate;

    @FXML
    private JFXTextField roomType;

    @FXML
    private DatePicker checkoutDate;

    @FXML
    private JFXTextField prepaidField;

    @FXML
    private JFXTextField discountField;

    @FXML
    private JFXButton checkinBtn;

    @FXML
    private JFXButton bookingBtn;

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

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        identityNumber.getValidators().add(validator);
        customerName.getValidators().add(validator);

        identityNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
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
                if (!newValue) {
                    validator.setMessage("Customer name is required!");
                    customerName.validate();
                }
            }
        });

        //Populate data to room name combobox

        potulateRoomName(conn, roomNameComboBox);

        //Set room type from room name
        roomType.setText(getRoomType(conn, getRoomName()));

        //Combo box change event
        roomNameComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
//                System.out.println(newValue);
                roomType.setText(getRoomType(conn, (String) newValue));
            }
        });

        //Set local date for checkin date
        checkinDate.setValue(LocalDate.now());
        System.out.println(LocalDateTime.now());

        //Set checkout date default is one date after checkin
        checkoutDate.setValue(LocalDate.now().plusDays(1));

        checkinDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate oldValue, LocalDate newValue) {
                checkoutDate.setValue(newValue.plusDays(1));
//                System.out.println(newValue);
            }
        });

        //Disable date from past
        checkinDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
                if (date.compareTo(today) < 0) {
                    setStyle("-fx-background-color: #ffc0cb;");

                }
            }
        });
        checkoutDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
                if (date.compareTo(today) < 0) {
                    setStyle("-fx-background-color: #ffc0cb;");

                }
            }
        });

//        checkinBtn.setOnAction(new EventHandler<ActionEvent>() {
//            String roomName = (String) roomNameComboBox.getValue();
//            String today = String.valueOf(LocalDate.now());
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                if (checkinDate.getValue() == LocalDate.now()) {
//                    addCheckin(conn, identityNumber.getText(), roomName), LocalDate.now(), checkoutDate.getValue(), prepaidField.getText(), discountField.getText());
//                }
//            }
//        });
    }

    public String getCustomerNameFromID(String id, Connection conn) {
        ResultSet rs = null;
        CallableStatement cstm = null;
        String result = null;
        try {
            cstm = conn.prepareCall("{call getCusNameFromIDNumber(?)}");
            cstm.setString(1, id);
            rs = cstm.executeQuery();
            if (rs.next()) {
                result = rs.getString("cusName");

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public void potulateRoomName(Connection conn, JFXComboBox comboBox) {

        ResultSet rs = null;
        Statement stm = null;
        ObservableList<String> listRoomName = FXCollections.observableArrayList();
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT * FROM Room");
            while (rs.next()) {
                listRoomName.add(rs.getString("roomName"));
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
        comboBox.setItems(listRoomName);
        int indexRoomName = listRoomName.indexOf(getRoomName());
        comboBox.getSelectionModel().select(indexRoomName);
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

    public void addCheckin(Connection conn, String cusIdNum, String roomName, String checkinDate, String checkOutDate, int prepaid, int discount) {
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call addCheckin(?, ?, ?, ?, ?, ?)}");
            cstm.setString(1, cusIdNum);
            cstm.setString(2, roomName);
            cstm.setString(3, checkinDate);
            cstm.setString(4, checkOutDate);
            cstm.setInt(5, prepaid);
            cstm.setInt(6, discount);
            int effectedRow = cstm.executeUpdate();
            if (effectedRow > 0) {
                TrayNotification tray = new TrayNotification();
                tray.setTitle("Checkin Successful");
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(2));
            } else {
                TrayNotification tray = new TrayNotification();
                tray.setTitle("Check in failed");
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
