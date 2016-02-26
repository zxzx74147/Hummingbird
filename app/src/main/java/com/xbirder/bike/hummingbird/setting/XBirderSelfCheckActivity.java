package com.xbirder.bike.hummingbird.setting;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.setting.widget.SelfCheckRollView;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;

/**
 * Created by Administrator on 2015/8/27.
 */
public class XBirderSelfCheckActivity extends BaseActivity {

    private enum selfCheckStateEnum {isNull, isChecking,isdataback, isStop, isCompleted}
    private selfCheckStateEnum mSelfCheckStateEnum = selfCheckStateEnum.isNull;

    private ImageView checkStart;
    private ImageView selfCheckingRuningBg;
    private ListView mListView;
    private MyListAdapter mMyListAdapter;
    private ListInfo[] LIST_INFOS = new ListInfo[6];

    private  boolean checktimeout = false;

    private CustomAlertDialog mCustomAlertDialog;

    private SelfCheckRollView selfCheckRollView;

    private TextView selfCheckingScore;
    private TextView selfCheckingScoreDsc;
    //private int backnum = -1;

    //private Handler mHandler = new Handler();
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mSelfCheckStateEnum == selfCheckStateEnum.isStop){
                return;
            }
            ViewHolder holder = null;
            if(msg.what <= 5) {
                holder = (ViewHolder) mListView.getChildAt(msg.what).getTag();
                if(msg.arg1 == 1){
                    holder.jiancezhong();
                    if(msg.what == 5){

                    }
                }else if(msg.arg1 == 2){
                    holder.jianceend();
                }else if(msg.arg1 == 3){
                    holder.jianceerror(msg.arg2);

                }
            }else if(msg.what == 6){
                if(mSelfCheckStateEnum == selfCheckStateEnum.isChecking) {
                    mSelfCheckStateEnum = selfCheckStateEnum.isStop;
                    initListData();
                    mMyListAdapter.notifyDataSetChanged();
                    //mSelfCheckStateEnum = selfCheckStateEnum.isNull;
                    showDialog("检测失败，请稍后尝试");
                }
            }
//            switch (msg.what) {
//                case 0:
//                    if(msg.arg1 == 0) {
//                        //等待
//                        LIST_INFOS[0].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                       //检测中
//                        holder.jiancezhong();
////                        LIST_INFOS[0].setDesc(R.string.self_check_jiancezhong);
////                        holder.desc.setText(R.string.self_check_jiancezhong);
//
//                    }else if(msg.arg1 == 2){
//                       //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        //电机出错
//                        LIST_INFOS[0].setDesc(R.string.self_check_dianjichucuo);
//                    }else if(msg.arg1 == 4){
//                        //电机堵转
//                        LIST_INFOS[0].setDesc(R.string.self_check_dianjiduzhuan);
//                    }
//
//                    break;
//                case 1:
//                    if(msg.arg1 == 0) {
//                        LIST_INFOS[1].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                        holder.jiancezhong();
////                        LIST_INFOS[1].setDesc(R.string.self_check_jiancezhong);
////                        holder.desc.setText(R.string.self_check_jiancezhong);
//                    }else if(msg.arg1 == 2){
//                        //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        LIST_INFOS[1].setDesc(R.string.self_check_lanyachucuo);
//                    }else if(msg.arg1 == 4){
//                        LIST_INFOS[1].setDesc(R.string.self_check_lanyaweilianjie);
//                    }
//                    break;
//                case 2:
//                    if(msg.arg1 == 0) {
//                        LIST_INFOS[2].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                        holder.jiancezhong();
//                    }else if(msg.arg1 == 2){
//                        //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        LIST_INFOS[2].setDesc(R.string.self_check_dianyadianliangdi);
//                    }
//                    break;
//                case 3:
//                    if(msg.arg1 == 0) {
//                        LIST_INFOS[3].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                        holder.jiancezhong();
//                    }else if(msg.arg1 == 2){
//                        //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        LIST_INFOS[3].setDesc(R.string.self_check_shachexitongyichang);
//                    }
//                    break;
//                case 4:
//                    if(msg.arg1 == 0) {
//                        LIST_INFOS[4].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                        holder.jiancezhong();
//                    }else if(msg.arg1 == 2){
//                        //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        LIST_INFOS[4].setDesc(R.string.self_check_youmenxitongyichang);
//                    }
//                    break;
//                case 5:
//                    if(msg.arg1 == 0) {
//                        LIST_INFOS[5].setDesc(R.string.self_check_dengdai);
//                    }else if(msg.arg1 == 1){
//                        holder.jiancezhong();
//                    }else if(msg.arg1 == 2){
//                        //检测完成
//
//                    }else if(msg.arg1 == 3){
//                        LIST_INFOS[5].setDesc(R.string.self_check_dianluduanlu);
//                    }
//                    break;
//                case 6:
//                    if(mSelfCheckStateEnum == selfCheckStateEnum.isChecking) {
//                        mSelfCheckStateEnum = selfCheckStateEnum.isStop;
//                        initListData();
//                        mMyListAdapter.notifyDataSetChanged();
//                        mSelfCheckStateEnum = selfCheckStateEnum.isNull;
//                        showDialog("检测失败，请稍后尝试");
//                    }
//                    break;
//
//
//            }
//            if(msg.what <= 5) {
//                //mMyListAdapter.notifyDataSetChanged();
//                ViewHolder holder = (ViewHolder) mListView.getChildAt(msg.what).getTag();
//                holder.desc.setText("ceshiceshi");
//
////                int visiblePosition = mListView.getFirstVisiblePosition();
////                if (msg.what - visiblePosition >= 0) {
////                    mMyListAdapter.getView(msg.what, mListView.getChildAt(msg.what - visiblePosition), mListView);
////                }
//                    //mMyListAdapter.getView(msg.what, mListView.getChildAt(msg.what - visiblePosition), mListView);
//            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_selfcheck);

        checkStart = (ImageView) findViewById(R.id.self_checking_start);
        checkStart.setOnClickListener(mOnClickListener);

        selfCheckingRuningBg = (ImageView) findViewById(R.id.self_checking_runing_bg);

        selfCheckRollView = (SelfCheckRollView) findViewById(R.id.self_check_roll_view);
        selfCheckingScore = (TextView) findViewById(R.id.self_checking_score);
        selfCheckingScoreDsc = (TextView) findViewById(R.id.self_checking_score_dsc);

        initListData();

        mListView = (ListView) findViewById(R.id.listView);
        // 添加ListItem，设置事件响应
        mMyListAdapter = new MyListAdapter();
        mListView.setAdapter(mMyListAdapter);

