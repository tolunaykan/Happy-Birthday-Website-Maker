package com.zisantolunay.happybirthday;

public class PagerItem {

    public static final int ADDMESSAGE = 100;
    public static final int ADDPHOTO = 200;
    public static final int EDITBUTTON = 300;

    public static final int PAGETITLE = 0;
    public static final int TURNONLIGHT = 1;
    public static final int PLAYMUSIC = 2;
    public static final int LETSDECORATE = 3;
    public static final int FLYBALLOONS = 4;
    public static final int CAKE = 5;
    public static final int CANDLE = 6;
    public static final int HAPPYBIRTHDAY = 7;
    public static final int MESSAGESFORYOU = 8;


    private String title, description, hint;
    private int image, type, secondType;

    public int getSecondType() {
        return secondType;
    }

    public void setSecondType(int secondType) {
        this.secondType = secondType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
