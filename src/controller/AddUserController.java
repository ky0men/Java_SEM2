package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.util.Duration;
import models.EmployeeList;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.animations.AnimationType;
import tray.notification.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controller.LoginController.stage;

public class AddUserController implements Initializable {
    ObservableList<String> positionList = FXCollections.observableArrayList( "Manager","Front Office");

    @FXML
    private HBox titleBar;

    @FXML
    private JFXTextField txtFullName;

    @FXML
    private JFXTextField txtUserName;

    @FXML
    private JFXTextField txtNoID;

    @FXML
    private JFXTextField txtPhoneNumber;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtReEnterPassword;

    @FXML
    private Label lbWarning;

    @FXML
    private JFXComboBox<String> cbPosition;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private DatePicker dpBirthday;

    @FXML
    private FontIcon iconWarning;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    private double x, y;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set DatePicker
        dpBirthday.setValue(LocalDate.now().minusYears(18));
        dpBirthday.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate day = LocalDate.now().minusYears(18);

                setDisable(empty || date.compareTo(day) > 0 );
            }
        });

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

        //Set Combobox
        cbPosition.setValue("Manager");
        cbPosition.setItems(positionList);

        //Validate
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        txtUserName.getValidators().add(requiredFieldValidator);
        txtPassword.getValidators().add(requiredFieldValidator);
        txtReEnterPassword.getValidators().add(requiredFieldValidator);
        txtFullName.getValidators().add(requiredFieldValidator);
        txtNoID.getValidators().add(requiredFieldValidator);
        txtAddress.getValidators().add(requiredFieldValidator);
        txtEmail.getValidators().add(requiredFieldValidator);
        txtPhoneNumber.getValidators().add(requiredFieldValidator);

        txtUserName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("User Name is required!");
                    txtUserName.validate();
                }
            }
        });

        txtPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Password is required!");
                    txtPassword.validate();
                }
            }
        });

        txtReEnterPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Confirm Password is required!");
                    txtReEnterPassword.validate();
                }
            }
        });

        txtFullName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Full Name is required!");
                    txtFullName.validate();
                }
            }
        });

        txtNoID.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("ID Number is required!");
                    txtNoID.validate();
                }
            }
        });

        txtAddress.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Address is required!");
                    txtAddress.validate();
                }
            }
        });

        txtEmail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Email is required!");
                    txtEmail.validate();
                }
            }
        });

        txtPhoneNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    requiredFieldValidator.setMessage("Phone Number is required!");
                    txtPhoneNumber.validate();
                }
            }
        });

        //Add Employee
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(txtUserName.getText().equals("") && txtPassword.getText().equals("") && txtReEnterPassword.getText().equals("") &&
                        txtFullName.getText().equals("") && txtNoID.getText().equals("") && txtAddress.getText().equals("") &&
                        txtEmail.getText().equals("") && txtPhoneNumber.getText().equals("")){
                    requiredFieldValidator.setMessage("User Name is required!");
                    txtUserName.validate();
                    requiredFieldValidator.setMessage("Password is required!");
                    txtPassword.validate();
                    requiredFieldValidator.setMessage("Confirm Password is required!");
                    txtReEnterPassword.validate();
                    requiredFieldValidator.setMessage("Full Name is required!");
                    txtFullName.validate();
                    requiredFieldValidator.setMessage("ID Number is required!");
                    txtNoID.validate();
                    requiredFieldValidator.setMessage("Address is required!");
                    txtAddress.validate();
                    requiredFieldValidator.setMessage("Email is required!");
                    txtEmail.validate();
                    requiredFieldValidator.setMessage("Phone Number is required!");
                    txtPhoneNumber.validate();}
//                }else if(txtUserName.getText().equals("")){
//                    requiredFieldValidator.setMessage("User Name is required!");
//                    txtUserName.validate();
//                }else if(txtPassword.getText().equals("")){
//                    requiredFieldValidator.setMessage("Password is required!");
//                    txtPassword.validate();
//                }else if(txtReEnterPassword.getText().equals("")){
//                    requiredFieldValidator.setMessage("Confirm Password is required!");
//                    txtReEnterPassword.validate();
//                }else if(txtFullName.getText().equals("")){
//                    requiredFieldValidator.setMessage("Full Name is required!");
//                    txtFullName.validate();
//                }else if(txtNoID.getText().equals("")){
//                    requiredFieldValidator.setMessage("ID Number is required!");
//                    txtNoID.validate();
//                }else if(txtAddress.getText().equals("")){
//                    requiredFieldValidator.setMessage("Address is required!");
//                    txtAddress.validate();
//                } else if(txtEmail.getText().equals("")){
//                    requiredFieldValidator.setMessage("Email is required!");
//                    txtEmail.validate();
//                }else if(txtPhoneNumber.getText().equals("")){
//                    requiredFieldValidator.setMessage("Phone Number is required!");
//                    txtPhoneNumber.validate();
//                }
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Node node = (Node)event.getSource();
                Stage stage = (Stage)node.getScene().getWindow();
                stage.close();
                GaussianBlur blur = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blur);
            }
        });




    }



