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


    private double x, y;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/DancingScript-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Roboto-Regular.ttf"), 14);
        changeContentScene("/resources/views/RoomMap.fxml");
//        resetButton();
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
                stage.hide();
                stage.setScene(loginScene);
                stage.setMaximized(false);
                stage.show();

            }
        });

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
}
