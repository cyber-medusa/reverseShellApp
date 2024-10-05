package com.example.reverseshellapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendFromClient(String fromClient) throws IOException {
        out.println(fromClient);
    }

    public String receiveFromServer() throws IOException {
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
