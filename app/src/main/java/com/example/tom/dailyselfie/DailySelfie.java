package com.example.tom.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// Requirement #1: camera icon
// <item android:id="@+id/item1" android:title="Launch Camera" android:icon="@android:drawable/ic_menu_camera" />
// From the initial app screen, is there a camera icon in the Action Bar?
// If so, then if the user clicks on the camera icon in the ActionBar, does the app open up a
// picture-taking app already installed on the device?

// Requirement #2: thumbnail picture added to listview
// If the user snaps a picture with the picture-taking and then accepts it, is
// the picture returned to the DailySelfie app and then displayed in some way to the user along
// with other selfies the user may have already taken?
// along with other already taken is in requirement #4

// Requirement #3: clicking thumbnail shows full picture
// If the user clicks on a small view of a selfie, does a large view of the same selfie appear?

// Requirement #4: pics are saved in persistent storage
// If the user opens the app, takes at least one selfie, then exits the app,
// and then reopens it, do they have access to all the selfies saved on their device?

// TODO: Requirement #5: periodic alarm in notification area
// If the app starts up at least once, does the app create and set an Alarm
// that fires roughly once every two minutes, putting a notification area notification in the
// notification area? If so, does pulling down on the notification area and clicking on the
// notification view cause the Daily Selfie app to reopen?

public class DailySelfie extends ListActivity {

    private static final int ACTION_TAKE_PHOTO = 1;
    private SelfieListAdapter mImagesAdapter;
    private String TAG = "Tag-DailySelfie";
    private Uri mCurrentImageUri = null;
    private String mThumbnailFolder = null;
    private String mImageFolder = null;
    private final String mSaveFilename = "SelfieList.json";
    //private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
    //private static final long REPEAT_ALARM_DELAY = 2 * 60 * 1000L;

    private static final long INITIAL_ALARM_DELAY = 30 * 1000L;
    private static final long REPEAT_ALARM_DELAY =  60 * 1000L;

    private PendingIntent mNotificationReceiverPendingIntent;
    private Intent mNotificationReceiverIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFolderNames();

        List selfies = new ArrayList();

        mImagesAdapter = new SelfieListAdapter(DailySelfie.this,selfies);
        setListAdapter(mImagesAdapter);

        ListView v = getListView();
        v.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Selfie s = mImagesAdapter.getItem(position);
                showBigPicture(s.getFullimageUri());
            }});

        setupAlarm();
    }

    private void showBigPicture(Uri imageUri) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/jpg");
        startActivity(intent);
    }

    private void setupAlarm()
    {
        mNotificationReceiverIntent = new Intent(DailySelfie.this,
                AlarmNotificationReceiver.class);

        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                DailySelfie.this, 0, mNotificationReceiverIntent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                REPEAT_ALARM_DELAY,
                mNotificationReceiverPendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Selfie> selfies = getLocalSelfies();

        if (selfies.isEmpty())
            return;

        mImagesAdapter.clear();
        for (Selfie s : selfies)
                mImagesAdapter.add(s);
    }

    private List getLocalSelfies()
    {
        Log.i(TAG, "enter getLocalSelfies()");
        return loadSelfies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.daily_selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.take_picture:
                launchCamera();
                return true;
            }

        return super.onOptionsItemSelected(item);
        }

    private void saveImageAsSelfie(Uri fullimageUri)
    {
        // TODO : give image a proper text field
        Selfie selfie = new Selfie("mad selfie3");
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fullimageUri.getPath()), 64, 64);
        Uri thumbnailUri = storeImage(thumbImage);
        selfie.setThumbnailUri(thumbnailUri);
        selfie.setFullimageUri(fullimageUri);
        saveSelfie(selfie);
    }

    private void launchCamera() {
        // launch camera app
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = createImageFile(false);
        mCurrentImageUri = Uri.fromFile(f);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, actionCode);
        }
        else
            Toast.makeText(this,"Could not launch the camera.",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult");
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    saveImageAsSelfie(mCurrentImageUri);
                }
                else
                    Toast.makeText(this,"The pic was not taken",Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    private Uri storeImage(Bitmap image) {
        File pictureFile = createImageFile(true);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            return Uri.fromFile(pictureFile);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return null;
    }

    private File createImageFile(boolean thumbnail)  {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File storageDir = thumbnail?
                DailySelfie.this.getExternalFilesDir(mThumbnailFolder):
                DailySelfie.this.getExternalFilesDir(mImageFolder);
        String suffix = thumbnail?".png":".jpg";

        try {
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    suffix,          /* suffix */
                    storageDir      /* directory */
            );
        }catch (IOException e) {
            Log.d(TAG, "Error creating image file: " + e.getMessage());
            return null;
        }
    }

    private void initFolderNames() {
        mThumbnailFolder = Environment.DIRECTORY_PICTURES+"/thumbnails";
        mImageFolder = Environment.DIRECTORY_PICTURES+"/images";
        File folder = DailySelfie.this.getExternalFilesDir(mThumbnailFolder);
        if (!folder.exists())
            folder.mkdirs();
        Log.i(TAG,"mThumbnailFolder:"+folder.toString());

        folder = DailySelfie.this.getExternalFilesDir(mImageFolder);
        if (!folder.exists())
            folder.mkdirs();
        Log.i(TAG,"mImageFolder:"+folder.toString());

    }

private void saveSelfie(Selfie selfie) {

    List<Selfie> selfies = loadSelfies();
    selfies.add(selfie);

    JSONArray jsonArray = new JSONArray();
    for (Selfie s : selfies) {
        jsonArray.put(s.getJSONObject());
    }

    File f = new File(DailySelfie.this.getExternalFilesDir(null),mSaveFilename);
    if (f.exists())
        f.delete();

    try {
        FileWriter out = new FileWriter(f);
        out.write(jsonArray.toString());
        out.flush();
        out.close();

    } catch (IOException e) {
        Log.i(TAG,"Error writing file: "+e.getMessage());
    }
}

private List loadSelfies() {

    List selfies = new ArrayList();

    File f = new File(DailySelfie.this.getExternalFilesDir(null),mSaveFilename);
    if (!f.exists())
        return selfies;

    try {
        FileReader in = new FileReader(f);
    } catch (FileNotFoundException e) {};

    StringBuilder text = new StringBuilder();
    BufferedReader br = null;

    try {
        br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
    } catch (IOException e) {
        // do exception handling
    } finally {
        try { br.close(); } catch (Exception e) { }
    }

    try {
        JSONArray array = new JSONArray(text.toString());
        for (int i = 0;i<array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            selfies.add(new Selfie(obj));
        }

    } catch (JSONException e) {
        Log.i(TAG,"loadSelfies JSON Exception: " + e.getMessage());
    }

    return selfies;
}



}
