package com.easemob.chatuidemo.domain;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by guhuixiong on 2015/11/25.
 */
public class Follow extends BmobObject {
    private String userObjectId;
    private List<String> myFollows;
    private List<String> followMes;

    public Follow(){}


    public Follow(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public List<String> getMyFollows() {
        return myFollows;
    }

    public void setMyFollows(List<String> myFollows) {
        this.myFollows = myFollows;
    }

    public List<String> getFollowMes() {
        return followMes;
    }

    public void setFollowMes(List<String> followMes) {
        this.followMes = followMes;
    }
}
