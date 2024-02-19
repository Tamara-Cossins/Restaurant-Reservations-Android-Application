package com.example.docksidedelight;

public class Meal {
    // Meal class to structure and organise variables of a meal, with getters and setters

    private String name;
    private boolean isFavourite;
    private int imageResID;

    public Meal(String name, int imageResID){
        this.name = name;
        this.imageResID = imageResID;
        this.isFavourite = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public int getImageResID() {
        return imageResID;
    }
}
