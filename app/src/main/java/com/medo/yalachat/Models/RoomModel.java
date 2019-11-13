package com.medo.yalachat.Models;

public class RoomModel {
    private String room_name,password,image,id;

    public RoomModel() {
    }

    public RoomModel(String room_name, String password, String image, String id) {
        this.room_name = room_name;
        this.password = password;
        this.image = image;
        this.id = id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}