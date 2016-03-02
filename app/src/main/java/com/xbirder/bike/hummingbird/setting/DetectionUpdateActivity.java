package com.xbirder.bike.hummingbird.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.config.NetworkConfig;
import com.xbirder.bike.hummingbird.setting.widget.DetectionUpdateRollView;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;
import com.xbirder.bike.hummingbird.util.StringHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DetectionUpdateActivity extends BaseActivity {


    private boolean isUpdateing = false;//false,未开始 true，已开始

    private static final int START_DETECTION_UPDATE = 0;
    private static final int DETECTION_UI_UPDATE = 1;
    private static final int DISMISS_DIALOG = 2;
    private static final int UPDATE_CURRENT_VERSION = 3;

    private TextView line_version;
    private TextView current_version;
    private Button bt_make_sure;
    private String file_url_data = null;
    private String line_version_data = null;

    private ArrayList<byte[]> mReadList = new ArrayList<byte[]>();

    private int sendid = 0;
    private int maxlines = 0;
    private CustomAlertDialog detectionUpdateCustomAlertDialog;
    private CustomAlertDialog detectionUpdateRunCustomAlertDialog;

    private DetectionUpdateRollView mDetectionUpdateRollView;
    private ImageView detection_update_view_bg;
    private TextView update_persent;
    private TextView update_persent_desc;
    private Button bt_dialog_make_sure;
    private Button bt_dialog_cancel;
    private Button bt_dp_reset;

    private String mBikeCurrentVersion;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_DETECTION_UPDATE:
                    detectionUpdate();
                    break;
                case DETECTION_UI_UPDATE:
                    updatePercent(msg.arg1);
                    break;
                case DISMISS_DIALOG:
                    isUpdateing = false;
                    //detectionUpdateRunCustomAlertDialog.dismissDialog();
                    break;
                case UPDATE_CURRENT_VERSION:
                    current_version.setText(line_version_data);
                    //detectionUpdateRunCustomAlertDialog.dismissDialog();
                    update_persent.setText("100%");
                    update_persent_desc.setText("complete");
                    isUpdateing = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_update);
        mDetectionUpdateRollView = (DetectionUpdateRollView) findViewById(R.id.detection_update_view);

        detection_update_view_bg = (ImageView) findViewById(R.id.detection_update_view_bg);

        update_persent = (TextView) findViewById(R.id.update_persent);
        update_persent_desc = (TextView) findViewById(R.id.update_persent_desc);
        line_version = (TextView) findViewById(R.id.line_version);

        current_version = (TextView) findViewById(R.id.current_version);

        bt_make_sure = (Button) findViewById(R.id.quit_login);
        bt_make_sure.setOnClickListener(mOnClickListener);

        bt_dp_reset = (Button) findViewById(R.id.dp_reset);
        bt_dp_reset.setOnClickListener(mOnClickListener);

