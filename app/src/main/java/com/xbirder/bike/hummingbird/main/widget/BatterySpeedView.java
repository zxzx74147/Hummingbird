package com.xbirder.bike.hummingbird.main.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xbirder.bike.hummingbird.R;


/**
 * Created by zhengxin on 15/7/13.
 */
public class BatterySpeedView extends ImageView {

    private static int ROLL_WIDTH = 0;
    private float mPercent = 10;

    private Paint mGrayPaint = new Paint();
    private Paint mColorPaint = new Paint();
    private RectF mArgRect = new RectF();


    public BatterySpeedView(Context context) {
        super(context);
        init();
    }

    public BatterySpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }

    public BatterySpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed,left,top,right,bottom);
        int width = right-left;
        int height = bottom-top;
        LinearGradient linearGradient = new LinearGradient(0,0,0,height, Color.BLACK, Color.WHITE, Shader.TileMode.REPEAT);
        LinearGradient linearGradient2 = new LinearGradient(0,0,0,height, Color.GREEN, Color.BLUE, Shader.TileMode.REPEAT);
        mGrayPaint.setShader(linearGradient);
        mColorPaint.setShader(linearGradient2);
    }


    private void init(){
        ROLL_WIDTH = getResources().getDimensionPixelSize(R.dimen.default_gap_10);
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        mGrayPaint.setColorFilter(new ColorMatrixColorFilter(grayMatrix));
        mGrayPaint.setStrokeCap(Paint.Cap.ROUND);
        mGrayPaint.setAntiAlias(true);
        mGrayPaint.setStrokeWidth(30);
        mGrayPaint.setStyle(Paint.Style.STROKE); //绘制空心圆

        mColorPaint.setStrokeCap(Paint.Cap.ROUND);
        mGrayPaint.setAntiAlias(true);
        mColorPaint.setStrokeWidth(30);
        mColorPaint.setStyle(Paint.Style.STROKE); //绘制空心圆

        mGrayPaint.setShadowLayer(8,0,5, Color.YELLOW);
        mColorPaint.setShadowLayer(8, 0, 5, Color.YELLOW);
    }

    /**
     * 0~100
     * @param percent
     */
    public void setPercent(float percent){
        mPercent = percent;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        mArgRect.set(ROLL_WIDTH/2,ROLL_WIDTH/2,width-ROLL_WIDTH/2,height-ROLL_WIDTH/2);
        canvas.drawArc(mArgRect, 0, 360, false, mGrayPaint);
        float end = -360 * mPercent/100;
        float start = -90;
        canvas.drawArc(mArgRect, start, end, false, mColorPaint);


    }
}
