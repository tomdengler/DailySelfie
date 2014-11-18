package com.example.tom.dailyselfie;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


// TODO: Requirement #1: camera icon
// <item android:id="@+id/item1" android:title="Launch Camera" android:icon="@android:drawable/ic_menu_camera" />
// From the initial app screen, is there a camera icon in the Action Bar?
// If so, then if the user clicks on the camera icon in the ActionBar, does the app open up a
// picture-taking app already installed on the device?

// TODO: Requirement #2: thumbnail picture added to listview
// If the user snaps a picture with the picture-taking and then accepts it, is
// the picture returned to the DailySelfie app and then displayed in some way to the user along
// with other selfies the user may have already taken?

// TODO: Requirement #3: clicking thumbnail shows full picture
// If the user clicks on a small view of a selfie, does a large view of the same selfie appear?

// TODO: Requirement #4: pics are saved in persistent storage
// If the user opens the app, takes at least one selfie, then exits the app,
// and then reopens it, do they have access to all the selfies saved on their device?

// TODO: Requirement #5: periodic alarm in notification area
// If the app starts up at least once, does the app create and set an Alarm
// that fires roughly once every two minutes, putting a notification area notification in the
// notification area? If so, does pulling down on the notification area and clicking on the
// notification view cause the Daily Selfie app to reopen?


public class DailySelfie extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
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

    private void launchCamera() {
        // TODO: launch camera app
        Toast.makeText(this,"Launching the camera app",Toast.LENGTH_LONG).show();
    }

}
