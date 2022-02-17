package controller;

import com.jfoenix.controls.JFXButton;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Room;
import models.UsedServices;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CheckoutController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private Label roomNumberLabel;

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
    private Label timePriceLabel;

    @FXML
    private Label usedTimeLabel;

    @FXML
    private TableView<UsedServices> usedServiceTable;

    @FXML
    private TableColumn<?, ?> noIDCol;

    @FXML
    private TableColumn<?, ?> serviceNameCol;

    @FXML
    private TableColumn<?, ?> priceCol;

    @FXML
    private TableColumn<?, ?> quantityCol;

    @FXML
    private TableColumn<?, ?> unitCol;

    @FXML
    private TableColumn<?, ?> sumCol;

    @FXML
    private Label roomChargeLabel;

    @FXML
    private Label serviceChargeLabel;

    @FXML
    private Label prepaidLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label changeLabel;

    @FXML
    private JFXButton printBillBtn;

    @FXML
    private JFXButton paymentBtn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private Label usedDaysLabel;

    @FXML
    private Label usedHoursLable;

    @FXML
    private Label usedMinsLabel;

    private double x, y;
    Stage stage;
    double serviceCharge;

    ObservableList<UsedServices> usedServicesData = FXCollections.observableArrayList();

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

        roomNumberLabel.setText("Room " + getRoomName());
        roomTypeLabel.setText(getRoomType(conn, getRoomName()));
        roomPriceLabel.setText(getRoomPrice(conn, getRoomName()));
        cusIDLabel.setText(getDataFromCheckin(conn, getRoomName(), "cusIdentityNumber"));
        cusNameLabel.setText(getCustomerName(conn, getDataFromCheckin(conn, getRoomName(), "cusIdentityNumber")));
        checkinTimeLabel.setText(getDataFromCheckin(conn, getRoomName(), "checkinDate"));

        checkoutTimeLabel.setText(getCurrentTimeStamp());
        timePriceLabel.setText(getRoomTimePrice(conn, getRoomName()));

        calculateTime();
        calculateRoomCharge();


        //Populate data to table

        buildDataForTable(conn, getRoomName());

        roomChargeLabel.setText(String.valueOf(calculateRoomCharge()));
        prepaidLabel.setText(getDataFromCheckin(conn, getRoomName(), "prepaid"));
        discountLabel.setText(getDataFromCheckin(conn, getRoomName(), "discount"));
        serviceChargeLabel.setText(String.valueOf(serviceCharge));
        totalLabel.setText(String.valueOf(getTotal()));
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
            rs = stm.executeQuery("SELECT R.roomPrice FROM Room R WHERE R.roomName = '" + roomName + "'");
            while (rs.next()) {
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

    public String getRoomTimePrice(Connection conn, String roomName) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT R.roomTimePrice FROM Room R WHERE R.roomName = '" + roomName + "'");
            while (rs.next()) {
                result = rs.getString("roomTimePrice");
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

    public String getDataFromCheckin(Connection conn, String roomName, String column) {
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT * FROM Checkin CI WHERE CI.roomNumber = '" + roomName + "' AND CI.wasPayment = '0'");
            while (rs.next()) {
                result = rs.getString(column);
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
            rs = stm.executeQuery("SELECT C.cusName FROM Customer C WHERE C.cusIdentityNumber = '" + customerID + "'");
            while (rs.next()) {
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

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void buildDataForTable(Connection conn, String roomNumber) {
        ResultSet rs = null;
        Statement stm = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT S.ServiceName, S.Price, SUM(US.usedServiceQty) AS N'Quantity', S.Unit FROM usedServices US JOIN Service S ON US.usedServiceID = S.ID JOIN Checkin CI ON CI.checkinID = US.checkinID WHERE CI.roomNumber = '" + roomNumber + "' AND CI.wasPayment = '0' GROUP BY S.ServiceName, S.Price, S.Unit");
            int id = 0;
            while (rs.next()) {
                UsedServices usedServices = new UsedServices();
                usedServices.setId(++id);
                usedServices.setServiceName(rs.getString("ServiceName"));
                usedServices.setPrice(Double.parseDouble(rs.getString("Price")));
                usedServices.setQuantity(Integer.parseInt(rs.getString("Quantity")));
                usedServices.setUnit(rs.getString("Unit"));
                usedServices.setSum(Double.parseDouble(rs.getString("Price")) * Double.parseDouble(rs.getString("Quantity")));
                usedServicesData.add(usedServices);
                serviceCharge += usedServices.getSum();
            }
            usedServiceTable.setItems(usedServicesData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        usedServiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        noIDCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        serviceNameCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        quantityCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        unitCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        sumCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        noIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        serviceNameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        sumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));

        usedServiceTable.setItems(usedServicesData);

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
        stage = (Stage) paymentBtn.getScene().getWindow();
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
        stage = (Stage) paymentBtn.getScene().getWindow();
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

    public void calculateTime() {
//        System.out.println(checkinTimeLabel.getText());
//        System.out.println(checkoutTimeLabel.getText());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = formatter.parse(checkinTimeLabel.getText());
            date2 = formatter.parse(checkoutTimeLabel.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = date2.getTime() - date1.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) ((diff - days * 1000 * 60 * 60 * 24) / (1000 * 60 * 60));
//        int hours = (int) (diff / (1000 * 60 * 60));
        int mins = (int) ((diff - days * 1000 * 60 * 60 * 24 - hours * 1000 * 60 * 60) / (1000 * 60));

        usedDaysLabel.setText(String.valueOf(days));
        usedHoursLable.setText(String.valueOf(hours));
        usedMinsLabel.setText(String.valueOf(mins));

    }

    public double calculateRoomCharge() {
        Double usedDays = Double.parseDouble(usedDaysLabel.getText());
        Double usedHours = Double.parseDouble(usedHoursLable.getText());
        Double usedMins = Double.parseDouble(usedMinsLabel.getText());
        Double roomPrice = Double.parseDouble(roomPriceLabel.getText());
        Double roomTimePrice = Double.parseDouble(timePriceLabel.getText());
        Double roomCharge = 0.0;
        if (usedMins > 15) {
            usedHours++;
        }
        if (usedHours >= 8) {
            usedDays++;
            usedHours = 0.0;
        }
        if (usedHours != 0) {
            roomCharge = usedDays * roomPrice + roomTimePrice + (usedHours - 1) * 30000;

        }else{
            roomCharge = usedDays * roomPrice;
        }
//        System.out.println(roomCharge);
        return roomCharge;
    }

    public double getTotal(){
        Double roomCharge = Double.valueOf(roomChargeLabel.getText());
        Double prepaid = Double.valueOf(prepaidLabel.getText());
        Double discount = Double.valueOf(discountLabel.getText());
        double total = roomCharge + serviceCharge - prepaid - discount;
        return total;
    }
}
