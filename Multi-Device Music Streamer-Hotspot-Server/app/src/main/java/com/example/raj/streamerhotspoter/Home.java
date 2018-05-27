package com.example.raj.streamerhotspoter;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;


public class Home extends AppCompatActivity {
    Globals globals = Globals.getInstance();
    MediaPlayer mediaPlayer = globals.getInstance().getPlayer();
    private TextView mStatusView;
    private Handler mUpdateHandler;
    NsdHelper mNsdHelper;

    //  public static final String TAG = "NsdChat";
    private Socket mSocket;
    private int mPort = -1;
    NsdServiceInfo service ;
    private static final String TAG = "Home";
    private ArrayList<Song> songList;
    private ListView songView;
    private SlidingUpPanelLayout mLayout;

    //WifiP2pManager dummy = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);;
    public void play( Music selection ) {
        Intent moveToPlayer = new Intent( this, Player.class );
        moveToPlayer.putExtra( "select", selection );
        startActivity( moveToPlayer );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

     //   setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

  /*   ListView lv = (ListView) findViewById(R.id.list);
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Home.this, "onItemClick", Toast.LENGTH_SHORT).show();
            }
        });
*/
        List<String> your_array_list = Arrays.asList(
                "This",
                "Is",
                "An",
                "Example",
                "ListView",
                "That",
                "You",
                "Can",
                "Scroll",
                ".",
                "It",
                "Shows",
                "How",
                "Any",
                "Scrollable",
                "View",
                "Can",
                "Be",
                "Included",
                "As",
                "A",
                "Child",
                "Of",
                "SlidingUpPanelLayout"
        );
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
/*
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
               your_array_list );

*/
     //   lv.setAdapter(arrayAdapter);

       // mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
     /*   mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
*/
      /* // TextView t = (TextView) findViewById(R.id.name);
       // t.setText(Html.fromHtml(getString(R.string.hello)));
    //    Button f = (Button) findViewById(R.id.follow);
     //   f.setText(Html.fromHtml(getString(R.string.follow)));
     //   f.setMovementMethod(LinkMovementMethod.getInstance());
      //  f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.twitter.com/umanoapp"));
                startActivity(i);
            }
        });*/
/*between thesse are code new

        songView = (ListView)findViewById(R.id.audioList);

        songList = new ArrayList<Song>();
        getSongList();
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });



























        */      //get audio
        String query = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.ALBUM_ID
        };
        List<Music> audio = new ArrayList<Music>();

        Cursor cursor = getBaseContext().getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, query, null, null);
        while( cursor.moveToNext() ) {
            audio.add( new Music( cursor.getString( 4 ), cursor.getString( 3 ), cursor.getString( 7 ) ) );
        }
        ArrayAdapter<Music> audioAdapter = new ArrayAdapter<Music>(this, android.R.layout.simple_list_item_1, audio);
        final ListView musicList = (ListView)findViewById( R.id.audioList );
        musicList.setAdapter(audioAdapter);
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Music selection = (Music) musicList.getItemAtPosition(position);
                play(selection);
            }
        });

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

    //  public void clickConn()
    {
 /*       new Thread(new Runnable() {

            public void run() {
                try {
                    globals.showMessage(Home.this,Home.this, "wait");

                    NsdServiceInfo service = globals.mNsdHelper.getChosenServiceInfo();
                    while (service == null) ;
                    globals.showMessage(Home.this,Home.this, "go ahead");
                    globals.setGoahead(1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });*/
    }


    public void clickConnect(View v) {
          NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
          if (service != null) {
              Log.d(TAG, "Connecting.");
              globals.setIp(service.getHost());
              globals.showMessage(Home.this,Home.this, "go ahead");
              globals.setGoahead(1);
            //  glmConnection.connectToServer(service.getHost(),
              //          service.getPort());
          } else {
              Log.d(TAG, "No service to connect to!");
          }
      }/*
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
        globals.showMessage(Home.this, "The onstart() event");
        // mConnection = new ChatConnection(mUpdateHandler);
        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        super.onStart();
    }
    @Override
    protected void onPause() {
        globals.showMessage(Home.this,"The onpause() event");
        Log.d(TAG, "Pausing.");
      //  if (mNsdHelper != null) {
         //   mNsdHelper.stopDiscovery();
      //  }
        super.onPause();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming.");
        globals.showMessage(Home.this,"The onres() event");
        super.onResume();
        if (mNsdHelper != null) {
            mNsdHelper.discoverServices();
        }
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
        globals.showMessage(Home.this, "The onstop() event");
        Log.d(TAG, "Being stopped.");
        mNsdHelper.tearDown();
        //  mConnection.tearDown();
        mNsdHelper = null;
        //   mConnection = null;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        globals.showMessage(Home.this, "The ondes() event");
        Log.d(TAG, "Being destroyed.");
       // mNsdHelper.tearDown();
        //  mConnection.tearDown();
       //   mNsdHelper = null;
        super.onDestroy();
    }



    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }
}
