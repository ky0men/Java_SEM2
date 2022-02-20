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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Service;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditServiceController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField tfID;

    @FXML
    private JFXTextField tfName;

    @FXML
    private JFXTextField tfPrice;

    @FXML
    private JFXComboBox<String> cmbUnit;

    @FXML
    private Label svUnitValidation;

    @FXML
    private Label svNameValidation;

    @FXML
    private JFXComboBox<String> cmbType;

    @FXML
    private Label svTypeValidation;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnCancel;



    ObservableList<String> typeService = FXCollections.observableArrayList( "Food Service","Traveling Service","Relaxing Service","Sport - Entertainment Service","Others Service");
    ObservableList<String> unit = FXCollections.observableArrayList( "bottle","can","person","time","date");
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    private double x, y;

    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

    public void setService(Service service){
        tfID.setText(String.valueOf(service.getID()));
        tfName.setText(service.getName());
        tfPrice.setText(String.valueOf(service.getPrice()));
        cmbType.setValue(service.getType());
        cmbUnit.setValue(service.getUnit());
    }

    private boolean formNotNull(){
        if(tfID.getText() == "" || tfName.getText().equals("") || tfPrice.getText() == ""
                || cmbType.getSelectionModel().getSelectedItem() == "" || cmbUnit.getSelectionModel().getSelectedItem() == "") {
            return false;
        }else {
            return true;
        }
    }
    @FXML
    private void EditServiceTable(){
        int id = Integer.parseInt(tfID.getText());
        String Name = tfName.getText();
        String Type = cmbType.getSelectionModel().getSelectedItem();
        int price = Integer.parseInt(tfPrice.getText());
        String unit = cmbUnit.getSelectionModel().getSelectedItem();
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement ctsm = null;
        try {
            ctsm = conn.prepareCall("{call updateService(?,?,?,?,?)}");
            ctsm.setString(1,tfID.getText());
            ctsm.setString(2,tfName.getText());
            ctsm.setString(3,cmbType.getValue());
            ctsm.setString(4,tfPrice.getText());
            ctsm.setString(5,cmbUnit.getValue());
            ctsm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader serviceLoader = new FXMLLoader(getClass().getResource("/resources/views/Services.fxml"));
        try {
            Parent serviceParent = serviceLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServiceController serviceController = serviceLoader.getController();
        Service selected = serviceController.setModelService();
        setService(selected);
//        System.out.println(selected);
        cmbUnit.setItems(unit);
        cmbType.setItems(typeService);
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

        //Validate form
        RequiredFieldValidator validator = new RequiredFieldValidator();
        tfName.getValidators().add(validator);
        tfPrice.getValidators().add(validator);
        cmbType.getValidators().add(validator);
        cmbUnit.getValidators().add(validator);

        tfName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    validator.setMessage("Service Name is required!");
                    tfName.validate();
                }
            }
        });
        tfPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    validator.setMessage("Price is required!");
                    tfPrice.validate();
                }
            }
        });
        cmbType.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    validator.setMessage("Service Type is required!");
                    cmbType.validate();
                }
            }
        });
        cmbUnit.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    validator.setMessage("Unit is required!");
                    cmbUnit.validate();
                }
            }
        });
        RegexValidator priceRegexValidator = new RegexValidator();
        String priceRegex = "^\\d+$";
        priceRegexValidator.setRegexPattern(priceRegex);
        priceRegexValidator.setMessage("Price is only number");
        tfPrice.getValidators().add(priceRegexValidator);

        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkDuplicateData() == true){
                    svUnitValidation.setText("This unit has been existed.");
                    cmbUnit.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svUnitValidation.setStyle("-fx-text-background-color: #D34437;");

                    svNameValidation.setText("This name has been existed.");
                    tfName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svNameValidation.setStyle("-fx-text-background-color: #D34437;");

                    svTypeValidation.setText("This type has been existed.");
                    cmbType.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svTypeValidation.setStyle("-fx-text-background-color: #D34437;");
                }
                else if(!tfPrice.getText().matches(priceRegex)){
                    tfPrice.validate();
                }
                else if(formNotNull() == true && tfPrice.getText().matches(priceRegex)){
                    EditServiceTable();
                    Node node = (Node)event.getSource();
                    Stage stage = (Stage)node.getScene().getWindow();
                    stage.close();
                    String serviceText = tfName.getText();
                    String title = "Successfully edit service";
                    String mess = "Service "+ serviceText +" has been successfully edited";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    GaussianBlur blur = new GaussianBlur(0);
                    LoginController.stage.getScene().getRoot().setEffect(blur);
                    System.out.println("Edit successfull");
                }else {
                    svNameValidation.setText("");
                    tfName.setStyle("");
                    svTypeValidation.setText("");
                    cmbType.setStyle("");
                    svUnitValidation.setText("");
                    cmbUnit.setStyle("");
                    String title = "Incomplete Data";
                    String mess = "Please fill the data";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.WARNING);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    System.out.println("Incomplete Data.");
                }
            }
        });
    }

    private boolean checkDuplicateData(){
            String name = tfName.getText();
            String type = cmbType.getValue();
            String unit = cmbUnit.getValue();
            boolean flag = false;
            String query = "SELECT ServiceName, ServiceType,Unit FROM Service WHERE Unit = '" + unit + "' AND ((ServiceName = '"+name+"') AND (ServiceType = '"+type+"')) AND isDeleted = 0";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    flag = true;
                } else {
                    svUnitValidation.setText("");
                    cmbUnit.setStyle("");
                    flag = false;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        return flag;
    }
}
