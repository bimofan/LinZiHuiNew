package com.easemob.chatuidemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.bmob.btp.callback.UploadListener;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.ShengHuoQuan;
import com.easemob.chatuidemo.utils.Bimp;
import com.easemob.chatuidemo.utils.FileUtils;
import com.easemob.chatuidemo.utils.ImageItem;

import android.view.ViewGroup.LayoutParams;

import com.easemob.chatuidemo.utils.KeyBoardUtils;
import com.easemob.chatuidemo.utils.T;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import android.os.Handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

/**
 * 发布朋友圈
 */
public class PublishLifeActivity extends Activity implements TencentLocationListener {

    public static final String TAG = "PublishLifeActivity";
    private TextView mTvBack,mTvTitle;
    private ImageView mIvRight;
    private GridView mGv;
    private GridAdapter adapter;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap ;
    private View parentView;
    private View view;
    private Button mBtCamera,mBtPhoto,mBtCancel;
    private EditText mEtCentext;

    TencentLocationRequest request;
    TencentLocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_publish_life, null);
        setContentView(parentView);
        initLocation();
        findView();
        initData();
        addAction();
    }

    private void initLocation() {
        //定位服务
        request = TencentLocationRequest.create();
        // set请求周期
        request.setInterval(5000);
        // 设置定位的 request level
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        request.setAllowCache(true);
        locationManager = TencentLocationManager.getInstance(this);
        int error = locationManager.requestLocationUpdates(request, this);
    }
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String arg2) {
        // TODO Auto-generated method stub
        if (TencentLocation.ERROR_OK == i) {
            // 定位成功
            try {
                String lat = String.valueOf(tencentLocation.getLatitude());
                String lon = String.valueOf(tencentLocation.getLongitude());
                Log.i(TAG, "维度" + lat + "经度" + lon + "具体地址" + tencentLocation.getAddress());

            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // 定位失败
            Log.i(TAG,"定位失败");
        }

    }
    @Override
    public void onStatusUpdate(String arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub

    }

    private void findView() {
        mTvBack = (TextView) findViewById(R.id.back_tv);
        mTvTitle = (TextView) findViewById(R.id.message_title);
        mIvRight = (ImageView) findViewById(R.id.iv_new_contact);
        mGv = (GridView) findViewById(R.id.gridView);
        view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop = new PopupWindow(this);
        mBtCamera = (Button) view.findViewById(R.id.item_popupwindows_camera);
        mBtPhoto = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        mBtCancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        mEtCentext = (EditText) findViewById(R.id.offline_feedback_content_et);

    }

    private void initData() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setText("<生活圈");
        mTvTitle.setText("");
        mIvRight.setVisibility(View.VISIBLE);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        mGv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        mGv.setAdapter(adapter);
        mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    KeyBoardUtils.closeKeybord(mEtCentext,PublishLifeActivity.this);
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(PublishLifeActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(PublishLifeActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    private void addAction() {
        mTvBack.setOnClickListener(new MyOnClickListener());
        mIvRight.setOnClickListener(new MyOnClickListener());
        mBtCamera.setOnClickListener(new MyOnClickListener());
        mBtPhoto.setOnClickListener(new MyOnClickListener());
        mBtCancel.setOnClickListener(new MyOnClickListener());

    }

    private class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_tv://返回
                    finish();
                    break;
                case R.id.iv_new_contact://发布
                    KeyBoardUtils.closeKeybord(mEtCentext,PublishLifeActivity.this);
                    release();
                    break;
                case R.id.item_popupwindows_camera://拍照
                    photo();
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    break;
                case R.id.item_popupwindows_Photo://相册
                    Intent intent = new Intent(PublishLifeActivity.this,AlbumActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    break;
                case R.id.item_popupwindows_cancel://取消
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    break;
            }
        }
    }

    private void release() {
        final String text = mEtCentext.getText().toString();
        //判断是否有图片
        if(Bimp.tempSelectBitmap.size()>0){
            List<String> list = new ArrayList<String>();
            for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
                Bitmap bit = getimage(Bimp.tempSelectBitmap.get(i).getImagePath());
                list.add(Bimp.tempSelectBitmap.get(i).getImagePath());
            }
            String[] files =  list.toArray(new String[1]); //返回一个包含所有对象的指定类型的数组

            BmobProFile.getInstance(this).uploadBatch(files, new UploadBatchListener() {
                @Override
                public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {
                    Log.i(TAG, "boolean ==== ");
                    if(isFinish) {
                        ShengHuoQuan shengHuoQuan = new ShengHuoQuan();
                        shengHuoQuan.setText(text);
                        List<String> listImage = new ArrayList<String>();
                        for (BmobFile file : files) {
                            listImage.add(file.getUrl());
                        }
                        Log.i(TAG, "listImage.size=====" + listImage.size());
                        shengHuoQuan.setImage_url(listImage);
                        shengHuoQuan.setLocation(new BmobGeoPoint(116.39727786183357, 39.913768382429105));
                        BmobUser bmobUser = BmobUser.getCurrentUser(getApplicationContext());
                        shengHuoQuan.setPublisher(bmobUser);
                        shengHuoQuan.setUsername(bmobUser.getUsername());
                        shengHuoQuan.save(PublishLifeActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                T.show(PublishLifeActivity.this, "添加数据成功!", 1);
                                PublishLifeActivity.this.finish();
                            }

                            @Override
                            public void onFailure(int code, String arg0) {
                                // TODO Auto-generated method stub
                                // 添加失败
                                Log.i(TAG, "添加失败!code==" + code + "---------str==" + arg0);
//                                T.show(PublishLifeActivity.this, "添加失败!", 1);
                            }
                        });
                    }
                    // isFinish ：批量上传是否完成
                    // fileNames：文件名数组
                    // urls        : url：文件地址数组
                    // files     : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
                    //注：若上传的是图片，url(s)并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`版本可直接从BmobFile中获得可访问的文件地址。
                }

                @Override
                public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                    // curIndex    :表示当前第几个文件正在上传
                    // curPercent  :表示当前上传文件的进度值（百分比）
                    // total       :表示总的上传文件数
                    // totalPercent:表示总的上传进度（百分比）
                    Log.i(TAG, "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "批量上传出错：" + statuscode + "--" + errormsg);
                }
            });
        }else{
            ShengHuoQuan shengHuoQuan = new ShengHuoQuan();
            shengHuoQuan.setText(text);
            shengHuoQuan.setLocation(new BmobGeoPoint(116.39727786183357, 39.913768382429105));
            BmobUser bmobUser = BmobUser.getCurrentUser(this);
            shengHuoQuan.setPublisher(bmobUser);
            shengHuoQuan.setUsername(bmobUser.getUsername());
            shengHuoQuan.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    T.show(PublishLifeActivity.this, "添加数据成功!", 1);
                    PublishLifeActivity.this.finish();
                }

                @Override
                public void onFailure(int code, String arg0) {
                    // TODO Auto-generated method stub
                    // 添加失败
//                    T.show(PublishLifeActivity.this, "添加失败!======"+arg0, 1);
                    Log.i(TAG,"添加失败!code=="+code+"---------str=="+arg0);
                }
            });
        }

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 8){
                return 8;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 8) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 8 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


}
