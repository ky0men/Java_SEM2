package controller;

import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import models.CustomerList;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import static controller.LoginController.stage;

public class CustomerController implements Initializable {
    private ObservableList<CustomerList> customerLists;

    @FXML
    private TableView<CustomerList> tableCustomer;

    @FXML
    private TableColumn<?, ?> columnName;

    @FXML
    private TableColumn<?, ?> columnGender;

    @FXML
    private TableColumn<?, ?> columnPhoneNumber;

    @FXML
    private TableColumn<?, ?> columnNumberID;

    @FXML
    private Button btnRefresh;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label lbTotalCustomer;

    @FXML
    private Button btnAddCustomer;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    CustomerList data = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTotalCustomers();
        initLoadTable();

        //Style Button
        ImageView editImg = new ImageView("/resources/images/edit.png");
        editImg.setFitHeight(16);
        btnEdit.setGraphic(editImg);

        ImageView deleteImg = new ImageView("/resources/images/delete.png");
        deleteImg.setFitHeight(16);
        btnDelete.setGraphic(deleteImg);

        //Refresh TableView
        btnRefresh.setOnAction(event -> {
            initTotalCustomers();
            initLoadTable();
        });

        btnAddCustomer.setOnAction(event -> openScene("/resources/views/AddCustomer.fxml"));

        //Edit Information
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                data = tableCustomer.getSelectionModel().getSelectedItem();
                if(data == null){
                    String title = "Choose Customer";
                    String mess = "Please choose Customer";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                }else{
                    String idNumber = data.getIdNumber();
                    int id = 0;
                    String birthday;
                    int day = 0;
                    int month = 0;
                    int year = 0;
                    String address = null;
                    String query = "SELECT Customer.cusID, Customer.cusDOB, Customer.cusAddress FROM Customer WHERE Customer.cusIdentityNumber = '" + idNumber + "'";

                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();

                    try {
                        Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery(query);
                        while (rs.next()) {
                            id = rs.getInt("cusID");
                            birthday = rs.getString("cusDOB");
                            String[] dateParts = birthday.split("-");
                            day = Integer.parseInt(dateParts[2]);
                            month = Integer.parseInt(dateParts[1]);
                            year = Integer.parseInt(dateParts[0]);
                            address = rs.getString("cusAddress");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/EditCustomer.fxml"));
                    Parent parent = null;
                    try {
                        parent = loader.load();
                        EditCustomerController editCustomerController = loader.getController();
                        editCustomerController.id = id;
                        editCustomerController.txtFullName.setText(data.getName());
                        editCustomerController.txtNoID.setText(data.getIdNumber());
                        editCustomerController.dpBirthday.setValue(LocalDate.of(year, month, day));
                        if(data.getGender().equals("Male")){
                            editCustomerController.radioMale.setSelected(true);
                        }else if(data.getGender().equals("Female")){
                            editCustomerController.radioFemale.setSelected(true);
                        }
                        editCustomerController.txtPhoneNumber.setText(data.getPhoneNumber());
                        editCustomerController.txtAddress.setText(address);
                        editCustomerController.defaultIdNumber = data.getIdNumber();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    GaussianBlur blurEffect = new GaussianBlur(10);
                    stage.getScene().getRoot().setEffect(blurEffect);
                    Stage stage = new Stage();
                    Scene scene = new Scene(parent);
                    scene.setFill(Color.TRANSPARENT);
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                }
            }
        });

        btnDelete.setOnAction(event -> {
            data = tableCustomer.getSelectionModel().getSelectedItem();
            if(data == null){
                String title = "Choose Customer";
                String mess = "Please choose Customer";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();
            }else{
                String idNumber = data.getIdNumber();
                String query = "UPDATE Customer SET cusDeleted = '1' WHERE cusIdentityNumber = '" + idNumber + "'";

                DBConnect dbConnect = new DBConnect();
                dbConnect.readProperties();
                Connection conn = dbConnect.getDBConnection();
                try {
                    Statement st = conn.createStatement();
                    st.executeUpdate(query);
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                String name = data.getName();
                String title = "Successfully deleted information";
                String mess = "Customer " + name + " has successfully deleted the information";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();
                initLoadTable();
                initTotalCustomers();
            }
        });
    }

    //Load Total Customers
    private void initTotalCustomers() {
        int totalCustomer = 0;
        String query = "SELECT COUNT(cusID) AS TotalCustomer FROM Customer WHERE cusDeleted = '0'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                totalCustomer = rs.getInt("TotalCustomer");
            }
            lbTotalCustomer.setText(String.valueOf(totalCustomer));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Load TableView
    private void initLoadTable() {
        customerLists = FXCollections.observableArrayList();
        setCellValue();
        loadCustomerTable();
        SearchAction();
    }

    //Search Customer
    private void SearchAction() {
        FilteredList<CustomerList> customerListData = new FilteredList<>(customerLists, b -> true);
        txtSearch.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
            customerListData.setPredicate(customerList -> {
                if (newvalue.isEmpty() || newvalue.isBlank() || newvalue == null) {
                    return true;
                }

                String keyword = newvalue.toLowerCase(Locale.ROOT);
                if ((customerList.getName().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else if ((customerList.getIdNumber().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else if ((customerList.getPhoneNumber().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<CustomerList> sortedList = new SortedList<>(customerListData);
        sortedList.comparatorProperty().bind(tableCustomer.comparatorProperty());
        tableCustomer.setItems(sortedList);
    }

    //Set Cell Value for TableView
    public void setCellValue() {
        tableCustomer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        columnName.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        columnGender.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        columnNumberID.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        columnPhoneNumber.setMaxWidth(1f * Integer.MAX_VALUE * 35);

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnNumberID.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

    }

    //Load Customer from Database
    public void loadCustomerTable() {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        Statement st = null;
        String query = "SELECT Customer.cusName, Customer.cusGender,Customer.cusPhone, Customer.cusIdentityNumber FROM Customer WHERE cusDeleted = '0'";
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int i = 0;
            while (rs.next()) {
                customerLists.add(new CustomerList(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tableCustomer.setItems(customerLists);
    }

    //Open new Scene
    private void openScene(String sceneUrl) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneUrl));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GaussianBlur blurEffect = new GaussianBlur(10);
        stage.getScene().getRoot().setEffect(blurEffect);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
