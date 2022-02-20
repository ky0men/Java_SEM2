package models;

public class CustomerList {
    private String name;
    private String gender;
    private String phoneNumber;
    private String idNumber;

    public CustomerList(String name, String gender, String phoneNumber, String idNumber) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
