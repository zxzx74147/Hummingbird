package com.xbirder.bike.hummingbird.profile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;

/**
 * Created by zhengxin on 15/7/27.
 */
public class ProfileAdapter extends BaseAdapter {

    private LinkedList<Object> mData;
    @Override
    public int getCount() {
        return mData == null? 0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
