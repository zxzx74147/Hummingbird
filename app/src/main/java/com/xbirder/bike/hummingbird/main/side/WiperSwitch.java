package com.xbirder.bike.hummingbird.main.side;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/11.
 */
public class WiperSwitch extends View implements View.OnTouchListener{

    private Bitmap switchOnBkg; // 开关开启时的背景
    private Bitmap switchOffBkg; // 开关关闭时的背景
    private Bitmap slipSwitchButton; // 滑动开关的图片

    private boolean isSlipping = false; // 是否正在滑动
    private boolean isSwitchOn = false; // 当前开关的状态，true表示开启，flase表示关闭
    private float previousX; // 手指按下时的水平坐标x
    private float currentX; // 当前的水平坐标X
    float leftSlipBtnX; // 滑动按钮的左边坐标

    private ArrayList<OnSwitchListener> onSwitchListenerList; // 开关监听器列表

    public WiperSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setOnTouchListener(this); // 设置触摸监听器
        onSwitchListenerList = new ArrayList<OnSwitchListener>();
    }

    public void setImageResource(int switchBkg, int slipBtn) {
        switchOnBkg = BitmapFactory.decodeResource(this.getResources(),
                switchBkg);
        switchOffBkg = BitmapFactory.decodeResource(this.getResources(),
                switchBkg);
        slipSwitchButton = BitmapFactory.decodeResource(this.getResources(),
                slipBtn);

    }

    public void setIsSlipping(boolean isSlipping){
        this.isSlipping = isSlipping;
    }

    public boolean getIsSlipping(){
        return isSlipping;
    }

    public void setLeftSlipBtnX(float leftSlipBtnX){
        this.leftSlipBtnX = leftSlipBtnX;
    }

    public float getLeftSlipBtnX(){
        return leftSlipBtnX;
    }

    public void setSwitchState(boolean switchState) {
        this.isSwitchOn = switchState;
        this.invalidate();
    }

    public boolean getSwitchState() {
        return this.isSwitchOn;
    }

    public void setOnSwitchStateListener(OnSwitchListener listener){
        onSwitchListenerList.add(listener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Matrix matrix = new Matrix();
        Paint paint = new Paint();

        //画开关的背景图片
        canvas.drawBitmap(switchOnBkg, matrix, paint);

        if (isSlipping) {
            // 如果正在滑动
            if (currentX > switchOnBkg.getWidth()) {
                leftSlipBtnX = switchOnBkg.getWidth()
                        - slipSwitchButton.getWidth();
            } else {
                leftSlipBtnX = currentX - slipSwitchButton.getWidth();
            }
        } else {
            //如果没有滑动
            if (isSwitchOn) {
                leftSlipBtnX = switchOnBkg.getWidth()
                        - slipSwitchButton.getWidth();
            } else {
                leftSlipBtnX = 0;
            }
        }

        //如果手指滑出了开关的范围，应当这样处理
        if (leftSlipBtnX < 0) {
            leftSlipBtnX = 0;
        } else if (leftSlipBtnX > switchOnBkg.getWidth()
                - slipSwitchButton.getWidth()) {
            leftSlipBtnX = switchOnBkg.getWidth() - slipSwitchButton.getWidth();
        }

        //在画布上画开关图片
        canvas.drawBitmap(slipSwitchButton, leftSlipBtnX, 0, paint);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(switchOnBkg.getWidth(), switchOnBkg.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //获取触摸动作类型
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                //如果现在处于手指一动状态
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_DOWN:
                //如果现在手指刚刚按上屏幕状态
                isSlipping = false;
                break;
            case MotionEvent.ACTION_UP:
                //如果现在手指刚刚离开屏幕状态
                isSlipping = false;
                boolean previousState = isSwitchOn;
                if (event.getX() > (switchOnBkg.getWidth() / 2)) {
                    isSwitchOn = true;
                } else {
                    isSwitchOn = false;
                }

                //调用接口回调方法，将开关状态通知给监听对象
                if(previousState != isSwitchOn){
                    if(onSwitchListenerList.size() > 0){
                        for(OnSwitchListener listener : onSwitchListenerList){
                            listener.onSwitched(isSwitchOn);
                        }
                    }
                }
                break;

            default:
                break;
        }

        this.invalidate();
        return true;
    }

    public interface OnSwitchListener{
        public abstract void onSwitched(boolean isSwitchOn);
    }
}
