package com.xbirder.bike.hummingbird.common.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhengxin on 14-7-30.
 */
public class FixViewPager extends ViewPager {
    public FixViewPager(Context context) {
        super(context);
    }

    public FixViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;//super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}
