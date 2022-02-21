package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Service;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddServiceController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField tfName;

    @FXML
    private JFXComboBox<String> cmbType;

    @FXML
    private JFXTextField tfPrice;

    @FXML
    private JFXComboBox<String> cmbUnit;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private Label svNameValidation;

    @FXML
    private Label svTypeValidation;

    @FXML
    private Label svPriceValidation;

    @FXML
    private Label svUnitValidation;

    ObservableList<String> typeService = FXCollections.observableArrayList( "Food Service","Traveling Service","Relaxing Service","Sport - Entertainment Service","Others Service");
    ObservableList<String> unit = FXCollections.observableArrayList( "bottle","can","person","time","date");
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbUnit.setItems(unit);
        cmbType.setItems(typeService );
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

        //Connect to database
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        RequiredFieldValidator svNameValidation = new RequiredFieldValidator();
        tfName.getValidators().add(svNameValidation);
        svNameValidation.setMessage("Service Name is required!");

        RequiredFieldValidator svTypeValidation = new RequiredFieldValidator();
        cmbType.getValidators().add(svTypeValidation);
        svTypeValidation.setMessage("Type of Service is required!");

        RequiredFieldValidator svPriceValidation = new RequiredFieldValidator();
        tfPrice.getValidators().add(svPriceValidation);
        svPriceValidation.setMessage("Price is required!");

        RequiredFieldValidator svUnitValidation = new RequiredFieldValidator();
        cmbUnit.getValidators().add(svUnitValidation);
        svUnitValidation.setMessage("Unit is required!");

        RegexValidator priceRegexValidator = new RegexValidator();
        String priceRegrex = "^\\d+$";
        priceRegexValidator.setRegexPattern(priceRegrex);
        priceRegexValidator.setMessage("Price is only number");
        tfPrice.getValidators().add(priceRegexValidator);

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        tfName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    tfName.validate();
                }
                svNameisExist();
            }
        });
        tfPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    tfPrice.validate();
                }
            }
        });
        cmbType.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    cmbType.validate();
                }
            }
        });
        cmbUnit.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    cmbUnit.validate();
                }
            }
        });
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(tfName.getText().equals("") || tfPrice.getText().equals("")|| !tfPrice.getText().matches(priceRegrex)
                        || cmbType.getSelectionModel().getSelectedItem().equals("")
                        || cmbUnit.getSelectionModel().getSelectedItem().equals("")) {
                    tfName.validate();
                    cmbType.validate();
                    cmbUnit.validate();
                    tfPrice.validate();
                }
                else if(svNameisExist() == true || tfPrice.getText().equals("") ||!tfPrice.getText().matches(priceRegrex)
                        || cmbType.getSelectionModel().getSelectedItem().equals("")
                        || cmbUnit.getSelectionModel().getSelectedItem().equals("")){
                    cmbType.validate();
                    cmbUnit.validate();
                    tfPrice.validate();
                }
                else {
                    AddServiceTable();
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                    String serviceText = tfName.getText();
                    String title = "Successfully added service";
                    String mess = "Service " + serviceText + " has been successfully added";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    GaussianBlur blur = new GaussianBlur(0);
                    LoginController.stage.getScene().getRoot().setEffect(blur);
                }
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

    private void AddServiceTable(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement ctsm = null;
        try {
            ctsm = conn.prepareCall("{call addService(?,?,?,?)}");
            ctsm.setString(1,tfName.getText());
            ctsm.setString(2,cmbType.getValue());
            ctsm.setString(3,tfPrice.getText());
            ctsm.setString(4,cmbUnit.getValue());
            ctsm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void setService(Service service){
        tfName.setText(service.getName());
        tfPrice.setText(String.valueOf(service.getPrice()));
        cmbType.setValue(service.getType());
        cmbUnit.setValue(service.getUnit());
    }

    private boolean svNameisExist(){
        String svName = tfName.getText();
        boolean flag = false;
        String query = "SELECT ServiceName FROM Service WHERE ServiceName = '"+svName+"' AND isDeleted = 0";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                svNameValidation.setText("Service Name is Exist");
                tfName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                svNameValidation.setStyle("-fx-text-background-color: #D34437;");
                flag = true;
            }else {
                svNameValidation.setText("");
                tfName.setStyle("");
                flag = false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }
}