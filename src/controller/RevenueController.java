package controller;

import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Revenue;
import models.UsedServices;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RevenueController implements Initializable {

    @FXML
    private Label thisMonthRevenueLabel;

    @FXML
    private Label thisYearRevenue;

    @FXML
    private ChoiceBox<String> choiceTimeOption;

    @FXML
    private ChoiceBox<String> choiceDay;

    @FXML
    private ChoiceBox<String> choiceMonth;

    @FXML
    private ChoiceBox<String> choiceYear;

    @FXML
    private TableView<Revenue> tableRevenue;

    @FXML
    private TableColumn<?, ?> noCol;

    @FXML
    private TableColumn<?, ?> employeeCol;

    @FXML
    private TableColumn<?, ?> customerCol;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableColumn<?, ?> revenueCol;

    @FXML
    private Label typeRevenueLabel;

    @FXML
    private Label totalRevenue;


    private String filterType[] = {"Day", "Month", "Year"};
    private String filterYear[];
    private String filterMonth[];
    private String filterDay[];

    List<String> yearList = new ArrayList<String>();
    List<String> monthList = new ArrayList<String>();
    List<String> dayList = new ArrayList<String>();

    ObservableList<Revenue> revenueData = FXCollections.observableArrayList();

    double total;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();


        for (int i = 0; i < 10; i++) {
            yearList.add(String.valueOf(Integer.parseInt(getCurrentYear()) - 9 + i));
        }
        filterYear = new String[yearList.size()];
        yearList.toArray(filterYear);
        for (int i = 0; i < 12; i++) {
            monthList.add(String.valueOf(i + 1));
        }
        filterMonth = new String[monthList.size()];
        monthList.toArray(filterMonth);

        for (int i = 0; i < 31; i++) {
            dayList.add(String.valueOf(i + 1));
        }
        filterDay = new String[dayList.size()];
        dayList.toArray(filterDay);
        choiceDay.getItems().addAll(filterDay);


        thisMonthRevenueLabel.setText(formatCurrency(getRevenueByMonth(getCurrentMonth())));
        thisYearRevenue.setText(formatCurrency(getRevenueByYear(getCurrentYear())));

        choiceTimeOption.getItems().addAll(filterType);
        choiceMonth.getItems().addAll(filterMonth);
        choiceYear.getItems().addAll(filterYear);

        choiceYear.getSelectionModel().selectLast();
        choiceMonth.getSelectionModel().selectFirst();
        choiceDay.getSelectionModel().selectFirst();

        choiceYear.setDisable(true);
        choiceMonth.setDisable(true);
        choiceDay.setDisable(true);


        choiceTimeOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (choiceTimeOption.getValue().equals("Year")) {
                    choiceYear.setDisable(false);
                    choiceMonth.setDisable(true);
                    choiceDay.setDisable(true);
                    typeRevenueLabel.setText("year");
                    buildDataForTable(conn, "printYear = '" + choiceYear.getValue() + "'");
                } else if (choiceTimeOption.getValue().equals("Month")) {
                    choiceYear.setDisable(false);
                    choiceMonth.setDisable(false);
                    choiceDay.setDisable(true);
                    typeRevenueLabel.setText("month");
                    buildDataForTable(conn, "printYear = '" + choiceYear.getValue() + "' AND printMonth = '" + choiceMonth.getValue() + "'");
                } else if (choiceTimeOption.getValue().equals("Day")) {
                    choiceYear.setDisable(false);
                    choiceMonth.setDisable(false);
                    choiceDay.setDisable(false);
                    typeRevenueLabel.setText("day");
//                    getDayOfMonth();
                    buildDataForTable(conn, "printDate = '" + choiceYear.getValue() + "-" + choiceMonth.getValue() + "-" + choiceDay.getValue() + "'");
//                    getDataWhenChoseDay(conn);
                }
            }
        });

        choiceYear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (choiceTimeOption.getValue().equals("Year")) {
                    buildDataForTable(conn, "printYear = '" + choiceYear.getValue() + "'");
                } else if (choiceTimeOption.getValue().equals("Month")) {
                    buildDataForTable(conn, "printYear = '" + choiceYear.getValue() + "' AND printMonth = '" + choiceMonth.getValue() + "'");
                } else if (choiceTimeOption.getValue().equals("Day")) {
                    buildDataForTable(conn, "printDate = '" + choiceYear.getValue() + "-" + choiceMonth.getValue() + "-" + choiceDay.getValue() + "'");
                }
            }
        });

        choiceMonth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                getDayOfMonth();
                if (choiceTimeOption.getValue().equals("Month")) {
                    buildDataForTable(conn, "printYear = '" + choiceYear.getValue() + "' AND printMonth = '" + choiceMonth.getValue() + "'");
                }else if(choiceTimeOption.getValue().equals("Day")){
                    getDayOfMonth();
                    buildDataForTable(conn, "printDate = '" + choiceYear.getValue() + "-" + choiceMonth.getValue() + "-" + choiceDay.getValue() + "'");
                }
            }
        });

        choiceDay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                buildDataForTable(conn, "printDate = '" + choiceYear.getValue() + "-" + choiceMonth.getValue() + "-" + choiceDay.getValue() + "'");

            }
        });


    }

    public String getRevenueByMonth(String month) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT B.printMonth, SUM(B.revenue) AS 'Sum' FROM bill B WHERE B.printMonth = '" + month + "' GROUP By B.printMonth");
            while (rs.next()) {
                result = rs.getString("Sum");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    public String getRevenueByYear(String year) {
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;
        String result = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT B.printYear, SUM(B.revenue) AS 'Sum' FROM bill B WHERE B.printYear = '" + year + "' GROUP By B.printYear");
            while (rs.next()) {
                result = rs.getString("Sum");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }


    public String getCurrentMonth() {
        return new SimpleDateFormat("MM").format(new Date());
    }

    public String getCurrentYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    public String formatCurrency(String inputString) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        String newValueStr = formatter.format(Double.parseDouble(inputString));
        return newValueStr;
    }

    //Populate table
    public void buildDataForTable(Connection conn, String condition) {
        ResultSet rs = null;
        Statement stm = null;
        total = 0;
        revenueData.clear();
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT * FROM bill WHERE " + condition);
            int id = 0;
            while (rs.next()) {
                Revenue revenue = new Revenue();
                revenue.setId(++id);
                revenue.setCustomerName(rs.getString("customerName"));
                revenue.setDate(rs.getString("printDate"));
                revenue.setRevenueValue(rs.getString("revenue"));
                total += Double.parseDouble(revenue.getRevenueValue());
                revenueData.add(revenue);
            }
            tableRevenue.setItems(revenueData);
        } catch (SQLException throwables) {
//            throwables.printStackTrace();
        }finally {
            if(rs!= null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        tableRevenue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        noCol.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        customerCol.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        dateCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        revenueCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);

        noCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenueValue"));

        tableRevenue.setItems(revenueData);

        totalRevenue.setText(formatCurrency(String.valueOf(total)));

    }

    public void getDayOfMonth() {
        dayList.clear();
        int day = 0;
        if (choiceMonth.getValue().equals("1") || choiceMonth.getValue().equals("3") || choiceMonth.getValue().equals("5") || choiceMonth.getValue().equals("7") ||
                choiceMonth.getValue().equals("8") || choiceMonth.getValue().equals("10") || choiceMonth.getValue().equals("12")) {
            day = 31;
        }
        if (choiceMonth.getValue().equals("4") || choiceMonth.getValue().equals("6") || choiceMonth.getValue().equals("9") || choiceMonth.getValue().equals("11")) {
            day = 30;
        }
        if (choiceMonth.getValue().equals("2")) {
            Integer yearInt = Integer.valueOf(choiceYear.getValue());
            if (yearInt % 4 == 0 && yearInt % 100 == 0) {
                day = 29;
            } else {
                day = 28;
            }
        }

        for (int i = 0; i < day; i++) {
            dayList.add(String.valueOf(i + 1));
        }
        filterDay = new String[dayList.size()];
        dayList.toArray(filterDay);
        choiceDay.getItems().clear();
        choiceDay.getItems().addAll(filterDay);
        choiceDay.getSelectionModel().selectFirst();
    }

}
