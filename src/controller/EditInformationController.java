package controller;

import dao.DBConnect;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class EditInformationController implements Initializable {

    @FXML
    public TextField txtFullName;

    @FXML
    public TextField txtNoID;

    @FXML
    public DatePicker dpBirthday;

    @FXML
    public TextField txtEmail;

    @FXML
    public ComboBox<String> cbPosition;

    @FXML
    public ComboBox<String> cbStatus;

    @FXML
    public TextField txtAddress;

    @FXML
    public TextField txtPhoneNumber;

    @FXML
    public Button btnSave;

    @FXML
    public Button btnCancel;

    @FXML
    private Label lbWarning;

    @FXML
    private FontIcon iconWarning;

    public int id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }



    @FXML
    void CancelAction(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

    @FXML
    void SaveUser(ActionEvent event) {
        formNotNull();
        if(formNotNull()){
            UpdateTableAccount();
            UpdateTableProfile();
        }
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
        String title = "Successfully changed information";
        String mess = "Employee "+ txtFullName.getText() +" has successfully changed information";
        TrayNotification cancel = new TrayNotification(title, mess, NotificationType.SUCCESS);
        cancel.setAnimationType(AnimationType.POPUP);
        cancel.showAndWait();
    }

    private boolean formNotNull(){
        if(txtFullName.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
                || txtPhoneNumber.getText() == ""|| txtEmail.getText() == ""|| txtAddress.getText() == "" ){
            iconWarning.setVisible(true);
            lbWarning.setText("Please complete all information");
            return false;
        }else {
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return true;
        }
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
        String deleted = null;
        if(cbStatus.getSelectionModel().getSelectedItem().equals("Use")){
            deleted = "0";
        }else if(cbStatus.getSelectionModel().getSelectedItem().equals("Don't Use")){
            deleted = "1";
        }
        String query = "UPDATE EmployeeInformation SET fullName = '"+ fullName +"', numberId = '"+ numberId +"'," +
                " birthday = '"+ birthday +"', userEmail = '"+ email +"', userPhone = '"+ phone +"'," +
                " userAddress = '"+ address +"', deleted = '"+ deleted +"' WHERE userID = '"+ id +"'";

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
