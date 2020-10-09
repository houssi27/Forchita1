package com.example.forchita;

public class UserHelperClass {
    String Note;
    String name;
    String UserID;
    String RestoID;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String UserID, String RestoID, String Note) {
        this.UserID = UserID;
        this.RestoID = RestoID;
        this.Note = Note;
        this.name = name;
    }
    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getRestoID() {
        return RestoID;
    }

    public void setRestoID(String restoID) {
        RestoID = restoID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        this.Note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
