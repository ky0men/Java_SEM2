package models;

public class Revenue {
    private int id;
    private String customerName, Date, revenueValue;

    public Revenue() {
    }

    public Revenue(int id, String customerName, String date, String revenueValue) {
        this.id = id;
        this.customerName = customerName;
        Date = date;
        this.revenueValue = revenueValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getRevenueValue() {
        return revenueValue;
    }

    public void setRevenueValue(String revenueValue) {
        this.revenueValue = revenueValue;
    }

}
