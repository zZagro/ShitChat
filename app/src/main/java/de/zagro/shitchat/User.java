package de.zagro.shitchat;

import android.graphics.drawable.Drawable;

public class User {
    String name, message;
    Integer drawable;

    public User(String name, String message, Integer drawable) {
        this.name = name;
        this.message = message;
        this.drawable = drawable;
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

    public Integer getDrawable() {
        return drawable;
    }

    public void setDrawable(Integer drawable) {
        this.drawable = drawable;
    }
}
