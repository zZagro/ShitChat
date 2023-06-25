package de.zagro.shitchat.ui.chat;

public class Message {
    private String message, time;
    private boolean sent;

    public Message(String message, String time, boolean sent) {
        this.message = message;
        this.time = time;
        this.sent = sent;
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

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
