package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;

/**
 * Created by Administrator on 2015/8/22.
 */
public class MySetting extends Activity{

    private String[] sex = new String[]{"男","女"};
    private boolean[] sexState=new boolean[]{true, false};
    private RadioOnClick radioOnClick = new RadioOnClick(1);
    private ListView sexRadioListView;
    private TextView my_head_portrait;
    private RelativeLayout my_setting_name;
    private Button my_setting_sex;
    private TextView tv_new_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_information);
        my_head_portrait = (TextView)findViewById(R.id.my_head_portrait);
        my_setting_name = (RelativeLayout)findViewById(R.id.my_setting_name);
        tv_new_name = (TextView)findViewById(R.id.tv_new_name);
        if (tv_new_name.getText() == null ){
            tv_new_name.setText(AccountManager.sharedInstance().getNickname());
        }
        tv_new_name.setText(AccountManager.sharedInstance().getNickname());
        my_setting_sex = (Button)findViewById(R.id.my_setting_sex);

        my_head_portrait.setOnClickListener(mOnClickListener);
        my_setting_name.setOnClickListener(mOnClickListener);
        my_setting_sex.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (v == my_head_portrait){
                return;
            }else if (v == my_setting_name){
                Intent intent = new Intent(MySetting.this,SettingName.class);
                startActivityForResult(intent,100);
            }else if (v == my_setting_sex){
                AlertDialog ad = new AlertDialog.Builder(MySetting.this).setTitle("选择性别").
                        setSingleChoiceItems(sex,radioOnClick.getIndex(),radioOnClick).create();
                sexRadioListView = ad.getListView();
                ad.show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (20 == resultCode){
            String newName = data.getExtras().getString("str");
            AccountManager.sharedInstance().setNickName(newName);
            tv_new_name.setText(newName);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class RadioOnClick implements DialogInterface.OnClickListener{
        private int index;

        public RadioOnClick(int index){
            this.index = index;
        }

        public void setIndex(int index){
            this.index = index;
        }

        public int getIndex(){
            return index;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setIndex(i);
            Toast.makeText(MySetting.this, sex[index], Toast.LENGTH_LONG).show();
            my_setting_sex.setText(sex[index]);
            dialogInterface.dismiss();
        }
    }
}
