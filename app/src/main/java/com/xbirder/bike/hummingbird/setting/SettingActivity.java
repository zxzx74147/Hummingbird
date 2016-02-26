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
//                if(!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()){
//                    toast("请先连接锋鸟电动车");
//                    return;
//                }
//                mBikeCurrentVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
//                if(mBikeCurrentVersion.equals("1")){
//                    toast("当前版本暂不支持此功能！");
//                    return;
//                }
                ActivityJumpHelper.startActivity(SettingActivity.this, DetectionUpdateActivity.class);
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



//    private void getVersions(String versionnums){
//        if(!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()){
//            toast("未连接锋鸟");
//            return;
//        }
//      mBikeCurrentVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
////        if(!StringHelper.checkString(mBikeCurrentVersion)){
////            toast("获取车身固件版本失败");
////            //return;
////        }else
//        if(mBikeCurrentVersion.equals("1")){
//            toast("当前版本暂不支持此功能！");
//            return;
//        }
//
//        //showDetectionUpdateING();
//
//        if(StringHelper.checkString(versionnums)) {
//            DetectionUpdateRequest request = new DetectionUpdateRequest(new HttpResponse.Listener<JSONObject>() {
//                @Override
//                public void onResponse(HttpResponse<JSONObject> response) {
//                    if (response.isSuccess()) {
//                        try {
//                            if (response.result.getInt("error") == 0) {
//                                //String accessToken = response.result.getJSONObject("user").getString("accessToken");
//                                String verNum = "";
//                                String fileURL = "";
//                                JSONArray datas = response.result.getJSONArray("versionList");
//                                if (datas != null) {
//                                    JSONObject temp = datas.getJSONObject(0);
//                                    verNum = temp.getString("verNum");
//                                    fileURL = temp.getString("file");
//                                    Intent mIntent = new Intent();
//                                    mIntent.setClass(SettingActivity.this, DetectionUpdateActivity.class);
//                                    mIntent.putExtra("LINE_VERSION", verNum);
//                                    mIntent.putExtra("CURRENT_VERSION", mBikeCurrentVersion);
//                                    mIntent.putExtra("FILE_URL", fileURL);
//                                    SettingActivity.this.startActivity(mIntent);
////                                    if(Integer.parseInt(verNum) > Integer.parseInt(mBikeCurrentVersion)){
////                                        //下载文件
////                                        filedown(NetworkConfig.SERVER_ADDRESS_DEV_HEAD + fileURL);
////                                        //Log.d("filedown",NetworkConfig.SERVER_ADDRESS_DEV_HEAD+fileURL);
////                                    }
//                                }
//                            } else {
//                                toast("获取固件信息失败");
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//            });
//            request.setParam(versionnums);
//            sendRequest(request);
//        }
//    }

