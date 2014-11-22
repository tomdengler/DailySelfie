package com.example.tom.dailyselfie;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 11/18/2014.
 */
public class Selfie {
    private String name;
    private Uri thumbnailUri;
    private Uri fullimageUri;
    private String TAG = "Tag-DailySelfie";

    public Selfie(String name) {
        this.name = name;
        this.thumbnailUri = null;
        this.fullimageUri = null;
    }

    public Selfie(String name,Uri uri) {
        this.name = name;
        this.thumbnailUri = uri;
    }

    public Selfie(JSONObject obj) {
        try {
            this.name = obj.getString("Name");
            this.thumbnailUri = Uri.parse(obj.getString("Thumbnail"));
            this.fullimageUri = Uri.parse(obj.getString("Fullimage"));
        } catch (JSONException e) {
            Log.i(TAG, "JSONException in constructor: " + e.getMessage());
        }
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

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Name", name);
            obj.put("Thumbnail", thumbnailUri.toString());
            obj.put("Fullimage", fullimageUri.toString());
        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e.getMessage());
        }
        return obj;
    }

}