package controller;

import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static controller.LoginController.stage;

public class EmployeeProfileController implements Initializable {
    @FXML
    public Label lbFullName;

    @FXML
    public Label lbIdNumber;

    @FXML
    public Label lbBirthday;

    @FXML
    public Label lbEmail;

    @FXML
    public Label lbPhoneNumber;

    @FXML
    public Label lbAddress;

    @FXML
    public Label lbGender;

    @FXML
    public Label lbStartWork;

    @FXML
    public Label lbPosition;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnChangePassword;

    @FXML
    private Button btnRefresh;

    @FXML
    private ImageView imgAvatar;

    public int id;

    ObservableList<String> positionList = FXCollections.observableArrayList("Manager", "Front Office");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set Image
        Image avt = new Image("resources/images/lotus-logo.png");
        imgAvatar.setImage(avt);

        //Style Button
        ImageView editImg = new ImageView("/resources/images/edit.png");
        editImg.setFitHeight(16);
        btnEdit.setGraphic(editImg);

        ImageView changePasswordImg = new ImageView("/resources/images/synchronize.png");
        changePasswordImg.setFitHeight(16);
        btnChangePassword.setGraphic(changePasswordImg);

        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String birthday = lbBirthday.getText();
                String[] dateParts = birthday.split("-");
                int day = Integer.parseInt(dateParts[2]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[0]);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/EditEmployee.fxml"));
                Parent parent = null;
                try {
                    parent = loader.load();
                    EditEmployeeController editEmployeeController = loader.getController();
                    editEmployeeController.id = id;
                    editEmployeeController.txtEmail.setText(lbEmail.getText());
                    editEmployeeController.txtNoID.setText(lbIdNumber.getText());
                    editEmployeeController.txtPhoneNumber.setText(lbPhoneNumber.getText());
                    editEmployeeController.txtFullName.setText(lbFullName.getText());
                    if(lbGender.getText().equals("Male")){
                        editEmployeeController.radioMale.setSelected(true);
                    }else if(lbGender.getText().equals("Female")){
                        editEmployeeController.radioFemale.setSelected(true);
                    }
                    editEmployeeController.dpBirthday.setValue(LocalDate.of(year, month, day));
                    editEmployeeController.txtAddress.setText(lbAddress.getText());
                    editEmployeeController.cbPosition.setValue(lbPosition.getText());
                    editEmployeeController.cbPosition.setItems(positionList);
                    editEmployeeController.defaultEmail = lbEmail.getText();
                    editEmployeeController.defaultPhone = lbPhoneNumber.getText();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                GaussianBlur blurEffect = new GaussianBlur(10);
                stage.getScene().getRoot().setEffect(blurEffect);
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        });

        btnChangePassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/ChangePassword.fxml"));
                Parent parent = null;
                try {
                    parent = loader.load();
                    ChangePasswordController changePasswordController = loader.getController();
                    changePasswordController.email = lbEmail.getText();
                    changePasswordController.fullName = lbFullName.getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GaussianBlur blurEffect = new GaussianBlur(10);
                stage.getScene().getRoot().setEffect(blurEffect);
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        });

        btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String query = "SELECT * FROM Account JOIN EmployeeInformation ON Account.id = EmployeeInformation.userID WHERE Account.id = '" + id + "'";
                DBConnect dbConnect = new DBConnect();
                dbConnect.readProperties();
                Connection conn = dbConnect.getDBConnection();

                try {
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    while (rs.next()) {
                        id = rs.getInt("id");
                        lbFullName.setText(rs.getString("fullName"));
                        lbPosition.setText(rs.getString("position"));
                        lbGender.setText(rs.getString("userGender"));
                        lbIdNumber.setText(rs.getString("numberId"));
                        lbBirthday.setText(rs.getString("birthday"));
                        lbEmail.setText(rs.getString("userEmail"));
                        lbPhoneNumber.setText(rs.getString("userPhone"));
                        lbAddress.setText(rs.getString("userAddress"));
                    }
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


            }
        });
    }
}
