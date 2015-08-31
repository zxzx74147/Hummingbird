package com.xbirder.bike.hummingbird.setting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;

import com.xbirder.bike.hummingbird.R;

/**
 * Created by Administrator on 2015/8/27.
 */
public class SmoothImageView extends View {
    private Drawable image;
    private int SmoothControler = 20;

    public SmoothImageView(Context context) {
        super(context);
        image = context.getResources().getDrawable(R.drawable.xbirder_map);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //控制图像的宽度和高度
        image.setBounds((getWidth() / 2) - SmoothControler, (getHeight() / 2) - SmoothControler, (getWidth() / 2) + SmoothControler, (getHeight() / 2) + SmoothControler);
        image.draw(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)//放大
            SmoothControler += 10;

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) //缩小
            SmoothControler -= 10;

        if (SmoothControler < 10)
            SmoothControler = 10;

        invalidate();
        return true;
    }

}
