package com.example.raj.streamerhotspoter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;




public class Player extends AppCompatActivity {
    Globals globals = Globals.getInstance();
    MediaPlayer mediaPlayer = globals.getInstance().getPlayer();
    NsdHelper mNsdHelper;
    private TextView mStatusView;
    private Handler mUpdateHandler;
    public static final String TAG = "NsdChat";
    private Socket mSocket;
    private int mPort = -1;
    NsdServiceInfo service ;
    SeekBar seeker;
    Music selection;


    private synchronized void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");
        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    // TODO(alexlucas): Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }
    private Socket getSocket() {
        return mSocket;
    }
    /*public int getLocalPort() {
        return mPort;
    }
    public void setLocalPort(int port) {
        mPort = port;
    }*/

    private class GroupServer extends AsyncTask<Integer, Integer, Integer> {
        int type;
        final byte MUSIC = 0;
        final byte AHEAD = 1;
        final byte JBACK = 2;
        final byte STATE = 3;
        final byte CURRENTPOS = 4;

        final byte SYNC = 5;
        final byte LAG = 6;


        int portno = globals.getPort();
        int bytes;

        private void sendFile( ClientScanResult1 client) throws Exception {
            byte[] buf = new byte[64*1024];
            File file = new File(selection.path);

            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);

            OutputStream out = socket.getOutputStream();
            FileInputStream fin = new FileInputStream( file );
            BufferedInputStream bfin = new BufferedInputStream( fin );
            out.write(MUSIC);
            while( (bytes = bfin.read( buf, 0, buf.length )) != -1 ) {
                out.write( buf, 0, bytes );
            }
            bfin.close();
            fin.close();
            out.close();
            socket.close();
        }

        private void jumpAhead( ClientScanResult1 client ) throws Exception {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();
            out.write(AHEAD);
            out.close();
            socket.close();
        }

        private void jumpBack( ClientScanResult1 client ) throws Exception {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();
            out.write(JBACK);
            out.close();
            socket.close();
        }

        private void changeState( ClientScanResult1 client ) throws Exception {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();


            out.write(STATE);
            out.close();
            socket.close();

        }
        private void setCurrentPos(ClientScanResult1 client) throws Exception
        {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();

            out.write(CURRENTPOS);
            DataOutputStream output = new DataOutputStream(out);
            output.writeInt(mediaPlayer.getCurrentPosition());
            output.writeLong(System.currentTimeMillis());
            out.close();
            output.close();

            socket.close();
        }
        private void syncTime(ClientScanResult1 client ) throws Exception {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();
            out.write(SYNC);

            DataOutputStream output = new DataOutputStream(out);
            output.writeInt(mediaPlayer.getCurrentPosition());
            output.writeLong(System.currentTimeMillis());
            out.close();
            output.close();
            socket.close();
        }

        private void lagCalculate( ClientScanResult1 client ) throws Exception {
            Socket socket = new Socket();
            socket.connect((new InetSocketAddress(client.getIpAddr(), portno)), 0);
            OutputStream out = socket.getOutputStream();
            out.write(LAG);


            DataOutputStream output = new DataOutputStream(out);
            do if (mediaPlayer.isPlaying())
                break;
                while(true);
            do if(mediaPlayer.getCurrentPosition()>=25000)
                break;
                while(true);

            output.writeLong(System.currentTimeMillis());
            out.close();
            output.close();
            socket.close();
        }


        protected void onPreExecute() {

        }


