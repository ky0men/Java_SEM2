package controller;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Room;

import java.io.FileNotFoundException;
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

    @FXML
    private JFXButton servicesBtn;

    @FXML
    private HBox hboxServiceContainer;

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
            servicesBtn.setText("Services");
            servicesBtn.setVisible(true);
            hboxServiceContainer.setStyle("-fx-padding: 0;");
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
                if (actionBtn.getText().equals("Check in")) {
                    //Get column and row of gridpane
                    col = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-column");
                    row = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-row");
                    gridRoomType = actionBtn.getParent().getParent().getParent().getParent().getParent().getId();

                    stage = (Stage) actionBtn.getScene().getWindow();
                    stage.close();
                    //Show booking room stage
                    showAction("/resources/views/CheckinRoom.fxml");
                } else if (actionBtn.getText().equals("Check Out")) {
                    //Get column and row of gridpane
                    col = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-column");
                    row = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-row");
                    gridRoomType = actionBtn.getParent().getParent().getParent().getParent().getParent().getId();

                    stage = (Stage) actionBtn.getScene().getWindow();
                    stage.close();
                    //Show check out stage
                    showAction("/resources/views/Checkout.fxml");
                } else if (actionBtn.getText().equals("Cleaned")) {
//                    System.out.println("Cleaned room");
                    //Get column and row of gridpane
                    col = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-column");
                    row = (Integer) actionBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-row");
                    gridRoomType = actionBtn.getParent().getParent().getParent().getParent().getParent().getId();

                    stage = (Stage) actionBtn.getScene().getWindow();
                    stage.close();
                    //Show confirm cleaned room stage
                    showAction("/resources/views/CleanedConfirm.fxml");
                }
            }
        });


        //ServiceBtn Action
        servicesBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                GaussianBlur blurEffect = new GaussianBlur(10);
//                servicesBtn.getScene().getRoot().setEffect(blurEffect);
//                stage.getScene().getRoot().setEffect(blurEffect);
                //Get column and row of gridpane
                col = (Integer) servicesBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-column");
                row = (Integer) servicesBtn.getParent().getParent().getParent().getParent().getProperties().get("gridpane-row");
                gridRoomType = servicesBtn.getParent().getParent().getParent().getParent().getParent().getId();
                stage = (Stage) servicesBtn.getScene().getWindow();
                stage.close();
//                System.out.println(servicesBtn.getParent().getParent());
                showAction("/resources/views/BookingService.fxml");
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
