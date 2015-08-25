package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "18858668384_1440341168.jpg";

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private String[] sex = new String[]{"男", "女"};
    private boolean[] sexState = new boolean[]{true, false};
    private RadioOnClick radioOnClick = new RadioOnClick(1);
    private ListView sexRadioListView;
    private TextView my_head_portrait;
    private RelativeLayout my_setting_name;
    private Button my_setting_sex;
    private TextView tv_new_name;
    private String sharedNickName;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (20 == resultCode) {
            String newName = data.getExtras().getString("str");
            tv_new_name.setText(newName);
            System.out.println("newName" + newName);
        }

        //结果码不等于取消的时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());//裁剪
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Tools.hasSdcard()) {
                        file = new File(
                                Environment.getExternalStorageDirectory()
                                        + IMAGE_FILE_NAME);//获取完整的图片url
                        startPhotoZoom(Uri.fromFile(file));//裁剪?
                    } else {
                        Toast.makeText(MySetting.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery,
                                        IMAGE_REQUEST_CODE);
                                break;
                            case 1:

                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (Tools.hasSdcard()) {

                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    IMAGE_FILE_NAME)));
                                }

                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
                                break;
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
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            try{
                Bitmap photo = extras.getParcelable("data");
                Drawable drawable = new BitmapDrawable(photo);
                String tupian = saveImg(photo,"18858668384_1440341168.jpg");
                my_head.setImageDrawable(drawable);
                uploadImage(tupian);
            }catch (Exception e){

            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        System.out.print(uri);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 上传图片到服务器
     *
     * @param path 图片的路径
     */
    public void uploadImage(String path) {
        String url = "http://120.26.43.158/xbird/web/index.php?r=user/changeavatar";
        File file = new File(path);
        RequestParams params = new RequestParams();
        try {
            params.put("pic", file);
            params.put("token", AccountManager.sharedInstance().getToken());
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, String content) {//上传成功
                    System.out.println("content:" + content);
                }

                @Override
                public void onFailure(Throwable e, String data) {//上传失败
                    System.out.println("图片上传失败");
                }
            });
        } catch (FileNotFoundException e) {

        }
    }


    /**
     * 将bitmap转化成图片
     * @param b bitmap对象
     * @param name 图片名称
     * @return 图片的路径
     */
    public String saveImg(Bitmap b, String name)throws Exception {
        String path = Environment.getExternalStorageDirectory() + "/" + "18858668384_1440341168.jpg";
        System.out.println(path);
        File dirFile = new File(path);
        File mediaFile = new File(path + File.separator + name);
        if (mediaFile.exists()) {
            mediaFile.delete();
        }
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        mediaFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(mediaFile);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        b.recycle();
        b = null;
        System.gc();
        return mediaFile.getPath();
    }
}
