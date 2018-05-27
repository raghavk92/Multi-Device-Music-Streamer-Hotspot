package com.example.raj.streamerhotspoter;

/**
 * Created by Raj on 5/8/2015.
 */

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiApManager {
    private final WifiManager mWifiManager;

    public WifiApManager(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }



    /**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     * @param onlyReachables {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @return ArrayList of {@link ClientScanResult}
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables) {
        return getClientList(onlyReachables, 300);
    }

    /**
     * Gets a list of the clients connected to the Hotspot
     * @param onlyReachables {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @return ArrayList of {@link ClientScanResult}
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables, int reachableTimeout) {
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<ClientScanResult>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");

                if ((splitted != null) && (splitted.length >= 4)) {
                    // Basic sanity check
                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:..")) {
                        boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                        if (!onlyReachables || isReachable) {
                            result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().toString(), e.getMessage());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), e.getMessage());
            }
        }

        return result;
    }
}