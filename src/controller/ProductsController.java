package controller;

import com.jfoenix.controls.JFXComboBox;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ProductsType;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {
    @FXML
    private JFXComboBox<?> sizeComboBox;

    @FXML
    private TableView<ProductsType> productTypeTable;

    @FXML
    private TableColumn<ProductsType, String> colID;

    @FXML
    private TableColumn<ProductsType, String> colTypeName;

    @FXML
    private TableColumn<ProductsType, String> colSize;

    ObservableList<ProductsType> obList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        ArrayList size = null;

        try {
            size = new ArrayList();
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT DISTINCT PT.[Size] FROM ProductType PT");
            while(rs.next()){
                size.add(rs.getString("Size"));
            }
            sizeComboBox.setItems(FXCollections.observableArrayList(size));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        //Table view
//        ObservableList<ProductsType> obList = FXCollections.observableArrayList();
        try {
            rs = conn.createStatement().executeQuery("SELECT * FROM ProductType");
            while(rs.next()){

//                System.out.println(rs.getString("IDType"));
                obList.add(new ProductsType(rs.getString("IDType"), rs.getString("TypeName"), rs.getString("Size")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTypeName.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        productTypeTable.setItems(obList);
    }
}
