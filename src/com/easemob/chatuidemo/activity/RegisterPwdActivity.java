package com.easemob.chatuidemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.Follow;
import com.easemob.chatuidemo.utils.T;
import com.easemob.exceptions.EaseMobException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RegisterPwdActivity extends BaseActivity {

    public static final String TAG = "RegisterPwdActivity";
    private TextView mTvBack,title;
    private EditText pwd1,pwd2,plaseCode;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pwd);
        initView();
        initData();
    }

    private void initView() {
        mTvBack = (TextView) findViewById(R.id.back_tv);
        title = (TextView) findViewById(R.id.message_title);
        pwd1 = (EditText) findViewById(R.id.et_pwd1);
        pwd2 = (EditText) findViewById(R.id.et_pwd2);
        plaseCode = (EditText) findViewById(R.id.et_plase_code);
    }

    private void initData() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setText("<注册");
        phone = getIntent().getStringExtra("phone");
    }

    /**
     * 注册
     * @param view
     */
    public void registered(View view){
        final String pass1 = pwd1.getText().toString().trim();
        final String pass2 = pwd2.getText().toString().trim();
        checkpwd(pass1,pass2);//检验密码是否正确
        BmobUser bu = new BmobUser();
        bu.setUsername(phone);
        bu.setPassword(pass1);
        bu.setMobilePhoneNumber(phone);
        //注意：不能用save方法进行注册
        bu.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass1)) {
                    final ProgressDialog pd = new ProgressDialog(RegisterPwdActivity.this);
                    pd.setMessage(getResources().getString(R.string.Is_the_registered));
                    pd.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                // 调用sdk注册方法
                                EMChatManager.getInstance().createAccountOnServer(phone, pass1);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!RegisterPwdActivity.this.isFinishing())
                                            pd.dismiss();
                                        // 保存用户名
                                        DemoApplication.getInstance().setUserName(phone);
                                        BmobUser bmobUser = BmobUser.getCurrentUser(RegisterPwdActivity.this);
                                        Follow follow = new Follow();
                                        //注意：不能调用follow.setObjectId("")方法
                                        follow.setUserObjectId(bmobUser.getObjectId());
                                        follow.save(RegisterPwdActivity.this, new SaveListener() {

                                            @Override
                                            public void onSuccess() {
                                                // TODO Auto-generated method stub
                                                T.show(RegisterPwdActivity.this, getResources().getString(R.string.Registered_successfully), 1);
                                            }

                                            @Override
                                            public void onFailure(int code, String arg0) {
                                                // TODO Auto-generated method stub
                                                // 添加失败
                                            }
                                        });

//                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), 0).show();
                                        finish();
                                    }
                                });
                            } catch (final EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!RegisterPwdActivity.this.isFinishing())
                                            pd.dismiss();
                                        int errorCode=e.getErrorCode();
                                        if(errorCode== EMError.NONETWORK_ERROR){
                                            T.show(RegisterPwdActivity.this, getResources().getString(R.string.network_anomalies), 1);
                                        }else if(errorCode == EMError.USER_ALREADY_EXISTS){
                                            T.show(RegisterPwdActivity.this, getResources().getString(R.string.User_already_exists), 1);
                                        }else if(errorCode == EMError.UNAUTHORIZED){
                                            T.show(RegisterPwdActivity.this, getResources().getString(R.string.registration_failed_without_permission), 1);
                                        }else if(errorCode == EMError.ILLEGAL_USER_NAME){
                                            T.show(RegisterPwdActivity.this, getResources().getString(R.string.illegal_user_name), 1);
                                        }else{
                                            T.show(RegisterPwdActivity.this, getResources().getString(R.string.Registration_failed) + e.getMessage(), 1);
                                        }
                                    }
                                });
                            }
                        }
                    }).start();

                }
               /* T.show(RegisterPwdActivity.this, "注册成功!", 1);
                finish();*/
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                T.show(RegisterPwdActivity.this, "注册失败:" + msg, 1);
            }
        });
    }

    private void checkpwd(String pass1, String pass2) {
        if(pass1.equals("") || pass1.length()==0){
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_LONG).show();
            return ;
        }
        if(pass2.equals("") || pass2.length()==0){
            Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_LONG).show();
            return ;
        }
        if(!pass1.equals(pass2)){
            Toast.makeText(this, "两次密码不一致！", Toast.LENGTH_LONG).show();
            return ;
        }
    }

}
