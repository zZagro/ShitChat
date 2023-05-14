package de.zagro.shitchat;

import android.graphics.drawable.Drawable;

public class User {
    String name, message, time;
    Integer drawable;

    public User(String name, String message, String time, Integer drawable) {
        this.name = name;
        this.message = message;
        this.drawable = drawable;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getDrawable() {
        return drawable;
    }

    public void setDrawable(Integer drawable) {
        this.drawable = drawable;
    }
}
