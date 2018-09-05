package in.ac.bkbiet.bkbiet.models;

import com.google.firebase.database.Exclude;

/**
 * Noty Created by Ashish on 9/6/2017.
 */

public class Noty {
    private int id;
    private String nId;
    private String title;
    private String body;
    private String senderId;
    private String sender;
    private String color;
    private String sentAt;
    private String receivedAt;
    private boolean isRead;
    private String readAt;
    private String sendTo;
    private String receiver;

    public Noty() {
        //For Firebase
    }

    public Noty(String nId, String title, String body, String senderId, String sender, String color, String receivedAt) {
        this.nId = nId;
        this.title = title;
        this.body = body;
        this.senderId = senderId;
        this.sender = sender;
        this.color = color;
        this.receivedAt = receivedAt;
    }

    public Noty(String nId, String title, String body, String color, String senderId, String sender, String receivedAt, String sentAt, boolean isRead) {
        this(nId, title, body, senderId, sender, color, receivedAt);
        this.sentAt = sentAt;
        this.isRead = isRead;
        this.readAt = "";
    }

    @Exclude
    public String getReadAt() {
        return readAt;
    }

    @Exclude
    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    @Exclude
    public boolean isRead() {
        return isRead;
    }

    @Exclude
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Exclude
    public int getId() {
        return id;
    }

    @Exclude
    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public String getReceivedAt() {
        return receivedAt;
    }

    @Exclude
    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
}