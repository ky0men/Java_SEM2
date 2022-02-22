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
    private Label svPriceValidation;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnCancel;
    public int id;
    public String defaultName;
    public String defaultUnit;


    ObservableList<String> typeService = FXCollections.observableArrayList("Food Service", "Traveling Service", "Relaxing Service", "Sport - Entertainment Service", "Others Service");
    ObservableList<String> unit = FXCollections.observableArrayList("bottle", "can", "person", "time", "date");
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    private double x, y;

    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        GaussianBlur blur = new GaussianBlur(0);
        LoginController.stage.getScene().getRoot().setEffect(blur);
    }

    public void setService(Service service) {
        tfID.setText(String.valueOf(service.getID()));
        tfName.setText(service.getName());
        tfPrice.setText(String.valueOf(service.getPrice()));
        cmbType.setValue(service.getType());
        cmbUnit.setValue(service.getUnit());
    }

    @FXML
    private void EditServiceTable() {
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
            ctsm.setString(1, tfID.getText());
            ctsm.setString(2, tfName.getText());
            ctsm.setString(3, cmbType.getValue());
            ctsm.setString(4, tfPrice.getText());
            ctsm.setString(5, cmbUnit.getValue());
            ctsm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if (ctsm != null){
                try {
                    ctsm.close();
                }catch (SQLException throwables){
                    throwables.printStackTrace();
                }
            }
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
                if (!newValue) {
                    validator.setMessage("Service Name is required!");
                    tfName.validate();
                }
            }
        });
        tfPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    validator.setMessage("Price is required!");
                    tfPrice.validate();
                }
            }
        });
        cmbType.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    validator.setMessage("Service Type is required!");
                    cmbType.validate();
                }
            }
        });
        cmbUnit.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    validator.setMessage("Unit is required!");
                    cmbUnit.validate();
                }
                checkUnitExist();
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
                if (tfName.getText().equals("") || cmbType.getSelectionModel().getSelectedItem().equals("") ||
                        cmbUnit.getSelectionModel().getSelectedItem().equals("") || tfPrice.getText().equals("") || !tfPrice.getText().matches(priceRegex)) {
                    tfName.validate();
                    tfPrice.validate();
                    cmbUnit.validate();
                    cmbType.validate();
                }
                else if (checkNameDoup()) {
                    tfName.validate();
                }
                else if (tfPrice.getText().matches(priceRegex) && !checkUnitExist()) {
                    EditServiceTable();
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                    String serviceText = tfName.getText();
                    String title = "Successfully edit service";
                    String mess = "Service " + serviceText + " has been successfully edited";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    GaussianBlur blur = new GaussianBlur(0);
                    LoginController.stage.getScene().getRoot().setEffect(blur);
                    System.out.println("Edit successfull");
                }
            }
        });
    }

    private int getID(String name) {
        Integer id = 0;
        String unit;
        try {
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            String query1 = "SELECT ID FROM Service WHERE ServiceName = '" + name + "' AND isDeleted = 0";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while (rs.next()) {
                id = rs.getInt(1);
            }
            System.out.println(id);
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }
    private String getUnit(String name){
        String unit = null;
        try {
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            String query1 = "SELECT Unit FROM Service WHERE ServiceName = '" + name + "' AND isDeleted = 0";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query1);
            while (rs.next()) {
                unit = rs.getString(1);
            }
            System.out.println(unit);
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return unit;
    }

    private boolean checkNameDoup() {
        int id1 = Integer.parseInt(tfID.getText());
        String unit1 = cmbUnit.getValue();
        String type1 = cmbType.getValue();
        boolean star = false;
        if (getID(tfName.getText()) != id1 && getID(tfName.getText())!=0
            && getUnit(tfName.getText()).equals(unit1) && !getUnit(tfName.getText()).equals("0")
            ) {
            svNameValidation.setText("This name existed.");
            tfName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
            svNameValidation.setStyle("-fx-text-background-color: #D34437;");
            star = true;
            System.out.println("true");
        }
        else {
            svNameValidation.setText("");
            svUnitValidation.setText("");
        }
        return star;
    }

    private boolean checkUnitExist() {
        boolean flag = false;
        Integer id1 = Integer.parseInt(tfID.getText());
        String name = tfName.getText();
        String type = cmbType.getValue();
        String unit = cmbUnit.getValue();
        String price = tfPrice.getText();

        if (!unit.equals(defaultUnit) && price.matches("^\\d+$") && !name.equals(defaultName)) {
            String query = "SELECT Unit from Service WHERE Unit ='" + unit + "' AND ServiceName = '" + name + "' AND ServiceType = '" + type + "' AND Price = '" + price + "' AND isDeleted = 0";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    svNameValidation.setText("This name existed.");
                    tfName.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svNameValidation.setStyle("-fx-text-background-color: #D34437;");

                    svTypeValidation.setText("This type existed.");
                    cmbType.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svTypeValidation.setStyle("-fx-text-background-color: #D34437;");

                    svUnitValidation.setText("This unit existed.");
                    cmbUnit.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svUnitValidation.setStyle("-fx-text-background-color: #D34437;");

                    svPriceValidation.setText("This price existed.");
                    tfPrice.setStyle("-jfx-focus-color:#E3867E;-jfx-unfocus-color:#D34437");
                    svPriceValidation.setStyle("-fx-text-background-color: #D34437;");
                    flag = true;
                } else {
                    svUnitValidation.setText("");
                    cmbUnit.setStyle("");
                    flag = false;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            tfPrice.validate();
            svUnitValidation.setText("");
            cmbUnit.setStyle("");
            flag = false;
        }
        return flag;
    }
}
