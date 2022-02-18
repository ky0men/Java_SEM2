package models;

public class RoomSettingModel {
     String number;
     String type;
     String status;
     int floor1;
     String price;
     String perHours;


    public RoomSettingModel(String number, String type, String status, int floor1, String price, String perHours) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.floor1 = floor1;
        this.price = price;
        this.perHours = perHours;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFloor1() {
        return floor1;
    }

    public void setFloor1(int floor1) {
        this.floor1 = floor1;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPerHours() {
        return perHours;
    }

    public void setPerHours(String perHours) {
        this.perHours = perHours;
    }




}
