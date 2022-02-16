package controller;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Service;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import static controller.LoginController.stage;

public class ServiceController implements Initializable {

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
    private TableColumn<Service, Integer> col_volume;

    @FXML
    private TextField tfSearch;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnDelete;

    int index = -1;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    ObservableList<Service> list;
    ObservableList<Service> dataList;

    static Service selected;



    public static ObservableList<Service> getService(){
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();
        ObservableList<Service> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Service");
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
        openScene("/resources/views/AddService.fxml");
    }

    @FXML
    void search_service(){
        col_id.setCellValueFactory(new PropertyValueFactory<Service,Integer>("ID"));
        col_name.setCellValueFactory(new PropertyValueFactory<Service,String>("name"));
        col_type.setCellValueFactory(new PropertyValueFactory<Service,String>("type"));
        col_price.setCellValueFactory(new PropertyValueFactory<Service,Integer>("price"));
        col_unit.setCellValueFactory(new PropertyValueFactory<Service,String>("unit"));
        col_volume.setCellValueFactory(new PropertyValueFactory<Service,Integer>("volume"));

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
                else if (Integer.toString(person.getVolume()).toLowerCase(Locale.ROOT).indexOf(lowertCaseFilter) != -1){
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
        col_volume.setCellValueFactory(new PropertyValueFactory<Service,Integer>("volume"));

        list = ServiceController.getService();
        table_service.setItems(list);
    }


    @FXML
    public void Edit_Action(ActionEvent event) throws  IOException{
//        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/resources/views/EditService.fxml"));
//        Parent serviceAdd = loader.load();
//        Scene scene = new Scene(serviceAdd);
//        EditServiceController controller = loader.getController();
//        Service selected = table_service.getSelectionModel().getSelectedItem();
//        System.out.println(table_service.getSelectionModel().getSelectedItem());
//        System.out.println(table_service.getSelectionModel().getSelectedItem());
        selected = table_service.getSelectionModel().getSelectedItem();
//        System.out.println(selected);
        openScene("/resources/views/EditService.fxml");

//        controller.setService(selected);
//        stage.setScene(scene);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        //TODO
        updateTable();
        search_service();

    }

    public Service setModelService(){
        return selected;
    }
}