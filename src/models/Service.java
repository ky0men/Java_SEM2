package models;


import javafx.scene.image.ImageView;

public class Service {
     int ID;
     String Name;
     String Type;
     int Price;
     String Unit;
     int isDeleted;

    public Service(int ID, String name, String type, int price, String unit, int isDeleted) {
        this.ID = ID;
        Name = name;
        Type = type;
        Price = price;
        Unit = unit;
        isDeleted = isDeleted;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Service{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", Type='" + Type + '\'' +
                ", Price=" + Price +
                ", Unit='" + Unit + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
