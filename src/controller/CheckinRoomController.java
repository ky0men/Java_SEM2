package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
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
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CheckinRoomController implements Initializable {
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

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        identityNumber.getValidators().add(validator);
        customerName.getValidators().add(validator);
        RegexValidator numberValidator = new RegexValidator();
        numberValidator.setRegexPattern("^\\d+$");
        prepaidField.getValidators().add(numberValidator);
        discountField.getValidators().add(numberValidator);

        identityNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    validator.setMessage("Customer ID is required!");
                    identityNumber.validate();
                    String cusName = getCustomerNameFromID(identityNumber.getText(), conn);
                    if(cusName != null){
                        customerName.setText(cusName);
                        customerName.setEditable(false);
                    }else{
                        customerName.setEditable(true);
                    }
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

        prepaidField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!isInteger(prepaidField.getText())) {
                    numberValidator.setMessage("Please input integer number!");
                    prepaidField.validate();
                }
            }
        });

        discountField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!isInteger(discountField.getText())) {
                    numberValidator.setMessage("Please input integer number!");
                    discountField.validate();
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

        checkinBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String roomName = (String) roomNameComboBox.getValue();
                String today = String.valueOf(LocalDate.now());
                String todayTime = getDateTimeString();
                String checkinDay = String.valueOf(checkinDate.getValue());
                String checkoutDay = String.valueOf(checkoutDate.getValue());
                if (identityNumber.getText().equals("") && customerName.getText() == null) {
                    validator.setMessage("Customer ID is required!");
                    identityNumber.validate();
                    validator.setMessage("Customer name is required!");
                    customerName.validate();
                } else if (identityNumber.getText().equals("")) {
                    validator.setMessage("Customer ID is required!");
                    identityNumber.validate();
                } else if (customerName.getText() == null) {
                    validator.setMessage("Customer name is required!");
                    customerName.validate();
                } else if (!isInteger(prepaidField.getText())) {
                    numberValidator.setMessage("Please input integer number!");
                    prepaidField.validate();
                } else if (!isInteger(discountField.getText())) {
                    numberValidator.setMessage("Please input integer number!");
                    discountField.validate();
                } else if (checkinDay.equals(today) && isInteger(prepaidField.getText()) && isInteger(discountField.getText())) {
//                    System.out.println("Checkin Done");
                    changeStatusRentedRoom(roomName);
                    if (getCustomerNameFromID(identityNumber.getText(), conn) == null) {
                        addNameAndIdCustomer(identityNumber.getText(), customerName.getText());
//                        System.out.println("Add Cus Done");
                    }
                    addCheckin(conn, identityNumber.getText(), roomName, todayTime, checkoutDay, prepaidField.getText(), discountField.getText());

                    String position = getAccountPosition();
                    if (position.equals("Employee")) {
                        showStaffDashboard();
                    } else {
                        showAdminDashboard();
                    }
                } else {
                    TrayNotification tray = new TrayNotification();
                    tray.setTitle("Checkin date is not today");
                    tray.setMessage("Please booking if checkin is not today.");
                    tray.setNotificationType(NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                }
            }
        });
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
            rs = stm.executeQuery("SELECT * FROM Room R WHERE R.roomStatus = 'Available'");
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

    public void addCheckin(Connection conn, String cusIdNum, String roomName, String checkinDate, String checkOutDate, String prepaid, String discount) {
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call addCheckin(?, ?, ?, ?, ?, ?)}");
            cstm.setString(1, cusIdNum);
            cstm.setString(2, roomName);
            cstm.setString(3, checkinDate);
            cstm.setString(4, checkOutDate);
            cstm.setString(5, prepaid);
            cstm.setString(6, discount);
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

    public String getDateTimeString() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    //Change status rented room
    public void changeStatusRentedRoom(String roomName) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call changeStatusRoom (?, ?)}");
            cstm.setString(1, roomName);
            cstm.setString(2, "Rented");
            cstm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addNameAndIdCustomer(String cusID, String cusName) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call addNameAndIDCustomer (?, ?)}");
            cstm.setString(1, cusID);
            cstm.setString(2, cusName);
            cstm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
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
        stage = (Stage) checkinBtn.getScene().getWindow();
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
        stage = (Stage) checkinBtn.getScene().getWindow();
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
}
