package com.ojiofong.toxins.model;


import java.io.Serializable;

public class Data implements Serializable {

    private String title;
    private int iconID;
    private String subtitle;


    public Data(String title, int iconID, String subtitle) {
        super();
        this.title = title;
        this.iconID = iconID;
        this.subtitle = subtitle;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
