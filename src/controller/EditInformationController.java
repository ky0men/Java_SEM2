package controller;

import com.jfoenix.controls.JFXTextField;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditInformationController implements Initializable {
    @FXML
    private HBox titleBar;

    @FXML
    public JFXTextField txtFullName;

    @FXML
    public JFXTextField txtNoID;

    @FXML
    public DatePicker dpBirthday;

    @FXML
    public JFXTextField txtEmail;

    @FXML
    public ComboBox<String> cbPosition;

    @FXML
    public JFXTextField txtAddress;

    @FXML
    public JFXTextField txtPhoneNumber;

    @FXML
    public Button btnSave;

    @FXML
    public Button btnCancel;

    @FXML
    private Label lbWarning;

    @FXML
    private FontIcon iconWarning;

    @FXML
    private Label lbPhoneValidator;

    @FXML
    private Label lbEmailValidator;

    public int id;

    private double x, y;

    public String defaultPhone;

    public String defaultEmail;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set DatePicker
        dpBirthday.setValue(LocalDate.now().minusYears(18));
        dpBirthday.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate day = LocalDate.now().minusYears(18);

                setDisable(empty || date.compareTo(day) > 0 );
            }
        });

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
        RequiredFieldValidator fullNameValidation = new RequiredFieldValidator();
        txtFullName.getValidators().add(fullNameValidation);
        fullNameValidation.setMessage("Full Name is required!");

        RequiredFieldValidator numberIdValidation = new RequiredFieldValidator();
        txtNoID.getValidators().add(numberIdValidation);
        numberIdValidation.setMessage("ID Number is required!");

        RequiredFieldValidator addressValidation = new RequiredFieldValidator();
        txtAddress.getValidators().add(addressValidation);
        addressValidation.setMessage("Address is required!");

        RequiredFieldValidator emailValidation = new RequiredFieldValidator();
        txtEmail.getValidators().add(emailValidation);
        emailValidation.setMessage("Email is required!");

        RequiredFieldValidator phoneNumberValidation = new RequiredFieldValidator();
        txtPhoneNumber.getValidators().add(phoneNumberValidation);
        phoneNumberValidation.setMessage("Phone Number is required!");

        RegexValidator emailRegexValidator = new RegexValidator();
        emailRegexValidator.setRegexPattern("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        emailRegexValidator.setMessage("Your Email is not valid");
        txtEmail.getValidators().add(emailRegexValidator);

        RegexValidator phoneRegexValidator = new RegexValidator();
        phoneRegexValidator.setRegexPattern("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
        phoneRegexValidator.setMessage("Your Phone Number is not valid");
        txtPhoneNumber.getValidators().add(phoneRegexValidator);

        txtFullName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtFullName.validate();
                }
            }
        });

        txtNoID.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtNoID.validate();
                }
            }
        });

        txtAddress.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtAddress.validate();
                }
            }
        });

        txtEmail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtEmail.validate();
                }
                emailIsExist();
            }
        });

        txtPhoneNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtPhoneNumber.validate();
                }
                phoneNumberIsExist();
            }
        });

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(txtFullName.getText().equals("") || txtNoID.getText().equals("") || txtAddress.getText().equals("") ||
                        txtEmail.getText().equals("") || txtPhoneNumber.getText().equals("")){
                    txtFullName.validate();
                    txtNoID.validate();
                    txtAddress.validate();
                    txtEmail.validate();
                    txtPhoneNumber.validate();
                }
                emailIsExist();
                phoneNumberIsExist();
                if(!emailIsExist() && !phoneNumberIsExist()){
                    UpdateTableAccount();
                    UpdateTableProfile();
                    Node node = (Node)event.getSource();
                    Stage stage = (Stage)node.getScene().getWindow();
                    stage.close();
                    String title = "Successfully changed information";
                    String mess = "Employee "+ txtFullName.getText() +" has successfully \n changed information";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
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

//    private boolean formNotNull(){
//        if(txtFullName.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
//                || txtPhoneNumber.getText() == ""|| txtEmail.getText() == ""|| txtAddress.getText() == "" ){
//            iconWarning.setVisible(true);
//            lbWarning.setText("Please complete all information");
//            return false;
//        }else {
//            iconWarning.setVisible(false);
//            lbWarning.setText("");
//            return true;
//        }
//    }

//    public static boolean emailIsValid(final String email) {
//        String EMAIL_PATTERN =
//                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
//                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    private boolean emailIsExist(){
        boolean flag = false;
        String email = txtEmail.getText();
        if(!email.equals(defaultEmail)){
            String query = "Select userEmail from EmployeeInformation WHERE userEmail='"+email+"'";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                if(rs.next()){
                    lbEmailValidator.setText("Email is Exist");
                    txtEmail.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    lbEmailValidator.setStyle("-fx-text-background-color: #D34437;");
                    flag = true;
                }else {
                    lbEmailValidator.setText("");
                    txtEmail.setStyle("");
                    flag = false;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else{
            lbEmailValidator.setText("");
            txtEmail.setStyle("");
            flag = false;
        }
        return flag;
    }

//    public static boolean phoneIsValid(final String phone) {
//        String PHONE_PATTERN =
//                "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
//        Pattern pattern = Pattern.compile(PHONE_PATTERN);
//        Matcher matcher = pattern.matcher(phone);
//        return matcher.matches();
//    }

    private boolean phoneNumberIsExist(){
        boolean flag = false;
        String phoneNumber = txtPhoneNumber.getText();
        if(!phoneNumber.equals(defaultPhone)){
        String query = "Select userPhone from EmployeeInformation WHERE userPhone= '"+ phoneNumber+"'";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                lbPhoneValidator.setText("Phone Number is Exist");
                txtPhoneNumber.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                lbPhoneValidator.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            }else{
                lbPhoneValidator.setText("");
                txtPhoneNumber.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        }else {
            lbPhoneValidator.setText("");
            txtPhoneNumber.setStyle("");
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
        String query = "UPDATE EmployeeInformation SET fullName = '"+ fullName +"', numberId = '"+ numberId +"'," +
                " birthday = '"+ birthday +"', userEmail = '"+ email +"', userPhone = '"+ phone +"'," +
                " userAddress = '"+ address +"' WHERE userID = '"+ id +"'";

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
