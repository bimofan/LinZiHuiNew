package com.easemob.chatuidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.ShengHuoQuan;
import com.easemob.chatuidemo.utils.T;
import com.easemob.chatuidemo.widget.XListView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 生活圈界面
 */
public class LifeCircleActivity extends BaseActivity implements XListView.IXListViewListener{

    public static final String TAG = "LifeCircleActivity";
    private TextView mTvBack,mTvTitle;
    private XListView mLv;
    private ImageView mIvRight;
    private LinearLayout mLlBar;
    private ShqAdapter shqAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_circle);
        findView();
        initData();
        addAction();
    }



    private void findView() {
        mTvBack = (TextView) findViewById(R.id.back_tv);
        mTvTitle = (TextView) findViewById(R.id.message_title);
        mIvRight = (ImageView) findViewById(R.id.iv_new_contact);
        mLv = (XListView) findViewById(R.id.order_lists_lv);
        mLlBar = (LinearLayout) findViewById(R.id.progress_bar);
    }

    private void initData() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setText("<邻近");
        mTvTitle.setText("生活圈");
        mIvRight.setVisibility(View.VISIBLE);
        mIvRight.setImageResource(R.drawable.write_icon);
        mLv.setXListViewListener(this);
        mLv.setPullLoadEnable(true);
        selectAllData();
    }

    private void selectAllData() {
        mLlBar.setVisibility(View.VISIBLE);
        BmobQuery<ShengHuoQuan> query = new BmobQuery<ShengHuoQuan>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("username", "15201931110");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
//        query.setLimit(10);
        //执行查询方法
        query.findObjects(this, new FindListener<ShengHuoQuan>() {
            @Override
            public void onSuccess(List<ShengHuoQuan> object) {
                // TODO Auto-generated method stub
                mLlBar.setVisibility(View.GONE);
                Log.i(TAG, "查询成功：共" + object.size() + "条数据。");
                shqAdapter = new ShqAdapter(object);
                mLv.setAdapter(shqAdapter);
            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                T.show(LifeCircleActivity.this, "查询失败：" + msg, 1);
                Log.i(TAG, "查询失败：" + msg);
            }
        });
    }

    private void addAction() {
        mTvBack.setOnClickListener(new MyOnClickListener());
        mIvRight.setOnClickListener(new MyOnClickListener());
    }

    @Override
    public void onRefresh() {//下拉刷新
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLv.stopRefresh();
                mLv.stopLoadMore();
                mLv.setRefreshTime("");
                selectAllData();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {//上拉加载
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLv.stopRefresh();
                mLv.stopLoadMore();
                mLv.setRefreshTime("");
                selectAllData();
            }
        }, 2000);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_tv://返回
                    finish();
                    break;
                case R.id.iv_new_contact://发布朋友圈
                    startActivity(new Intent(LifeCircleActivity.this,PublishLifeActivity.class));
                    break;
            }
        }
    }

    private class ShqAdapter extends BaseAdapter{

        private List<ShengHuoQuan> datas;

        private ShqAdapter(List<ShengHuoQuan> datas) {
            this.datas = datas;
        }

        public List<ShengHuoQuan> getDatas() {
            return datas;
        }

        public void addList(List<ShengHuoQuan> datalist) {
            datas.addAll(datalist);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = convertView;

            ViewHolder viewHolder = null;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.shq_lists_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_username);
                viewHolder.tv_context = (TextView) view.findViewById(R.id.tv_context);
                viewHolder.gridView = (GridView) view.findViewById(R.id.gridView);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (null != datas && datas.size() > 0) {
                ShengHuoQuan shq = datas.get(position);
                viewHolder.tv_name.setText(shq.getUsername());
                viewHolder.tv_context.setText(shq.getText());
                /*if(shq.getImage_url()!=null){
                    //有图片
                    viewHolder.gridView.setVisibility(View.VISIBLE);
                    ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
                    HashMap<String,Object> map = null;
                    for(int i=0;i<shq.getImage_url().size();i++)
                    {
                        map=new HashMap<String,Object>();
//                        Log.i(TAG,"imageurl======"+BitmapHelper.getBitmap(shq.getImage_url().get(i)));
                        map.put("ItemImage", BitmapHelper.getBitmap("http://file.bmob.cn/M02/D0/A8/oYYBAFZftbOAW91gAB8T-v2OYNo225.jpg"));
                        list.add(map);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(LifeCircleActivity.this,list,R.layout.item_published_grida,new String[]{"ItemImage"},new int[]{R.id.item_grida_image});
                    adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

                        public boolean setViewValue(View view, Object data,String textRepresentation) {
                            //判断是否为我们要处理的对象
                            if (view instanceof ImageView && data instanceof Bitmap) {
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap((Bitmap) data);
                                return true;
                            } else
                                return false;
                        }
                    });
                    viewHolder.gridView.setAdapter(adapter);
                }else{
                    viewHolder.gridView.setVisibility(View.GONE);
                }*/

            }

            return view;
        }
    }

    class ViewHolder {
        TextView tv_name,tv_context;
        GridView gridView;
    }


    public void back(View v) {
        finish();
    }


}
