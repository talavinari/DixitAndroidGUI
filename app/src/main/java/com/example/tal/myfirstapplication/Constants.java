package com.example.tal.myfirstapplication;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by gront on 12/03/2016.
 */
public class Constants extends AppCompatActivity {
    private static String ipAdd;

    public Constants() {
        if (ipAdd==null){
            ipAdd="192.168.14.197"; //getLocalIpAddress();
        }
    }



    public String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public static final String MY_MESSAGE = "my message";
    public static final String ID_EXTRA = "extra";
    public static final String REST_API_PREFIX = "http://192.168.14.197:8080/DixitRESTfulAPI/rest/service/";
    public static final String ADD_ROOM_API_URL = REST_API_PREFIX + "addRoom";
    public static final String GET_ROOMS_API_URL = REST_API_PREFIX + "rooms";

    public static final String GET_ROOM_DETAILS_API_URL = REST_API_PREFIX + "players"; // /players/{roomName}
}
