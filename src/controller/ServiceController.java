package controller;

import com.jfoenix.validation.RequiredFieldValidator;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import models.Service;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import static controller.LoginController.stage;

public class ServiceController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<Service> table_service;

    @FXML
    private TableColumn<Service, Integer> col_id;

    @FXML
    private TableColumn<Service, String> col_name;

    @FXML
    private TableColumn<Service, String> col_type;

    @FXML
    private TableColumn<Service, Integer> col_price;

    @FXML
    private TableColumn<Service, String> col_unit;

    @FXML
    private TableColumn<Service, Integer> col_delete;

    int index = -1;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    ObservableList<Service> list;
    ObservableList<Service> dataList;
    Service data = null;
    static Service selected;

    public static ObservableList<Service> getService(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        ObservableList<Service> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Service WHERE isDeleted = 0");
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(new Service(Integer.parseInt(rs.getString(1)),rs.getString(2),
                        rs.getString(3),Integer.parseInt(rs.getString(4)),
                        rs.getString(5),
                        Integer.parseInt(rs.getString(6))));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private void openScene(String sceneUrl){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneUrl));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GaussianBlur blurEffect = new GaussianBlur(10);
        stage.getScene().getRoot().setEffect(blurEffect);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    @FXML
    void AddServiceAction(ActionEvent event) {
        openScene("/resources/views/AddService1.fxml");
    }

    @FXML
    void DeleteServiceAction(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        String id;
        try {
            Service selected = (Service) table_service.getSelectionModel().getSelectedItem();
            String query = "UPDATE Service SET isDeleted = 1 WHERE ID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,String.valueOf(selected.getID()));
            id = String.valueOf(selected.getID());
            ps.executeUpdate();
            ps.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        updateTable();
    }


    @FXML
    void search_service(){
        col_id.setCellValueFactory(new PropertyValueFactory<Service,Integer>("ID"));
        col_name.setCellValueFactory(new PropertyValueFactory<Service,String>("name"));
        col_type.setCellValueFactory(new PropertyValueFactory<Service,String>("type"));
        col_price.setCellValueFactory(new PropertyValueFactory<Service,Integer>("price"));
        col_unit.setCellValueFactory(new PropertyValueFactory<Service,String>("unit"));
        col_delete.setCellValueFactory(new PropertyValueFactory<Service, Integer>("isDeleted"));

        dataList = ServiceController.getService();
        table_service.setItems(dataList);
        FilteredList <Service> filterData = new FilteredList<>(dataList, b -> true);
        tfSearch.textProperty().addListener((observable, oldValue, newValue) ->{
            filterData.setPredicate(person ->{
                if (newValue == null || newValue.isEmpty()){
                    return true;
                }
                String lowertCaseFilter = newValue.toLowerCase(Locale.ROOT);

                if (person.getName().toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
                    return true;
                }
                else if (person.getType().toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
                    return true;
                }
                else if (person.getUnit().toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
                    return true;
                }
                else if (Integer.toString(person.getID()).toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
                    return true;
                }
                else if (Integer.toString(person.getPrice()).toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
                    return true;
                }
                else {
                    return false;
                }
            });
        });
        SortedList <Service> sortedList = new SortedList<>(filterData);
        sortedList.comparatorProperty().bind(table_service.comparatorProperty());
        table_service.setItems(sortedList);
    }

    public void updateTable(){
        col_id.setCellValueFactory(new PropertyValueFactory<Service,Integer>("ID"));
        col_name.setCellValueFactory(new PropertyValueFactory<Service,String>("name"));
        col_type.setCellValueFactory(new PropertyValueFactory<Service,String>("type"));
        col_price.setCellValueFactory(new PropertyValueFactory<Service,Integer>("price"));
        col_unit.setCellValueFactory(new PropertyValueFactory<Service,String>("unit"));
        col_delete.setCellValueFactory(new PropertyValueFactory<Service, Integer>("isDeleted"));

        list = ServiceController.getService();
        table_service.setItems(list);
    }



    @FXML
    public void Edit_Action(ActionEvent event) throws  IOException{
        data = table_service.getSelectionModel().getSelectedItem();
        if(data == null){
            String title = "Choose Service";
            String mess = "Please choose service you want to edit";
            TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
            tray.setAnimationType(AnimationType.POPUP);
            tray.showAndDismiss(Duration.seconds(3));
            tray.showAndWait();
            System.out.println("Please choose service to edit");
        }else{
        selected = table_service.getSelectionModel().getSelectedItem();
        openScene("/resources/views/EditService1.fxml");
        }
    }

    public void setCellValue(){
        table_service.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY );
        col_id.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        col_name.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        col_type.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        col_price.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        col_unit.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        col_delete.setMaxWidth(1f * Integer.MAX_VALUE * 0);

        col_id.setCellValueFactory(new PropertyValueFactory<Service,Integer>("ID"));
        col_name.setCellValueFactory(new PropertyValueFactory<Service,String>("name"));
        col_type.setCellValueFactory(new PropertyValueFactory<Service,String>("type"));
        col_price.setCellValueFactory(new PropertyValueFactory<Service,Integer>("price"));
        col_unit.setCellValueFactory(new PropertyValueFactory<Service,String>("unit"));
        col_delete.setCellValueFactory(new PropertyValueFactory<Service, Integer>("isDeleted"));

    }
    @Override
    public void initialize(URL url, ResourceBundle rb){
        //TODO
//        updateTable();
        search_service();

        //Alert Delete
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                data = table_service.getSelectionModel().getSelectedItem();
                if(data == null){
                    String title = "Choose Service";
                    String mess = "Please choose service you want to delete";
                    TrayNotification tray = new TrayNotification(title, mess, NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(3));
                    tray.showAndWait();
                    System.out.println("Please choose Service to delete");
                }else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to permanently delete this row?");
                    ButtonType btnTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                    ButtonType btnTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
                    alert.getButtonTypes().setAll(btnTypeYes, btnTypeNo);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == btnTypeYes) {
                        DeleteServiceAction();
                        String title = "Notify";
                        String mess = "This row was deleted";
                        TrayNotification tray = new TrayNotification(title, mess, NotificationType.SUCCESS);
                        tray.setAnimationType(AnimationType.POPUP);
                        tray.showAndDismiss(Duration.seconds(3));
                        tray.showAndWait();
                        System.out.println("Delete completed");
                    } else {
                        System.out.println("No delete the row.");
                    }
                }
            }
        });
        setCellValue();

    }

    public Service setModelService(){
        return selected;
    }
}
