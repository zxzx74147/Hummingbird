package com.xbirder.bike.hummingbird.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.util.StringHelper;

/**
 * Created by zhengxin on 2015/7/9.
 */
public class TitleBar extends RelativeLayout {
    private TextView mLeftText;
    private TextView mTitleText;
    private TextView mRigntText;
    public TitleBar(Context context) {
        super(context);
        init(null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.title_bar_layout,this);
        mLeftText = (TextView) findViewById(R.id.title_bar_back);
        mTitleText = (TextView) findViewById(R.id.title_bar_center);
        mRigntText = (TextView) findViewById(R.id.title_bar_right);
        if(attrs !=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
            String backText = typedArray.getString(R.styleable.TitleBar_back_text);
            String titleText = typedArray.getString(R.styleable.TitleBar_title_text);
            String rightText = typedArray.getString(R.styleable.TitleBar_right_text);
            if(StringHelper.checkString(backText)){
                mLeftText.setText(backText);
            }
            if(StringHelper.checkString(titleText)){
                mTitleText.setText(titleText);
            }
            if(StringHelper.checkString(rightText)){
                mRigntText.setText(rightText);
            }
            typedArray.recycle();
        }
        mLeftText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    public void setTitle(String text){
        mTitleText.setText(text);
    }

    public void setTitle(int id){
        mTitleText.setText(id);
    }

    public void setLeftText(String text){
        mLeftText.setText(text);
    }

    public void setLeftText(int id){
        mLeftText.setText(id);
    }

    public void setRigntOnClickListener(OnClickListener listener){
        mRigntText.setOnClickListener(listener);
    }

    public void setBackOnClickListener(OnClickListener listener){
        mLeftText.setOnClickListener(listener);
    }


}
