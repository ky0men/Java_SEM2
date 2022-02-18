package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.util.Duration;
import models.EmployeeList;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controller.LoginController.stage;

public class AddUserController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList( "Manager","Front Office");

    @FXML
    private HBox titleBar;

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
    private DatePicker dpBirthday;

    @FXML
    private FontIcon iconWarning;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private Label lbUserNameValidator;

    @FXML
    private Label lbPasswordValidator;

    @FXML
    private Label lbPhoneValidator;

    @FXML
    private Label lbEmailValidator;

    private double x, y;



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

        //Set Combobox
        cbPosition.setValue("Manager");
        cbPosition.setItems(positionList);

        //Validate
        RequiredFieldValidator userNameValidation = new RequiredFieldValidator();
        txtUserName.getValidators().add(userNameValidation);
        userNameValidation.setMessage("User Name is required!");

        RequiredFieldValidator passwordValidation = new RequiredFieldValidator();
        txtPassword.getValidators().add(passwordValidation);
        passwordValidation.setMessage("Password is required!");

        RequiredFieldValidator confirmPassValidation = new RequiredFieldValidator();
        txtReEnterPassword.getValidators().add(confirmPassValidation);
        confirmPassValidation.setMessage("Confirm Password is required!");

        RequiredFieldValidator fullNameValidation = new RequiredFieldValidator();
        txtFullName.getValidators().add(fullNameValidation);
        fullNameValidation.setMessage("Full Name is required!");

        RequiredFieldValidator numberIdValidation = new RequiredFieldValidator();
        txtNoID.getValidators().add(numberIdValidation);
        numberIdValidation.setMessage("ID Number is required!");

        NumberValidator isnumberIDValidator = new NumberValidator();
        txtNoID.getValidators().add(isnumberIDValidator);
        String RegexNoID = "^[0-9 \\-]+$";
        isnumberIDValidator.setMessage("Only Numbers are supported!");

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
        String RegexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        emailRegexValidator.setRegexPattern(RegexEmail);
        emailRegexValidator.setMessage("Your Email is not valid");
        txtEmail.getValidators().add(emailRegexValidator);

        RegexValidator phoneRegexValidator = new RegexValidator();
        String RegexPhone = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
        phoneRegexValidator.setRegexPattern(RegexPhone);
        phoneRegexValidator.setMessage("Your Phone Number is not valid");
        txtPhoneNumber.getValidators().add(phoneRegexValidator);




        txtUserName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtUserName.validate();
                }
                userNameisExist();
            }
        });

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

        //Add Employee
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(txtUserName.getText().equals("") || txtPassword.getText().equals("") || txtReEnterPassword.getText().equals("") ||
                        txtFullName.getText().equals("") || txtNoID.getText().equals("") || !txtNoID.getText().matches(RegexNoID) || txtAddress.getText().equals("") ||
                        txtEmail.getText().equals("") || !txtEmail.getText().matches(RegexEmail) ||
                        txtPhoneNumber.getText().equals("") || !txtPhoneNumber.getText().matches(RegexPhone)){
                        txtUserName.validate();
                        txtPassword.validate();
                        txtReEnterPassword.validate();
                        txtFullName.validate();
                        txtNoID.validate();
                        txtAddress.validate();
                        txtEmail.validate();
                        txtPhoneNumber.validate();
                }
                userNameisExist();
                checkPassword();
                emailIsExist();
                phoneNumberIsExist();
                if(!userNameisExist() && checkPassword() && txtNoID.getText().matches(RegexNoID)
                        && txtEmail.getText().matches(RegexEmail) && !emailIsExist()  &&
                        txtPhoneNumber.getText().matches(RegexPhone) && !phoneNumberIsExist()){
                    AddTableAccount();
                    AddTableProfile();
                    Node node = (Node)event.getSource();
                    Stage stage = (Stage)node.getScene().getWindow();
                    stage.close();
                    String fullNameText = txtFullName.getText();
                    String title = "Successfully added employee";
                    String mess = "Employee "+ fullNameText +" has been successfully added";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    GaussianBlur blur = new GaussianBlur(0);
                    LoginController.stage.getScene().getRoot().setEffect(blur);
                }
            }
        }
        );

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

    private boolean userNameisExist(){
        String userName = txtUserName.getText();
        boolean flag = false;
        String query = "SELECT username FROM Account WHERE username = '"+userName+"'";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                lbUserNameValidator.setText("User Name is Exist");
                txtUserName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                lbUserNameValidator.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            }else {
                lbUserNameValidator.setText("");
                txtUserName.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

//    public static boolean emailIsValid(final String email) {
//        String EMAIL_PATTERN =
//                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    private boolean emailIsExist(){
        boolean flag = false;
        String email = txtEmail.getText();
//        if(emailIsValid(email)){
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
//        }else{
//            flag = false;
//        }
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
//        if(phoneIsValid(phoneNumber)){
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
//        }else {
//            flag = false;
//        }

        return flag;
    }

//    private boolean formIsNull(){
//        if(txtFullName.getText() == "" || txtUserName.getText() == "" || txtPassword.getText() == ""
//                || txtReEnterPassword.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
//                || txtPhoneNumber.getText() == ""|| txtEmail.getText() == ""|| txtAddress.getText() == "" ){
//            return true;
//        }else {
//            return false;
//        }
//    }

    private void AddTableAccount(){
        String username = txtUserName.getText();
        String password = txtPassword.getText();
        String position = cbPosition.getSelectionModel().getSelectedItem();
        String query = "INSERT INTO Account VALUES ('" + username +"', HASHBYTES('SHA2_512', '"+ password +"'), '" + position +"', 0);";

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

