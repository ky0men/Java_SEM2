package models;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;

public class EmployeeList {
    private String name;
    private String position;
    private String email;
    private String phoneNumber;
    ButtonBar buttonBar;
    JFXButton btnEdit, btnDelele, btnChangePassword;




    public EmployeeList() {
    }

    public EmployeeList(String name, String position, String email, String phoneNumber, ButtonBar buttonBar, JFXButton btnEdit, JFXButton btnDelele, JFXButton btnChangePassword) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.phoneNumber = phoneNumber;

        this.buttonBar = buttonBar;
        this.buttonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        this.buttonBar.setButtonMinWidth(30);

        ImageView editImg = new ImageView("/resources/images/edit.png");
        editImg.setFitHeight(16);
        this.btnEdit = btnEdit;
        this.btnEdit.setGraphic(editImg);

        ImageView deleteImg = new ImageView("/resources/images/delete.png");
        deleteImg.setFitHeight(16);
        this.btnDelele = btnDelele;
        this.btnDelele.setGraphic(deleteImg);

        ImageView changePasswordImg = new ImageView("/resources/images/synchronize.png");
        changePasswordImg.setFitHeight(16);
        this.btnChangePassword = btnChangePassword;
        this.btnChangePassword.setGraphic(changePasswordImg);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ButtonBar getButtonBar() {
        return buttonBar;
    }

    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }

    public JFXButton getBtnEdit() {
        return btnEdit;
    }

    public void setBtnEdit(JFXButton btnEdit) {
        this.btnEdit = btnEdit;
    }

    public JFXButton getBtnDelele() {
        return btnDelele;
    }

    public void setBtnDelele(JFXButton btnDelele) {
        this.btnDelele = btnDelele;
    }

    public JFXButton getBtnChangePassword() {
        return btnChangePassword;
    }

    public void setBtnChangePassword(JFXButton btnChangePassword) {
        this.btnChangePassword = btnChangePassword;
    }
}
