package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Room;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static controller.LoginController.stage;

public class RoomViewController implements Initializable {

    @FXML
    private Label roomNameLabel;

    @FXML
    private Label roomTypeNameLabel;

    @FXML
    private Label roomStatus;

    @FXML
    private AnchorPane roomViewAnchorPane;

    @FXML
    private JFXButton actionBtn;

    @FXML
    private HBox hBoxBtn;

    private static Integer col, row;

    private static String gridRoomType;

    private Room room;

    public void setData(Room room) {
        this.room = room;
        roomNameLabel.setText(room.getRoomName());
        roomTypeNameLabel.setText(room.getRomeTypeName());
        roomStatus.setText(String.valueOf(room.getRoomStatus()));
        if (room.getRoomStatus().equals("Rented")) {
            roomViewAnchorPane.setStyle("-fx-background-color: #ee6a5f;-fx-background-radius: 10;");
            actionBtn.setText("Check Out");
            JFXButton servicesBtn = new JFXButton("Services");
            servicesBtn.getStylesheets().add("/resources/styles/setButton.css");
            servicesBtn.getStyleClass().add("set-type2-btn");
            servicesBtn.setCursor(Cursor.HAND);
            hBoxBtn.getChildren().add(servicesBtn);
        } else if (room.getRoomStatus().equals("Dirty")) {
            roomViewAnchorPane.setStyle("-fx-background-color: #f5bd4f; -fx-background-radius: 10;");
            actionBtn.setText("Cleaned");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionBtn.getText().equals("Booking")) {
                    System.out.println("Book Room");
                    //Blur primary stage
                    GaussianBlur blurEffect = new GaussianBlur(10);
                    stage.getScene().getRoot().setEffect(blurEffect);
                    //Get column and row of gridpane
                    col = (Integer) actionBtn.getParent().getParent().getParent().getProperties().get("gridpane-column");
                    row = (Integer) actionBtn.getParent().getParent().getParent().getProperties().get("gridpane-row");
                    gridRoomType = actionBtn.getParent().getParent().getParent().getParent().getId();

                    stage = (Stage) actionBtn.getScene().getWindow();
                    stage.close();
                    //Show booking room stage
                    showAction("/resources/views/BookingRoom.fxml");
                } else if (actionBtn.getText().equals("Check Out")) {
                    System.out.println("Check out");
                } else if (actionBtn.getText().equals("Cleaned")) {
                    System.out.println("Cleaned room");
                    //Get column and row of gridpane
                    col = (Integer) actionBtn.getParent().getParent().getParent().getProperties().get("gridpane-column");
                    row = (Integer) actionBtn.getParent().getParent().getParent().getProperties().get("gridpane-row");
                    gridRoomType = actionBtn.getParent().getParent().getParent().getParent().getId();
//                    System.out.println(getRow());
//                    System.out.println(actionBtn.getParent().getParent().getParent().getProperties());

                    stage = (Stage) actionBtn.getScene().getWindow();
                    stage.close();
                    //Show confirm cleaned room stage
                    showAction("/resources/views/CleanedConfirm.fxml");
                }
            }
        });
    }

    private void showAction(String actionUrl) {
        Stage actionStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(actionUrl));
        Parent actionRoot = null;
        try {
            actionRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(actionRoot);
        scene.setFill(Color.TRANSPARENT);
        actionStage.setScene(scene);
        actionStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/hotel-icon.png")));
        actionStage.initModality(Modality.APPLICATION_MODAL);
        actionStage.initStyle(StageStyle.TRANSPARENT);
        actionStage.showAndWait();
    }

    public Integer getCol() {
        return col;
    }

    public Integer getRow() {
        return row;
    }

    public String getGridRoomType(){
        return gridRoomType;
    }
}