//        setViewVisbility(View.VISIBLE);
//        senderror(6,7);
    }
    private void setViewVisbility(int visibility){
        selfCheckRollView.setVisibility(visibility);
        selfCheckingScore.setVisibility(visibility);
        selfCheckingScoreDsc.setVisibility(visibility);
        selfCheckingRuningBg.setVisibility(visibility);
        if(visibility == View.VISIBLE){
            checkStart.setVisibility(View.INVISIBLE);
        }else{
            checkStart.setVisibility(View.VISIBLE);
        }

    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == checkStart) {
                if(!HuApplication.sharedInstance().XBirdBluetoothManager().getIsConnect()){
                    toast("未连接锋鸟");
                     return;
                }

                if(mSelfCheckStateEnum == selfCheckStateEnum.isNull||mSelfCheckStateEnum == selfCheckStateEnum.isStop) {
                    mSelfCheckStateEnum = selfCheckStateEnum.isChecking;

                    setViewVisbility(View.VISIBLE);
                    xbirdSelfCheckStart();

                    checkBegin(0);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 6;
                            msg.sendToTarget();
                        }
                    }, 5000);
                }
            }
        }
    };
    private void initListData() {
        LIST_INFOS = new ListInfo[]{
        new ListInfo(R.string.self_check_dianji,
                R.string.self_check_dengdai, R.drawable.self_check_dianji),
                new ListInfo(R.string.self_check_lanya,
                        R.string.self_check_dengdai, R.drawable.self_check_lanya),
                new ListInfo(R.string.self_check_dianchi,
                        R.string.self_check_dengdai, R.drawable.self_check_dianchi),
                new ListInfo(R.string.self_check_shache,
                        R.string.self_check_dengdai, R.drawable.self_check_shache),
                new ListInfo(R.string.self_check_youmen,
                        R.string.self_check_dengdai, R.drawable.self_check_youmen),
                new ListInfo(R.string.self_check_dianlu,
                        R.string.self_check_dengdai, R.drawable.self_check_dianlu),
        };
        setViewVisbility(View.INVISIBLE);
    }
    private class MyListAdapter extends BaseAdapter {
        public MyListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView) {

            convertView = View.inflate(XBirderSelfCheckActivity.this,
                    R.layout.listview_self_check_info_item, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.img_round = (ImageView) convertView.findViewById(R.id.img_round);
                holder.img_end = (ImageView) convertView.findViewById(R.id.img_end);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.desc.setTextColor(Color.rgb(132, 132, 132));
            holder.title.setText(LIST_INFOS[index].title);
            holder.desc.setText(LIST_INFOS[index].desc);
            holder.img.setImageResource(LIST_INFOS[index].imgresId);
            holder.img_round.clearAnimation();
            holder.img_round.setVisibility(View.INVISIBLE);

            holder.img_end.setImageResource(R.drawable.self_check_end);
            holder.img_end.setVisibility(View.INVISIBLE);

//            if(LIST_INFOS[index].desc == R.string.self_check_dengdai){
//                holder.desc.setTextColor(Color.rgb(132, 132, 132));
//            }else if(LIST_INFOS[index].desc == R.string.self_check_jiancezhong){
//                holder.desc.setTextColor(Color.rgb(249, 159, 61));
//                holder.img_round.setVisibility(View.VISIBLE);
//                RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F);
//                ra.setDuration(5000);
//                holder.img_round.startAnimation(ra);
//            }else{
//
//                holder.desc.setTextColor(Color.rgb(241, 90, 37));
//            }
            return convertView;
        }
        @Override
        public int getCount() {
            return LIST_INFOS.length;
        }

        @Override
        public Object getItem(int index) {
            return LIST_INFOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private class ListInfo {
        private  int title;
        private  int desc;
        private  int imgresId;

        public ListInfo(int title, int desc,
                        int imgresId) {
            this.title = title;
            this.desc = desc;
            this.imgresId = imgresId;
        }
        public void setTitle(int mtitle){
            title = mtitle;
        }
        public void setDesc(int mdesc){
            desc = mdesc;
        }
        public void setImgresId(int mimgresId){
            imgresId = mimgresId;
        }
    }

    private void xbirdSelfCheckStart() {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.XBIRD_SELF_CHECK, XBirdBluetoothConfig.END};
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
    //检测启动
    private void checkBegin(int i){
        if(mSelfCheckStateEnum == selfCheckStateEnum.isStop || i > 5 ){
                return;
         }
        int delaytime = 2000;
        if(i == 0){
            delaytime = 100;
        }
            final Message msg = mHandler.obtainMessage();
            final int k = i;

            msg.what = i;
            msg.arg1 = 1;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    msg.sendToTarget();
                    checkBegin(k + 1);
                }
            }, delaytime);
    }
    private void read(byte[] bytes) {

        if (bytes == null || bytes.length < 3) return;
        if (bytes[0] == XBirdBluetoothConfig.PREFIX && bytes[bytes.length - 1] == XBirdBluetoothConfig.END) {
            switch (bytes[1]) {
                case XBirdBluetoothConfig.XBIRD_SELF_CHECK:
                    if(mSelfCheckStateEnum == selfCheckStateEnum.isStop) {
                        return;
                    }else if(mSelfCheckStateEnum == selfCheckStateEnum.isChecking){
                        mSelfCheckStateEnum = selfCheckStateEnum.isdataback;
                    }
                    if (bytes[2] == (byte)0x00) {
                        int totalnum = 6;
                        for(int i=0 ; i<totalnum; i++){
                            final Message msg = mHandler.obtainMessage();
                            msg.what = i;
                            msg.arg1 = 2;
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    msg.sendToTarget();
                                }
                            }, 2000*(i+1));
                        }
                        updateScore(0);
                    } else if (bytes[2] ==  (byte)0x01) {
                        senderror(1,1);
                    } else if (bytes[2] ==  (byte)0x02) {
                        senderror(2,2);
                    } else if (bytes[2] ==  (byte)0x03) {
                        senderror(3,3);
                    } else if (bytes[2] ==  (byte)0x04) {
                        senderror(5,4);
                    } else if (bytes[2] ==  (byte)0x05) {
                        senderror(4,5);
                    } else if (bytes[2] ==  (byte)0x06) {
                        senderror(1,6);
                    } else if (bytes[2] ==  (byte)0x07) {
                        senderror(6,7);
                    } else if (bytes[2] ==  (byte)0x08) {
                        senderror(2,8);
                    }
                    break;
            }
        }
    }
    private void senderror(int totalnum,int errornum){
        for(int i=0 ; i<totalnum; i++){
            final Message msg = mHandler.obtainMessage();
            msg.what = i;
            msg.arg1 = 2;
            if(totalnum == (i+1)){
                msg.arg1 = 3;
                msg.arg2 = errornum;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    msg.sendToTarget();
                }
            }, 2000*(i+1));
        }
        updateScore(totalnum);
    }
    private void updateScore(int totalnum){
        int score = 100;
        int time = 12000;
        switch(totalnum){
            case 0:
                score = 100;
                time = 12000;
                break;
            case 1:
                score = 0;
                time = 2000;
                break;
            case 2:
                score = 25;
                time = 4000;
                break;
            case 3:
                score = 50;
                time = 6000;
                break;
            case 4:
                score = 62;
                time = 8000;
                break;
            case 5:
                score = 75;
                time = 10000;
                break;
            case 6:
                score = 87;
                time = 12000;
                break;
        }
        if(score == 0){
        }else{
            ValueAnimator animator = createSelfCheckAnimator(0,score);
                animator.setDuration(time);
                animator.start();
        }
    }
    private ValueAnimator createSelfCheckAnimator(int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (Integer) valueAnimator.getAnimatedValue();
                        setScore(value);
                    }
                });
        return animator;
    }
    private void setScore(int score){
        selfCheckRollView.setPercent(score);
        selfCheckingScore.setText(""+score);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void showDialog(String title) {
        final String stitle = title;
       mCustomAlertDialog = new CustomAlertDialog(XBirderSelfCheckActivity.this);
       mCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_self_check, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //detectionUpdateCustomAlertDialog.dismissDialog();
                }
            }
            @Override
            public void showWindowDetail(Window window) {
                TextView show_title = (TextView) window.findViewById(R.id.tv_title);
                show_title.setText(stitle);

                Button bt_make_sure = (Button) window.findViewById(R.id.bt_make_sure);
                bt_make_sure.setText("确认");
                bt_make_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCustomAlertDialog.dismissDialog();
                    }
                });
