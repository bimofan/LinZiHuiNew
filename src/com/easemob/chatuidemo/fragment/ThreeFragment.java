package com.easemob.chatuidemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.AddContactActivity;
import com.easemob.chatuidemo.activity.LifeCircleActivity;
import com.easemob.chatuidemo.activity.NearbyActivitiesActivity;
import com.easemob.chatuidemo.activity.NearbyGroupActivity;
import com.easemob.chatuidemo.adapter.CommonAdapter;
import com.easemob.chatuidemo.adapter.ViewHolder;
import com.easemob.chatuidemo.domain.ItemData;
import com.easemob.chatuidemo.utils.T;

import java.util.ArrayList;
import java.util.List;

/**
 * 邻近界面
 */
public class ThreeFragment extends Fragment {

    private Activity activity;
    private View conventView;
    private ListView mLv;
    private List<ItemData> list = new ArrayList<ItemData>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        findView();
        initData();
        addAction();

    }

    private void findView() {
        mLv = (ListView) activity.findViewById(R.id.three_lv);
    }

    private void initData() {
        list.add(new ItemData("生活圈", R.drawable.life_circle_icon));
        list.add(new ItemData("邻近活动",R.drawable.lingjin_activity_icon));
        list.add(new ItemData("邻近群组",R.drawable.lingjin_group_icon));
        mLv.setAdapter(new CommonAdapter<ItemData>(activity.getApplicationContext(), list, R.layout.lv_item) {
            @Override
            public void convert(ViewHolder helper, ItemData item) {
                helper.getView(R.id.iv_right).setVisibility(View.VISIBLE);
                helper.setImageResource(R.id.iv_icon, item.getIcon());
                helper.setText(R.id.tv_item_text, item.getText());
            }
        });
    }

    private void addAction() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://生活圈
                        startActivity(new Intent(getActivity(), LifeCircleActivity.class));
                        break;
                    case 1://邻近活动
                        startActivity(new Intent(getActivity(), NearbyActivitiesActivity.class));
                        break;
                    case 2://邻近群组
                        startActivity(new Intent(getActivity(), NearbyGroupActivity.class));
                        break;
                }
            }
        });
    }

}
