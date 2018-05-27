package com.example.raj.streamerhotspoter;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by rohan on 24/3/15.
 */
public class Globals {
    private static Globals instance;
    private static MediaPlayer mediaPlayer;
  //  private static server myServer;
    private static boolean isOwner;
    private static boolean isConnected;
    private static int port = 14323;
    private static int goahead=0;
    public static InetAddress getIp() {
        return ip;
    }

    public static void setIp(InetAddress ip) {
        Globals.ip = ip;
    }

    private static InetAddress ip;

    public static int getGoahead() {
        return goahead;
    }

    public static void setGoahead(int goahead) {
        Globals.goahead = goahead;
    }


    private static ArrayList<InetAddress> clients;
    private static Socket socket;

    public static String getPath() {
        return "";
    }
    public static Socket getClientSocket() {
        if( socket == null ) {
            try {
                socket = new Socket();
                socket.setReuseAddress(true);
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    public static ServerSocket getServerSocket() {
        if( serverSocket == null ) {
            try {
                serverSocket = new ServerSocket(getPort());
                serverSocket.setReuseAddress(true);
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
        return serverSocket;
    }

    private static ServerSocket serverSocket;
    public static ArrayList<InetAddress> getClients() {
        if( clients == null ) {
            clients = new ArrayList<InetAddress>();
        }
        return clients;
    }

    public static void addClient( InetAddress a ) {
        if( clients == null ) {
            clients = new ArrayList<InetAddress>();
        }
        clients.add( a );
    }

    public static int getPort() {
        return port;
    }





    public static boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Globals.isConnected = isConnected;
    }

    public static boolean isOwner() {
        return isOwner;
    }
    public static void setIsOwner(boolean isOwner) {
        Globals.isOwner = isOwner;
    }



    public void showMessage( Context c, String msg ) {
        Toast.makeText( c, msg, Toast.LENGTH_SHORT ).show();
    }


    public void showMessage(final Player mainObj,final  Context c, final String msg ) {
        mainObj.runOnUiThread(new Runnable() {
            public void run() {
                //  Toast.makeText(mainObj,msg, Toast.LENGTH_SHORT).show();
                Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void showMessage(final Home mainObj,final  Context c, final String msg ) {
        mainObj.runOnUiThread(new Runnable() {
            public void run() {
                //  Toast.makeText(mainObj,msg, Toast.LENGTH_SHORT).show();
                Toast.makeText( c, msg, Toast.LENGTH_SHORT ).show();
            }
        });

    }


    private Globals() {}

    public MediaPlayer getPlayer() {
        if( mediaPlayer == null ) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    /*public server getServer() {
        if( myServer == null ) {
            myServer = new server();
        }
        return myServer;
    }*/



    public static synchronized Globals getInstance() {
        if(instance == null){
            instance = new Globals();
            mediaPlayer = new MediaPlayer();
        }
        return instance;
    }
}
