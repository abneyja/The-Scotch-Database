package edu.csc4360.thescotchdatabase;

public class Scotch {
    private int sId;
    private String sName;
    private String sTasting_notes;
    private float sStars;
    private Boolean sFavorite;
    private String sImage;

    public Scotch() {
        this.sId = -1; this.sName = "Scotch"; this.sTasting_notes = "Notes";
        this.sStars = 3.0f; this.sFavorite = false; this.sImage = "image";
    }

    public Scotch(int id, String name, String tasting_notes, float stars, boolean favorite, String image) {
        sId = id;
        sName = name;
        sTasting_notes = tasting_notes;
        sStars = stars;
        sFavorite = favorite;
        sImage = image;
    }

    public int getId() {
        return sId;
    }

    public void setId(int id) {
        this.sId = id;
    }

    public String getName() {
        return sName;
    }

    public void setName(String name) {
        this.sName = name;
    }

    public String getTasting_notes() {
        return sTasting_notes;
    }

    public void setTasting_notes(String tasting_notes) {
        this.sTasting_notes = tasting_notes;
    }

    public float getStars() {
        return sStars;
    }

    public void setStars(float stars) {
        this.sStars = stars;
    }

    public boolean getFavorite() {
        return sFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.sFavorite = favorite;
    }

    public String getImage() {
        return sImage;
    }

    public void setImage (String image) {
        this.sImage = image;
    }

    public String toString() {
        return "ID: " + this.getId() + " Name: " + this.getName();
    }
}