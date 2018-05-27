package com.example.raj.streamerhotspoter;

/**
 * Created by Raj on 5/3/2015.
 */
public class ClientBuilder {



    private int _port = 12300;  // SNTP UDP port. Default value is 123.
    private String _host = "time.apple.com"; // NTP server address.
    private int _timeout = 0; // Connection timeout. Default value is 0.

    public ClientBuilder() {}

    public ClientBuilder host(String _host) {
        this._host = new String(_host);

        return this;
    }

    public ClientBuilder port(int _port) {
        this._port = _port;
        return this;
    }

    public ClientBuilder timeout(int _timeout) {
        this._timeout = _timeout;
        return this;
    }

    public Client build(Player c) {
        return new Client(_port, _host, _timeout,c);
    }
}
