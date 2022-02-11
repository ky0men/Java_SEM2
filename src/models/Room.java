package models;

public class Room {
    private String roomName, romeTypeName, roomStatus;

    public Room() {
    }
    public Room(String roomName){
        this.roomName = roomName;
    }
    public Room(String roomName, String romeTypeName, String roomStatus) {
        this.roomName = roomName;
        this.romeTypeName = romeTypeName;
        this.roomStatus = roomStatus;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRomeTypeName() {
        return romeTypeName;
    }

    public void setRomeTypeName(String romeTypeName) {
        this.romeTypeName = romeTypeName;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }
}
