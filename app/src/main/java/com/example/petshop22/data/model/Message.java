package com.example.petshop22.data.model;

public class Message {

    private long id;
    private long userId;
    private long productId;
    private String message;
    private String sender;
    private long timestamp;

    public Message() {}

    public Message(long id, long userId, long productId, String message, String sender, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}