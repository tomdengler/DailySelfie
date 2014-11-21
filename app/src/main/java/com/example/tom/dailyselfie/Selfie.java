package com.example.tom.dailyselfie;

import android.net.Uri;

/**
 * Created by Tom on 11/18/2014.
 */
public class Selfie {
    private String name;
    private Uri thumbnailUri;
    private Uri fullimageUri;

    public Selfie(String name) {
        this.name = name;
        this.thumbnailUri = null;
        this.fullimageUri = null;
    }

    public Selfie(String name,Uri uri) {
        this.name = name;
        this.thumbnailUri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public Uri getFullimageUri() {
        return fullimageUri;
    }

    public void setFullimageUri(Uri fullimageUri) {
        this.fullimageUri = fullimageUri;
    }
}