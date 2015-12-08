package com.xbirder.bike.hummingbird.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by zhhz on 15/12/7.
 */
public class MyVideoView extends VideoView {

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context) {
        super(context);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        return false;
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        
        //int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        int width = height * 1920 / 1080;

        setMeasuredDimension(width, height);
    }



}
