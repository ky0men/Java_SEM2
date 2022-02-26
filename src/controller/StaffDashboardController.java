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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

public class StaffDashboardController implements Initializable {

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
    private HBox hboxBookingManage;

    @FXML
    private JFXButton bookingManageBtn;

    @FXML
    private FontIcon bookingManageIcon;

    @FXML
    private HBox hboxCustomer;

    @FXML
    private JFXButton customerBtn;

    @FXML
    private FontIcon customersIcon;

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

    @FXML
    private Button minimizeBtnContainer;

    @FXML
    private Button maximizeBtnContainer;

    @FXML
    private Button exitBtnContainer;


    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changeContentScene("/resources/views/RoomMap.fxml");
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
                stage = (Stage) adminMainPane.getScene().getWindow();
                stage.close();
            }
        });
        exitBtnContainer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage = (Stage) adminMainPane.getScene().getWindow();
                stage.close();
            }
        });


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
        maximizeBtnContainer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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
        minimizeBtnContainer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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

        roomMapBtnClick();
        changeSceneWhenClickButton(bookingManageBtn, bookingManageIcon, hboxHome, hboxCustomer, "BookingManage.fxml");
        customerBtnClick();

        //Get account information in use
        getAccountInformationInUse();
    }

    private void changeContentScene(String sceneUrl) {
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
                hboxBookingManage.setStyle("-fx-background-radius: 0 20 0 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 0 0 10;");
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

    public void customerBtnClick(){
        customerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                System.out.println("Change to history scene");
                changeContentScene("/resources/views/Customers.fxml");
                removeSelectBtn();
                customerBtn.setStyle("-fx-background-color: #E9E9E9; -fx-text-fill:  #16314f; -fx-background-radius: 20 0 0 20;");
                customersIcon.setIconColor(Color.web("#16314f"));
                hboxBookingManage.setStyle("-fx-background-radius: 0 0 20 0;");
                vboxFooterLeft.setStyle("-fx-background-radius: 0 20 0 10;");
            }
        });
    }

    private void removeSelectBtn() {
        logoPane.setStyle("-fx-background-radius: 0;");
        vboxFooterLeft.setStyle("-fx-background-radius: 0;");
        roomMapBtn.setStyle("");
        hboxHome.setStyle("-fx-background-radius: 0;");
        roomMapIcon.setIconColor(Color.WHITE);
        bookingManageBtn.setStyle("");
        hboxBookingManage.setStyle("-fx-background-radius: 0;");
        bookingManageIcon.setIconColor(Color.WHITE);
        customerBtn.setStyle("");
        hboxCustomer.setStyle("-fx-background-radius: 0;");
        customersIcon.setIconColor(Color.WHITE);
    }

    private void resetButton() {
        roomMapBtn.setFocusTraversable(false);
        bookingManageBtn.setFocusTraversable(false);
        customerBtn.setFocusTraversable(false);
    }

    //Get account information is in use
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
}
