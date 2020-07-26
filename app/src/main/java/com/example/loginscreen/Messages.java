package com.example.loginscreen;

//class to retrieve messages
public class Messages {
    //should match in database
    private String message, type, from;

    public Messages() {
    }

    public Messages(String message, String type, String from) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
