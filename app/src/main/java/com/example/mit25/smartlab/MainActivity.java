package com.example.mit25.smartlab;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity{

    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private ConnectionsClient client;
    private final String codeName = Codename.generate();
    TextView cname;
    Button con,dis;
    HashMap<String,String> map=new HashMap<>();
    ArrayList<Device> list=new ArrayList<>();

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
                    String opponentName="";
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getID().equals(endpointId)){
                            opponentName=list.get(i).getName();
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Msg received: "+msg+"\nfrom: "+opponentName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {

                }
            };

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    String opponentName=connectionInfo.getEndpointName();
                    Toast.makeText(getApplicationContext(), "Accepting Connection "+opponentName, Toast.LENGTH_SHORT).show();
                    client.acceptConnection(endpointId, payloadCallback);
                    map.put(endpointId,opponentName);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    String opponentName="";
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getID().equals(endpointId)){
                            opponentName=list.get(i).getName();
                            break;
                        }
                    }
                    if (result.getStatus().isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Connection Successful "+opponentName, Toast.LENGTH_SHORT).show();
                        Device d=new Device(endpointId,map.get(endpointId));
                        map.remove(endpointId);
                        list.add(d);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Connection Failed "+opponentName, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    String opponentName="";
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getID().equals(endpointId)){
                            opponentName=list.get(i).getName();
                            list.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Disconnected "+opponentName, Toast.LENGTH_SHORT).show();
                }
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    String opponentName=info.getEndpointName();
                    Toast.makeText(getApplicationContext(), "Connecting "+opponentName, Toast.LENGTH_SHORT).show();;
                    client.requestConnection(codeName, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    String opponentName="";
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getID().equals(endpointId)){
                            opponentName=list.get(i).getName();
                            list.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Endpoint Lost "+opponentName, Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cname=findViewById(R.id.cname);
        con=findViewById(R.id.btn_con);
        dis=findViewById(R.id.btn_dis);
        dis.setEnabled(false);
        cname.setText(codeName);
        client = Nearby.getConnectionsClient(this);
        String flag=getIntent().getStringExtra("Flag");
        if(flag!=null && flag.equals("true")){
            final String opponentId=getIntent().getStringExtra("ID");
            LayoutInflater li=LayoutInflater.from(this);
            View pv=li.inflate(R.layout.prompts,null);

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setView(pv);

            final EditText et=pv.findViewById(R.id.msg);

            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Send",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           String msg1=et.getText().toString();
                            client.sendPayload(opponentId,Payload.fromBytes(msg1.getBytes(UTF_8)));
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void onConnect(View v){
        startAdvertising();
        startDiscovery();
        v.setEnabled(false);
        dis.setEnabled(true);
    }

    public void onDisconnect(View v){
        v.setEnabled(false);
        con.setEnabled(true);
        client.stopAdvertising();
        client.stopDiscovery();
        list.clear();
        client.stopAllEndpoints();
    }

    public void devlist(View v){
        Intent i= new Intent(getApplicationContext(), Devicelist.class);
        i.putExtra("List", list);
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

    /*public void sendMsg(View v){
        String msg="Message from "+codeName;
        if(!opponentId.equals("")){
            client.sendPayload(opponentId,Payload.fromBytes(msg.getBytes(UTF_8)));
            Toast.makeText(this, "Msg sent to "+opponentName, Toast.LENGTH_LONG).show();
        }
        if(!opponentId1.equals("")){
            client.sendPayload(opponentId1,Payload.fromBytes(msg.getBytes(UTF_8)));
            Toast.makeText(this, "Msg sent to "+opponentName1, Toast.LENGTH_LONG).show();
        }

    }*/

    protected void onStart() {
        super.onStart();

        /*if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }*/
    }

    @Override
    protected void onStop() {
        //client.stopAllEndpoints();
        super.onStop();
    }

    //Returns true if the app was granted all the permissions. Otherwise, returns false.
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Handles user acceptance (or denial) of our permission request.
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
