package com.xbirder.bike.hummingbird;

import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.common.widget.MyVideoView;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;


public class LogoActivity extends BaseActivity {

    //private View mLoginView;
    private Button mLoginView;
    private Button mRegisterView;
    private MyVideoView mVideoView;

    private SurfaceView surfaceview;
    private MediaPlayer mediaPlayer;

    private int screenWidth;
    private int screenHeight;
    private ViewGroup.LayoutParams sufaceviewParams;

    private Handler mHandler = new Handler();

    private boolean touchable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        //DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenWidth = dm.widthPixels;//屏幕的宽
//        screenHeight = dm.heightPixels;//屏幕的高
        mVideoView = (MyVideoView) findViewById(R.id.logo_videoView);
//        surfaceview = (SurfaceView) findViewById(R.id.logo_videoView);
//
//        sufaceviewParams = surfaceview.getLayoutParams();
//        sufaceviewParams.height = screenHeight;
//        sufaceviewParams.width = screenHeight * 1920 / 1080;
//
//        surfaceview.setLayoutParams(sufaceviewParams);
//        mediaPlayer = new MediaPlayer();
//        //android.widget.LinearLayout.LayoutParams sufaceviewParams = (android.widget.LinearLayout.LayoutParams) surfaceview.getLayoutParams();
//        surfaceview.getHolder().setKeepScreenOn(true);
//        surfaceview.getHolder().addCallback(new SurfaceViewLis());


        mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login);
        //Uri uri = Uri.parse("android.resource://com.xbirder.bike.hummingbird/drawable/login.mp4");
        //Uri uri = Uri.parse("file:///android_asset/login.mp4");
        //mVideoView.setMediaController(new MediaController(LogoActivity.this));
        //mVideoView.setVideoURI(uri);
        //mVideoView.start();

//        try{
//            mVideoView.start();
//        }catch(Exception e) {
//
//            mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login2);
//            try {
//                mVideoView.start();
//            }catch(Exception e2) {
//
//            }
//        }

       //mVideoView.requestFocus();

        mLoginView = (Button) findViewById(R.id.btn_login);
        mRegisterView = (Button) findViewById(R.id.btn_reg);
        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(touchable){
                    ActivityJumpHelper.startActivity(LogoActivity.this, LoginActivity.class);
                }
            }
        });
        mRegisterView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(touchable) {
                    ActivityJumpHelper.startActivity(LogoActivity.this, RegisterActivity.class);
                }
            }
        });

        String userName = AccountManager.sharedInstance().getUser();
        String pass = AccountManager.sharedInstance().getPass();
        if ((userName != null && userName != "") &&
                (pass != null && pass != "")) {
            touchable = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityJumpHelper.startActivity(LogoActivity.this, MainActivity.class);
                    //LogoActivity.this.finish();
                }
            }, 2000);
                    //ActivityJumpHelper.startActivity(LogoActivity.this, MainActivity.class);
        }

    }

//    private class SurfaceViewLis implements SurfaceHolder.Callback {
//
//        @Override
//        public void surfaceChanged(SurfaceHolder holder, int format, int width,
//                                   int height) {
//            if (mediaPlayer != null) {
//                mediaPlayer.setDisplay(holder);
//            }
//        }
//
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    play();
//                } catch (IllegalArgumentException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (SecurityException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IllegalStateException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//
//        }
//
//    }
//
//    public void play() throws IllegalArgumentException, SecurityException,
//            IllegalStateException, IOException {
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        AssetFileDescriptor fd = this.getAssets().openFd("newvedio.mp4");
//        mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
//                fd.getLength());
//        //mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.setDisplay(surfaceview.getHolder());
//        // 通过异步的方式装载媒体资源
//        mediaPlayer.prepareAsync();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                // 装载完毕回调
//                mediaPlayer.start();
//            }
//        });
//    }


    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login);
        mVideoView.start();
//        try{
//            mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login);
//            mVideoView.start();
//        }catch(Exception e) {
//            mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login2);
//            try {
//                mVideoView.start();
//            }catch(Exception e2) {
//
//            }
//        }
    }
}
