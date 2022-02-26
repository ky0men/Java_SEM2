package models;

public class EmployeeList {
    private String name;
    private String position;
    private String email;
    private String phoneNumber;

    public EmployeeList(String name, String position, String email, String phoneNumber) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.phoneNumber = phoneNumber;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
