package com.easemob.chatuidemo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.CommonAdapter;
import com.easemob.chatuidemo.adapter.ViewHolder;
import com.easemob.chatuidemo.domain.Follow;
import com.easemob.chatuidemo.domain.MyUser;
import com.easemob.chatuidemo.utils.SPUtils;
import com.easemob.chatuidemo.utils.T;
import com.easemob.chatuidemo.widget.CommonRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 关注页面
 */
public class FollowActivity extends BaseActivity {

    private static final String TAG = "FollowActivity";
    private TextView title;
    private CommonRadioGroup rg;
    private ListView mLv;
    private View progressBar;
    private List<MyUser> myFollowsList=new ArrayList<MyUser>();
    private List<MyUser> followsMeList=new ArrayList<MyUser>();
    private final BmobQuery<Follow> followQuery = new BmobQuery<Follow>();
    private final BmobQuery<MyUser> userQuery = new BmobQuery<MyUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        initView();
        initData();
        addAction();
    }

    private void initView() {
        progressBar = (View)findViewById(R.id.progress_bar);
        title = (TextView)findViewById(R.id.tv_title);
        rg = (CommonRadioGroup) findViewById(R.id.main_rg);
        mLv = (ListView) findViewById(R.id.list);
    }

    private void initData() {
        title.setText(getResources().getString(R.string.chat_room));
        showMyFollows();

    }


    private void addAction() {
        rg.setOnCheckedChangeListener(new CommonRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CommonRadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_myFollows://我的关注
                      showFollow(myFollowsList);
                        break;
                    case R.id.rb_followMes://关注我的
                        showFollow(followsMeList);
                        break;
                }
            }
        });
    }
    private void showFollow(List<MyUser> list){
        mLv.removeAllViewsInLayout();
        mLv.setAdapter(new CommonAdapter<MyUser>(getApplicationContext(), list, R.layout.lv_item) {
            @Override
            public void convert(ViewHolder helper, MyUser item) {
                helper.setText(R.id.tv_item_text, item.getUsername());
            }
        });
    }

    private void showMyFollows(){
        progressBar.setVisibility(View.VISIBLE);
        //获取当前用户
        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser != null){
            Log.i(TAG,"bmobUser.getObjectId()==="+ bmobUser.getObjectId());
            followQuery.addWhereEqualTo("userObjectId", bmobUser.getObjectId());
            followQuery.findObjects(this, new FindListener<Follow>() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onSuccess(List<Follow> list) {
                    //查询我关注的
                    userQuery.addWhereContainedIn("objectId", list.get(0).getMyFollows());
                    userQuery.findObjects(FollowActivity.this, new FindListener<MyUser>() {
                        @Override
                        public void onSuccess(List<MyUser> object) {
                            // TODO Auto-generated method stub
                            myFollowsList = object;
                            progressBar.setVisibility(View.GONE);
                            mLv.setAdapter(new CommonAdapter<MyUser>(getApplicationContext(), object, R.layout.lv_item) {
                                @Override
                                public void convert(ViewHolder helper, MyUser item) {
                                    helper.setText(R.id.tv_item_text, item.getUsername());
                                }
                            });
                            for (MyUser user : object){
                                Log.i(TAG,"MyFollowsId====="+user.getObjectId());
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            T.show(FollowActivity.this, "查询失败：" + code, 1);
                        }

                    });

                    //查询关注我的
                    userQuery.addWhereContainedIn("objectId", list.get(0).getFollowMes());
                    userQuery.findObjects(FollowActivity.this, new FindListener<MyUser>() {
                        @Override
                        public void onSuccess(List<MyUser> object) {
                            // TODO Auto-generated method stub
                            followsMeList = object;
                            for (MyUser user : object){
                                Log.i(TAG,"FollowMesId====="+user.getObjectId());
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            T.show(FollowActivity.this, "查询失败：" + code, 1);
                        }

                    });

                }

            });
        }
    }



}

