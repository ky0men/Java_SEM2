package controller;


import com.jfoenix.controls.JFXButton;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

public class EmployeeController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList( "Manager","Front Office");
    ObservableList<String> statusList = FXCollections.observableArrayList( "Use","Don't Use");

    @FXML
    private Button btnAddUser;

    @FXML
    private TextField txtSearch;

    private ObservableList<EmployeeList> employeeLists;

    @FXML
    public TableView<EmployeeList> tableEmployee;

    @FXML
    private TableColumn<?, ?> columnName = null;

    @FXML
    private TableColumn<?, ?> columnPosition;

    @FXML
    private TableColumn<?, ?> columnEmail;

    @FXML
    private TableColumn<?, ?> columnPhoneNumber;

    @FXML
    private TableColumn<?, ?> columnAction;

    @FXML
    private Label lbTotalEmployee;

    EmployeeList data = null;

    private ButtonBar[] buttonBar = new ButtonBar[100];
    private JFXButton[] btnEdit = new JFXButton[100];
    private JFXButton[] btnDelete = new JFXButton[100];
    private JFXButton[] btnChangePassword = new JFXButton[100];





    @Override
    public void initialize(URL url, ResourceBundle rb){
        initLoadTable();
        initTotalEmployee();
    }

    private void initTotalEmployee(){
        int totalEmployee = 0;
        String query = "SELECT COUNT(userID) AS TotalEmployee FROM EmployeeInformation";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                totalEmployee = rs.getInt("TotalEmployee");
            }
            lbTotalEmployee.setText(String.valueOf(totalEmployee));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initLoadTable(){
        employeeLists = FXCollections.observableArrayList();
        setCellValue();
        for (int i = 0; i < buttonBar.length; i++) {
            buttonBar[i] = new ButtonBar();
            btnEdit[i] = new JFXButton();

            btnEdit[i].setOnAction(this::HandleEdit);
            btnDelete[i] = new JFXButton();
            btnDelete[i].setOnAction(this::HandleDelete);
            btnChangePassword[i] = new JFXButton();
            btnChangePassword[i].setOnAction(this::HandleChangePassword);
            buttonBar[i].getButtons().addAll(btnChangePassword[i],btnDelete[i],btnEdit[i] );
        }
        loadEmployeeTable();
        SearchAction();
    }

    private void SearchAction() {
        FilteredList<EmployeeList> employeeListsData = new FilteredList<>(employeeLists, b -> true);
        txtSearch.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
            employeeListsData.setPredicate(employeeList -> {
                if(newvalue.isEmpty() || newvalue.isBlank() || newvalue == null){
                    return true;
                }

                String keyword = newvalue.toLowerCase(Locale.ROOT);
                if((employeeList.getName().toLowerCase().indexOf(keyword) > -1)){
                    return true;
                }else if ((employeeList.getEmail().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                }else if ((employeeList.getPhoneNumber().toLowerCase().indexOf(keyword) > -1)) {
                    return true;
                }else {
                    return false;
                }
            });
        } );
        SortedList<EmployeeList> sortedList = new SortedList<>(employeeListsData);
        sortedList.comparatorProperty().bind(tableEmployee.comparatorProperty());
        tableEmployee.setItems(sortedList);
    }

    private void openScene(String sceneUrl, int x, int y){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneUrl));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setX(x);
        stage.setY(y);
        stage.showAndWait();
    }

    private void HandleEdit(ActionEvent event){
        data = tableEmployee.getSelectionModel().getSelectedItem();
        String email = data.getEmail();
        int id = 0;
        String numberId = null;
        String birthday;
        int day = 0;
        int month = 0;
        int year = 0;
        String address = null;
        String deleted = null;
        String status;
        String query = "SELECT Account.id, EmployeeInformation.numberId, EmployeeInformation.birthday, EmployeeInformation.userAddress, EmployeeInformation.deleted  FROM Account JOIN EmployeeInformation ON Account.id = EmployeeInformation.userID WHERE EmployeeInformation.userEmail = '"+ email +"'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                id = rs.getInt("id");
                numberId = rs.getString("numberId");
                birthday = rs.getString("birthday");
                String [] dateParts = birthday.split("-");
                day = Integer.parseInt(dateParts[2]);
                month = Integer.parseInt(dateParts[1]);
                year = Integer.parseInt(dateParts[0]);
                address = rs.getString("userAddress");
                deleted = rs.getString("deleted");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/EditInformation.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
            EditInformationController editInformationController = loader.getController();
            editInformationController.id = id;
            editInformationController.txtEmail.setText(data.getEmail());
            editInformationController.txtNoID.setText(numberId);
            editInformationController.txtPhoneNumber.setText(data.getPhoneNumber());
            editInformationController.txtFullName.setText(data.getName());
            editInformationController.dpBirthday.setValue(LocalDate.of(year, month, day));;
            editInformationController.txtAddress.setText(address);
            editInformationController.cbPosition.setValue(data.getPosition());
            editInformationController.cbPosition.setItems(positionList);
            if(deleted.equals("0")){
                editInformationController.cbStatus.setValue("Use");
            } else if(deleted.equals(("1"))){
                editInformationController.cbStatus.setValue("Don't Use");
            }
            editInformationController.cbStatus.setItems(statusList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setX(600);
        stage.setY(200);
        stage.showAndWait();
    }

    private String getUsername(){
        data = tableEmployee.getSelectionModel().getSelectedItem();
        String email = data.getEmail();
        String userName = null;
        String query = "SELECT username FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID WHERE userEmail = '"+ email +"'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                userName = rs.getString("username");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userName;
    }

    private boolean CheckDeleted(){
        data = tableEmployee.getSelectionModel().getSelectedItem();
        boolean flag = false;
        String email = data.getEmail();
        String deleted = null;
        String query = "SELECT deleted FROM EmployeeInformation WHERE userEmail = '"+ email +"'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                deleted = rs.getString("deleted");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if(deleted.equals("1")){
            flag = true;
        }
        return flag;
    }

    private void HandleDelete(ActionEvent event){
        if(CheckDeleted()){
            data = tableEmployee.getSelectionModel().getSelectedItem();
            String email = data.getEmail();
            String deleteProfile = "DELETE FROM EmployeeInformation WHERE userEmail = '"+ email +"'";
            String deleteAccount = "DELETE FROM Account WHERE username = '"+ getUsername() +"'";

            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();

            try {
                Statement st = conn.createStatement();
                st.executeUpdate(deleteProfile);
                st.executeUpdate(deleteAccount);
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            data = tableEmployee.getSelectionModel().getSelectedItem();
            String name = data.getName();
            String title = "Successfully deleted information";
            String mess = "Employee "+ name +" has successfully deleted the information";
            TrayNotification cancel = new TrayNotification(title, mess, NotificationType.SUCCESS);
            cancel.setAnimationType(AnimationType.POPUP);
            cancel.showAndWait();
            initLoadTable();
        } else{
            String name = data.getName();
            String title = "Failed deleted information";
            String mess = "Employee "+ name +" is still working";
            TrayNotification cancel = new TrayNotification(title, mess, NotificationType.ERROR);
            cancel.setAnimationType(AnimationType.POPUP);
            cancel.showAndWait();
        }


    }

    private void HandleChangePassword(ActionEvent event){
        data = tableEmployee.getSelectionModel().getSelectedItem();
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
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(600);
        stage.setY(300);
        stage.show();

    }

    @FXML
    void AddUserAction(ActionEvent event) {
        openScene("/resources/views/AddUser.fxml", 600, 100);
        System.out.println("ADD");
    }

    public void setCellValue(){
        tableEmployee.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        columnName.setMaxWidth( 1f * Integer.MAX_VALUE * 22 );
        columnPosition.setMaxWidth( 1f * Integer.MAX_VALUE * 16  );
        columnEmail.setMaxWidth( 1f * Integer.MAX_VALUE * 22 );
        columnPhoneNumber.setMaxWidth( 1f * Integer.MAX_VALUE * 22 );
        columnAction.setMaxWidth( 1f * Integer.MAX_VALUE * 18 );

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        columnAction.setCellValueFactory(new PropertyValueFactory<>("buttonBar"));

    }


    public void loadEmployeeTable(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        Statement st  = null;
        String query = "SELECT EmployeeInformation.fullName, Account.position, EmployeeInformation.userEmail, EmployeeInformation.userPhone FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID";
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int i = 0;
            while (rs.next()){
                employeeLists.add(new EmployeeList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4), buttonBar[i], btnEdit[i], btnDelete[i], btnChangePassword[i]));
                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tableEmployee.setItems(employeeLists);
    }

    @FXML
    void RefreshAction(ActionEvent event) {
        initLoadTable();
        initTotalEmployee();
    }
}
