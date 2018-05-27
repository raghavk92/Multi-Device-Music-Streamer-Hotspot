package com.example.raj.streamerhotspoter;

/**
 * Created by Raj on 5/3/2015.
 */
public class ServerBuilder {


    /**
     * Sample usage. Run with root access.
     */


    private int _port = 123;  // SNTP UDP port. Default value is 123.

    public ServerBuilder() {
    }

    public ServerBuilder port(int _port) {
        this._port = _port;
        return this;
    }

    public Server build(Player c) {
        return new Server(_port,c);
    }
}