//    @FXML
//    void AddUser(ActionEvent event) {
//        formNotNull();
//        checkUserName();
//        checkPassword();
//        System.out.println("Add");
//        if(formNotNull() && checkUserName() && checkPassword() && checkEmail() && checkPhoneNumber()){
//            AddTableAccount();
//            AddTableProfile();
//            Node node = (Node)event.getSource();
//            Stage stage = (Stage)node.getScene().getWindow();
//            stage.close();
//            String fullNameText = txtFullName.getText();
//            String title = "Successfully added employee";
//            String mess = "Employee "+ fullNameText +" has been successfully added";
//            TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
//            tray.setAnimationType(AnimationType.POPUP);
//            tray.showAndDismiss(Duration.seconds(3));
//            tray.showAndWait();
//            GaussianBlur blur = new GaussianBlur(0);
//            LoginController.stage.getScene().getRoot().setEffect(blur);
//        }
//    }


    private boolean checkPassword(){
        String password = txtPassword.getText();
        String rePassword = txtReEnterPassword.getText();
        if(password != "" && rePassword != "" && password.equals(rePassword)){
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return true;
        }else {
            iconWarning.setVisible(true);
            lbWarning.setText("Wrong password try again");
            return false;
        }
    }
    private boolean checkUserName(){
        String userName = txtUserName.getText();
        boolean flag = false;
        String query = "SELECT username FROM Account";
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                if(userName.equals(rs.getString("username"))){
                    iconWarning.setVisible(true);
                    lbWarning.setText("Username already exists");
                    flag = false;
                }else{
                    iconWarning.setVisible(false);
                    lbWarning.setText("");
                    flag = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    public static boolean emailIsValid(final String email) {
        String EMAIL_PATTERN =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean checkEmail(){
        boolean flag = false;
        String email = txtEmail.getText();
        if(emailIsValid(email)){
            String query = "Select userEmail from EmployeeInformation";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()){
                    if(email.equals(rs.getString("userEmail"))){
                        iconWarning.setVisible(true);
                        lbWarning.setText("Email already exists");
                        flag = false;
                    }else{
                        iconWarning.setVisible(false);
                        lbWarning.setText("");
                        flag = true;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            iconWarning.setVisible(true);
            lbWarning.setText("Email invalid");
            flag = false;
        }
        return flag;
    }

    public static boolean phoneIsValid(final String phone) {
        String PHONE_PATTERN =
                "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean checkPhoneNumber(){
        boolean flag = false;
        String phoneNumber = txtPhoneNumber.getText();
        if(phoneIsValid(phoneNumber)){
            String query = "Select userPhone from EmployeeInformation";
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()){
                    if(phoneNumber.equals(rs.getString("userPhone"))){
                        iconWarning.setVisible(true);
                        lbWarning.setText("Phone Number already exists");
                        flag = false;
                    }else{
                        iconWarning.setVisible(false);
                        lbWarning.setText("");
                        flag = true;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            iconWarning.setVisible(true);
            lbWarning.setText("Phone Number invalid");
            flag = false;
        }

        return flag;
    }

    private boolean formIsNull(){
        if(txtFullName.getText() == "" || txtUserName.getText() == "" || txtPassword.getText() == ""
                || txtReEnterPassword.getText() == "" || txtNoID.getText() == "" || dpBirthday.getValue() == null
                || txtPhoneNumber.getText() == ""|| txtEmail.getText() == ""|| txtAddress.getText() == "" ){
            iconWarning.setVisible(true);
            lbWarning.setText("Please complete all information");
            return true;
        }else {
            iconWarning.setVisible(false);
            lbWarning.setText("");
            return false;
        }
    }

    private void AddTableAccount(){
        String username = txtUserName.getText();
        String password = txtPassword.getText();
        String position = cbPosition.getSelectionModel().getSelectedItem();
        String query = "INSERT INTO Account VALUES ('" + username +"', HASHBYTES('SHA2_512', '"+ password +"'), '" + position +"', 0);";

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

    private int getUserID(){
        int userId = 0;
        String username = txtUserName.getText();
        String query = "SELECT id FROM Account WHERE username = '"+ username +"';";

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                userId = rs.getInt("id");
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userId;
    }

    private void AddTableProfile(){
        int userId = getUserID();
        String fullName = txtFullName.getText();
        String numberId = txtNoID.getText();
        String startWork = LocalDate.now().toString();
        String birthday = dpBirthday.getValue().toString();
        String email = txtEmail.getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String query = "INSERT INTO EmployeeInformation VALUES ("+ userId +", '"+ fullName +"', '"+ numberId +"', '"+ startWork +"'," +
                " '"+ birthday +"', '"+ email +"', '"+ phone +"', '"+ address +"', '0');";

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

