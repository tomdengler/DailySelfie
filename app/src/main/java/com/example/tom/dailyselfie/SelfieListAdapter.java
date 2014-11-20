package com.example.tom.dailyselfie;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.ViewGroup;
import java.util.List;


/**
 * Created by Tom on 11/18/2014.
 */
public class SelfieListAdapter extends ArrayAdapter {

    private final Activity activity;
    private final List selfies;

    public SelfieListAdapter(Activity activity, List objects) {
        super(activity, R.layout.list_item, objects);
        this.activity = activity;
        this.selfies = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        Selfie currentSelfie = (Selfie) selfies.get(position);
        txtTitle.setText(currentSelfie.getName());
        imageView.setImageURI(currentSelfie.getImageUri());

        return rowView;
    }

    public void add(Selfie listItem)
    {
        selfies.add(listItem);
        notifyDataSetChanged();
    }

}