//    private void filedown(String urlStr){
//        final String url = urlStr;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //另起线程执行下载，安卓最新sdk规范，网络操作不能再主线程。
//                MyDownload l = new MyDownload(url);
//                /**
//                 * 下载文件到sd卡，虚拟设备必须要开始设置sd卡容量
//                 * downhandler是Download的内部类，作为回调接口实时显示下载数据
//                 */
//                int status = l.down2sd("/xbird/", "update.xbird", l.new downhandler() {
//                    @Override
//                    public void fileOK() {
//                        Message msg = handler.obtainMessage();
//                        msg.arg1 = 1;
//                        msg.sendToTarget();
//                    }
//                });
//                //log输出
//                Log.d("log", Integer.toString(status));
//
//            }
//        }).start();
//
////
////
////
////        //String urlStr="http://172.17.54.91:8080/download/down.txt";
////        try {
////                /*
////                 * 通过URL取得HttpURLConnection
////                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
////                 * <uses-permission android:name="android.permission.INTERNET" />
////                 */
////
////            mReadList = null;
////            maxlines = 0;
////
////            URL url=new URL(urlStr);
////            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
////            //取得inputStream，并进行读取
////            InputStream input=conn.getInputStream();
////            BufferedReader buffreader=new BufferedReader(new InputStreamReader(input));
////
////            String line;
////            int checkNum = 0;
////            //分行读取
////            while (( line = buffreader.readLine()) != null) {
////                if(line.length() < 3){
////                    continue;
////                }
////                byte[] value = new byte[20];
////                value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
////                value[1] = (byte) (XBirdBluetoothConfig.DETECTION_DATA & 0xFF);
////                value[19] = (byte) (XBirdBluetoothConfig.END & 0xFF);
////
////                int checkvalue = 0;
////                String linehead = line.substring(0,3);
////                if(linehead.equals(":10")) {
////                    for (int i = 0; i < 16; i++) {
////                        String lineciel = line.substring(9+2*i,9+2*i+2);
////
////                        byte v= (byte) Integer.parseInt(lineciel, 16);
////                        value[i+2] = (byte) (v & 0xFF);
////
////                        int ilineciel = hexStringToInt(lineciel);
////                        checkvalue += ilineciel;
////
////                    }
////                    checkvalue = checkvalue%256;
////                    byte checkvalueByte = (byte) checkvalue;
////                    value[18] = (byte) (checkvalueByte & 0xFF);
////                    mReadList.add(value);
////                }else if(linehead.equals(":00")){
////                    //结束读取
////                    break;
////                }else{
////                    //跳过条目
////                }
////            }
////        maxlines = mReadList.size();
////        if(maxlines >0){
////            detectionUpdate_total_lines.setText("/"+maxlines);
////            detectionUpdate_progressbar.setMax(maxlines);
////            detectionUpdateStart();
////
////        }
////
////        } catch (MalformedURLException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    private void updatePercent(int numid){
//        detectionUpdate_complete_lines.setText(""+numid);
//        detectionUpdate_progressbar.setProgress(numid);
//
//    }
//
//    private void showDetectionUpdateING(){
//        detectionUpdateRunCustomAlertDialog = new CustomAlertDialog(SettingActivity.this);
//        detectionUpdateRunCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_update_run, new CustomAlertDialog.IHintDialog() {
//            @Override
//            public void onKeyDown(int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK){
//                    //detectionUpdateRunCustomAlertDialog.dismissDialog();
//                }
//            }
//            @Override
//            public void showWindowDetail(Window window) {
//                detectionUpdate_progressbar = (ProgressBar) window.findViewById(R.id.progressbar);
//                detectionUpdate_complete_lines = (TextView) window.findViewById(R.id.complete_lines);
//                detectionUpdate_total_lines = (TextView) window.findViewById(R.id.total_lines);
//            }
//        });
//    }
//
//    private void detectionUpdate(){
//       // showDetectionUpdateING();
//
//        //String fileName = "osfile"; //文件名字
//        String fileName = Environment.getExternalStorageDirectory()+"/xbird/update.xbird";
//
//        try{
//            //ArrayList<String> mReadList = new ArrayList<String>();
//            //得到资源中的asset数据流
//            FileInputStream instream = openFileInput(fileName);
//            //InputStream instream = getResources().getAssets().open(fileName);
//
//            if (instream != null)
//            {
//                InputStreamReader inputreader = new InputStreamReader(instream);
//                BufferedReader buffreader = new BufferedReader(inputreader);
//                String line;
//                int checkNum = 0;
//
//                //分行读取
//                while (( line = buffreader.readLine()) != null) {
//                    if(line.length() < 3){
//                        continue;
//                    }
//                    byte[] value = new byte[20];
//                    value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
//                    value[1] = (byte) (XBirdBluetoothConfig.DETECTION_DATA & 0xFF);
//                    value[19] = (byte) (XBirdBluetoothConfig.END & 0xFF);
//
//                    int checkvalue = 0;
//
//                    String linehead = line.substring(0,3);
//                    if(linehead.equals(":10")) {
//                        for (int i = 0; i < 16; i++) {
//                            String lineciel = line.substring(9+2*i,9+2*i+2);
//                            //String lineciel2 = line.substring(9+2*i+1,9+2*i+2);
//                            //Integer.parseInt(lineciel, 16);
////                            byte devBin = (byte) Integer.parseInt("0x"+lineciel, 16);
////
////                            Log.d("aa", "aa");
//                            //byte value5 = (byte) 0xC8;
//                            //int v1 = Integer.parseInt(lineciel);
//                            byte v= (byte) Integer.parseInt(lineciel, 16);
//                            value[i+2] = (byte) (v & 0xFF);
//
//                            int ilineciel = hexStringToInt(lineciel);
//                            checkvalue += ilineciel;
////                            bytes[i] = (byte) ilineciel;
//
//                            }
//                        checkvalue = checkvalue%256;
//                        byte checkvalueByte = (byte) checkvalue;
//                        value[18] = (byte) (checkvalueByte & 0xFF);
//                        mReadList.add(value);
//                    }else if(linehead.equals(":00")){
//                    //结束读取
//                     break;
//                    }else{
//                        //跳过条目
//                    }
//                }
//                instream.close();
//            }
//             maxlines = mReadList.size();
//            if(maxlines >0){
//                detectionUpdate_total_lines.setText("/"+maxlines);
//                detectionUpdate_progressbar.setMax(maxlines);
//                detectionUpdateStart();
//
//            }
//            //res = EncodingUtils.getString(buffer, "UTF-8");
//        }catch(Exception e){
//
//            e.printStackTrace();
//
//        }
//
//    }
//    private void detectionUpdateStart() {
//        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_START, XBirdBluetoothConfig.END};
//        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
//    }
//
//    private void detectionUpdateING(int t) {
//        byte[] value =  mReadList.get(t);
//        //byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_DATA, XBirdBluetoothConfig.END};
//        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
//    }
//
//    private void detectionUpdateEnd() {
//        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_END, XBirdBluetoothConfig.END};
//        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
//    }
//
//
//
//    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                read(bytes);
//            }
//        }
//    };
//    private void read(byte[] bytes) {
//        if (bytes == null || bytes.length < 3) return;
//        if (bytes[0] == XBirdBluetoothConfig.PREFIX && bytes[bytes.length - 1] == XBirdBluetoothConfig.END) {
//            switch (bytes[1]) {
//                case XBirdBluetoothConfig.DETECTION_START:
//                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
//                        sendid = 0;
//                        detectionUpdateING(sendid);
//                        //updatePercent(sendid);
//                        sendid++;
//                        Log.d("ceshi","刷新固件开始");
//                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
//                        detectionUpdateRunCustomAlertDialog.dismissDialog();
//                        toast("刷新固件失败");
//                        Log.d("ceshi", "刷新固件开始");
//                    }
//                    break;
//                case XBirdBluetoothConfig.DETECTION_DATA:
//                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
//                        Log.d("ceshi","刷新固件开始ing");
//                        if(mReadList.size() > sendid) {
//
//                            detectionUpdateING(sendid);
//                            updatePercent(sendid);
//                            sendid++;
//
//                        }else{
//                            detectionUpdateEnd();
//                        }
//                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
//                        detectionUpdateRunCustomAlertDialog.dismissDialog();
//                        toast("刷新固件失败");
//                    }
//                    break;
//                case XBirdBluetoothConfig.DETECTION_END:
//                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
//                        Log.d("ceshi","刷新固件成功");
//                        toast("刷新固件成功");
//                        detectionUpdateRunCustomAlertDialog.dismissDialog();
//                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
//                        detectionUpdateRunCustomAlertDialog.dismissDialog();
//                        toast("刷新固件失败");
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//    public int hexStringToInt(String str){
////        String lineciel1 = str.substring(0,1);
////        String lineciel2 = str.substring(1,2);
//////        int value1 =Integer.parseInt(str.substring(0, 1));//49
//////        int value2 =Integer.parseInt(str.substring(1, 2));//49
//
//
//        char[] chars = str.toCharArray();
//        int valueset1 = (int) chars[0];//49
//        int valueset2 = (int) chars[1];//49
//
//
//
//        int int_ch1;
//        if(valueset1 >= 48 && valueset1 <= 57)
//            int_ch1 = (valueset1-48)*16;   //// 0 的Ascll - 48
//        else if(valueset1 >= 65 && valueset1 <=70)
//            int_ch1 = (valueset1-55)*16; //// A 的Ascll - 65
//        else
//            int_ch1 = (valueset1-87)*16; //// a 的Ascll - 97
//
//        int int_ch2;
//        if(valueset2 >= 48 && valueset2 <= 57)
//            int_ch2 = (valueset2-48);   //// 0 的Ascll - 48
//        else if(valueset2 >= 65 && valueset2 <=70)
//            int_ch2 = (valueset2-55); //// A 的Ascll - 65
//        else
//            int_ch2 = (valueset2-87); //// a 的Ascll - 97
//
//        return int_ch1 + int_ch2;
//    }

