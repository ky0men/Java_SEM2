package controller;

import com.jfoenix.controls.*;
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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
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
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

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
    private Label lbPasswordValidator;

    public String email;
    public String fullName;

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

        RequiredFieldValidator passwordValidation = new RequiredFieldValidator();
        txtPassword.getValidators().add(passwordValidation);
        passwordValidation.setMessage("Password is required!");

        RequiredFieldValidator confirmPassValidation = new RequiredFieldValidator();
        txtReEnterPassword.getValidators().add(confirmPassValidation);
        confirmPassValidation.setMessage("Confirm Password is required!");

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

        btnSave.setOnAction(event -> {
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

                //Show Notification
                String title = "Change password successfully";
                String mess = "Employee "+ fullName +" has changed his password";
                TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
                tray.showAndWait();

                //Close window
                Node node = (Node)event.getSource();
                Stage stage = (Stage)node.getScene().getWindow();
                stage.close();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);

                //Send new password to email
                try {
                    sendAccount();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });

        //Close Window
        btnCancel.setOnAction(event -> {
            Node node = (Node)event.getSource();
            Stage stage = (Stage)node.getScene().getWindow();
            stage.close();
            GaussianBlur blur = new GaussianBlur(0);
            LoginController.stage.getScene().getRoot().setEffect(blur);
        });
    }

    //Check Confirm Password
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

    //Get Username
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

    //Send New Password
    private void sendAccount() throws MessagingException {
        String host = "smtp.gmail.com";
        String user ="lotushotel.infor@gmail.com";
        String pass="165165165";
        String to = email;
        String subject="Lotus Hotel - Change Password";
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
        String h1 = "Change Password Successfully";
        String title = "Your new password";
        String content = "Password: "+ txtPassword.getText();
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
