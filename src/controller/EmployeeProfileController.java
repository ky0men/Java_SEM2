package controller;

import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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
    private Button btnCancel;

    @FXML
    private Button btnLoadAvatar;

    @FXML
    public ImageView imgAvatar;

    @FXML
    private AnchorPane titleBar;

    public int id;

    private double x, y;

    ObservableList<String> positionList = FXCollections.observableArrayList("Manager", "Employee", "Front Office");

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

        btnRefresh.setOnAction(event -> {
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


        });

        btnCancel.setOnAction(event -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            GaussianBlur blur = new GaussianBlur(0);
            LoginController.stage.getScene().getRoot().setEffect(blur);
        });

        btnLoadAvatar.setOnAction(event -> {
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Picture");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                //Change ImageView
                String fileName = null;
                try {
                    fileName = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Image avatar = new Image(fileName);
                imgAvatar.setImage(avatar);

                //Convert Image
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    if(!avatarIsNull()){
                        String query = "INSERT INTO EmployeeAvatar VALUES (?, ?)";
                        try {
                            PreparedStatement pst = conn.prepareStatement(query);
                            pst.setInt(1, getID());
                            pst.setBinaryStream(2,(InputStream) fileInputStream, (int) file.length());
                            pst.execute();
                            pst.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        String title = "Successfully Update Avatar";
                        String mess = "Your avatar has been changed successfully";
                        TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                        tray.setAnimationType(AnimationType.POPUP);
                        tray.showAndDismiss(Duration.seconds(3));
                        tray.showAndWait();
                    }else{
                        String query = "UPDATE EmployeeAvatar SET avatar = ? WHERE userID = ?";
                        try {
                            PreparedStatement pst = conn.prepareStatement(query);
                            pst.setBinaryStream(1,(InputStream) fileInputStream, (int) file.length());
                            pst.setInt(2, getID());
                            pst.execute();
                            pst.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        String title = "Successfully Update Avatar";
                        String mess = "Your avatar has been changed successfully";
                        TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                        tray.setAnimationType(AnimationType.POPUP);
                        tray.showAndDismiss(Duration.seconds(3));
                        tray.showAndWait();
                    }

                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Check status Avatar
    private boolean avatarIsNull() throws SQLException {
        String query = "SELECT avatar FROM EmployeeAvatar WHERE userID = "+ getID() +"";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs.next();
    }

    //Get ID
    private int getID(){
        int userID = 0;
        String query = "SELECT Account.id FROM Account JOIN EmployeeInformation " +
                "ON Account.id = EmployeeInformation.userID WHERE EmployeeInformation.userEmail = '"+ lbEmail.getText() +"'";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                userID = rs.getInt("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userID;
    }


}
