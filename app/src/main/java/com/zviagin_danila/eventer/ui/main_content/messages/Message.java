package com.zviagin_danila.eventer.ui.main_content.messages;

public class Message {

    private String text;
    private String name;
    private String sender;
    private String recipient;
    private String imageUrl;
    private boolean isMineMessage;

    public Message() {
    }

    public Message(String text, String name, String sender, String recipient, String imageUrl, boolean isMineMessage) {
        this.text = text;
        this.name = name;
        this.sender = sender;
        this.recipient = recipient;
        this.imageUrl = imageUrl;
        this.isMineMessage = isMineMessage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isMineMessage() {
        return isMineMessage;
    }

    public void setMineMessage(boolean mineMessage) {
        isMineMessage = mineMessage;
    }
}