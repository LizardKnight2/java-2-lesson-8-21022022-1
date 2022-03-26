package com.geekbrains.client;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.ServerCommandConstants;

import java.io.*;
import java.net.Socket;

public class Network {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String nickname;

    private final ChatController controller;

    public Network(ChatController chatController) {
        this.controller = chatController;
    }

    private void startReadServerMessages() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String messageFromServer = inputStream.readUTF();
                        System.out.println(messageFromServer);
                        if (messageFromServer.contains("пользователь " + nickname + "зашел в чат")) {
                            nextLine();
                            String[] client = messageFromServer.split(" ");
                            controller.displayClient(client[0]);
                        } else if (messageFromServer.startsWith(ServerCommandConstants.EXIT)) {
                            String[] client = messageFromServer.split(" ");
                            controller.removeClient(client[1]);
                            controller.displayMessage(client[1] + " покинул чат", true);
                        } else if (messageFromServer.startsWith(ServerCommandConstants.CLIENTS)) {
                            String[] client = messageFromServer.split(" ");
                            for (int i = 1; i < client.length; i++) {
                                controller.displayClient(client[i]);
                            }
                        } else {
                            controller.displayMessage(messageFromServer, true);
                            //TODO: запись в конец файла
                            /*BufferedReader bufferedReader = new BufferedReader(new FileReader("history.txt"));
                            *//*try(InputStream inputStream = new BufferedInputStream(new FileInputStream("history.txt"))){
                                int x;
                                while ((x = inputStream.read()) != -1){
                                    System.out.println((char) x);
                                }
                            }*/
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    private void nextLine() {
    }

    private void initializeNetwork() throws IOException {
        socket = new Socket(CommonConstants.SERVER_ADDRESS, CommonConstants.SERVER_PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }


    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean sendAuth(String login, String password) {
        try {
            if (socket == null || socket.isClosed()) {
                initializeNetwork();
            }
            outputStream.writeUTF(ServerCommandConstants.AUTHENTICATION + " " + login + " " + password);

            boolean authenticated = inputStream.readBoolean();
            if (authenticated) {
                //TODO: считать из файла последние сто строк
                startReadServerMessages();
            }
            return authenticated;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeConnection() {
        try {
            outputStream.writeUTF(ServerCommandConstants.EXIT);
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.exit(1);
    }


}