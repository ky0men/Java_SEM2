package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    @FXML
    private HBox titleBar;

    private double x, y;

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
    private Label lbPasswordValidator;

    public String email;
    public String fullName;

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

        //Validate

        RequiredFieldValidator passwordValidation = new RequiredFieldValidator();
        txtPassword.getValidators().add(passwordValidation);
        passwordValidation.setMessage("Password is required!");

        RequiredFieldValidator confirmPassValidation = new RequiredFieldValidator();
        txtReEnterPassword.getValidators().add(confirmPassValidation);
        confirmPassValidation.setMessage("Confirm Password is required!");

        txtPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtPassword.validate();
                }
            }
        });

        txtReEnterPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtReEnterPassword.validate();
                }
                checkPassword();
            }
        });

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(txtPassword.getText().equals("") || txtReEnterPassword.getText().equals("")){
                    txtPassword.validate();
                    txtReEnterPassword.validate();
                }
                checkPassword();
                if(checkPassword()){
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
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    Node node = (Node)event.getSource();
                    Stage stage = (Stage)node.getScene().getWindow();
                    stage.close();
                    GaussianBlur blur = new GaussianBlur(0);
                    LoginController.stage.getScene().getRoot().setEffect(blur);
                }
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Node node = (Node)event.getSource();
                Stage stage = (Stage)node.getScene().getWindow();
                stage.close();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);
            }
        });
    }

    private boolean checkPassword(){
        boolean flag = false;
        String password = txtPassword.getText();
        String rePassword = txtReEnterPassword.getText();
        if(!txtReEnterPassword.getText().equals("") && !password.equals(rePassword)){
            lbPasswordValidator.setText("Password does not match");
            txtReEnterPassword.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
            lbPasswordValidator.setStyle("-fx-text-background-color: #D34437;");
            flag = false;
        }else if(txtReEnterPassword.getText().equals("")) {
            lbPasswordValidator.setText("");
            txtReEnterPassword.setStyle("");
            flag = false;
        }else if(password.equals(rePassword)){
            lbPasswordValidator.setText("");
            txtReEnterPassword.setStyle("");
            flag = true;
        }
        return flag;
    }

//    private boolean formNotNull(){
//        if(txtPassword.getText() == "" || txtReEnterPassword.getText() == "" ){
//            iconWarning.setVisible(true);
//            lbWarning.setText("Please complete all information");
//            return false;
//        }else {
//            iconWarning.setVisible(false);
//            lbWarning.setText("");
//            return true;
//        }
//    }

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
}
