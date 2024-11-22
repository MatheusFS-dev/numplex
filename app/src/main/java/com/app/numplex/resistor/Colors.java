package com.app.numplex.resistor;

import java.io.Serializable;

public class Colors implements Serializable {

    private String name;
    private int image;

    public Colors() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}