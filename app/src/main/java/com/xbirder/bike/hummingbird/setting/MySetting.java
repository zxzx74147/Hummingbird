package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2015/8/22.
 */
public class MySetting extends Activity {

    private String[] items = new String[]{"选择本地图片", "拍照"};

    private Uri photoUri;

    private final int PIC_FROM_CAMERA = 1;//照相
    private final int PIC_FROM＿LOCALPHOTO = 0;//相册
    private String[] sex = new String[]{"男", "女"};
    private boolean[] sexState = new boolean[]{true, false};
    private RadioOnClick radioOnClick = new RadioOnClick(1);
    private ListView sexRadioListView;
    private TextView my_head_portrait;
    private RelativeLayout my_setting_name;
    private Button my_setting_sex;
    private TextView tv_new_name;
    private RelativeLayout my_setting_head;
    private RoundedImageView my_head;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_information);
        my_setting_head = (RelativeLayout) findViewById(R.id.my_setting_head);
        my_head = (RoundedImageView) findViewById(R.id.my_head);
        my_head_portrait = (TextView) findViewById(R.id.my_head_portrait);
        my_setting_name = (RelativeLayout) findViewById(R.id.my_setting_name);
        tv_new_name = (TextView) findViewById(R.id.tv_new_name);
        tv_new_name.setText(AccountManager.sharedInstance().getUsername());
        //System.out.print("首选项:" + sharedNickName);
        my_setting_sex = (Button) findViewById(R.id.my_setting_sex);


        my_head.setOnClickListener(mOnClickListener);
        my_head_portrait.setOnClickListener(mOnClickListener);
        my_setting_name.setOnClickListener(mOnClickListener);
        my_setting_sex.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == my_head_portrait || v == my_head) {
                showDialog();
            } else if (v == my_setting_name) {
                ActivityJumpHelper.startActivityForResule(MySetting.this, SettingName.class, 20);
            } else if (v == my_setting_sex) {
                my_setting_sex.setText(AccountManager.sharedInstance().getSex());
                AlertDialog ad = new AlertDialog.Builder(MySetting.this).setTitle("选择性别").
                        setSingleChoiceItems(sex, radioOnClick.getIndex(), radioOnClick).create();
                sexRadioListView = ad.getListView();
                ad.show();
            }
        }
    };

    public void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                doHandlerPhoto(PIC_FROM＿LOCALPHOTO);// 从相册中去获取
                                break;
                            case 1:
                                // 判断存储卡是否可以用，可用进行存储
                                if (Tools.hasSdcard()) {

                                    doHandlerPhoto(PIC_FROM_CAMERA);// 用户点击了照相获取
                                }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (20 == resultCode) {
            String newName = data.getExtras().getString("str");
            tv_new_name.setText(newName);
            System.out.println("newName" + newName);
        }

        switch (requestCode) {
            case PIC_FROM_CAMERA: // 拍照
                try {
                    cropImageUriByTakePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PIC_FROM＿LOCALPHOTO:
                try {
                    if (photoUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(photoUri);
                        Bitmap roundBitMap = getRoundedCornerBitmap(bitmap,1.0f);
                        my_head.setImageBitmap(roundBitMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class RadioOnClick implements DialogInterface.OnClickListener {
        private int index;

        public RadioOnClick(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setIndex(i);
            Toast.makeText(MySetting.this, sex[index], Toast.LENGTH_LONG).show();
            AccountManager.sharedInstance().setSex(sex[index]);
            my_setting_sex.setText(sex[index]);
            dialogInterface.dismiss();
        }
    }

    /**
     * 根据不同方式选择图片设置ImageView
     *
     * @param type 0-本地相册选择，非0为拍照
     */
    private void doHandlerPhoto(int type) {
        try {
            //保存裁剪后的图片文件
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/pic");
            System.out.println("pictureFileDir : " + pictureFileDir);//pictureFileDir : /storage/emulated/0/pic
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdirs();
            }
            File picFile = new File(pictureFileDir, "upload.jpg");
            System.out.println("picFile : " + picFile);//picFile : /storage/emulated/0/pic/upload.jpg
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            photoUri = Uri.fromFile(picFile);

            if (type == PIC_FROM＿LOCALPHOTO) {
                Intent intent = getCropImageIntent();
                startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }

        } catch (Exception e) {
            Log.i("HandlerPicError", "处理图片出现错误");
        }
    }

    /**
     * 调用图片剪辑程序
     */
    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 137);
        intent.putExtra("outputY", 137);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    /**
     * 圆形图片
     * @param bitmap
     * @param ratio
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
                bitmap.getHeight() / ratio, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
