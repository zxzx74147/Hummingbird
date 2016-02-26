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

import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;

/**
 * Created by zhengxin on 2015/7/10.
 */
public class VelocityRollView extends ImageView {

    private Paint mImagePaint = null;
    private Paint mPointPaint = null;
    private ColorFilter mFilter = null;
    private RectF mRect;
    private float mPercent = 0;


    public VelocityRollView(Context context) {
        super(context);
        init();
    }

    public VelocityRollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VelocityRollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPercent(float percent){
        mPercent = percent;
        invalidate();
    }

    private void init(){
        ColorMatrix mColorMatrix = new ColorMatrix();
        mColorMatrix.setSaturation(0);
        mFilter = new ColorMatrixColorFilter(mColorMatrix);
        setColorFilter(mFilter);
        mImagePaint = new Paint();
        mImagePaint.setFilterBitmap(true);
        mImagePaint.setStrokeCap(Paint.Cap.ROUND);
        mImagePaint.setStyle(Paint.Style.STROKE); //绘制空心圆


        mRect = new RectF();

        mPointPaint = new Paint();
        //TypedValue typedValue = new TypedValue();
        //mcontext.getTheme().resolveAttribute(R.attr., typedValue, true);
        //mPointPaint.setColor("?attr/main_text_value_color");
       // int mode = SkinManager.sharedInstance().getSkinMode();
//        if (mode == SkinConfig.SKIN_MODE_DAY) {
//
//
//        } else {
//
//        }
//
//
//        mPointPaint.setARGB(255,216,216,216);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(getDrawable() instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            Matrix matrix = getImageMatrix();
            //float strokeWidth = getWidth()*0.065f;
            float strokeWidth = getWidth()*0.056f;
            mRect.set(0.061f * getWidth() + strokeWidth/2,0.061f * getWidth() + strokeWidth/2,getWidth() - (0.061f * getWidth() + strokeWidth/2),getHeight() - (0.061f * getWidth() + strokeWidth/2));
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (matrix != null) {
                shader.setLocalMatrix(matrix);
            }
            mImagePaint.setStrokeWidth(strokeWidth);
            mImagePaint.setShader(shader);
            float end = 236 * mPercent/100;
            float start = -208;
            canvas.drawArc(mRect, start, end, false, mImagePaint);

            int lightPoint = 0;
            if (mPercent*236.0f/100 <=  10.00f) {
                lightPoint = 0;
            }else{
                lightPoint = (int) (((mPercent*236.0f/100 - 10.0f) / 27.0f) + 1.0f);
            }
           // Log.d("lightPoint",""+lightPoint);

            int mode = SkinManager.sharedInstance().getSkinMode();
            for(int i = 0; i < 9; i++) {
                if (mode == SkinConfig.SKIN_MODE_DAY) {
                        if(i == lightPoint){
                            mPointPaint.setARGB(255, 241, 90, 36);
                        }else{
                            mPointPaint.setARGB(255, 216, 216, 216);
                        }
                }else{
                        if(i == lightPoint){
                            mPointPaint.setARGB(255, 255, 255, 255);
                        }else{
                            mPointPaint.setARGB(255, 155, 155, 155);
                        }
                }

                 float x0 = (float) (getWidth() / 2 + (getWidth() / 2 - getWidth()*0.011f) * Math.cos((-198 + i*27)* 3.14f / 180));
                 float y0 = (float) (getWidth() / 2 + (getWidth() / 2 - getWidth()*0.011f) * Math.sin((-198 + i*27)* 3.14f / 180));
                 canvas.drawCircle(x0, y0, strokeWidth/5, mPointPaint);

            }



        }

    }



}
