package controller;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.charts.Legend;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.RoomSettingModel;
import models.RoomSettingTypeModel;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomSettingController implements Initializable {
    @FXML
    private TableView<RoomSettingModel> table;

    @FXML
    private TableColumn<?, ?> number;

    @FXML
    private TableColumn<?, ?> type;

    @FXML
    private TableColumn<?, ?> status;

    @FXML
    private TableColumn<?, ?> floor1;

    @FXML
    private TableColumn<?, ?> price;

    @FXML
    private TableColumn<?,?> perHours;

    @FXML
    private JFXTextField roomNumber;

    @FXML
    private JFXTextField roomType;

    @FXML
    private JFXTextField roomFloor;

    @FXML
    private JFXTextField roomPrice;

    @FXML
    private JFXTextField pricePerHours;

    @FXML
    private TableView<RoomSettingTypeModel> table1;

    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> name;
    @FXML
    private ComboBox comboBox;




    ObservableList<RoomSettingModel> oblist = FXCollections.observableArrayList();
    ObservableList<RoomSettingTypeModel> oblist1 = FXCollections.observableArrayList();
    

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBox.getItems().addAll("Option A", "Option B", "Option C");
        try {
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice,roomTimePrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID");
            while(rs.next()){
                oblist.add(new RoomSettingModel(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6)));
            }

        } catch (SQLException e) {
            Logger.getLogger(RoomSettingController.class.getName()).log(Level.SEVERE,null,e);
            e.printStackTrace();
        }
        try{
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs1 = conn.createStatement().executeQuery("select * from RoomType");
            while (rs1.next()){
                oblist1.add(new RoomSettingTypeModel(rs1.getInt(1),rs1.getString(2)));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        floor1.setCellValueFactory(new PropertyValueFactory<>("floor1"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        perHours.setCellValueFactory(new PropertyValueFactory<>("perHours"));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));


        table.setItems(oblist);
        table1.setItems(oblist1);

        table.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                roomNumber.setText(table.getSelectionModel().getSelectedItem().getNumber());
                roomType.setText(table.getSelectionModel().getSelectedItem().getType());
                roomFloor.setText(String.valueOf(table.getSelectionModel().getSelectedItem().getFloor1()));
                roomPrice.setText(table.getSelectionModel().getSelectedItem().getPrice());
                pricePerHours.setText(table.getSelectionModel().getSelectedItem().getPerHours());
            }
        });

        table1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
    }


}



