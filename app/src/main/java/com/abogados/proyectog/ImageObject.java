package com.abogados.proyectog;

import android.graphics.Bitmap;

public class ImageObject {
    protected Integer id;
    protected Bitmap image;
    protected int mandatory;

    public ImageObject(Integer id, Bitmap image, int mandatory) {
        this.id = id;
        this.image = image;
        this.mandatory = mandatory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getMandatory() {
        return mandatory;
    }

    public void setMandatory(int mandatory) {
        this.mandatory = mandatory;
    }
}
