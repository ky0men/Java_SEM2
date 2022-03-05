package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ResetPasswordController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField userNameLabel;

    @FXML
    private JFXTextField emailLabel;

    @FXML
    private Label validateMessage;

    @FXML
    private JFXButton resetBtn;

    @FXML
    private JFXButton cancelBtn;

    private double x, y;
    Stage stage;
    private int randomCode;


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


        //Cancel button action
        cancelBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) cancelBtn.getScene().getWindow();
                stage.close();
            }
        });

        //Connect database
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();


        RequiredFieldValidator validator = new RequiredFieldValidator();
        userNameLabel.getValidators().add(validator);
        emailLabel.getValidators().add(validator);

        RegexValidator emailRegexValidator = new RegexValidator();
        String RegexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        emailRegexValidator.setRegexPattern(RegexEmail);
        emailRegexValidator.setMessage("Your Email is not valid");
        emailLabel.getValidators().add(emailRegexValidator);

        userNameLabel.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    validator.setMessage("User name is required!");
                    userNameLabel.validate();
                }
            }
        });

        emailLabel.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    validator.setMessage("Email is required!");
                    emailLabel.validate();
                }
            }
        });

        resetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                validateMessage.setVisible(false);
                if(userNameLabel.getText().equals("") && emailLabel.getText().equals("")){
                    validator.setMessage("User name is required!");
                    userNameLabel.validate();
                    validator.setMessage("Email is required!");
                    emailLabel.validate();
                }else if(userNameLabel.getText().equals("")){
                    validator.setMessage("User name is required!");
                    userNameLabel.validate();
                }else if(emailLabel.getText().equals("")){
                    validator.setMessage("Email is required!");
                    emailLabel.validate();
                }else if(!emailLabel.getText().matches(RegexEmail)){
                    emailLabel.validate();
                }else{
                    if(getAccountIDFromUsername(conn, userNameLabel.getText()) == null){
                        validateMessage.setVisible(true);
                        validateMessage.setText("User name " + userNameLabel.getText() + " does not exist!");
                    }else if(!getEmailFromUserID(conn, getAccountIDFromUsername(conn, userNameLabel.getText())).equals(emailLabel.getText())){
                        validateMessage.setVisible(true);
                        validateMessage.setText("User name " +userNameLabel.getText()+" does not match with email " +emailLabel.getText()+ "") ;
                    }else{
                        System.out.println("reset pass");
                        randomCode = (int) Math.floor(((Math.random() * 899999) + 100000));
                        sendEmailResetPass(randomCode);
                        changePasswordInDB(conn, randomCode, userNameLabel.getText());
                        stage = (Stage) resetBtn.getScene().getWindow();
                        stage.close();
                    }
                }
            }
        });
    }

    public String getAccountIDFromUsername(Connection conn, String userName){
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT * FROM Account WHERE username = '"+userName+"'");
            if(rs.next()){
                result = rs.getString("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return result;
    }

    public String getEmailFromUserID(Connection conn, String id){
        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT * FROM EmployeeInformation WHERE userID = '"+id+"'");
            if(rs.next()){
                result = rs.getString("userEmail");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return result;
    }

    public void sendEmailResetPass(int randomCode){
        String host = "smtp.gmail.com";
        String user ="lotushotel.infor@gmail.com";
        String pass="165165165";
        String to = emailLabel.getText();
        String subject="Project Sem2 - Reseting Code";
        String message = "Your reset code is "+ randomCode;
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
        try {
            msg.setFrom (new InternetAddress(user));
            InternetAddress [] address = {new InternetAddress(to)};
            msg.setRecipients (Message. RecipientType.TO, address);
            msg.setSubject (subject);
//            msg.setText (message);
//            Transport transport = mailSession.getTransport ("smtp");
//            transport.connect (host, user, pass);
//            transport.sendMessage (msg, msg.getAllRecipients());
//            transport.close ();
//            System.out.println("Send Code");
            //ReplaceString HTML File
            String h1 = "Reset Password Successfully";
            String title = "Your new password";
            String content = "Password: "+ randomCode;
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
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
    //HTML Template config
    private void replaceString(String oldValue, String newValue){
        Path path = Paths.get("email-template/email-template.txt");
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
            scanner = new Scanner(new File("email-template/email-template.txt"));
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

    public void changePasswordInDB(Connection conn, int randomCode, String username){
        Statement stm = null;
        try {
            stm = conn.createStatement();
            int rowEffect = stm.executeUpdate("UPDATE Account SET passwordHash = HASHBYTES('SHA2_512', '"+randomCode+"') WHERE username = '"+username+"'");
            if(rowEffect > 0){
                //Show notification login successful
                TrayNotification tray = new TrayNotification();
                tray.setTitle("Reset password successful");
                tray.setMessage("Please check your email.");
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.setAnimationType(AnimationType.POPUP);
                tray.showAndDismiss(Duration.seconds(3));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

}
