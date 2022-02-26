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
import models.EmployeeList;
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

public class EmployeeController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList("Manager", "Employee", "Front Office");

    @FXML
    private Button btnAddUser;

    @FXML
    private Button btnRefresh;

    @FXML
    private TextField txtSearch;

    private ObservableList<EmployeeList> employeeLists;

    @FXML
    public TableView<EmployeeList> tableEmployee;

    @FXML
    private TableColumn<?, ?> columnName = null;

    @FXML
    private TableColumn<?, ?> columnGender;

    @FXML
    private TableColumn<?, ?> columnPosition;

    @FXML
    private TableColumn<?, ?> columnEmail;

    @FXML
    private TableColumn<?, ?> columnPhoneNumber;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnChangePassword;

    @FXML
    private Label lbTotalEmployee;

    EmployeeList data = null;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLoadTable();
        initTotalEmployee();

        //Style Button
        ImageView editImg = new ImageView("/resources/images/edit.png");
        editImg.setFitHeight(16);
        btnEdit.setGraphic(editImg);

        ImageView deleteImg = new ImageView("/resources/images/delete.png");
        deleteImg.setFitHeight(16);
        btnDelete.setGraphic(deleteImg);

        ImageView changePasswordImg = new ImageView("/resources/images/synchronize.png");
        changePasswordImg.setFitHeight(16);
        btnChangePassword.setGraphic(changePasswordImg);


        //Add Employee
        btnAddUser.setOnAction(event -> openScene("/resources/views/AddEmployee.fxml"));

        //Refresh TableView
        btnRefresh.setOnAction(event -> {
            initLoadTable();
            initTotalEmployee();
        });

        //Edit Information
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                data = tableEmployee.getSelectionModel().getSelectedItem();
                if(data == null){
                    String title = "Choose employee";
                    String mess = "Please choose Employee";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                }else{
                    String email = data.getEmail();
                    int id = 0;
                    String numberId = null;
                    String birthday;
                    int day = 0;
                    int month = 0;
                    int year = 0;
                    String address = null;
                    String query = "SELECT Account.id, EmployeeInformation.numberId, EmployeeInformation.birthday, EmployeeInformation.userAddress  FROM Account JOIN EmployeeInformation ON Account.id = EmployeeInformation.userID WHERE EmployeeInformation.userEmail = '" + email + "'";

                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();

                    try {
                        Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery(query);
                        while (rs.next()) {
                            id = rs.getInt("id");
                            numberId = rs.getString("numberId");
                            birthday = rs.getString("birthday");
                            String[] dateParts = birthday.split("-");
                            day = Integer.parseInt(dateParts[2]);
                            month = Integer.parseInt(dateParts[1]);
                            year = Integer.parseInt(dateParts[0]);
                            address = rs.getString("userAddress");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/EditEmployee.fxml"));
                    Parent parent = null;
                    try {
                        parent = (Parent) loader.load();
                        EditEmployeeController editInformationController = loader.getController();
                        editInformationController.id = id;
                        editInformationController.txtEmail.setText(data.getEmail());
                        editInformationController.txtNoID.setText(numberId);
                        editInformationController.txtPhoneNumber.setText(data.getPhoneNumber());
                        editInformationController.txtFullName.setText(data.getName());
                        editInformationController.dpBirthday.setValue(LocalDate.of(year, month, day));
                        if(data.getGender().equals("Male")){
                            editInformationController.radioMale.setSelected(true);
                        }else if(data.getGender().equals("Female")){
                            editInformationController.radioFemale.setSelected(true);
                        }
                        editInformationController.txtAddress.setText(address);
                        editInformationController.cbPosition.setValue(data.getPosition());
                        editInformationController.cbPosition.setItems(positionList);
                        editInformationController.defaultEmail = data.getEmail();
                        editInformationController.defaultPhone = data.getPhoneNumber();

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
            data = tableEmployee.getSelectionModel().getSelectedItem();
            if(data == null){
                String title = "Choose employee";
                String mess = "Please choose Employee";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();
            }else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/DeleteEmployeeConfirm.fxml"));
                Parent parent = null;
                try {
                    parent = loader.load();
                    DeleteEmployeeConfirmController deleteEmployeeConfirmController = loader.getController();
                    deleteEmployeeConfirmController.lbFullname.setText(data.getName()) ;
                    deleteEmployeeConfirmController.email = data.getEmail();
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
        });

        btnChangePassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                data = tableEmployee.getSelectionModel().getSelectedItem();
                if(data == null){
                    String title = "Choose employee";
                    String mess = "Please choose Employee";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                }else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/ChangePassword.fxml"));
                    Parent parent = null;
                    try {
                        parent = loader.load();
                        ChangePasswordController changePasswordController = loader.getController();
                        changePasswordController.email = data.getEmail();
                        changePasswordController.fullName = data.getName();
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
    }


    //Load Total Employee
    private void initTotalEmployee() {
        int totalEmployee = 0;
        String query = "SELECT COUNT(userID) AS TotalEmployee FROM Account JOIN EmployeeInformation ON Account.id = EmployeeInformation.userID WHERE deleted = '0' AND username != 'admin'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                totalEmployee = rs.getInt("TotalEmployee");
            }
            lbTotalEmployee.setText(String.valueOf(totalEmployee));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Load TableView
    private void initLoadTable() {
        employeeLists = FXCollections.observableArrayList();
        setCellValue();
        loadEmployeeTable();
        SearchAction();
    }

    //Search Employee
    private void SearchAction() {
        FilteredList<EmployeeList> employeeListsData = new FilteredList<>(employeeLists, b -> true);
        txtSearch.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
            employeeListsData.setPredicate(employeeList -> {
                if (newvalue.isEmpty() || newvalue.isBlank() || newvalue == null) {
                    return true;
                }

                String keyword = newvalue.toLowerCase(Locale.ROOT);
                if ((employeeList.getName().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else if ((employeeList.getEmail().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else if ((employeeList.getPhoneNumber().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<EmployeeList> sortedList = new SortedList<>(employeeListsData);
        sortedList.comparatorProperty().bind(tableEmployee.comparatorProperty());
        tableEmployee.setItems(sortedList);
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

    //Get User Name for TableView
    private String getUsername() {
        data = tableEmployee.getSelectionModel().getSelectedItem();
        String email = data.getEmail();
        String userName = null;
        String query = "SELECT username FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID WHERE userEmail = '" + email + "'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                userName = rs.getString("username");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userName;
    }

    //Set Cell Value for TableView
    public void setCellValue() {
        tableEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        columnName.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        columnGender.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        columnPosition.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        columnEmail.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        columnPhoneNumber.setMaxWidth(1f * Integer.MAX_VALUE * 20);

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

    }

    //Load Employee from Database
    public void loadEmployeeTable() {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        Statement st = null;
        String query = "SELECT EmployeeInformation.fullName, EmployeeInformation.userGender, Account.position, EmployeeInformation.userEmail, EmployeeInformation.userPhone FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID WHERE deleted = '0' AND username != 'admin'";
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int i = 0;
            while (rs.next()) {
                employeeLists.add(new EmployeeList(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tableEmployee.setItems(employeeLists);
    }

}
