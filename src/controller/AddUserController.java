package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

public class AddUserController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList("Manager", "Front Office", "Employee");

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
    private JFXComboBox<String> cbPosition;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private DatePicker dpBirthday;

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

                setDisable(empty || date.compareTo(day) > 0);
            }
        });

        //Window move action
        titleBar.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            titleBar.getScene().getWindow().setX(event.getScreenX() - x);
            titleBar.getScene().getWindow().setY(event.getScreenY() - y);
        });

        //Set Combobox
        cbPosition.setValue("Manager");
        cbPosition.setItems(positionList);

        //Validate
        RequiredFieldValidator userNameValidation = new RequiredFieldValidator();
        txtUserName.getValidators().add(userNameValidation);
        userNameValidation.setMessage("Username is required!");

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


        txtUserName.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtUserName.validate();
            }
            userNameisExist();
        });

        txtPassword.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtPassword.validate();
            }
        });

        txtReEnterPassword.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtReEnterPassword.validate();
            }
            checkPassword();
        });

        txtFullName.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtFullName.validate();
            }
        });

        txtNoID.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtNoID.validate();
            }
        });

        txtAddress.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtAddress.validate();
            }
        });

        txtEmail.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtEmail.validate();
            }
            emailIsExist();
        });

        txtPhoneNumber.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                txtPhoneNumber.validate();
            }
            phoneNumberIsExist();
        });

        //Add Employee
        btnAdd.setOnAction(event -> {
            if (txtUserName.getText().equals("") || txtPassword.getText().equals("") || txtReEnterPassword.getText().equals("") ||
                    txtFullName.getText().equals("") || txtNoID.getText().equals("") || txtAddress.getText().equals("") ||
                    txtEmail.getText().equals("") || !txtEmail.getText().matches(RegexEmail) ||
                    txtPhoneNumber.getText().equals("") || !txtPhoneNumber.getText().matches(RegexPhone)) {
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
            if (!userNameisExist() && checkPassword() && txtEmail.getText().matches(RegexEmail) && !emailIsExist() &&
                    txtPhoneNumber.getText().matches(RegexPhone) && !phoneNumberIsExist()) {
                addTableAccount();
                addTableProfile();

                //Close Window
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();

                //Show Notification
                String fullNameText = txtFullName.getText();
                String title = "Successfully added employee";
                String mess = "Employee " + fullNameText + " has been successfully added";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);

                //Send Email
                try {
                    sendAccount();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        );

        btnCancel.setOnAction(event -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            GaussianBlur blur = new GaussianBlur(0);
            LoginController.stage.getScene().getRoot().setEffect(blur);
        });
    }

    private boolean checkPassword() {
        boolean flag = false;
        String password = txtPassword.getText();
        String rePassword = txtReEnterPassword.getText();
        if (!txtReEnterPassword.getText().equals("") && !password.equals(rePassword)) {
            lbPasswordValidator.setText("Password does not match");
            txtReEnterPassword.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
            lbPasswordValidator.setStyle("-fx-text-background-color: #D34437;");
            flag = false;
        } else if (txtReEnterPassword.getText().equals("")) {
            lbPasswordValidator.setText("");
            txtReEnterPassword.setStyle("");
            flag = false;
        } else if (password.equals(rePassword)) {
            lbPasswordValidator.setText("");
            txtReEnterPassword.setStyle("");
            flag = true;
        }
        return flag;
    }

    private boolean userNameisExist() {
        String username = txtUserName.getText();
        boolean flag = false;
        String query = "SELECT username FROM Account WHERE username = '" + username + "'";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                lbUserNameValidator.setText("Username is Exist!");
                txtUserName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                lbUserNameValidator.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            } else {
                lbUserNameValidator.setText("");
                txtUserName.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private boolean emailIsExist() {
        boolean flag = false;
        String email = txtEmail.getText();
        String query = "Select userEmail from EmployeeInformation WHERE userEmail='" + email + "'";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                lbEmailValidator.setText("Email is Exist!");
                txtEmail.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                lbEmailValidator.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            } else {
                lbEmailValidator.setText("");
                txtEmail.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private boolean phoneNumberIsExist() {
        boolean flag = false;
        String phoneNumber = txtPhoneNumber.getText();
        String query = "Select userPhone from EmployeeInformation WHERE userPhone= '" + phoneNumber + "'";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                lbPhoneValidator.setText("Phone Number is Exist!");
                txtPhoneNumber.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                lbPhoneValidator.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            } else {
                lbPhoneValidator.setText("");
                txtPhoneNumber.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    private void addTableAccount() {
        String username = txtUserName.getText();
        String password = txtPassword.getText();
        String position = cbPosition.getSelectionModel().getSelectedItem();
        String query = "INSERT INTO Account VALUES ('" + username + "', HASHBYTES('SHA2_512', '" + password + "'), '" + position + "', 0);";

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

    private int getUserID() {
        int userId = 0;
        String username = txtUserName.getText();
        String query = "SELECT id FROM Account WHERE username = '" + username + "';";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                userId = rs.getInt("id");
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userId;
    }

    private void addTableProfile() {
        int userId = getUserID();
        String fullName = txtFullName.getText();
        String numberId = txtNoID.getText();
        String startWork = LocalDate.now().toString();
        String birthday = dpBirthday.getValue().toString();
        String email = txtEmail.getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String query = "INSERT INTO EmployeeInformation VALUES (" + userId + ", N'" + fullName + "', '" + numberId + "', '" + startWork + "'," +
                " '" + birthday + "', '" + email + "', '" + phone + "', N'" + address + "', '0');";

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

    private void sendAccount() throws MessagingException {
        String host = "smtp.gmail.com";
        String user ="sem2.batch165@gmail.com";
        String pass="165165165";
        String to = txtEmail.getText ();
        String subject="Lotus Hotel - Welcome to Lotus Hotel";
        boolean sessionDebug = false;
        Properties prop = System.getProperties ();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        Session mailSession;
        mailSession = Session.getDefaultInstance (prop, null);
        mailSession.setDebug (sessionDebug);
        Message msg = new MimeMessage(mailSession);
        msg.setFrom (new InternetAddress(user));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients (Message. RecipientType.TO, address);
        msg.setSubject (subject);

        //ReplaceString HTML File
        String h1 = "Create Account Successfully";
        String title = "Your Account";
        String content = "Username: " + txtUserName.getText() +
                "<br> Password: "+ txtPassword.getText();
        replaceString("thisIsYourH1", h1);
        replaceString("thisIsYourTitle", title);
        replaceString("thisIsYourContent" , content);

        //Set Content Email
        msg.setContent(readFileHTMLToString(), "text/html; charset=UTF-8");
        Transport transport = mailSession.getTransport ("smtp");
        transport.connect (host, user, pass);
        transport.sendMessage (msg, msg.getAllRecipients());
        transport.close ();

        //Default HTML File
        replaceString(h1,"thisIsYourH1");
        replaceString(title,"thisIsYourTitle");
        replaceString(content,"thisIsYourContent");
    }

    private void replaceString(String oldValue, String newValue){
        Path path = Paths.get("src/resources/html/EmailTemplate.txt");
        Charset charset = StandardCharsets.UTF_8;

        String content = null;
        try {
            content = Files.readString(path, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        content = content.replaceAll(oldValue, newValue);
        try {
            Files.writeString(path, content, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileHTMLToString(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/resources/html/EmailTemplate.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }

        String body = sb.toString();
        return body;
    }


}

