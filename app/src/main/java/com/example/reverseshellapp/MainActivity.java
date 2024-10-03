package com.example.reverseshellapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ServerConnect serverConnect;

    public class ServerConnect {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) throws IOException {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
            clientSocket.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        serverConnect = new ServerConnect();
        Button connectToHostBtn = findViewById(R.id.connect_btn);
        connectToHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverConnect.startConnection("192.168.31.85", 5656);
                            Log.i("Connection", "Connection started");
                            serverConnect.sendFromClient("-------SPAWNED SHELL-------");
                            while (true) {
                                String S2C = serverConnect.receiveFromServer();
                                Log.i("Received from the server", S2C);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        Button closeConnectionBtn = findViewById(R.id.close_connection_btn);
        closeConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    serverConnect.stopConnection();
                    Log.i("Connection", "Connection closed");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
