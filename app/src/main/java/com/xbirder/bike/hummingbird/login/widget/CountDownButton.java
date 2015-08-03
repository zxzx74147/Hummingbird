package com.xbirder.bike.hummingbird.login.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by zhengxin on 15/8/3.
 */
public class CountDownButton extends Button {
    private long dstTime;
    private String mText;



    public CountDownButton(Context context) {
        super(context);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startCountDown(long dstTime){
        this.dstTime = dstTime;
        mCalContentRunnable.run();
    }

    public void setTextString(String text){
        super.setText(text);
        mText = text;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

    }

    public boolean isCountDown(){
        if(System.currentTimeMillis()>dstTime) {
            return false;
        }
        return true;
    }


    private Runnable mCalContentRunnable = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis()>dstTime){
                setText(mText);
            }else{
                int diff = (int) (dstTime-System.currentTimeMillis()/1000);
                setText(mText+"("+diff+"s)");
                postDelayed(mCalContentRunnable,1000);
            }
        }
    };

    @Override
    public void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        removeCallbacks(mCalContentRunnable);
    }



}