//        Intent intent = getIntent();
//        //获取数据
//        line_version_data = intent.getStringExtra("LINE_VERSION");
//        String current_version_data = intent.getStringExtra("CURRENT_VERSION");
//        file_url_data = intent.getStringExtra("FILE_URL");
        mBikeCurrentVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
        if (StringHelper.checkString(mBikeCurrentVersion)) {
            current_version.setText(mBikeCurrentVersion);
        }
        getVersions("1");
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isUpdateing){
                return;
            }
            if (v == bt_make_sure) {
                showDetectionUpdateDialog(1);
            }else if(v == bt_dp_reset){
                showDetectionUpdateDialog(2);
            }
        }

    };
    private void filedown(String urlStr) {
        final String murlStr = urlStr;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //另起线程执行下载，安卓最新sdk规范，网络操作不能再主线程。
                File fileDir = new File(Environment.getExternalStorageDirectory(), "/xbird/");
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                String newFilename = Environment.getExternalStorageDirectory() + "/xbird/update.xbird";
                File file = new File(newFilename);
                //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                if(file.exists())
                {
                    file.delete();
                }
                try {
                    // 构造URL
                    URL url = new URL(murlStr);
                    // 打开连接
                    URLConnection con = url.openConnection();
                    //获得文件的长度
                    int contentLength = con.getContentLength();
                    //System.out.println("长度 :"+contentLength);
                    // 输入流
                    InputStream is = con.getInputStream();
                    // 1K的数据缓冲
                    byte[] bs = new byte[1024];
                    // 读取到的数据长度
                    int len;
                    // 输出的文件流
                    OutputStream os = new FileOutputStream(newFilename);
                    // 开始读取
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    // 完毕，关闭所有链接
                    os.close();
                    is.close();

                    Message msg = handler.obtainMessage();
                    msg.what = START_DETECTION_UPDATE;
                    msg.sendToTarget();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void getVersions(String versionnums) {
        if(!StringHelper.checkString(versionnums)) {
            return;
        }
        DetectionUpdateRequest request = new DetectionUpdateRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if (response.isSuccess()) {
                    try {

                        if (response.result.getString("error").equals("0")) {
                            String verNum = "";
                            String fileURL = "";
                            JSONArray datas = response.result.getJSONArray("versionList");
                            if (datas != null) {
                                JSONObject temp = datas.getJSONObject(0);
                                verNum = temp.getString("verNum");
                                fileURL = temp.getString("file");
                                line_version_data = verNum;
                                file_url_data = fileURL;
                                line_version.setText(line_version_data);
                            }
                        } else {
                            toast("获取固件信息失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }
            });
        request.setParam(versionnums);
        sendRequest(request);
    }

    private void detectionUpdate(){
        String fileName = Environment.getExternalStorageDirectory()+"/xbird/update.xbird";
        File file = new File(fileName);
        try{
            //ArrayList<String> mReadList = new ArrayList<String>();
            //得到资源中的asset数据流
            //InputStream instream = getResources().getAssets().open(fileName);
            InputStream instream = new FileInputStream(file);

            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                String line;
                //分行读取
                while (( line = buffreader.readLine()) != null) {
                    if(line.length() < 3){
                        continue;
                    }
                    byte[] value = new byte[20];
                    value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
                    value[1] = (byte) (XBirdBluetoothConfig.DETECTION_DATA & 0xFF);
                    value[19] = (byte) (XBirdBluetoothConfig.END & 0xFF);

                    int checkvalue = 0;

                    String linehead = line.substring(0,3);
                    if(linehead.equals(":10")) {
                        for (int i = 0; i < 16; i++) {
                            String lineciel = line.substring(9+2*i,9+2*i+2);

                            byte v= (byte) Integer.parseInt(lineciel, 16);
                            value[i+2] = (byte) (v & 0xFF);

                            int ilineciel = hexStringToInt(lineciel);
                            checkvalue += ilineciel;
                        }
                        checkvalue = checkvalue%256;
                        byte checkvalueByte = (byte) checkvalue;
                        value[18] = (byte) (checkvalueByte & 0xFF);
                        mReadList.add(value);
                    }else if(linehead.equals(":00")){
                        //结束读取
                        break;
                    }else{
                        //跳过条目
                    }
                }
                instream.close();
            }
            maxlines = mReadList.size();
            if(maxlines > 0){
                //detectionUpdate_total_lines.setText("/"+maxlines);
                //detectionUpdate_progressbar.setMax(maxlines);
                detectionUpdateStart();
            }
            //res = EncodingUtils.getString(buffer, "UTF-8");
        } catch (java.io.FileNotFoundException e)
        {
            Log.d("TestFile", "The File doesn't not exist.");
            dismissDetectionUpdateRunCustomAlertDialog();
        }
        catch (IOException e)
        {
            Log.d("TestFile", e.getMessage());
            dismissDetectionUpdateRunCustomAlertDialog();
        }

    }
    private void detectionReset(){
        //showDetectionUpdateING();
        String fileName = "osfile"; //文件名字

        try{
            //ArrayList<String> mReadList = new ArrayList<String>();
            //得到资源中的asset数据流
            InputStream instream = getResources().getAssets().open(fileName);
            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                String line;

                //分行读取
                while (( line = buffreader.readLine()) != null) {
                    if(line.length() < 3){
                        continue;
                    }
                    byte[] value = new byte[20];
                    value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
                    value[1] = (byte) (XBirdBluetoothConfig.DETECTION_DATA & 0xFF);
                    value[19] = (byte) (XBirdBluetoothConfig.END & 0xFF);

                    int checkvalue = 0;

                    String linehead = line.substring(0,3);
                    if(linehead.equals(":10")) {
                        for (int i = 0; i < 16; i++) {
                            Log.d("aa", line);
                            String lineciel = line.substring(9+2*i,9+2*i+2);
                            //String lineciel2 = line.substring(9+2*i+1,9+2*i+2);
                            //Integer.parseInt(lineciel, 16);
//                            byte devBin = (byte) Integer.parseInt("0x"+lineciel, 16);
//
//                            Log.d("aa", "aa");
                            //byte value5 = (byte) 0xC8;
                            //int v1 = Integer.parseInt(lineciel);
                            byte v= (byte) Integer.parseInt(lineciel, 16);
                            value[i+2] = (byte) (v & 0xFF);

                            int ilineciel = hexStringToInt(lineciel);
                            checkvalue += ilineciel;
//                            bytes[i] = (byte) ilineciel;

                        }
                        checkvalue = checkvalue%256;
                        byte checkvalueByte = (byte) checkvalue;
                        value[18] = (byte) (checkvalueByte & 0xFF);
                        mReadList.add(value);
                    }else if(linehead.equals(":00")){
                        //结束读取
                        break;
                    }else{
                        //跳过条目
                    }
                }
                instream.close();
            }
            maxlines = mReadList.size();
            if(maxlines > 0){
                //detectionUpdate_total_lines.setText("/"+maxlines);
                //detectionUpdate_progressbar.setMax(maxlines);
                detectionUpdateStart();

            }
            //res = EncodingUtils.getString(buffer, "UTF-8");
        } catch (java.io.FileNotFoundException e)
        {
            dismissDetectionUpdateRunCustomAlertDialog();
            Log.d("TestFile", "The File doesn't not exist.");
        }
        catch (IOException e)
        {
            dismissDetectionUpdateRunCustomAlertDialog();
            Log.d("TestFile", e.getMessage());
        }

    }
    private void showDetectionUpdateING(){
        detectionUpdateRunCustomAlertDialog = new CustomAlertDialog(DetectionUpdateActivity.this);
        detectionUpdateRunCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_update_run, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //detectionUpdateRunCustomAlertDialog.dismissDialog();
                }
            }
            @Override
            public void showWindowDetail(Window window) {
                //detectionUpdate_progressbar = (ProgressBar) window.findViewById(R.id.progressbar);
//                detectionUpdate_complete_lines = (TextView) window.findViewById(R.id.complete_lines);
//                detectionUpdate_total_lines = (TextView) window.findViewById(R.id.total_lines);
            }
        });
    }
    
    private void detectionUpdateStart() {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_START, XBirdBluetoothConfig.END};
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private void detectionUpdateING(int t) {
        byte[] value =  mReadList.get(t);
        //byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_DATA, XBirdBluetoothConfig.END};
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private void detectionUpdateEnd() {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.DETECTION_END, XBirdBluetoothConfig.END};
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                read(bytes);
            }
        }
    };

    private void dismissDetectionUpdateRunCustomAlertDialog(){
        Message msg = handler.obtainMessage();
        msg.what = DISMISS_DIALOG;
        msg.sendToTarget();
    }
    private void read(byte[] bytes) {
        if (bytes == null || bytes.length < 3) return;
        if (bytes[0] == XBirdBluetoothConfig.PREFIX && bytes[bytes.length - 1] == XBirdBluetoothConfig.END) {
            switch (bytes[1]) {
                case XBirdBluetoothConfig.DETECTION_START:
                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
                        sendid = 0;
                        detectionUpdateING(sendid);
                        //updatePercent(sendid);
                        sendid++;
                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
                        dismissDetectionUpdateRunCustomAlertDialog();
                        toast("刷新固件失败");
                    }
                    break;
                case XBirdBluetoothConfig.DETECTION_DATA:
                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
                        if(mReadList.size() > sendid) {
                            detectionUpdateING(sendid);

                            Message msg = new Message();
                            msg.what = DETECTION_UI_UPDATE;
                            msg.arg1 = sendid;
                            handler.sendMessage(msg);
//                            Message msg = handler.obtainMessage();
//                            msg.what = DETECTION_UI_UPDATE;
//                            msg.arg1 = sendid;
//                            msg.sendToTarget();
                            //updatePercent(sendid);
                            sendid++;

                        }else{
                            detectionUpdateEnd();
                        }
                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
                        dismissDetectionUpdateRunCustomAlertDialog();
                        toast("刷新固件失败");
                    }
                    break;
                case XBirdBluetoothConfig.DETECTION_END:
                    if (bytes[2] == XBirdBluetoothConfig.DETECTION_OK) {
                        toast("刷新固件成功");
                        Message msg = handler.obtainMessage();
                        msg.what = UPDATE_CURRENT_VERSION;
                        msg.sendToTarget();

                    } else if (bytes[2] == XBirdBluetoothConfig.DETECTION_ERROR) {
                        dismissDetectionUpdateRunCustomAlertDialog();
                        toast("刷新固件失败");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updatePercent(int numid){
        //detectionUpdate_complete_lines.setText("" + numid);
        //detectionUpdate_progressbar.setProgress(numid);

        setUpdateRoll(100*numid/maxlines);

    }

    public int hexStringToInt(String str){
//        String lineciel1 = str.substring(0,1);
//        String lineciel2 = str.substring(1,2);
////        int value1 =Integer.parseInt(str.substring(0, 1));//49
////        int value2 =Integer.parseInt(str.substring(1, 2));//49
        char[] chars = str.toCharArray();
        int valueset1 = (int) chars[0];//49
        int valueset2 = (int) chars[1];//49

        int int_ch1;
        if(valueset1 >= 48 && valueset1 <= 57)
            int_ch1 = (valueset1-48)*16;   //// 0 的Ascll - 48
        else if(valueset1 >= 65 && valueset1 <=70)
            int_ch1 = (valueset1-55)*16; //// A 的Ascll - 65
        else
            int_ch1 = (valueset1-87)*16; //// a 的Ascll - 97

        int int_ch2;
        if(valueset2 >= 48 && valueset2 <= 57)
            int_ch2 = (valueset2-48);   //// 0 的Ascll - 48
        else if(valueset2 >= 65 && valueset2 <=70)
            int_ch2 = (valueset2-55); //// A 的Ascll - 65
        else
            int_ch2 = (valueset2-87); //// a 的Ascll - 97

        return int_ch1 + int_ch2;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void setUpdateRoll(int battery) {
        if(battery > 0&&battery<100){
            detection_update_view_bg.setImageResource(R.drawable.setting_detection_update_drawable_bg_c);
            update_persent.setVisibility(View.VISIBLE);
            update_persent_desc.setVisibility(View.VISIBLE);
            update_persent.setText("" + battery + "%");
            update_persent_desc.setText("updating");
        } else if(battery >= 100){
            update_persent.setText("100%");
            update_persent_desc.setText("complete");
        }

        mDetectionUpdateRollView.setPercent(battery);

    }
    public void showDetectionUpdateDialog(final int tag) {
        detectionUpdateCustomAlertDialog = new CustomAlertDialog(DetectionUpdateActivity.this);
        detectionUpdateCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_confirm, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //detectionUpdateCustomAlertDialog.dismissDialog();
                }
            }
            @Override
            public void showWindowDetail(Window window) {
                bt_dialog_make_sure = (Button) window.findViewById(R.id.bt_make_sure);
                bt_dialog_make_sure.setText("确认");
                bt_dialog_make_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tag == 1) {
                            detectionUpdateCustomAlertDialog.dismissDialog();
                            if (!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()) {
                                toast("未连接锋鸟");
                                return;
                            }
                            //mBikeCurrentVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
                            if (!StringHelper.checkString(mBikeCurrentVersion)) {
                                toast("获取车身固件版本失败");
                                return;
                            } else if (mBikeCurrentVersion.equals("1")) {
                                toast("当前版本暂不支持此功能！");
                                return;
                            }
                            if (line_version_data == null || file_url_data == null) {
                                toast("新版本信息未获取");
                                return;
                            }
                            if (line_version_data == null || file_url_data == null) {
                                toast("新版本信息未获取");
                                return;
                            }
                            if (Integer.parseInt(line_version_data) < Integer.parseInt(mBikeCurrentVersion)) {
                                toast("此版本无须更新");
                                return;
                            }
                            //filedown(NetworkConfig.SERVER_ADDRESS_DEV_HEAD + file_url_data);
                            filedown(NetworkConfig.SERVER_ADDRESS_DEV_HEAD + file_url_data);
                        }else if(tag == 2){
                            detectionUpdateCustomAlertDialog.dismissDialog();
                            if (!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()) {
                                toast("未连接锋鸟");
                                return;
                            }
                            if (mBikeCurrentVersion.equals("1")) {
                                toast("当前版本暂不支持此功能！");
                                return;
                            }
                            detectionReset();
                        }
                        isUpdateing = true;
                    }
                });
                bt_dialog_cancel = (Button) window.findViewById(R.id.bt_cancel);
                bt_dialog_cancel.setText("取消");
                bt_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detectionUpdateCustomAlertDialog.dismissDialog();
                    }
                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter());

    }
    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver2);
    }
}
