package com.xbirder.bike.hummingbird.setting;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.LogoActivity;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;

public class SettingActivity extends BaseActivity {
    private Button quitBtn = null;
//    private TextView change_pwd;
//    private TextView phone_number;
//    private TextView about;
//    private TextView resetView = null;
//    private LinearLayout cell_phone_number;

    private TextView resetXBird;
    private TextView generalOptions;
    private TextView detectionUpdate;
    private TextView useHelp;
    private TextView feedback;
    private TextView about;

    private CustomAlertDialog customAlertDialog;
    private CustomAlertDialog detectionUpdateCustomAlertDialog;
   // private CustomAlertDialog detectionUpdateRunCustomAlertDialog;
    private TextView tv_title;
    private EditText et_old_pwd;
    private Button bt_make_sure;
    private Button bt_cancel;
    private String storePass;
    private String get_et_old_pwd;


//    private ProgressBar detectionUpdate_progressbar;
//    private TextView detectionUpdate_complete_lines;
//    private TextView detectionUpdate_total_lines;

    private String mBikeCurrentVersion;

   // private ArrayList<byte[]> mReadList = new ArrayList<byte[]>();

//    private int sendid = 0;
//    private int maxlines = 0;


//    final Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            //这里就一条消息
//            if(msg.arg1 == 1){
//                detectionUpdate();
//            }
//
//        }
//    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        quitBtn = (Button) findViewById(R.id.quit_login);
        quitBtn.setOnClickListener(mOnClickListener);

//        cell_phone_number = (LinearLayout) findViewById(R.id.cell_phone_number);
//        phone_number = (TextView) findViewById(R.id.phone_number);
//        phone_number.setText(AccountManager.sharedInstance().getUser());
//        phone_number.setGravity(Gravity.RIGHT);
//        cell_phone_number.setOnClickListener(mOnClickListener);
//        resetView = (TextView) findViewById(R.id.resetXBird);
//        resetView.setOnClickListener(mOnClickListener);
//        change_pwd = (TextView) findViewById(R.id.change_pwd);
//        change_pwd.setOnClickListener(mOnClickListener);
        resetXBird = (TextView) findViewById(R.id.resetXBird);
        resetXBird.setOnClickListener(mOnClickListener);

        generalOptions = (TextView) findViewById(R.id.generalOptions);
        generalOptions.setOnClickListener(mOnClickListener);

        detectionUpdate = (TextView) findViewById(R.id.detectionUpdate);
        detectionUpdate.setOnClickListener(mOnClickListener);

        useHelp = (TextView) findViewById(R.id.useHelp);
        useHelp.setOnClickListener(mOnClickListener);

        feedback = (TextView) findViewById(R.id.feedback);
        feedback.setOnClickListener(mOnClickListener);

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
            } else if (v == resetXBird) {
                boolean isConnect = HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect();
                if (isConnect) {
                    byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
                    HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
                } else {
                    toast("请先连接锋鸟电动车");
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "请先连接锋鸟电动车", Toast.LENGTH_LONG);
//                    toast.show();
                }

//                byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
//                HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
            }else if (v == generalOptions) {
                ActivityJumpHelper.startActivity(SettingActivity.this, GeneralOptionsActivity.class);
            }else if (v == detectionUpdate) {
                toast("该功能暂未开放，敬请期待！");
               //ActivityJumpHelper.startActivity(SettingActivity.this, DetectionUpdateActivity.class);

//                if(!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()){
//                    toast("请先连接锋鸟电动车");
//                    return;
//                }
//                mBikeCurrentVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
//                if(mBikeCurrentVersion.equals("1")){
//                    toast("当前版本暂不支持此功能！");
//                    return;
//                }
                //getVersions("1");
                //showDetectionUpdateDialog();
            }else if (v == useHelp) {
                ActivityJumpHelper.startActivity(SettingActivity.this, XBirderHelp.class);
            }else if (v == feedback) {
                ActivityJumpHelper.startActivity(SettingActivity.this, FeedbackActivity.class);
            }else if (v == about){
                ActivityJumpHelper.startActivity(SettingActivity.this,AboutXBirder.class);
            }
//            else if (v == resetView) {
//                boolean isConnect = HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect();
////                if (isConnect) {
////                    byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
////                    HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
////                } else {
////                    Toast toast = Toast.makeText(getApplicationContext(),
////                            "请先连接锋鸟电动车", Toast.LENGTH_LONG);
////                    toast.show();
////                }
//                byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
//                HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
//            } else if (v == change_pwd) {
//                showDialog();
//            } else if (v == cell_phone_number) {
//                ActivityJumpHelper.startActivity(SettingActivity.this, MySetting.class);
//            } else if (v == about) {
//                ActivityJumpHelper.startActivity(SettingActivity.this, AboutXBirder.class);
//            }else if (v == change_pwd){
//                ActivityJumpHelper.startActivity(SettingActivity.this,ChangePassWord.class);
//            }else if (v == cell_phone_number){
//                ActivityJumpHelper.startActivity(SettingActivity.this,MySetting.class);
//            }
//            else if (v == about){
//                ActivityJumpHelper.startActivity(SettingActivity.this,AboutXBirder.class);
//            }
        }
    };

}
