package com.example.forchita;

public class Model {

    private int image;
    private String title,title2;

    public Model(int image, String title, String title2) {
        this.image = image;
        this.title = title;
        this.title2 = title2;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }
}
