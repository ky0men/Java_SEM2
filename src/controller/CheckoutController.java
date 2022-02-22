package controller;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Room;
import models.UsedServices;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private JFXButton cancelBtn;

    @FXML
    private Label usedDaysLabel;

    @FXML
    private Label usedHoursLable;

    @FXML
    private Label usedMinsLabel;

    @FXML
    private JFXTextField customerCash;

    @FXML
    private Label changeValidateLabel;

    private double x, y;
    Stage stage;
    double serviceCharge;
    double change;
    double roomPriceDouble, roomTimePriceDouble, roomChargeDouble, prepaidDouble, discountDouble, totalDouble;
    String pdfFileName = null;

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

        roomPriceDouble = Double.parseDouble(getRoomPrice(conn, getRoomName()));
        roomTimePriceDouble = Double.parseDouble(getRoomTimePrice(conn, getRoomName()));


        changeValidateLabel.setVisible(false);
        roomNumberLabel.setText("Room " + getRoomName());
        roomTypeLabel.setText(getRoomType(conn, getRoomName()));
        roomPriceLabel.setText(formatCurrency(getRoomPrice(conn, getRoomName())));
        cusIDLabel.setText(getDataFromCheckin(conn, getRoomName(), "cusIdentityNumber"));
        cusNameLabel.setText(getCustomerName(conn, getDataFromCheckin(conn, getRoomName(), "cusIdentityNumber")));
        checkinTimeLabel.setText(getDataFromCheckin(conn, getRoomName(), "checkinDate"));

        checkoutTimeLabel.setText(getCurrentTimeStamp());
        timePriceLabel.setText(formatCurrency(getRoomTimePrice(conn, getRoomName())));

        calculateTime();
        roomChargeDouble = calculateRoomCharge();


        //Populate data to table

        buildDataForTable(conn, getRoomName());

        prepaidDouble = Double.parseDouble(getDataFromCheckin(conn, getRoomName(), "prepaid"));
        discountDouble = Double.parseDouble(getDataFromCheckin(conn, getRoomName(), "discount"));

        roomChargeLabel.setText(formatCurrency(String.valueOf(calculateRoomCharge())));
        prepaidLabel.setText(formatCurrency(getDataFromCheckin(conn, getRoomName(), "prepaid")));
        discountLabel.setText(formatCurrency(getDataFromCheckin(conn, getRoomName(), "discount")));
        serviceChargeLabel.setText(formatCurrency(String.valueOf(serviceCharge)));
        totalLabel.setText(formatCurrency(String.valueOf(getTotal())));
        totalDouble = getTotal();


        //Print bill action
        printBillBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(customerCash.getText().equals("")){
                    changeValidateLabel.setVisible(true);
                    changeValidateLabel.setText("Please input customer cash!");
                }else if(!isInteger(customerCash.getText())){
                    changeValidateLabel.setVisible(true);
                    changeValidateLabel.setText("Please input number!");
                }else if((Double.parseDouble(customerCash.getText()) - totalDouble) < 0){
                    changeValidateLabel.setVisible(true);
                    changeValidateLabel.setText("Change must be great or equal than zero!");
                }else{
                    changeValidateLabel.setVisible(false);
                    printBill();
                    changeStatusDirtyRoom(getRoomName());
                    changeWasPayment(getRoomName());
                    addBill();
                    String position = getAccountPosition();
                    if (position.equals("Employee")) {
                        showStaffDashboard();
                    } else {
                        showAdminDashboard();
                    }
                    openInvoice();
                }

            }
        });

        customerCash.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(!isInteger(newValue)){
                    changeValidateLabel.setVisible(true);
                    changeValidateLabel.setText("Please input number!");
                }else{
                    changeValidateLabel.setVisible(false);
                    change = Double.parseDouble(customerCash.getText()) - totalDouble;
                    if(change >= 0){
                        changeLabel.setText(formatCurrency(String.valueOf(change)));
                        changeValidateLabel.setVisible(false);
                    }else{
                        changeLabel.setText("");
                        changeValidateLabel.setVisible(true);
                        changeValidateLabel.setText("Change must be great or equal than zero!");
                    }
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

        } else if (gridRoomType.equals("gridRentedRoom")) {
            rooms = roomMapController.getListRentedRoom();

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
    public String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    public String getCurrentMonth() {
        return new SimpleDateFormat("MM").format(new Date());
    }
    public String getCurrentYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }
    public String getCurrentTimeForInvoiceName() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    //Populate table
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
        stage = (Stage) printBillBtn.getScene().getWindow();
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
        stage = (Stage) printBillBtn.getScene().getWindow();
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
        Double roomPrice = roomPriceDouble;
        Double roomTimePrice = roomTimePriceDouble;
        Double roomCharge = 0.0;
        if (usedMins >= 0) {
            usedHours++;
        }
        if (usedHours >= 8) {
            usedDays++;
            usedHours = 0.0;
        }
        if (usedHours != 0) {
            roomCharge = usedDays * roomPrice + roomTimePrice + (usedHours - 1) * 30000;

        } else {
            roomCharge = usedDays * roomPrice;
        }
