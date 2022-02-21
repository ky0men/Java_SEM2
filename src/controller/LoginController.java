package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private FontIcon closeBtn;

    @FXML
    private FontIcon minimizeBtn;

    @FXML
    private Label messLabel;

    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private Button loginBtn;

    public static Stage stage;
    private double x, y;
    private String position;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Window move, close and minimize action
        mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainPane.getScene().getWindow().setX(event.getScreenX() - x);
                mainPane.getScene().getWindow().setY(event.getScreenY() - y);
            }
        });
        closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage = (Stage) mainPane.getScene().getWindow();
                stage.close();
            }
        });
        minimizeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage = (Stage) mainPane.getScene().getWindow();
                stage.setIconified(true);
            }
        });

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("This field must be fieled. Please input again!");
        txtUsername.getValidators().add(validator);
        txtPassword.getValidators().add(validator);

        txtUsername.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txtUsername.validate();
                }

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

    }

    //Login button action
    @FXML
    void loginBtnAction(ActionEvent event) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        //Check account
        if (conn != null) {
            //Reset account status
            resetAccountStatus(conn);

            if (txtUsername.getText().equals("") || txtPassword.getText().equals("")) {
                messLabel.setText("Please input both!");
                messLabel.setWrapText(true);
                messLabel.setStyle("-fx-text-fill: red;");
            } else {
                if (checkAccount(txtUsername.getText(), txtPassword.getText(), conn)) {
                    changeAccountStatus(conn, txtUsername.getText());
                    if (checkIsManager(txtUsername.getText(), conn)) {
                        messLabel.setText("Login successful");
                        messLabel.setStyle("-fx-text-fill: green;");
                        Parent adminParent = null;
                        try {
                            adminParent = FXMLLoader.load(getClass().getResource("/resources/views/AdminDashboard.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene adminScene = new Scene(adminParent);
                        adminScene.setFill(Color.TRANSPARENT);
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Screen screen = Screen.getPrimary();
                        Rectangle2D bounds = screen.getVisualBounds();
                        stage.hide();
                        stage.setScene(adminScene);
                        stage.setWidth(1000);
                        stage.setHeight(700);
                        stage.setX((bounds.getWidth() - stage.getWidth())/2);
                        stage.setY((bounds.getHeight() - stage.getHeight())/2);
                        stage.show();

                        //Show notification login successful
                        TrayNotification tray = new TrayNotification();
                        tray.setTitle("Login Successful");
                        tray.setMessage("Welcome to hotel management dashboard");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.setAnimationType(AnimationType.POPUP);
                        tray.showAndDismiss(Duration.seconds(3));
                    } else {
                        messLabel.setText("Login successful");
                        messLabel.setStyle("-fx-text-fill: green;");
                        Parent staffParent = null;
                        try {
                            staffParent = FXMLLoader.load(getClass().getResource("/resources/views/StaffDashboard.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene staffScene = new Scene(staffParent);
                        staffScene.setFill(Color.TRANSPARENT);
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Screen screen = Screen.getPrimary();
                        Rectangle2D bounds = screen.getVisualBounds();
                        stage.hide();
                        stage.setScene(staffScene);
                        stage.setWidth(1000);
                        stage.setHeight(700);
                        stage.setX((bounds.getWidth() - stage.getWidth())/2);
                        stage.setY((bounds.getHeight() - stage.getHeight())/2);
                        stage.show();

                        //Show notification login successful
                        TrayNotification tray = new TrayNotification();
                        tray.setTitle("Login Successful");
                        tray.setMessage("Welcome to hotel management dashboard");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.setAnimationType(AnimationType.POPUP);
                        tray.showAndDismiss(Duration.seconds(3));
                    }
                } else {
                    messLabel.setText("Username and password is not correct");
                    messLabel.setStyle("-fx-text-fill: red;");
                }
            }
        } else {
            System.out.println("Wrong setting database information");
            dbConnect.showAlert("Connection failed", "Wrong database \nPlease input database information carefully! ");
            dbConnect.showSetupDB();
        }


    }

    //Check account is correct or not
    public boolean checkAccount(String username, String password, Connection conn) {
        ResultSet rs = null;
        CallableStatement cstm = null;
        boolean result = false;
        try {
            cstm = conn.prepareCall("{call checkLogin(?, ?)}");
            cstm.setString(1, username);
            cstm.setString(2, password);
            rs = cstm.executeQuery();
            if (rs.next()) {
                position = rs.getString("position");
                result = true;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Cannot call SQL procedure");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //Check position
    public boolean checkIsManager(String username, Connection conn) {
        ResultSet rs = null;
        boolean result = false;
        CallableStatement cstm = null;

        try {
            cstm = conn.prepareCall("{call checkIsManager(?, ?)}");
            cstm.setString(1, username);
            cstm.setString(2, "Manager");
            rs = cstm.executeQuery();
            if (rs.next()) {
                result = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
            if (cstm != null) {
                try {
                    cstm.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //Reset account status
    public void resetAccountStatus(Connection conn){
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("UPDATE Account SET accountStatus = '0' WHERE accountStatus = '1'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Change Account status
    public void changeAccountStatus(Connection conn, String username){
        CallableStatement cstm = null;
        try {
            cstm = conn.prepareCall("{call changeAccountStatusInUse(?)}");
            cstm.setString(1, username);
            cstm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
