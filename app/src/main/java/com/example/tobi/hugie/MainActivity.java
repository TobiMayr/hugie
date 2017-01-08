package com.example.tobi.hugie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView status;
    // DEFAULT IP
    public static String SERVERIP = "10.0.2.15";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    private EditText serverIp;

    private Button pasteButton;
    private ImageView hugImage;
    AnimationDrawable hugAnimation;
    private TextView deviceServerIp;

    private String serverIpAddress = "";

    private boolean connected = false;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location myLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private FrameLayout contentFrame;
    private boolean isOthersOpen = false;
    private OtherFragment oF = new OtherFragment();
    float distanceInMeters;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.mipmap.logo_title);

        SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();
        deviceServerIp = (TextView) findViewById(R.id.device_server_ip);
        serverIp = (EditText) findViewById(R.id.server_ip);
        Button connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
        Button copyButton = (Button) findViewById(R.id.copy_button);
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
        hugImage.setBackgroundResource(R.drawable.hug_animation);
        hugAnimation = (AnimationDrawable) hugImage.getBackground();
        status = (TextView) findViewById(R.id.status);

        mLatitudeText = (TextView) findViewById(R.id.latitude_text);
        mLongitudeText = (TextView) findViewById(R.id.longitude_text);

        contentFrame = (FrameLayout) findViewById(R.id.other_frame);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        myLocation = getMostAccurateLocation(getApplicationContext());
        if (myLocation != null) {
            mLatitudeText.setText(String.valueOf(myLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(myLocation.getLongitude()));
        }
        distanceText = (TextView) findViewById(R.id.tv_distance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_other && !isOthersOpen) {
            fragmentManager.beginTransaction()
                    .replace(R.id.other_frame, oF)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(oF)
                    .commit();
            isOthersOpen = true;
            return true;
        } else if (id == R.id.action_other && isOthersOpen) {
            fragmentManager.beginTransaction()
                    .replace(R.id.other_frame, oF)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(oF)
                    .commit();
            isOthersOpen = false;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CopyIp() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ServerIP", SERVERIP);
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.copied_ip, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void PasteIp() {
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

    @Override
    public void onConnected(Bundle connectionHint) {
        myLocation = getMostAccurateLocation(getApplicationContext());
        if (myLocation != null) {
            mLatitudeText.setText(String.valueOf(myLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(myLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public Location getMostAccurateLocation(Context mCtx) {
        Location finalLoc = null;
        try {
            boolean gps_enabled = false;
            boolean network_enabled = false;

            LocationManager lm = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location net_loc = null, gps_loc = null;

            if (gps_enabled)
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gps_loc != null && net_loc != null) {

                //smaller the number more accurate result will
                if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                    finalLoc = net_loc;
                else
                    finalLoc = gps_loc;

                // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    finalLoc = gps_loc;
                } else if (net_loc != null) {
                    finalLoc = net_loc;
                }
            }
            return finalLoc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalLoc;
    }

    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
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
                                final String finalLine = line;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // DO WHATEVER YOU WANT TO THE FRONT END
                                        // THIS IS WHERE YOU CAN BE CREATIVE
                                        hugImage.setVisibility(View.VISIBLE);
                                        hugAnimation.start();

                                        String locationString = finalLine;
                                        String[] locArray = locationString.split(" ");
                                        if(locArray.length >= 2 ){
                                            Location targetLocation = new Location("");
                                            targetLocation.setLatitude(Double.parseDouble(locArray[0]));
                                            targetLocation.setLongitude(Double.parseDouble(locArray[1]));
                                            distanceInMeters =  targetLocation.distanceTo(myLocation);
                                            distanceText.setText(String.valueOf(distanceInMeters));
                                        }
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
        mGoogleApiClient.disconnect();
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

    //Client side
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
                        String locationString = locationStringFromLocation(myLocation);
                        out.println(locationString);
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