        protected Integer doInBackground(Integer... params) {
            WifiApManager wifiApManager = new WifiApManager(Player.this);
            ClientScanResult1 clientScanResult1=new ClientScanResult1(globals.getIp());

           // ArrayList<ClientScanResult> clients =  wifiApManager.getClientList(false);
            type = params[0];
            publishProgress(type);
            try {
                //TODO support multiple
                //for (ClientScanResult clientScanResult : clients) {
                    switch (type) {
                        case 0: sendFile   ( clientScanResult1 );break;
                        case 1: jumpAhead  ( clientScanResult1 );break;
                        case 2: jumpBack   ( clientScanResult1 );break;
                        case 3: changeState( clientScanResult1 );break;
                        case 5: break;
                        case 6: break;
                    }
               // }
                return 0;
            } catch( Exception e ) {
                e.printStackTrace();
                return -1;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            globals.showMessage(Player.this, "Sent " + Integer.toString(progress[0]));
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            switch (type) {
                case 0: play(selection, null);
                  new Thread(new Runnable() {
                      @Override
                      public void run() {
                          WifiApManager wifiApManager = new WifiApManager(Player.this);
                          ClientScanResult1 clientScanResult1=new ClientScanResult1(globals.getIp());

                        //  ArrayList<ClientScanResult> clients =  wifiApManager.getClientList(false);
                          do if (mediaPlayer.isPlaying()) {
                              break;
                          } while (true);

                          try {
                              //TODO support multiple
                           //   for (ClientScanResult clientScanResult : clients) {
                                  setCurrentPos(clientScanResult1);


                             // }

                          } catch( Exception e ) {
                              e.printStackTrace();

                          }

                      }
                  }).start();


                    break;
                case 1: mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                /*    while(!mediaPlayer.isPlaying());
                    try {
                        //TODO support multiple
                        for( int i=0;i<clients.size();i++ ) {
                            setCurrentPos(clients.get(i));


                        }

                    } catch( Exception e ) {
                        e.printStackTrace();

                    }
                    */break;

                case 2: mediaPlayer.seekTo(0);
                   /* while(!mediaPlayer.isPlaying());
                    try {
                        //TODO support multiple
                        for( int i=0;i<clients.size();i++ ) {
                            setCurrentPos(clients.get(i));


                        }

                    } catch( Exception e ) {
                        e.printStackTrace();

                    }
                    */break;
                case 3: if( mediaPlayer.isPlaying() ) { mediaPlayer.pause(); } else { mediaPlayer.start(); } break;
                case 5:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WifiApManager wifiApManager = new WifiApManager(Player.this);

                            ArrayList<ClientScanResult> clients =  wifiApManager.getClientList(false);
                            do if (mediaPlayer.isPlaying()) {
                                break;
                            } while (true);

                            try {
                                //TODO support multiple
                                ClientScanResult1 clientScanResult1=new ClientScanResult1(globals.getIp());

                                //  for (ClientScanResult clientScanResult : clients) {
                                    syncTime(clientScanResult1);
                               // }

                            } catch( Exception e ) {
                                e.printStackTrace();

                            }

                        }
                    }).start();break;
                case 6:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WifiApManager wifiApManager = new WifiApManager(Player.this);

                            ArrayList<ClientScanResult> clients =  wifiApManager.getClientList(false);
                            do if (mediaPlayer.isPlaying()) {
                                break;
                            } while (true);

