package com.xbirder.bike.hummingbird.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.asyncTask.CommonUniqueId;
import com.baidu.core.net.base.HttpManager;
import com.baidu.core.net.base.HttpRequest;
import com.baidu.core.net.base.IHttpCancelable;

public class BaseFragment extends Fragment implements IHttpCancelable {

	public Context context;
	private CommonUniqueId mUniqueId = CommonUniqueId.gen();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
	}
	private Vibrator vibrator;

	private void startVibrato() { // 定义震动
		vibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1); // 第一个｛｝里面是节奏数组，
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		HttpManager.cancelAll(mUniqueId);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
//		mShakeListener.setOnShakeListener(null);

	}

	@Override
	public void onResume() {
		super.onResume();
//		initShake();

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}






	@Override
	public CommonUniqueId getUniqueId() {
		return mUniqueId;
	}

	protected void sendRequest(HttpRequest request,boolean isLoadCache){
		HttpManager.sendRequest(mUniqueId,request,isLoadCache);
	}

	protected void sendRequest(HttpRequest request){
		sendRequest(request,false);
	}
}
