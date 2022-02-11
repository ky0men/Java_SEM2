package controller;


import com.jfoenix.controls.JFXButton;
import com.sun.nio.sctp.Notification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
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
    private JFXButton logoutBtn;


    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/DancingScript-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Roboto-Regular.ttf"), 14);
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

        maximizeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage = (Stage) adminMainPane.getScene().getWindow();
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                } else {
                    stage.setMaximized(true);
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
                stage.hide();
                stage.setScene(loginScene);
                stage.setMaximized(false);
                stage.show();

            }
        });



        //Change content tab button
        roomMapBtnClick();

        changeSceneWhenClickButton(roomSettingBtn, roomSettingIcon, hboxHome, hboxEmployee, "RoomSetting.fxml");
        changeSceneWhenClickButton(employeeBtn, employeeIcon, hboxProducts, hboxOrder, "Employee.fxml");
        changeSceneWhenClickButton(servicesBtn, serviceIcon, hboxEmployee, hboxCustomer, "Services.fxml");
        changeSceneWhenClickButton(customerBtn, customersIcon, hboxOrder, hboxRevenue, "Customers.fxml");
        changeSceneWhenClickButton(revenueBtn, revenueIcon, hboxCustomer, hboxPromotions, "Revenue.fxml");
        changeSceneWhenClickButton(stockManagerBtn, stockManagerIcon, hboxRevenue, hboxHistory, "StockManage.fxml");

        historyBtnClick();


    }
    public void roomMapBtnClick(){
        roomMapBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Change to home Scene");
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
    public void historyBtnClick(){
        historyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Change to history scene");
                changeContentScene("/resources/views/History.fxml");
                removeSelectBtn();
                historyBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill:  #16314f; -fx-background-radius: 20 0 0 20;");
                historyIcon.setIconColor(Color.web("#16314f"));
                hboxPromotions.setStyle("-fx-background-radius: 0 0 20 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 20 0 10;");
            }
        });
    }
    public void changeSceneWhenClickButton(JFXButton clickBtn, FontIcon icon, HBox topHbox, HBox bottomHbox, String url){
        clickBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Change to scene");
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
        employeeBtn.setFocusTraversable(false);
        servicesBtn.setFocusTraversable(false);
        customerBtn.setFocusTraversable(false);
        revenueBtn.setFocusTraversable(false);
        stockManagerBtn.setFocusTraversable(false);
        historyBtn.setFocusTraversable(false);
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
        historyBtn.setStyle("");
        hboxHistory.setStyle("-fx-background-radius: 0;");
        historyIcon.setIconColor(Color.WHITE);
    }

    public void closeStage(){
        stage = (Stage) adminMainPane.getScene().getWindow();
        stage.close();
    }
}
