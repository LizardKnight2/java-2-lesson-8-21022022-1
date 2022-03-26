package com.geekbrains.client;

import java.util.LinkedList;

public class FileWrite {

        private LinkedList<String> messages;

    public FileWrite(LinkedList<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public LinkedList<String> getMessages() {
        return messages;
    }

    public String getMessage(int index) {
        return messages.get(index);
    }
}