package com.example.chatapplication;

import android.graphics.Bitmap;

public class Post {
    private String message;
    private String user_name;
    private int ID;
    Bitmap image;

    public String getUser_name() {
        return user_name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}