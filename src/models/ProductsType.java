package models;

public class ProductsType {
    String id, typeName, size;

    public ProductsType(String id, String typeName, String size) {
        this.id = id;
        this.typeName = typeName;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
