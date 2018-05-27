package com.example.raj.streamerhotspoter;
import java.net.InetAddress;
/**
 * Created by Raj on 5/8/2015.
 */
public class ClientScanResult1 {

    private InetAddress IpAddr;


    public ClientScanResult1(InetAddress ipAddr) {
        super();
        IpAddr = ipAddr;

    }

    public InetAddress getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(InetAddress ipAddr) {
        IpAddr = ipAddr;
    }


}
