package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class StockManageController implements Initializable {

    @FXML
    private BarChart<?, ?> barChart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Stock In");
        series1.getData().add(new XYChart.Data<>("Coca-Cola", 220));
        series1.getData().add(new XYChart.Data<>("Beer Heiniken", 200));
        series1.getData().add(new XYChart.Data<>("Aqua", 130));
        series1.getData().add(new XYChart.Data<>("Snack", 130));
        series1.getData().add(new XYChart.Data<>("Car", 5));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Stock Out");
        series2.getData().add(new XYChart.Data<>("Coca-Cola", 20));
        series2.getData().add(new XYChart.Data<>("Beer Heiniken", 8));
        series2.getData().add(new XYChart.Data<>("Aqua", 5));
        series2.getData().add(new XYChart.Data<>("Snack", 5));
        series2.getData().add(new XYChart.Data<>("Car", 2));

        barChart.getData().addAll(series1, series2);
    }
}
