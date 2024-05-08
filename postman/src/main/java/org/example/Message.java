package org.example;

public class Message {
    private String login;
    private String message;
    private int clientsCount;
    private MessageType type;

    public Message(String login, String message, MessageType type) {
        this.login = login;
        this.message = message;
        this.type = type;
    }

    public Message(String login, String message, int clientsCount, MessageType type) {
        this.login = login;
        this.message = message;
        this.clientsCount = clientsCount;
        this.type = type;
    }

    public void setClientsCount(int clientsCount) {
        this.clientsCount = clientsCount;
    }

    @Override
    public String toString() {
        return "Message{" +
                "login='" + login + '\'' +
                ", message='" + message + '\'' +
                ", clientsCount='" + clientsCount + '\'' +
                ", type=" + type +
                '}';
    }
}
