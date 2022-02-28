package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class EditCustomerController implements Initializable {
    @FXML
    private HBox titleBar;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    public JFXTextField txtFullName;

    @FXML
    public JFXTextField txtNoID;

    @FXML
    private Label lbIdNumberWarning;

    @FXML
    public DatePicker dpBirthday;

    @FXML
    public ToggleGroup genderGroup;

    @FXML
    public JFXRadioButton radioMale;

    @FXML
    public JFXRadioButton radioFemale;
    @FXML
    public JFXTextField txtPhoneNumber;

    @FXML
    public JFXTextField txtAddress;

    private double x, y;

    public int id;

    public String defaultIdNumber;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Window move action
        titleBar.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            titleBar.getScene().getWindow().setX(event.getScreenX() - x);
            titleBar.getScene().getWindow().setY(event.getScreenY() - y);
        });

        //Validate
        RequiredFieldValidator fullNameValidation = new RequiredFieldValidator();
        txtFullName.getValidators().add(fullNameValidation);
        fullNameValidation.setMessage("Full Name is required!");

        RequiredFieldValidator numberIdValidation = new RequiredFieldValidator();
        txtNoID.getValidators().add(numberIdValidation);
        numberIdValidation.setMessage("ID Number is required!");

        RequiredFieldValidator addressValidation = new RequiredFieldValidator();
        txtAddress.getValidators().add(addressValidation);
        addressValidation.setMessage("Address is required!");

        RequiredFieldValidator phoneNumberValidation = new RequiredFieldValidator();
        txtPhoneNumber.getValidators().add(phoneNumberValidation);
        phoneNumberValidation.setMessage("Phone Number is required!");

        RegexValidator phoneRegexValidator = new RegexValidator();
        String RegexPhone = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
        phoneRegexValidator.setRegexPattern(RegexPhone);
        phoneRegexValidator.setMessage("Your Phone Number is not valid");
        txtPhoneNumber.getValidators().add(phoneRegexValidator);

        txtFullName.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtFullName.validate();
            }
        });

        txtNoID.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtNoID.validate();
            }
            idNumberIsExist();
        });

        txtAddress.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtAddress.validate();
            }
        });

        txtPhoneNumber.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtPhoneNumber.validate();
            }
        });

        btnSave.setOnAction(event -> {
            if (txtFullName.getText().equals("") || txtNoID.getText().equals("") || txtAddress.getText().equals("") ||
                    txtPhoneNumber.getText().equals("") || !txtPhoneNumber.getText().matches(RegexPhone)) {
                txtFullName.validate();
                txtNoID.validate();
                txtAddress.validate();
                txtPhoneNumber.validate();
            }
            idNumberIsExist();
            if (txtPhoneNumber.getText().matches(RegexPhone) && !idNumberIsExist()) {
                updateTableCustomer();
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                String title = "Successfully changed information";
                String mess = "Customer " + txtFullName.getText() + " has successfully changed";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);
            }
        });

        //Close Window
        btnCancel.setOnAction(event -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            GaussianBlur blur = new GaussianBlur(0);
            LoginController.stage.getScene().getRoot().setEffect(blur);
        });
    }

    //Check Exist ID Number
    private boolean idNumberIsExist() {
        boolean flag = false;
        String idNumber = txtNoID.getText();
        if(!idNumber.equals(defaultIdNumber)){
            String query = "Select cusIdentityNumber from Customer WHERE cusIdentityNumber= '" + idNumber + "'";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    lbIdNumberWarning.setText("ID Number is Exist");
                    txtNoID.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    lbIdNumberWarning.setStyle("-fx-text-background-color: #D34437;");
                    flag = true;
                } else {
                    lbIdNumberWarning.setText("");
                    txtNoID.setStyle("");
                    flag = false;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            lbIdNumberWarning.setText("");
            txtNoID.setStyle("");
            flag = false;
        }
        return flag;
    }

    //Update table Customer
    private void updateTableCustomer() {
        String fullName = txtFullName.getText();
        String numberId = txtNoID.getText();
        String birthday = dpBirthday.getValue().toString();
        String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String query = "UPDATE Customer SET cusIdentityNumber = '"+ numberId +"', cusName = N'" + fullName + "', cusGender = '" + gender + "', cusDOB = '" + birthday + "', cusPhone = '" + phone + "'," +
                " cusAddress = N'" + address + "' WHERE cusID = '" + id + "'";

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
