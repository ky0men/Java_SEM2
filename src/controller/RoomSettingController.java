package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import models.RoomSettingModel;
import models.RoomSettingTypeModel;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
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
    @FXML
    private JFXButton refreshBtn1;
    @FXML
    private JFXTextField txtTypeName;
    @FXML
    private JFXButton addTypeBtn;
    @FXML
    private JFXButton editTypeBtn;
    @FXML
    private JFXButton delTypeBtn;

    ObservableList<RoomSettingModel> oblist = FXCollections.observableArrayList();
    ObservableList<RoomSettingTypeModel> oblist1 = FXCollections.observableArrayList();
    ObservableList<String> oblistRoomType = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RequiredFieldValidator roomName = new RequiredFieldValidator();
        roomNumber.getValidators().add(roomName);
        roomName.setMessage("Require");
        RequiredFieldValidator roomFloor1 = new RequiredFieldValidator();
        roomFloor.getValidators().add(roomFloor1);
        roomFloor1.setMessage("Require");
        RequiredFieldValidator roomPrice1 = new RequiredFieldValidator();
        roomPrice.getValidators().add(roomPrice1);
        roomPrice1.setMessage("Require");
        RequiredFieldValidator PerHoursPrice = new RequiredFieldValidator();
        pricePerHours.getValidators().add(PerHoursPrice);
        PerHoursPrice.setMessage("Require");





        try {

            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice,roomTimePrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID where isDeleteRoom = 0 ");
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


        comboBox.getSelectionModel().select(0);
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
                if(table1.getSelectionModel().getSelectedItem()!=null){
                    txtTypeName.setText(table1.getSelectionModel().getSelectedItem().getName());
                }
            }
        });

        editBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    if(fieldBool()){
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
                            conn.createStatement().executeUpdate("UPDATE Room SET roomTypeID =" + type +",roomPrice = "+ rp +",roomFloor ="+rf+",roomTimePrice ="+ph+" WHERE roomName ="+ rn);
                            reloadTable();
                            sucNotify("Success Edit","Room has been edited");
                        }else {
                            failNotify("Invalid Room","Room doesn't exits");
                        }
                    }else{
                        requireAdd();
                    }
                }catch (Exception e){
//                    e.printStackTrace();
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
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete Room");
                        alert.setHeaderText("Are you sure want to delete?");
                        alert.setContentText("Delete room "+ roomNumber.getText());
                        Optional<ButtonType> option = alert.showAndWait();
                        if(option.get()==ButtonType.OK){
                            conn.createStatement().executeUpdate("UPDATE Room set isDeleteRoom = 1 where roomName = '"+rn+"'" );
                            sucNotify("Delete Room","Room " + roomNumber.getText() + " has been deleted");
                            reloadTable();
                        }else if(option.get()==ButtonType.CANCEL){
                            failNotify("Delete Room","Cancelled!");
                        }
                    }else {
                        failNotify("Invalid Room","Room " + rn +" doesn't exist");
                    }
                }catch (Exception e){
                    if(roomNumber.getText().replaceAll(" ","").length()==0){
                        roomNumber.validate();
                    };
                }
            }
        });

        addBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    if(fieldBool()){
                        DBConnect dbConnect = new DBConnect();
                        dbConnect.readProperties();
                        Connection conn = dbConnect.getDBConnection();
                        int flag =0;
                        int check =0;
                        ResultSet rs = conn.createStatement().executeQuery("select * from Room");
                        while(rs.next()){
                            if(roomNumber.getText().equals(rs.getString("roomName"))&&rs.getInt("isDeleteRoom")==0){
                                flag++;
                                check=1;
                            }
                        }
                        System.out.println(check);

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
                            if(check ==1){
                                conn.createStatement().executeUpdate("INSERT INTO Room VALUES ("+"'"+roomNumber.getText()+"'"+","+ type +",'Available',"+ roomPrice.getText() +","+pricePerHours.getText()+","+roomFloor.getText()+",0)");
                                sucNotify("Success","Room " + roomNumber.getText() + " has been added");
                                reloadTable();
                            } else {
                                conn.createStatement().executeUpdate("Update Room set isDeleteRoom = 0, roomTypeID = "+type + ",roomFloor = "+ roomFloor.getText()+",roomPrice =" + roomPrice.getText() +",roomTimePrice ="+ pricePerHours.getText()+"where roomName = "+roomNumber.getText());
                                sucNotify("Success","Room " + roomNumber.getText() + " has been added");
                                reloadTable();
                            }


                        }else {
                            failNotify("Invalid room name","Room name already exists");
                        }
                    }else {
                        requireAdd();
                    }
                }catch (SQLException e){

                }
            }

        });

        refreshBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                reloadTable();

            }
        });

        refreshBtn1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                realoadTable1();
            }
        });

        addTypeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();
                    int flag =0;
                    ResultSet rs = conn.createStatement().executeQuery("Select * from RoomType");
                    while(rs.next()){
                        if(txtTypeName.getText().equals(rs.getString(2))){
                            flag++;
                        }
                    }
                    if(flag ==0){
                        conn.createStatement().executeUpdate("INSERT INTO RoomType (roomTypeName)" +
                                "VALUES (N'" + txtTypeName.getText() + "');");
                        sucNotify("Add type successed","Type " + txtTypeName.getText() + " has been added" );
                        realoadTable1();
                    } else {
                        failNotify("Add type Fail","This type already exits");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        editTypeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    DBConnect dbConnect = new DBConnect();
                    dbConnect.readProperties();
                    Connection conn = dbConnect.getDBConnection();
                    conn.createStatement().executeUpdate("UPDATE RoomType set roomTypeName=' ' where roomtypeID=");
                }catch (Exception e){

                }
            }
        });
    }

    private void realoadTable1(){
        oblist1 =FXCollections.observableArrayList();
        try{
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs1 = conn.createStatement().executeQuery("select * from RoomType");
            while (rs1.next()){
                oblist1.add(new RoomSettingTypeModel(rs1.getInt(1),rs1.getString(2)));
            }
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            table1.setItems(oblist1);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    private void reloadTable(){
        oblist = FXCollections.observableArrayList();
        try{
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice,roomTimePrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID where isDeleteRoom =0");
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
    private void sucNotify(String title, String messages){
        TrayNotification tray = new TrayNotification(title,messages, NotificationType.SUCCESS);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.seconds(3));
        tray.showAndWait();
    }
    private void failNotify(String title, String messages){
        TrayNotification tray = new TrayNotification(title,messages, NotificationType.ERROR);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.seconds(3));
        tray.showAndWait();
    }



    private boolean fieldBool(){
        int a= roomNumber.getText().replaceAll(" ","").length();
        int b= roomFloor.getText().replaceAll(" ","").length();
        int c = pricePerHours.getText().replaceAll(" ","").length();
        int d = roomPrice.getText().replaceAll(" ","").length();
        if (a!=0&&b!=0&&c!=0&&d!=0){
            return true;
        }
        return false;
    }

    private void requireAdd(){

        if(roomNumber.getText().replaceAll(" ","").length()==0){
            roomNumber.validate();
        }
        if(roomFloor.getText().replaceAll(" ","").length()==0){
            roomFloor.validate();
        }
        if(pricePerHours.getText().replaceAll(" ","").length()==0){
            pricePerHours.validate();
        }
        if(roomPrice.getText().replaceAll(" ","").length()==0){
            roomPrice.validate();
        }

    }

}



