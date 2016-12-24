package com.example.tobi.hugie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class MainActivity extends AppCompatActivity {

    private TextView serverStatus;

    // DEFAULT IP
    public static String SERVERIP = "10.0.2.15";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    private EditText serverIp;

    private Button connectPhones;

    private Button copyButton;
    private Button pasteButton;
    private Button sendButton;
    private ImageView hugImage;

    private String serverIpAddress = "";

    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverStatus = (TextView) findViewById(R.id.server_status);

        SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
        copyButton = (Button) findViewById(R.id.copy_button);
        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CopyIp();
            }
        });
        pasteButton = (Button) findViewById(R.id.paste_button);
        pasteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PasteIp();
            }
        });
        hugImage = (ImageView) findViewById(R.id.hug_image);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                hugImage.setVisibility(View.VISIBLE);
            }
        });
    }

    public void CopyIp(){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ServerIP", SERVERIP);

        clipboard.setPrimaryClip(clip);
    }

    public void PasteIp(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String pasteData = "";

        if (!(clipboard.hasPrimaryClip())) {

        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text

        } else {

            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
        }
        serverIp.setText(pasteData);
    }


    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                            }
                        });

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerActivity", line);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // DO WHATEVER YOU WANT TO THE FRONT END
                                        // THIS IS WHERE YOU CAN BE CREATIVE
                                    }
                                });
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            if (serverSocket!= null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener connectListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, SERVERPORT);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS
                        out.println("Hey Server!");
                        Log.d("ClientActivity", "C: Sent.");
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}
