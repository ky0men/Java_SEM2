package controller;

import com.jfoenix.controls.*;
import dao.DBConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.sql.*;

public class ChangePasswordController {
    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtReEnterPassword;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private FontIcon iconWarning;

    @FXML
    private Label lbWarning;

    public String email;
    public String fullName;


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

    private boolean formNotNull(){
        if(txtPassword.getText() == "" || txtReEnterPassword.getText() == "" ){
            iconWarning.setVisible(true);
            lbWarning.setText("Please complete all information");
            return false;
        }else {
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return true;
        }
    }



    @FXML
    void SaveAction(ActionEvent event){
        formNotNull();
        checkPassword();
        if(formNotNull() && checkPassword()){
            String password = txtPassword.getText();
            String query = "UPDATE Account SET passwordHash = HASHBYTES('SHA2_512', '"+ password +"') WHERE username = '"+ getUsername() +"'";

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

            String fullNameText = fullName;
            String title = "Change password successfully";
            String mess = "Employee "+ fullName +" has changed his password";
            TrayNotification cancel = new TrayNotification(title, mess, NotificationType.SUCCESS);
            cancel.setAnimationType(AnimationType.POPUP);
            cancel.showAndWait();
        }
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

    private String getUsername(){
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

    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

}
