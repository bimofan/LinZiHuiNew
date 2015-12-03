package com.easemob.chatuidemo.domain;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by guhuixiong on 2015/12/3.
 */
public class ShengHuoQuan extends BmobObject {

    public ShengHuoQuan(){}

    private String username;
    private String text;
    private List<String> image_url;//图片
    private BmobGeoPoint location;//坐标
    private BmobUser publisher;
    private List<String> comment;//评论
    private List<String> zan;//赞

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImage_url() {
        return image_url;
    }

    public void setImage_url(List<String> image_url) {
        this.image_url = image_url;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public List<String> getComment() {
        return comment;
    }

    public void setComment(List<String> comment) {
        this.comment = comment;
    }

    public List<String> getZan() {
        return zan;
    }

    public void setZan(List<String> zan) {
        this.zan = zan;
    }

    public BmobUser getPublisher() {
        return publisher;
    }

    public void setPublisher(BmobUser publisher) {
        this.publisher = publisher;
    }
}
