package controller;


import com.jfoenix.controls.JFXButton;
import dao.DBConnect;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private AnchorPane adminMainPane;

    @FXML
    private AnchorPane logoPane;

    @FXML
    private HBox hboxHome;

    @FXML
    private JFXButton roomMapBtn;

    @FXML
    private FontIcon roomMapIcon;

    @FXML
    private HBox hboxProducts;

    @FXML
    private JFXButton roomSettingBtn;

    @FXML
    private FontIcon roomSettingIcon;

    @FXML
    private HBox hboxBookingManage;

    @FXML
    private JFXButton bookingManageBtn;

    @FXML
    private FontIcon bookingManageIcon;

    @FXML
    private HBox hboxEmployee;

    @FXML
    private JFXButton employeeBtn;

    @FXML
    private FontIcon employeeIcon;

    @FXML
    private HBox hboxOrder;

    @FXML
    private JFXButton servicesBtn;

    @FXML
    private FontIcon serviceIcon;

    @FXML
    private HBox hboxCustomer;

    @FXML
    private JFXButton customerBtn;

    @FXML
    private FontIcon customersIcon;

    @FXML
    private HBox hboxRevenue;

    @FXML
    private JFXButton revenueBtn;

    @FXML
    private FontIcon revenueIcon;

    @FXML
    private HBox hboxPromotions;

    @FXML
    private JFXButton stockManagerBtn;

    @FXML
    private FontIcon stockManagerIcon;

    @FXML
    private HBox hboxHistory;

    @FXML
    private JFXButton historyBtn;

    @FXML
    private FontIcon historyIcon;

    @FXML
    private VBox vboxFooterLeft;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private AnchorPane titleBar;

    @FXML
    private FontIcon minimizeBtn;

    @FXML
    private FontIcon maximizeBtn;

    @FXML
    private FontIcon closeBtn;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label accountPosition;

    @FXML
    private Label nameEmployee;

    @FXML
    private JFXButton aboutUsBtn;


    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changeContentScene("/resources/views/RoomMap.fxml");
