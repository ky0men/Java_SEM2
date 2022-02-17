package models;

public class UsedServices {
    private int id;
    private String serviceName;
    private double price;
    private int quantity;
    private String unit;
    private double sum;

    public UsedServices() {
    }

    public UsedServices(int id, String serviceName, double price, int quantity, String unit, double sum) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
