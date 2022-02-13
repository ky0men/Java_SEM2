package controller;

import com.jfoenix.controls.*;
import dao.DBConnect;
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
    private AnchorPane titleBar;

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
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

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
    }
}
