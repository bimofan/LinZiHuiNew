package com.easemob.chatuidemo.domain;

/**
 * Created by Administrator on 2015/11/22 0022.
 */
public class ItemData {
    private String text;
    private int icon;

    public ItemData(){}

    public ItemData(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