                            try {
                                //TODO support multiple
                                ClientScanResult1 clientScanResult1=new ClientScanResult1(globals.getIp());

                                //     for (ClientScanResult clientScanResult : clients) {
                                    lagCalculate(clientScanResult1);
                           //     }

                            } catch( Exception e ) {
                                e.printStackTrace();

                            }

                        }
                    }).start();break;
            }

        }
    }

    private class ClientServer extends AsyncTask<URL, Integer, Integer> {


        String Serveraddress;
        String path = Environment.getExternalStorageDirectory() + File.separator + "temp.mp3";
        int bytes;
        long posTime;


        long clientTime=-1;
        long serverTime=-1;
        int currentPos=-1;
        final int MUSIC = 0;
        final int AHEAD = 1;
        final int JBACK = 2;
        final int STATE = 3;
        final int CURRENTPOS = 4;
        final int SYNC = 5;
        final int LAG = 6;
        private void getFile( InputStream in ) throws  Exception{
            FileOutputStream tempFile;
            File file = new File(path);
            tempFile = new FileOutputStream( file );
            BufferedOutputStream btempFile = new BufferedOutputStream( tempFile );
            byte[] buf = new byte[64*1024];
            while( (bytes = in.read( buf, 0, buf.length )) != -1 ) {
                btempFile.write( buf, 0, bytes );
            }
            btempFile.close();
            tempFile.close();
        }
        public String intToIp(int i) {//never used

            return (( i & 0xFF ) + "." +
                    ((i >> 8 ) & 0xFF) + "." +
                    ((i >> 16 ) & 0xFF) + "." +
                    (i >> 24 ));
        }
        private void getCurrentPos( InputStream in ) throws  Exception{

            DataInputStream input= new DataInputStream(in);
            currentPos= input.readInt();
            posTime=input.readLong();
            globals.showMessage(Player.this,Player.this,new Long(posTime).toString());
            input.close();

        }
        private void lagCalculate( InputStream in ) throws  Exception{

            DataInputStream input= new DataInputStream(in);

            serverTime=input.readLong();
            globals.showMessage(Player.this,Player.this,new Long(posTime).toString());
            input.close();


        }

        protected void onPreExecute() {

        }

        protected Integer doInBackground(URL... urls) {
            try {
                ServerSocket socket = globals.getServerSocket();

                while( true ) {
                    Socket groupOwner = socket.accept();
                    InputStream in = groupOwner.getInputStream();
                    Serveraddress=groupOwner.getInetAddress().getHostAddress();
                    int message = in.read();
                    publishProgress( message );
                    switch (message) {
                        case MUSIC: getFile(in);publishProgress(10);break;
                        case AHEAD: mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);break;
                        case JBACK: mediaPlayer.seekTo(0);break;
                        case STATE: if( mediaPlayer.isPlaying() ) { mediaPlayer.pause(); } else { mediaPlayer.start(); } break;
                        case CURRENTPOS : getCurrentPos(in);publishProgress(50); break;
                        case SYNC : getCurrentPos(in);publishProgress(50); break;
                        case LAG :  lagCalculate(in);publishProgress(60);break;

                    }
                    in.close();
                    groupOwner.close();
                }
            } catch( Exception e ) {
                publishProgress(-11111);
                e.printStackTrace();
                return -1;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            globals.showMessage(Player.this, "Received " + Integer.toString(progress[0]));
            if (progress[0] == 10) {
                globals.showMessage(Player.this, "Receivedit " + Integer.toString(progress[0]));

                MediaMetadataRetriever m = new MediaMetadataRetriever();
                m.setDataSource(Player.this, Uri.parse(path));
                String title = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                //  final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
                //  final DhcpInfo dhcp = manager.getDhcpInfo();

                //  final String address = intToIp(dhcp.gateway);
                try {

                    play(new Music(title, path, null), m.getEmbeddedPicture());
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                do if(mediaPlayer.isPlaying())
                                    break;
                                while(true);

                                do if(mediaPlayer.getCurrentPosition()>=25000)
                                    break;
                                    while(true);

                                clientTime=System.currentTimeMillis();

                            } catch (
                                    Exception e
                                    ) {
                                e.printStackTrace();
                            }
                        }

                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


                    if(progress[0]==50)
                    {
                    new Thread(new Runnable() {
                        public void run() {
                            try {

                                while (currentPos == -1) ;

                                final long offset = new ClientBuilder().host(Serveraddress).build(Player.this).run();
                                final long currentServerTime = (System.currentTimeMillis() + offset);
                                final long seekTime = currentServerTime - posTime;
                                mediaPlayer.seekTo(currentPos + (int) seekTime);
                                globals.showMessage(Player.this,Player.this,Integer.toString(mediaPlayer.getCurrentPosition()));
                                globals.showMessage(Player.this,Player.this, new Long(currentPos).toString());
                                globals.showMessage(Player.this,Player.this, new Long(currentPos + (int) seekTime).toString());
                            } catch (
                                    Exception e
                                    ) {
                                e.printStackTrace();
                            }
                        }

                    }).start();

                }
            if(progress[0]==60)
            {
                new Thread(new Runnable() {
                    public void run() {
                        try {

                            while (clientTime == -1) ;
                            while (serverTime== -1);

                            final long offset = new ClientBuilder().host(Serveraddress).build(Player.this).run();
                            globals.showMessage(Player.this,Player.this,Long.toString(clientTime));

                            globals.showMessage(Player.this,Player.this,Long.toString(serverTime));
                            final long seekTime = clientTime + offset - serverTime;
                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + (int) seekTime);
                            globals.showMessage(Player.this,Player.this,Integer.toString(mediaPlayer.getCurrentPosition()));
                            globals.showMessage(Player.this,Player.this, new Long(seekTime).toString());
                            globals.showMessage(Player.this,Player.this, "synced");
                        } catch (
                                Exception e
                                ) {
                            e.printStackTrace();
                        }
                    }

                }).start();

            }
            }

        protected void onPostExecute(Integer result) {
            globals.showMessage(Player.this, "Server Crashed");

        }
    }


    public void play(Music selection, byte[] arr) {

        mediaPlayer.reset();
        Bitmap albumArt;
        if( arr == null ) {
            albumArt = selection.getArtwork( Player.this );
        } else {
            albumArt = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        }
        ((ImageView)findViewById(R.id.album)).setImageBitmap( albumArt);
        ((TextView)findViewById(R.id.title)).setText( selection.title );

        Uri myUri = Uri.parse(selection.path);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(Player.this, myUri);
           mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
         //   mediaPlayer.prepareAsync();
            //public void onPrepared(){}
            seeker.setMax( mediaPlayer.getDuration() );
            seeker.setProgress(0);
       //     mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Button lag ;
     Button controlButton;
     Button sync;
     Button seekForwardButton;
    Button seekBackwardButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        seeker = (SeekBar) findViewById(R.id.seeker);
        lag = (Button) findViewById(R.id.lag);
         controlButton = (Button) findViewById(R.id.control);
         sync = (Button) findViewById(R.id.sync);
         Button seekForwardButton = (Button) findViewById(R.id.seekForward);
         Button seekBackwardButton = (Button) findViewById(R.id.seekBackward);
      final  AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        selection = (Music) getIntent().getSerializableExtra("select");
        final TextView t = (TextView) findViewById(R.id.title);

       /* new Thread(new Runnable() {

            public void run() {
                try {
                    dothis();                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
*/


        //if(mWifi.isConnected()) {
            controlButton.setText("Mute");
            controlButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (controlButton.getText() == "Mute" ) {
                        mediaPlayer.setVolume(0, 0);
                        controlButton.setText("Unmute");
                    } else {

                        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setVolume( currentVolume, currentVolume );
                        controlButton.setText("Mute");
                    }
                }
            });
            new ClientServer().execute();
       // }
       // else  {
         /*   controlButton.setText("Pause");
            controlButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        controlButton.setText("Play");
                    } else {
                        mediaPlayer.start();
                        controlButton.setText("Pause");
                    }
                }
            });

            //Connect seekForward Button
            seekForwardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            });

            //Connect seekBackward Button
            seekBackwardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick( View v ) {
                    mediaPlayer.seekTo(0);
                }
            });

            seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                    if(fromUser) {
                        mediaPlayer.seekTo( progress );
                    }
                }
                public void onStartTrackingTouch(SeekBar seekBar) {}

                public void onStopTrackingTouch(SeekBar seekBar) {}
            });*/


      //  }
        //updating seekbar every second
     /*   new Runnable() {//this needs to be corrected run on ui thread
            public void run() {
                seeker.setProgress(mediaPlayer.getCurrentPosition());
                seeker.postDelayed(this, 33);
                t.setText( Integer.toString(mediaPlayer.getCurrentPosition()/1000));
            }
        }.run();
*/    }

    public void dothis()
    {
        while(globals.getGoahead()==0);
        if(globals.getGoahead()>0) {
            //Connect play/pause button

            new Thread(new Runnable() {

                public void run() {
                    try {
                        new ServerBuilder().port(12300).build(Player.this).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
            controlButton.setText("Pause");
            controlButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new GroupServer().execute(3);
                }
            });

            //Connect seekForward Button
            seekForwardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new GroupServer().execute(1);
                }
            });
            sync.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new GroupServer().execute(5);
                }
            });
            lag.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new GroupServer().execute(6);
                }
            });

            //Connect seekBackward Button
            seekBackwardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new GroupServer().execute(2);
                }
            });

            seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            new GroupServer().execute(0);
        }



    }
    public static boolean isSharingWiFi(final WifiManager manager)

    {
        try
        {
            final Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true); //in the case of visibility change in future APIs
            return (Boolean) method.invoke(manager);
        }
        catch (final Throwable ignored)
        {
        }

        return false;
    }
    public void clickAdvertise(View v) {
        // Register service
        if(globals.getPort() > -1) {
            mNsdHelper.registerService(globals.getPort());
        } else {
            Log.d(TAG, "ServerSocket isn't bound.");
        }
    }

    public void clickDiscover(View v) {
        mNsdHelper.discoverServices();
    }

    /*public void clickConn()
    {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();

    }
*/
  /*  public void clickConnect(View v) {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            Log.d(TAG, "Connecting.");
            mConnection.connectToServer(service.getHost(),
                    service.getPort());
        } else {
            Log.d(TAG, "No service to connect to!");
        }
    }
    public void clickSend(View v) {
        EditText messageView = (EditText) this.findViewById(R.id.chatInput);
        if (messageView != null) {
            String messageString = messageView.getText().toString();
            if (!messageString.isEmpty()) {
                mConnection.sendMessage(messageString);
            }
            messageView.setText("");
        }
    }*/
    public void addChatLine(String line) {
        mStatusView.append("\n" + line);
    }
    @Override
    protected void onStart() {
        Log.d(TAG, "Starting.");
        globals.showMessage(Player.this, "The onstart() event");
       // mConnection = new ChatConnection(mUpdateHandler);
      //  mNsdHelper = new NsdHelper(this);
       // mNsdHelper.initializeNsd();
        super.onStart();
    }
    @Override
    protected void onPause() {
        globals.showMessage(Player.this,"The onpause() event");
        Log.d(TAG, "Pausing.");
      //  if (mNsdHelper != null) {
      //      mNsdHelper.stopDiscovery();
      //  }
        super.onPause();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming.");
        globals.showMessage(Player.this, "The onres() event");
        super.onResume();
      //  if (mNsdHelper != null) {
       //     mNsdHelper.discoverServices();
       // }
    }
    // For KitKat and earlier releases, it is necessary to remove the
    // service registration when the application is stopped.  There's
    // no guarantee that the onDestroy() method will be called (we're
    // killable after onStop() returns) and the NSD service won't remove
    // the registration for us if we're killed.
    // In L and later, NsdService will automatically unregister us when
    // our connection goes away when we're killed, so this step is
    // optional (but recommended).
    @Override
    protected void onStop() {
        globals.showMessage(Player.this, "The onstop() event");
        Log.d(TAG, "Being stopped.");
      //  mNsdHelper.tearDown();
      //  mConnection.tearDown();
      //  mNsdHelper = null;
     //   mConnection = null;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        globals.showMessage(Player.this,"The ondes() event");
        Log.d(TAG, "Being destroyed.");
        super.onDestroy();
    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    String msg="Android: ";
    /** Called when the activity is about to become visible. */
  /*  @Override
    protected void onStart() {
        super.onStart();
        globals.showMessage(Player.this,"The onStart() event");
        Log.d(msg, "The onStart() event");
    }

    *//** Called when the activity has become visible. *//*
    @Override
    protected void onResume() {
        super.onResume();globals.showMessage(Player.this, "The onRes() event");
        Log.d(msg, "The onResume() event");
    }

    *//** Called when another activity is taking focus. *//*
    @Override
    protected void onPause() {
        super.onPause();
        globals.showMessage(Player.this, "The onPa() event");
        Log.d(msg, "The onPause() event");
    }

    *//** Called when the activity is no longer visible. *//*
    @Override
    protected void onStop() {
        super.onStop();
        globals.showMessage(Player.this, "The onStop() event");
        Log.d(msg, "The onStop() event");
    }
*/
    /** Called just before the activity is destroyed. */
}