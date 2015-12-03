/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.Follow;
import com.easemob.chatuidemo.domain.MyUser;
import com.easemob.chatuidemo.utils.KeyBoardUtils;
import com.easemob.chatuidemo.utils.T;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddContactActivity extends BaseActivity{
	public static final String TAG = "AddContactActivity";
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText,mTextView;
	private Button searchBtn,indicatorBtn;
	private ImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	private final BmobQuery<MyUser> userQuery = new BmobQuery<MyUser>();
	private final BmobQuery<Follow> followQuery = new BmobQuery<Follow>();
	private String userId;
	private Follow follow;
	private View progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (ImageView) findViewById(R.id.avatar);
		indicatorBtn = (Button) findViewById(R.id.indicator);
		progressBar = (View)findViewById(R.id.progress_bar);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		KeyBoardUtils.closeKeybord(editText,this);
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				String st = getResources().getString(R.string.Please_enter_a_username);
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", st));
				return;
			}

			progressBar.setVisibility(View.VISIBLE);

			/**
			 * 先通输入的用户去查询User表
			 *
			 */
			userQuery.addWhereEqualTo("username", editText.getText().toString());
			userQuery.findObjects(this, new FindListener<MyUser>() {
				@Override
				public void onSuccess(List<MyUser> list) {
					//判断是否关注
					Log.i(TAG, "list.get(0).getObjectId()===" + list.get(0).getObjectId());
					userId = list.get(0).getObjectId();
					isFollow(list.get(0).getObjectId());
				}

				@Override
				public void onError(int i, String s) {

				}
			});

			
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			

			
		} 
	}	
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		if(getString(R.string.chat_room).equals(indicatorBtn.getText().toString())){
			//加关注
			progressDialog=new ProgressDialog(this);
			String stri = getResources().getString(R.string.Is_sending_a_request);
			progressDialog.setMessage(stri);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();

			Log.i(TAG, "userId===" + userId);
			follow.getMyFollows().add(userId);
			follow.update(this, follow.getObjectId(), new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					progressDialog.dismiss();
					T.show(AddContactActivity.this,"关注成功！",1);
					Log.i("bmob", "更新成功：");

				}

				@Override
				public void onFailure(int code, String msg) {
					// TODO Auto-generated method stub
					Log.i("bmob", "更新失败：" + msg);
				}
			});
			return;
		}






		if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
			String str = getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", str));
			return;
		}
		
		if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().containsKey(nameText.getText().toString())){
		    //提示已在好友列表中，无需添加
		    if(EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())){
		        startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
		        return;
		    }
			String strin = getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", strin));
			return;
		}









		new Thread(new Runnable() {
			 public void run () {

				 try {
					 //demo写死了个reason，实际应该让用户手动填入
					 String s = getResources().getString(R.string.Add_a_friend);
					 EMContactManager.getInstance().addContact(toAddUsername, s);
					 runOnUiThread(new Runnable() {
						 public void run() {
							 progressDialog.dismiss();
							 String s1 = getResources().getString(R.string.send_successful);
							 Toast.makeText(getApplicationContext(), s1, 1).show();
						 }
					 });
				 } catch (final Exception e) {
					 runOnUiThread(new Runnable() {
						 public void run() {
							 progressDialog.dismiss();
							 String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							 Toast.makeText(getApplicationContext(), s2 + e.getMessage(), 1).show();
						 }
					 });
				 }
			 }
		 }).start();
	}

	/**
	 * 获取当前用户
	 * 能过用户的ID查Follow表
	 * 查看myFollows里有没有这个人
	 * 如果存在，代表已关注
	 */
	private void isFollow(final String str){
		BmobUser bmobUser = BmobUser.getCurrentUser(this);
		followQuery.addWhereEqualTo("userObjectId", bmobUser.getObjectId());
		followQuery.findObjects(this, new FindListener<Follow>() {
			@Override
			public void onSuccess(List<Follow> list) {
				progressBar.setVisibility(View.GONE);
				//服务器存在此用户，显示此用户和添加按钮
				searchedUserLayout.setVisibility(View.VISIBLE);
				nameText.setText(toAddUsername);
				follow = list.get(0);
				/**
				 * 获取用户的关注
				 */
				for (String s : list.get(0).getMyFollows()) {
					if (!str.equals(s)) {
						//没有关注
						indicatorBtn.setText(R.string.chat_room);
					} else {
						/**
						 * 必须互相关注才可以添加好友
						 */
						for (String mes : list.get(0).getFollowMes()) {
							if (str.equals(mes)) {
								indicatorBtn.setText(R.string.add_friend);
							}
						}

					}
				}
			}

			@Override
			public void onError(int i, String s) {
			}
		});

	}



	public void back(View v) {
		finish();
	}
}