//    public void showDialog() {
//        customAlertDialog = new CustomAlertDialog(SettingActivity.this);
//        customAlertDialog.showDialog(R.layout.custom_alert_dialog, new CustomAlertDialog.IHintDialog() {
//            @Override
//            public void onKeyDown(int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK){
//                    customAlertDialog.dismissDialog();
//                }
//            }
//
//            @Override
//            public void showWindowDetail(Window window) {
//                et_old_pwd = (EditText) window.findViewById(R.id.et_old_pwd);
//                bt_make_sure = (Button) window.findViewById(R.id.bt_make_sure);
//                bt_make_sure.setText("下一步");
//                bt_make_sure.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        storePass = AccountManager.sharedInstance().getPass();//获取首选项里的密码.
//                        get_et_old_pwd = et_old_pwd.getText().toString();
//                        if (!storePass.equals(get_et_old_pwd)){
//                            Toast.makeText(SettingActivity.this,"旧密码输入有误,请重新输入",Toast.LENGTH_LONG).show();
//                        }else {
//                            customAlertDialog.dismissDialog();
//                            ActivityJumpHelper.startActivity(SettingActivity.this,ChangePassWord.class);
//                        }
//                    }
//                });
//                bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
//                bt_cancel.setText("取消");
//                bt_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        customAlertDialog.dismissDialog();
//                    }
//                });
//            }
//        });
//    }

//    public void showDetectionUpdateDialog() {
//        detectionUpdateCustomAlertDialog = new CustomAlertDialog(SettingActivity.this);
//        detectionUpdateCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_confirm, new CustomAlertDialog.IHintDialog() {
//            @Override
//            public void onKeyDown(int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK){
//                    //detectionUpdateCustomAlertDialog.dismissDialog();
//                }
//            }
//            @Override
//            public void showWindowDetail(Window window) {
//                bt_make_sure = (Button) window.findViewById(R.id.bt_make_sure);
//                bt_make_sure.setText("确认");
//                bt_make_sure.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        detectionUpdateCustomAlertDialog.dismissDialog();
//                        getVersions("1");
//                        //detectionUpdate();
//
//
//                    }
//                });
//                bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
//                bt_cancel.setText("取消");
//                bt_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        detectionUpdateCustomAlertDialog.dismissDialog();
//                    }
//                });
//            }
//        });
//    }



//    private static IntentFilter makeGattUpdateIntentFilter() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
//        return intentFilter;
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter());
//
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mGattUpdateReceiver2);
//    }
}
