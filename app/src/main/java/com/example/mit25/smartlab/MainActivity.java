package com.example.mit25.smartlab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity{

    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private ConnectionsClient client;
    private String opponentName="",opponentId="",opponentName1="",opponentId1="";
    private final String codeName = Codename.generate();
    TextView cname,connected,connected2;
    Button con,dis;

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String msg=new String(payload.asBytes(),UTF_8);
                    Toast.makeText(getApplicationContext(), "Msg received: "+msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {

                }
            };

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Toast.makeText(getApplicationContext(), "Accepting Connection", Toast.LENGTH_SHORT).show();
                    client.acceptConnection(endpointId, payloadCallback);
                    if(opponentName.equals(""))
                        opponentName = connectionInfo.getEndpointName();
                    else
                        opponentName1 = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                        boolean flag=true;
                        if(opponentId.equals(""))
                            opponentId = endpointId;
                        else {
                            opponentId1 = endpointId;
                            flag=false;
                        }
                        if(connected.getText().toString().equals("Not Connected")) {
                            if(flag)
                                connected.setText(opponentName);
                            else
                                connected.setText(opponentName1);
                        }
                        else{
                            if(flag)
                                connected2.setText(opponentName);
                            else
                                connected2.setText(opponentName1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    if(endpointId.equals(opponentId))
                        Toast.makeText(getApplicationContext(), "Disconnected "+opponentName, Toast.LENGTH_SHORT).show();
                    if(endpointId.equals(opponentId1))
                        Toast.makeText(getApplicationContext(), "Disconnected "+opponentName, Toast.LENGTH_SHORT).show();
                }
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();;
                    client.requestConnection(codeName, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cname=findViewById(R.id.cname);
        connected=findViewById(R.id.connected);
        connected2=findViewById(R.id.connected2);
        con=findViewById(R.id.btn_con);
        dis=findViewById(R.id.btn_dis);
        dis.setEnabled(false);
        cname.setText(codeName);
        client = Nearby.getConnectionsClient(this);
    }

    void onConnect(View v){
        startAdvertising();
        startDiscovery();
        v.setEnabled(false);
        dis.setEnabled(true);
    }

    void onDisconnect(View v){
        v.setEnabled(false);
        con.setEnabled(true);
        if(!opponentId.equals(""))
            client.disconnectFromEndpoint(opponentId);
        if(!opponentId1.equals(""))
            client.disconnectFromEndpoint(opponentId1);
    }

    void devlist(View v){
        Intent i= new Intent(getApplicationContext(), Devicelist.class);
        startActivity(i);
    }

    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        client.startAdvertising(codeName,getResources().getString(R.string.service_id), connectionLifecycleCallback, new AdvertisingOptions(STRATEGY));
    }

    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        client.startDiscovery(getResources().getString(R.string.service_id), endpointDiscoveryCallback, new DiscoveryOptions(STRATEGY));
    }

    public void sendMsg(View v){
        String msg="Message from "+codeName;
        if(!opponentId.equals("")){
            client.sendPayload(opponentId,Payload.fromBytes(msg.getBytes(UTF_8)));
            Toast.makeText(this, "Msg sent to "+opponentName, Toast.LENGTH_LONG).show();
        }
        if(!opponentId1.equals("")){
            client.sendPayload(opponentId1,Payload.fromBytes(msg.getBytes(UTF_8)));
            Toast.makeText(this, "Msg sent to "+opponentName1, Toast.LENGTH_LONG).show();
        }

    }

    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    protected void onStop() {
        client.stopAllEndpoints();
        super.onStop();
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }
}