//                bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
//                bt_cancel.setText("取消");
//                bt_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        detectionUpdateCustomAlertDialog.dismissDialog();
//                    }
//                });
            }
        });
    }
    private class ViewHolder {
        private TextView title;

        private TextView desc;

        private ImageView img;

        private ImageView img_round;

        private ImageView img_end;

        private void jiancezhong(){
            desc.setTextColor(Color.rgb(249, 159, 61));
            desc.setText(R.string.self_check_jiancezhong);
            img_round.setVisibility(View.VISIBLE);
            RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F);
            ra.setDuration(4000);
            ra.setRepeatCount(100);
            img_round.startAnimation(ra);
        }
        private void jianceend(){
            img_round.clearAnimation();
            img_round.setVisibility(View.INVISIBLE);
            //desc.setVisibility(View.GONE);
            desc.setText("");
            img_end.setVisibility(View.VISIBLE);
        }
        private void jianceerror(int errorId){
            img_end.setImageResource(R.drawable.self_check_end_error);
            img_end.setVisibility(View.VISIBLE);

            img_round.clearAnimation();
            img_round.setVisibility(View.INVISIBLE);
            desc.setTextColor(Color.rgb(241, 90, 37));
            switch (errorId){
                case 1 :
                    img.setImageResource(R.drawable.self_check_dianji_c);
                    desc.setText(R.string.self_check_dianjichucuo);
                    break;
                case 2 :
                    img.setImageResource(R.drawable.self_check_lanya_c);
                    desc.setText(R.string.self_check_lanyachucuo);
                    break;
                case 3 :
                    img.setImageResource(R.drawable.self_check_dianchi_c);
                    desc.setText(R.string.self_check_dianyadianliangdi);
                    break;
                case 4 :
                    img.setImageResource(R.drawable.self_check_youmen_c);
                    desc.setText(R.string.self_check_youmenxitongyichang);
                    break;
                case 5 :
                    img.setImageResource(R.drawable.self_check_shache_c);
                    desc.setText(R.string.self_check_shachexitongyichang);
                    break;
                case 6 :
                    img.setImageResource(R.drawable.self_check_dianji_c);
                    desc.setText(R.string.self_check_dianjiduzhuan);
                    break;
                case 7 :
                    img.setImageResource(R.drawable.self_check_dianlu_c);
                    desc.setText(R.string.self_check_dianluduanlu);
                    break;
                case 8 :
                    img.setImageResource(R.drawable.self_check_lanya_c);
                    desc.setText(R.string.self_check_lanyaweilianjie);
                    break;
            }
            img_end.setVisibility(View.VISIBLE);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter());

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver2);
    }
}
