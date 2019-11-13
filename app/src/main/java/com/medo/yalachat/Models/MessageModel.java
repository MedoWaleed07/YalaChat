package com.medo.yalachat.Models;

public class MessageModel {
    private String name,message,image,id;

    public MessageModel() {
    }

    public MessageModel(String name, String message, String image, String id) {
        this.name = name;
        this.message = message;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