//        System.out.println(roomCharge);
        return roomCharge;
    }

    public double getTotal() {
        Double roomCharge = roomChargeDouble;
        Double prepaid = prepaidDouble;
        Double discount = discountDouble;
        double total = roomCharge + serviceCharge - prepaid - discount;
        return total;
    }

    //Print bill function
    public void printBill() {
//        String path = "D:\\invoice.pdf";
        pdfFileName = "Invoice_" + getCurrentTimeForInvoiceName() + ".pdf";
        File path = new File(pdfFileName);

        try {
            PdfWriter pdfWriter = new PdfWriter(String.valueOf(path));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            float col = 280f;
            float columnWidth[] = {col, col, col};
            Table table = new Table(columnWidth);

            // Load hotel image logo
            com.itextpdf.layout.element.Image logo = null;
            ImageData imageData = ImageDataFactory.create(getClass().getResource("/resources/images/hotel-icon.png"));
            logo = new com.itextpdf.layout.element.Image(imageData);
            logo.scaleToFit(100f, 100f);

            table.setBackgroundColor(new DeviceRgb(63, 169, 219)).setFontColor(com.itextpdf.kernel.color.Color.WHITE);
            table.addCell(new Cell().add("INVOICE").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setMarginTop(10f)
                    .setMarginBottom(10f)
                    .setFontSize(20f)
                    .setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(logo)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER)
                    .setMarginLeft(10f));
            table.addCell(new Cell().add("LOTUS HOTEL \n Address: 38 Yen Bai,\nHai Chau, Da Nang \n Phone: 0901234567")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10f)
                    .setMarginBottom(10f)
                    .setBorder(Border.NO_BORDER)
                    .setMarginRight(10f));


            //Body invoice
            float colWidth[] = {140, 300, 100, 80};
            Table customerInfoTable = new Table(colWidth);

            customerInfoTable.addCell(new Cell(0, 4).add("Customer Information").setBold().setBorder(Border.NO_BORDER));

            customerInfoTable.addCell(new Cell().add("Customer Name: ").setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add(cusNameLabel.getText()).setBold().setItalic().setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add("Invoice No:  ").setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add("01546").setBold().setItalic().setBorder(Border.NO_BORDER));

            customerInfoTable.addCell(new Cell().add("Room Number: ").setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add(roomNumberLabel.getText()).setBold().setItalic().setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER));
            customerInfoTable.addCell(new Cell().add(getCurrentDate()).setBold().setItalic().setBorder(Border.NO_BORDER));

            float itemInfoColWidth[] = {10, 150, 100, 100, 50, 100};
            Table itemInfoTable = new Table(itemInfoColWidth);

            itemInfoTable.addCell(new Cell().add("No").setBackgroundColor(new DeviceRgb(63, 169, 219)));
            itemInfoTable.addCell(new Cell().add("Service Name").setBackgroundColor(new DeviceRgb(63, 169, 219)));
            itemInfoTable.addCell(new Cell().add("Price").setBackgroundColor(new DeviceRgb(63, 169, 219)));
            itemInfoTable.addCell(new Cell().add("Quantity").setBackgroundColor(new DeviceRgb(63, 169, 219)));
            itemInfoTable.addCell(new Cell().add("Unit").setBackgroundColor(new DeviceRgb(63, 169, 219)));
            itemInfoTable.addCell(new Cell().add("Amount").setBackgroundColor(new DeviceRgb(63, 169, 219)));


            itemInfoTable.addCell(new Cell().add("1"));
            itemInfoTable.addCell(new Cell().add("Room Charge"));
            itemInfoTable.addCell(new Cell().add(roomPriceLabel.getText()));
            itemInfoTable.addCell(new Cell().add(usedDaysLabel.getText() + " days " + usedHoursLable.getText() + " hours " + usedMinsLabel.getText() + "mins"));
            itemInfoTable.addCell(new Cell().add("room"));
            itemInfoTable.addCell(new Cell().add(roomChargeLabel.getText()));

            usedServicesData.forEach(usedServices -> {
                itemInfoTable.addCell(new Cell().add(String.valueOf(usedServices.getId() + 1)));
                itemInfoTable.addCell(new Cell().add(usedServices.getServiceName()));
                itemInfoTable.addCell(new Cell().add(String.valueOf(usedServices.getPrice())));
                itemInfoTable.addCell(new Cell().add(String.valueOf(usedServices.getQuantity())));
                itemInfoTable.addCell(new Cell().add(usedServices.getUnit()));
                itemInfoTable.addCell(new Cell().add(String.valueOf(usedServices.getSum())));
            });

            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("Prepaid: ").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add(prepaidLabel.getText()).setBorder(Border.NO_BORDER));

            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("Discount: ").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add(discountLabel.getText()).setBorder(Border.NO_BORDER));

            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("Total: ").setBorder(Border.NO_BORDER).setBold().setFontColor(com.itextpdf.kernel.color.Color.BLUE));
            itemInfoTable.addCell(new Cell().add(totalLabel.getText()).setBorder(Border.NO_BORDER));

            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("Cash: ").setBorder(Border.NO_BORDER).setBold().setFontColor(com.itextpdf.kernel.color.Color.RED));
            itemInfoTable.addCell(new Cell().add(customerCash.getText()).setBorder(Border.NO_BORDER));

            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            itemInfoTable.addCell(new Cell().add("Change: ").setBorder(Border.NO_BORDER).setBold().setItalic());
            itemInfoTable.addCell(new Cell().add(changeLabel.getText()).setBorder(Border.NO_BORDER));

            float signColWidth[] = {280f, 280f};
            Table signTable = new Table(signColWidth);

            signTable.addCell(new Cell().add("Employee").setItalic().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            signTable.addCell(new Cell().add("Customer").setItalic().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));


            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(customerInfoTable);
            document.add(new Paragraph("\n"));
            document.add(itemInfoTable);
            document.add(new Paragraph("\n\n"));
            document.add(signTable);
            document.close();
            System.out.println("PDF Created");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public String formatCurrency(String inputString){
        DecimalFormat formatter = new DecimalFormat("#,###");
        String newValueStr = formatter.format(Double.parseDouble(inputString));
        return newValueStr;
    }

    public void openInvoice(){
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(pdfFileName);
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }

    //Change status to dirty room
    public void changeStatusDirtyRoom(String roomName) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call changeStatusRoom (?, ?)}");
            cstm.setString(1, roomName);
            cstm.setString(2, "Dirty");
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

    //Change was payment
    public void changeWasPayment(String roomName){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        Statement stm = null;
        try {
            stm = conn.createStatement();
            stm.executeUpdate("UPDATE Checkin SET wasPayment = '1' WHERE roomNumber = '"+roomName+"' AND wasPayment = '0'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addBill(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call addBill (?, ?, ?, ?, ?, ?)}");
            cstm.setString(1, getEmployeeID());
            cstm.setString(2, cusIDLabel.getText());
            cstm.setString(3, getCurrentDate());
            cstm.setString(4, getCurrentMonth());
            cstm.setString(5, getCurrentYear());
            cstm.setString(6, String.valueOf(totalDouble + prepaidDouble));
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

    public String getEmployeeID() {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT AC.id FROM Account AC JOIN EmployeeInformation EM ON AC.id = EM.userID WHERE AC.accountStatus = '1'");
            while (rs.next()) {
                result = rs.getString("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

}
