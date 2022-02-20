package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
    private TableColumn<?, ?> status;

    @FXML
    private TableColumn<?, ?> floor1;

    @FXML
    private TableColumn<?, ?> price;

    @FXML
    private TableColumn<?,?> perHours;
    @FXML
    private TableColumn<?,?> type;
    @FXML
    private JFXTextField roomNumber;


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
    private ComboBox<String> comboBox= new ComboBox<>();
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXButton editBtn;
    @FXML
    private JFXButton delBtn;
    @FXML
    private JFXButton refreshBtn;



    ObservableList<RoomSettingModel> oblist = FXCollections.observableArrayList();
    ObservableList<RoomSettingTypeModel> oblist1 = FXCollections.observableArrayList();
    ObservableList<String> oblistRoomType = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {

            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice,roomTimePrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID");
            while(rs.next()){
                oblist.add(new RoomSettingModel(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6)));
            }
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT * from RoomType");
            while(rs2.next()){
                comboBox.setItems(oblistRoomType);
                oblistRoomType.add(rs2.getString(2));
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
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        floor1.setCellValueFactory(new PropertyValueFactory<>("floor1"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        perHours.setCellValueFactory(new PropertyValueFactory<>("perHours"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));



        table.setItems(oblist);
        table1.setItems(oblist1);

        table.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(table.getSelectionModel().getSelectedItem()!=null){
                    roomNumber.setText(table.getSelectionModel().getSelectedItem().getNumber());
                    roomFloor.setText(String.valueOf(table.getSelectionModel().getSelectedItem().getFloor1()));
                    roomPrice.setText(table.getSelectionModel().getSelectedItem().getPrice());
                    pricePerHours.setText(table.getSelectionModel().getSelectedItem().getPerHours());
                    comboBox.getSelectionModel().select(table.getSelectionModel().getSelectedItem().getType());
                }
            }
        });

        table1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });

        editBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();
                    String rn = roomNumber.getText();
                    int type = 0;
                    String roomType = comboBox.getSelectionModel().getSelectedItem();
                    ResultSet rs1 = conn.createStatement().executeQuery("select * from RoomType");
                    while (rs1.next()){
                        type++;
                        if(roomType.equals(rs1.getString(2))){
                            break;
                        }
                    }
                    int flag =0;
                    ResultSet rs = conn.createStatement().executeQuery("select * from Room");
                    while(rs.next()){
                        if(Integer.parseInt(roomNumber.getText())==Integer.parseInt(rs.getString("roomName"))){
                            flag++;
                        }
                    }
                    String rf = roomFloor.getText();
                    String rp = roomPrice.getText();
                    String ph = pricePerHours.getText();
                    if (flag != 0){
                        conn.createStatement().executeUpdate("UPDATE Room SET roomTypeID =" + type +",roomPrice = "+ rp +",roomFloor ="+rf+",roomTimePrice ="+ph+" WHERE roomName ="+ rn+";");
                        reloadTable();
                    }else {
                        System.out.println("Romm khong Ton Tai");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        delBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();
                    int flag =0;
                    String rn = roomNumber.getText();
                    ResultSet rs = conn.createStatement().executeQuery("select * from Room");
                    while(rs.next()){
                        if(Integer.parseInt(rn)==Integer.parseInt(rs.getString("roomName"))){
                            flag++;
                        }
                    }
                    if(flag !=0){
                        conn.createStatement().executeUpdate("Delete from Room where roomName = "+ rn );
                        reloadTable();
                    }else {
                        System.out.println("Room kh ton tai");
                    }
                }catch (Exception e){
                    if(roomNumber.getText().replaceAll(" ","").length()==0){
                        System.out.println("Room name must be fill");
                    };
                }
            }
        });

        addBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();
                    int flag =0;
                    ResultSet rs = conn.createStatement().executeQuery("select * from Room");
                    while(rs.next()){
                        if(Integer.parseInt(roomNumber.getText())==Integer.parseInt(rs.getString("roomName"))){
                            flag++;
                        }
                    }
                    int type = 0;
                    String roomType = comboBox.getSelectionModel().getSelectedItem();
                    ResultSet rs1 = conn.createStatement().executeQuery("select * from RoomType");
                    while (rs1.next()){
                        type++;
                        if(roomType.equals(rs1.getString(2))){
                            break;
                        }
                    }
                    if(flag ==0){
                       conn.createStatement().executeUpdate("INSERT INTO Room VALUES ("+"'"+roomNumber.getText()+"'"+","+ type +",'Available',"+ roomPrice.getText() +","+pricePerHours.getText()+","+roomFloor.getText()+")");
                       reloadTable();
                    }else{
                        System.out.println("room ton tai");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }

        });

        refreshBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                reloadTable();
            }
        });


    }
    private void reloadTable(){
        oblist = FXCollections.observableArrayList();
        try{
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice,roomTimePrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID");
            while(rs.next()){
                oblist.add(new RoomSettingModel(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6)));
            }
            number.setCellValueFactory(new PropertyValueFactory<>("number"));
            status.setCellValueFactory(new PropertyValueFactory<>("status"));
            floor1.setCellValueFactory(new PropertyValueFactory<>("floor1"));
            price.setCellValueFactory(new PropertyValueFactory<>("price"));
            perHours.setCellValueFactory(new PropertyValueFactory<>("perHours"));
            type.setCellValueFactory(new PropertyValueFactory<>("type"));
            table.setItems(oblist);
        }catch (SQLException e){

        }
    }
}



