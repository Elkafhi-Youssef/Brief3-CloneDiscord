package org.example.server;

import java.io.*;
import java.net.*;
import java.util.*;


public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter pw;

    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public  void run() {
        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = socket.getOutputStream();
            pw = new PrintWriter(os, true);

            printUsers();
            String userName = br.readLine();
            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = br.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            pw.println("Connected users: " + server.getUserNames());
        } else {
            pw.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {

        pw.println(message);
    }
}
