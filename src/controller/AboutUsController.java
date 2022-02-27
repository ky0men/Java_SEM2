package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutUsController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private JFXButton closeBtn;

    @FXML
    private AnchorPane aboutUsPane;

    private double x, y;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                aboutUsPane.getScene().getWindow().setX(mouseEvent.getScreenX() - x);
                aboutUsPane.getScene().getWindow().setY(mouseEvent.getScreenY() - y);
            }
        });

        closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                GaussianBlur blurEffect = new GaussianBlur(0);
                LoginController.stage.getScene().getRoot().setEffect(blurEffect);
                stage = (Stage) aboutUsPane.getScene().getWindow();
                stage.close();
            }
        });
    }
}
