package com.xbirder.bike.hummingbird.main.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xbirder.bike.hummingbird.R;

/**
 * Created by zhengxin on 2015/7/10.
 */
public class BatteryRollView extends ImageView {

    private Paint mImagePaint = null;
    private ColorFilter mFilter = null;
    private RectF mRect;
    private float mPercent = 0;

    public BatteryRollView(Context context) {
        super(context);
        init();
    }

    public BatteryRollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryRollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPercent(float percent){
        mPercent = percent;
        if(mPercent<=20){
            setImageResource(R.drawable.circle_down_battery_roll_red);
        }else{
            setImageResource(R.drawable.circle_down_battery_roll);
        }

        invalidate();
    }

    private void init(){
        ColorMatrix mColorMatrix = new ColorMatrix();
        mColorMatrix.setSaturation(0);
        mFilter = new ColorMatrixColorFilter(mColorMatrix);
        setColorFilter(mFilter);
        mImagePaint = new Paint();
        mImagePaint.setFilterBitmap(true);
        //mImagePaint.setAntiAlias(true);
        mImagePaint.setStrokeCap(Paint.Cap.ROUND);
        mImagePaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mRect = new RectF();
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(getDrawable() instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            Matrix matrix = getImageMatrix();
            float strokeWidth = getWidth()*0.056f;
            mRect.set(0.061f * getWidth() + strokeWidth/2,0.061f * getWidth() + strokeWidth/2,getWidth() - (0.061f * getWidth() + strokeWidth/2),getHeight() - (0.061f * getWidth() + strokeWidth/2));
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (matrix != null) {
                shader.setLocalMatrix(matrix);
            }
            mImagePaint.setStrokeWidth(strokeWidth);
            mImagePaint.setShader(shader);
            float end = -84 * mPercent/100;
            float start = -228;
            canvas.drawArc(mRect,start,end,false,mImagePaint);
        }

    }



}
