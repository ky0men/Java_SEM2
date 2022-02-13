package controller;

import dao.DBConnect;
import javafx.collections.*;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditInformationController implements Initializable {

    @FXML
    private AnchorPane titleBar;

    private double x, y;

    @FXML
    public TextField txtFullName;

    @FXML
    public TextField txtNoID;

    @FXML
    public DatePicker dpBirthday;

    @FXML
    public TextField txtEmail;

    @FXML
    public ComboBox<String> cbPosition;

    @FXML
    public ComboBox<String> cbStatus;

    @FXML
    public TextField txtAddress;

    @FXML
    public TextField txtPhoneNumber;

    @FXML
    public Button btnSave;

    @FXML
    public Button btnCancel;

    @FXML
    private Label lbWarning;

    @FXML
    private FontIcon iconWarning;

    public int id;

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

    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

    @FXML
    void SaveUser(ActionEvent event) {
        formNotNull();
        if(formNotNull() && checkEmail() && checkPhoneNumber()){
            UpdateTableAccount();
            UpdateTableProfile();
        }
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
        String title = "Successfully changed information";
        String mess = "Employee "+ txtFullName.getText() +" has successfully changed information";
        TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.seconds(3));
        tray.showAndWait();
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

    private boolean formNotNull(){
        if(txtFullName.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
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

    public static boolean emailIsValid(final String email) {
        String EMAIL_PATTERN =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean checkEmail(){
        boolean flag = false;
        String email = txtEmail.getText();
        if(emailIsValid(email)){
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
        }else{
            iconWarning.setVisible(true);
            lbWarning.setText("Email invalid");
            flag = false;
        }
        return flag;
    }

    public static boolean phoneIsValid(final String phone) {
        String PHONE_PATTERN =
                "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean checkPhoneNumber(){
        boolean flag = false;
        String phoneNumber = txtPhoneNumber.getText();
        if(phoneIsValid(phoneNumber)){
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
        }else {
            iconWarning.setVisible(true);
            lbWarning.setText("Phone Number invalid");
            flag = false;
        }

        return flag;
    }

    private void UpdateTableAccount(){
        String position = cbPosition.getSelectionModel().getSelectedItem();
        String query = "UPDATE Account SET position = '"+ position +"' WHERE id = "+ id +"";

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

    private void UpdateTableProfile(){
        String fullName = txtFullName.getText();
        String numberId = txtNoID.getText();
        String birthday = dpBirthday.getValue().toString();
        String email = txtEmail.getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String deleted = null;
        if(cbStatus.getSelectionModel().getSelectedItem().equals("Use")){
            deleted = "0";
        }else if(cbStatus.getSelectionModel().getSelectedItem().equals("Don't Use")){
            deleted = "1";
        }
        String query = "UPDATE EmployeeInformation SET fullName = '"+ fullName +"', numberId = '"+ numberId +"'," +
                " birthday = '"+ birthday +"', userEmail = '"+ email +"', userPhone = '"+ phone +"'," +
                " userAddress = '"+ address +"', deleted = '"+ deleted +"' WHERE userID = '"+ id +"'";

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
