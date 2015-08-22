package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

/**
 * Created by Administrator on 2015/8/22.
 */
public class AboutXBirder extends Activity {


    private ImageView iv_xbirder_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about);
        //iv_xbirder_map = (ImageView)findViewById(R.id.iv_xbirder_map);

        //iv_xbirder_map.setOnClickListener(mOnClickListener);
    }

/*    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };*/

}
