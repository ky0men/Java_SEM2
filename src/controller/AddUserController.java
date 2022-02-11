package controller;

import com.jfoenix.controls.*;
import dao.DBConnect;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import models.EmployeeList;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AddUserController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList( "Manager","Front Office");

    @FXML
    private JFXTextField txtFullName;

    @FXML
    private JFXTextField txtUserName;

    @FXML
    private JFXTextField txtNoID;

    @FXML
    private JFXTextField txtPhoneNumber;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtReEnterPassword;

    @FXML
    private Label lbWarning;

    @FXML
    private JFXComboBox<String> cbPosition;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXDatePicker dpBirthday;

    @FXML
    private FontIcon iconWarning;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbPosition.setValue("Manager");
        cbPosition.setItems(positionList);
    }

    @FXML
    void AddUser(ActionEvent event) {
        formNotNull();
        checkUserName();
        checkPassword();
        if(formNotNull() && checkUserName() && checkPassword()){
            AddTableAccount();
            AddTableProfile();
            Node node = (Node)event.getSource();
            Stage stage = (Stage)node.getScene().getWindow();
            stage.close();
            String fullNameText = txtFullName.getText();
            String title = "Successfully added employee";
            String mess = "Employee "+ fullNameText +" has been successfully added";
            TrayNotification cancel = new TrayNotification(title, mess, NotificationType.SUCCESS);
            cancel.setAnimationType(AnimationType.POPUP);
            cancel.showAndWait();
        }
    }



    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

    private boolean checkPassword(){
        String password = txtPassword.getText();
        String rePassword = txtReEnterPassword.getText();
        if(password != "" && rePassword != "" && password.equals(rePassword)){
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return true;
        }else {
            iconWarning.setVisible(true);
            lbWarning.setText("Wrong password try again");
            return false;
        }
    }
    private boolean checkUserName(){
        String userName = txtUserName.getText();
        boolean flag = false;
        String query = "SELECT username FROM Account";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                if(userName.equals(rs.getString("username"))){
                    iconWarning.setVisible(true);
                    lbWarning.setText("Username already exists");
                    flag = false;
                }else{
                    iconWarning.setVisible(false);
                    lbWarning.setText("");
                    flag = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private boolean checkEmail(){
        String email = txtEmail.getText();
        boolean flag = false;
        String query = "Select userEmail from EmployeeInformation";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                if(email.equals(rs.getString("userEmail"))){
                    iconWarning.setVisible(true);
                    lbWarning.setText("Email already exists");
                    flag = false;
                }else{
                    iconWarning.setVisible(false);
                    lbWarning.setText("");
                    flag = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private boolean checkPhoneNumber(){
        String phoneNumber = txtPhoneNumber.getText();
        boolean flag = false;
        String query = "Select userPhone from EmployeeInformation";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                if(phoneNumber.equals(rs.getString("userPhone"))){
                    iconWarning.setVisible(true);
                    lbWarning.setText("Phone Number already exists");
                    flag = false;
                }else{
                    iconWarning.setVisible(false);
                    lbWarning.setText("");
                    flag = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private boolean formNotNull(){
        if(txtFullName.getText() == "" || txtUserName.getText() == "" || txtPassword.getText() == ""
                || txtReEnterPassword.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
                || txtPhoneNumber.getText() == ""|| txtEmail.getText() == ""|| txtAddress.getText() == "" ){
            iconWarning.setVisible(true);
            lbWarning.setText("Please complete all information");
            return false;
        }else {
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return true;
        }
    }

    private void AddTableAccount(){
        String username = txtUserName.getText();
        String password = txtPassword.getText();
        String position = cbPosition.getSelectionModel().getSelectedItem();
        String query = "INSERT INTO Account VALUES ('" + username +"', HASHBYTES('SHA2_512', '"+ password +"'), '" + position +"');";

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
    }

    private int getUserID(){
        int userId = 0;
        String username = txtUserName.getText();
        String query = "SELECT id FROM Account WHERE username = '"+ username +"';";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                userId = rs.getInt("id");
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userId;
    }

    private void AddTableProfile(){
        int userId = getUserID();
        String fullName = txtFullName.getText();
        String numberId = txtNoID.getText();
        String startWork = LocalDate.now().toString();
        String birthday = dpBirthday.getValue().toString();
        String email = txtEmail.getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String query = "INSERT INTO EmployeeInformation VALUES ("+ userId +", '"+ fullName +"', '"+ numberId +"', '"+ startWork +"'," +
                " '"+ birthday +"', '"+ email +"', '"+ phone +"', '"+ address +"', '0');";

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
    }


}

