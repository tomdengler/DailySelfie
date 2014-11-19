package com.example.tom.dailyselfie;

/**
 * Created by Tom on 11/18/2014.
 */
public class Selfie {
    private String name;
    private int drawableId;

    public Selfie(String name, int id) {
        this.name = name;
        this.drawableId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }


}

