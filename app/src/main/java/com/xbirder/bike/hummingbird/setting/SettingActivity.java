package com.xbirder.bike.hummingbird.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.LogoActivity;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothManager;
import com.xbirder.bike.hummingbird.register.ChangePassWord;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;

import cn.smssdk.SMSSDK;

public class SettingActivity extends AppCompatActivity {
    private Button quitBtn = null;
    private TextView change_pwd;
    private TextView phone_number;
    private TextView about;
    private TextView resetView = null;
    private LinearLayout cell_phone_number;
    private CustomAlertDialog customAlertDialog;
    private TextView tv_title;
    private EditText et_old_pwd;
    private Button bt_make_sure;
    private Button bt_cancel;
    private String storePass;
    private String get_et_old_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        quitBtn = (Button) findViewById(R.id.quit_login);
        quitBtn.setOnClickListener(mOnClickListener);
        cell_phone_number = (LinearLayout) findViewById(R.id.cell_phone_number);
        phone_number = (TextView) findViewById(R.id.phone_number);
        phone_number.setText(AccountManager.sharedInstance().getUser());
        phone_number.setGravity(Gravity.RIGHT);
        cell_phone_number.setOnClickListener(mOnClickListener);
        resetView = (TextView) findViewById(R.id.resetXBird);
        resetView.setOnClickListener(mOnClickListener);
        change_pwd = (TextView) findViewById(R.id.change_pwd);
        change_pwd.setOnClickListener(mOnClickListener);
        about = (TextView) findViewById(R.id.about);
        about.setOnClickListener(mOnClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == quitBtn) {
                AccountManager.sharedInstance().setPass("");
                AccountManager.sharedInstance().setFinalToken("");
                finish();
                ActivityJumpHelper.startActivity(SettingActivity.this, LogoActivity.class);
            } else if (v == resetView) {
                boolean isConnect = HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect();
//                if (isConnect) {
//                    byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
//                    HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
//                } else {
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "请先连接锋鸟电动车", Toast.LENGTH_LONG);
//                    toast.show();
//                }
                byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
                HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
            } else if (v == change_pwd) {
                showDialog();
            } else if (v == cell_phone_number) {
                ActivityJumpHelper.startActivity(SettingActivity.this, MySetting.class);
            } else if (v == about) {
                ActivityJumpHelper.startActivity(SettingActivity.this, AboutXBirder.class);
            }else if (v == change_pwd){
                ActivityJumpHelper.startActivity(SettingActivity.this,ChangePassWord.class);
            }else if (v == cell_phone_number){
                ActivityJumpHelper.startActivity(SettingActivity.this,MySetting.class);
            }
            else if (v == about){
                ActivityJumpHelper.startActivity(SettingActivity.this,AboutXBirder.class);
            }
        }
    };

    public void showDialog() {
        customAlertDialog = new CustomAlertDialog(SettingActivity.this);
        customAlertDialog.showDialog(R.layout.custom_alert_dialog, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    customAlertDialog.dismissDialog();
                }
            }

            @Override
            public void showWindowDetail(Window window) {
                et_old_pwd = (EditText) window.findViewById(R.id.et_old_pwd);
                bt_make_sure = (Button) window.findViewById(R.id.bt_make_sure);
                bt_make_sure.setText("下一步");
                bt_make_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storePass = AccountManager.sharedInstance().getPass();//获取首选项里的密码.
                        get_et_old_pwd = et_old_pwd.getText().toString();
                        if (!storePass.equals(get_et_old_pwd)){
                            Toast.makeText(SettingActivity.this,"旧密码输入有误,请重新输入",Toast.LENGTH_LONG).show();
                        }else {
                            customAlertDialog.dismissDialog();
                            ActivityJumpHelper.startActivity(SettingActivity.this,ChangePassWord.class);
                        }
                    }
                });
                bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
                bt_cancel.setText("取消");
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customAlertDialog.dismissDialog();
                    }
                });
            }
        });
    }
}
