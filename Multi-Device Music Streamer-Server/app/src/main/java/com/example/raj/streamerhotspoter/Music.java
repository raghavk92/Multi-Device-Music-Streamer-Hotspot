package com.example.raj.streamerhotspoter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by rohan on 22/3/15.
 */
public class Music implements Serializable {

    String title;
    String path;
    String albumId;

    public Music(String title, String path, String albumId) {
        this.title = title;
        this.path = path;
        this.albumId = albumId;

    }

    public Bitmap getArtwork( Context c ) {
        if( this.albumId == null ) {
            return null;
        }
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(this.albumId));
        ContentResolver res = c.getContentResolver();
        try {
            InputStream in = res.openInputStream(uri);
            return BitmapFactory.decodeStream(in);
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
    public String toString() {
        return this.title;
    }
}
