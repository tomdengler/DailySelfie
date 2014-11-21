package com.example.tom.dailyselfie;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

// TODO: Requirement #3: clicking thumbnail shows full picture
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFolderNames();

        List selfies = new ArrayList();

        mImagesAdapter = new SelfieListAdapter(DailySelfie.this,selfies);
        setListAdapter(mImagesAdapter);
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
        Log.i(TAG,"enter getLocalSelfies()");
        File storageDir = DailySelfie.this.getExternalFilesDir(mThumbnailFolder);
        File selfieImages[] = storageDir.listFiles();

        List selfies = new ArrayList();

        for (File imageFile : selfieImages)
        {
            Selfie selfie = new Selfie("existing");
            selfie.setThumbnailUri(Uri.fromFile(imageFile));
            selfies.add(selfie);
        }

        return selfies;
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

    private void addImage(Bitmap imageBitmap)
    {
        // TODO : give image a proper text field
        Selfie selfie = new Selfie("mad selfie3");
        selfie.setThumbnailUri(storeImage(imageBitmap));
        selfie.setFullimageUri(mCurrentImageUri);
        mImagesAdapter.add(selfie);
    }

    private void launchCamera() {
        // launch camera app
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // File f = createImageFile(true);
        // mCurrentImageUri = Uri.fromFile(f);

        // the following actually creates a jpg file (not a png)
        // and also makes it so that the thumbnail from data.getExtras() in onActivityResult
        // is not available.
        // todo: get back fullsize and convert to thumbnail
        // todo: persist the selfie array list to get references to the files
        // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, actionCode);
        }
        else
            Toast.makeText(this,"Could not launch the camera.",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    addImage(imageBitmap);
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
        String imageFileName = "PNG_" + timeStamp + "_";

        File storageDir = DailySelfie.this.getExternalFilesDir(mImageFolder);

        try {
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",         /* suffix */
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
            folder.mkdir();
        folder = DailySelfie.this.getExternalFilesDir(mImageFolder);
        if (!folder.exists())
            folder.mkdir();
    }


}
