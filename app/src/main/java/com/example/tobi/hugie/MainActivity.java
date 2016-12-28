package com.example.tobi.hugie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView listeningOnText;
    private TextView status;
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
    private ImageView hugImage;
    private TextView deviceServerIp;

    private String serverIpAddress = "";

    private boolean connected = false;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listeningOnText = (TextView) findViewById(R.id.listening_on);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();
        deviceServerIp = (TextView) findViewById(R.id.device_server_ip);
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
        status = (TextView) findViewById(R.id.status);
        /*sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                hugImage.setVisibility(View.VISIBLE);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }S

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_other) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new OtherFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CopyIp(){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ServerIP", SERVERIP);
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.copied_ip, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void PasteIp(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String pasteData = "";

        if (!(clipboard.hasPrimaryClip())) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.nothing_clipboard, Toast.LENGTH_SHORT);
            toast.show();

        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_text_clipboard, Toast.LENGTH_SHORT);
            toast.show();

        } else {

            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.pasted_ip, Toast.LENGTH_SHORT);
            toast.show();
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

                            deviceServerIp.setText(SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                status.setText(R.string.connected);

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
                                        hugImage.setVisibility(View.VISIBLE);
                                                                                }
                                });
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText(R.string.conn_interrupted);
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText(R.string.no_internet_conn);
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(R.string.error_msg);
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

    //Cliet side
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
