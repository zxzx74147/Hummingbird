package com.xbirder.bike.hummingbird.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by zhengxin on 15/5/30.
 */
public abstract class BaseView<T> {

    protected BaseActivity mActivity;
    protected LayoutInflater mInflater;
    protected View mRootView;
    protected T mData;

    public BaseView(BaseActivity activity, int id) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        mRootView = mInflater.inflate(id, null);
    }

    public BaseView(BaseActivity activity,View rootView) {
        mActivity = activity;
        mRootView = rootView;
    }

    public View getRootView() {
        return mRootView;
    }

    public void setData(T data) {
        mData = data;
    }

    public void notifyDataSetChanged() {

    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

}
