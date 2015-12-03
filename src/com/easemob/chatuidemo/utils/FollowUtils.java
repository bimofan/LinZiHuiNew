package com.easemob.chatuidemo.utils;

import android.content.Context;

import com.easemob.chatuidemo.domain.Follow;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by guhuixiong on 2015/11/25.
 */
public class FollowUtils {

    public List<Follow> queryAllFollow(final Context context){
        BmobQuery<Follow> query = new BmobQuery<Follow>();
        query.findObjects(context, new FindListener<Follow>() {
            @Override
            public void onSuccess(List<Follow> object) {
                // TODO Auto-generated method stub
                return;
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                T.show(context, "查询失败：" + msg,1);
            }
        });
        return null;
    }

}