//        System.out.println("admin loaded");

        resetButton();
        roomMapBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill: #16314f; -fx-background-radius: 20 0 0 20;");
        roomMapIcon.setIconColor(Color.web("#16314f"));


        titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            }
        });
        titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                adminMainPane.getScene().getWindow().setX(mouseEvent.getScreenX() - x);
                adminMainPane.getScene().getWindow().setY(mouseEvent.getScreenY() - y);
            }
        });

        closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                closeStage();
            }
        });

        //Maximize action
        maximizeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) adminMainPane.getScene().getWindow();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                if (stage.getX() == 0 && stage.getY() == 0) {
                    stage.setWidth(1000);
                    stage.setHeight(700);
                    stage.setX((bounds.getWidth() - stage.getWidth())/2);
                    stage.setY((bounds.getHeight() - stage.getHeight())/2);
                } else {
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                }
            }
        });

        //Double click to maximize
        titleBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) adminMainPane.getScene().getWindow();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                if(mouseEvent.getClickCount() == 2){
                    if (stage.getX() == 0 && stage.getY() == 0) {
                        stage.setWidth(1000);
                        stage.setHeight(700);
                        stage.setX((bounds.getWidth() - stage.getWidth())/2);
                        stage.setY((bounds.getHeight() - stage.getHeight())/2);
                    } else {
                        stage.setX(bounds.getMinX());
                        stage.setY(bounds.getMinY());
                        stage.setWidth(bounds.getWidth());
                        stage.setHeight(bounds.getHeight());
                    }
                }
            }
        });

        minimizeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) adminMainPane.getScene().getWindow();
                stage.setIconified(true);
            }
        });

        //Logout button event
        logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Parent loginParent = null;
                try {
                    loginParent = FXMLLoader.load(getClass().getResource("/resources/views/Login.fxml"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene loginScene = new Scene(loginParent);
                loginScene.setFill(Color.TRANSPARENT);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                stage.hide();
                stage.setScene(loginScene);
                stage.setWidth(600);
                stage.setHeight(400);
                stage.setX((bounds.getWidth() - stage.getWidth())/2);
                stage.setY((bounds.getHeight() - stage.getHeight())/2);
                stage.show();

            }
        });

        //Change content tab button
        roomMapBtnClick();

        changeSceneWhenClickButton(roomSettingBtn, roomSettingIcon, hboxHome, hboxBookingManage, "RoomSetting.fxml");
        changeSceneWhenClickButton(bookingManageBtn, bookingManageIcon, hboxProducts, hboxEmployee, "BookingManage.fxml");
        changeSceneWhenClickButton(employeeBtn, employeeIcon, hboxBookingManage, hboxOrder, "Employee.fxml");
        changeSceneWhenClickButton(servicesBtn, serviceIcon, hboxEmployee, hboxCustomer, "Services.fxml");
        changeSceneWhenClickButton(customerBtn, customersIcon, hboxOrder, hboxRevenue, "Customers.fxml");
        changeSceneWhenClickButton(revenueBtn, revenueIcon, hboxCustomer, hboxPromotions, "Revenue.fxml");

        setStockManagerBtnClick();

        //Get Account information in use
        getAccountInformationInUse();

    }
    public void roomMapBtnClick(){
        roomMapBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                System.out.println("Change to home Scene");
                changeContentScene("/resources/views/RoomMap.fxml");
                removeSelectBtn();
                roomMapBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill:  #16314f; -fx-background-radius: 20 0 0 20;");
                roomMapIcon.setIconColor(Color.web("#16314f"));
                logoPane.setStyle("-fx-background-radius: 0 0 20 0;");
                hboxProducts.setStyle("-fx-background-radius: 0 20 0 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 0 0 10;");
            }
        });
    }
    public void setStockManagerBtnClick(){
        stockManagerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                System.out.println("Change to history scene");
                changeContentScene("/resources/views/StockManage.fxml");
                removeSelectBtn();
                stockManagerBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill:  #16314f; -fx-background-radius: 20 0 0 20;");
                stockManagerIcon.setIconColor(Color.web("#16314f"));
                hboxRevenue.setStyle("-fx-background-radius: 0 0 20 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 20 0 10;");
            }
        });
    }
    public void changeSceneWhenClickButton(JFXButton clickBtn, FontIcon icon, HBox topHbox, HBox bottomHbox, String url){
        clickBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
//                System.out.println("Change to scene");
                changeContentScene("/resources/views/" + url);
                removeSelectBtn();
                clickBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill:  #16314f; -fx-background-radius: 20 0 0 20;");
                icon.setIconColor(Color.web("#16314f"));
                topHbox.setStyle("-fx-background-radius: 0 0 20 0;");
                bottomHbox.setStyle("-fx-background-radius: 0 20 0 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 0 0 10;");
            }
        });
    }

    public void changeContentScene(String sceneUrl) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneUrl));
        Region childrenPane = null;
        try {
            childrenPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentPane.getChildren().add(childrenPane);
        childrenPane.prefWidthProperty().bind(contentPane.widthProperty());
        childrenPane.prefHeightProperty().bind(contentPane.heightProperty());
//        System.out.println("changed");
    }

    private void resetButton() {
        roomMapBtn.setFocusTraversable(false);
        roomSettingBtn.setFocusTraversable(false);
        bookingManageBtn.setFocusTraversable(false);
        employeeBtn.setFocusTraversable(false);
        servicesBtn.setFocusTraversable(false);
        customerBtn.setFocusTraversable(false);
        revenueBtn.setFocusTraversable(false);
        stockManagerBtn.setFocusTraversable(false);
    }

    private void removeSelectBtn() {
        logoPane.setStyle("-fx-background-radius: 0;");
        vboxFooterLeft.setStyle("-fx-background-radius: 0;");
        roomMapBtn.setStyle("");
        hboxHome.setStyle("-fx-background-radius: 0;");
        roomMapIcon.setIconColor(Color.WHITE);
        roomSettingBtn.setStyle("");
        hboxProducts.setStyle("-fx-background-radius: 0;");
        roomSettingIcon.setIconColor(Color.WHITE);
        bookingManageBtn.setStyle("");
        hboxBookingManage.setStyle("-fx-background-radius: 0;");
        bookingManageIcon.setIconColor(Color.WHITE);
        employeeBtn.setStyle("");
        hboxEmployee.setStyle("-fx-background-radius: 0;");
        employeeIcon.setIconColor(Color.WHITE);
        servicesBtn.setStyle("");
        hboxOrder.setStyle("-fx-background-radius: 0;");
        serviceIcon.setIconColor(Color.WHITE);
        customerBtn.setStyle("");
        hboxCustomer.setStyle("-fx-background-radius: 0;");
        customersIcon.setIconColor(Color.WHITE);
        revenueBtn.setStyle("");
        hboxRevenue.setStyle("-fx-background-radius: 0;");
        revenueIcon.setIconColor(Color.WHITE);
        stockManagerBtn.setStyle("");
        hboxPromotions.setStyle("-fx-background-radius: 0;");
        stockManagerIcon.setIconColor(Color.WHITE);
    }

    //Get account infortion is in use
    public void getAccountInformationInUse(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT AC.position, EM.fullName FROM Account AC JOIN EmployeeInformation EM ON AC.id = EM.userID \n" +
                    "    WHERE AC.accountStatus = '1'");
            while (rs.next()){
                accountPosition.setText(rs.getString("position") + ":");
                nameEmployee.setText(rs.getString("fullName"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void closeStage(){
        stage = (Stage) adminMainPane.getScene().getWindow();
        stage.close();
    }
}
