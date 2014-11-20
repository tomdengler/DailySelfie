package com.example.tom.dailyselfie;

import android.net.Uri;

/**
 * Created by Tom on 11/18/2014.
 */
public class Selfie {
    private String name;
    private Uri imageUri;

    public Selfie(String name) {
        this.name = name;
        this.imageUri = null;
    }

    public Selfie(String name,Uri uri) {
        this.name = name;
        this.imageUri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}