package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private JFXTextField tfVolume;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnCancel;

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
    }

    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

    private boolean formNotNull(){
//        if(tfID.getText() == "" || tfName.getText() == "" || tfPrice.getText() == ""
//                || tfVolume.getText() == ""|| cmbType.getSelectionModel().getSelectedItem() == "" || cmbUnit.getSelectionModel().getSelectedItem() == ""){
//            iconWarning.setVisible(true);
//            lbWarning.setText("Please complete all information");
//            return false;
//        }else {
//            iconWarning.setVisible(false);
//            lbWarning.setText("");

//        }
        return true;
    }

    private void AddServiceTable(){
        String Name = tfName.getText();
        String Type = cmbType.getSelectionModel().getSelectedItem();
        int price = Integer.parseInt(tfPrice.getText());
        String unit = cmbUnit.getSelectionModel().getSelectedItem();
        int volume = Integer.parseInt(tfVolume.getText());
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        CallableStatement ctsm = null;
        try {
            ctsm = conn.prepareCall("{call addService(?,?,?,?,?)}");
            ctsm.setString(1,tfName.getText());
            ctsm.setString(2,cmbType.getValue());
            ctsm.setString(3,tfPrice.getText());
            ctsm.setString(4,cmbUnit.getValue());
            ctsm.setString(5,tfVolume.getText());
            ctsm.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void AddService(ActionEvent event) {
        formNotNull();
        if(formNotNull()){
            AddServiceTable();
            Node node = (Node)event.getSource();
            Stage stage = (Stage)node.getScene().getWindow();
            stage.close();
            String serviceText = tfName.getText();
            String title = "Successfully added service";
            String mess = "Service "+ serviceText +" has been successfully added";
            TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
            tray.setAnimationType(AnimationType.POPUP);
            tray.showAndDismiss(Duration.seconds(3));
            tray.showAndWait();
            GaussianBlur blur = new GaussianBlur(0);
            LoginController.stage.getScene().getRoot().setEffect(blur);
        }
    }
    public void setService(Service service){
        tfName.setText(service.getName());
        tfVolume.setText(String.valueOf(service.getVolume()));
        tfPrice.setText(String.valueOf(service.getPrice()));
        cmbType.setValue(service.getType());
        cmbUnit.setValue(service.getUnit());
    }
}