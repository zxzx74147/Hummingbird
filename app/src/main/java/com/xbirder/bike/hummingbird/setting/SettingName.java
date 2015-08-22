package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;

/**
 * Created by Administrator on 2015/8/22.
 */
public class SettingName extends Activity {

    private TextView new_name;
    private TextView btn_succeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_name);
        new_name = (TextView)findViewById(R.id.new_name);
        btn_succeed = (Button)findViewById(R.id.btn_succeed);

        btn_succeed.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_succeed){
                String newName = new_name.getText().toString();
                if (newName == null){
                    Toast.makeText(SettingName.this,"用户名不能为空,请重新输入",Toast.LENGTH_LONG).show();
                }else {
                    Intent data = new Intent();
                    data.putExtra("str",newName);
                    setResult(20,data);
                    finish();
                }
            }
        }
    };
}
