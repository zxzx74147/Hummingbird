package com.xbirder.bike.hummingbird.setting;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.config.NetworkConfig;
import com.xbirder.bike.hummingbird.register.ChangePassWord;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.Tools;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/22.
 */
public class MySetting extends BaseActivity {

    private String[] items = new String[]{"选择本地图片", "拍照"};

    private Uri photoUri;

    private String picPath = Environment.getExternalStorageDirectory()+"/xbird/pic";
    //private String picName = "upload.jpg";

    private final int PIC_FROM＿CROP = 2;//剪裁
    private final int PIC_FROM_CAMERA = 1;//照相
    private final int PIC_FROM＿LOCALPHOTO = 0;//相册


    private String[] sex = new String[]{"男", "女"};
    private boolean[] sexState = new boolean[]{true, false};
    //private RadioOnClick radioOnClick = new RadioOnClick(1);
    private ListView sexRadioListView;
    private TextView my_head_portrait;
    private RelativeLayout my_setting_name;
    //private Button my_setting_sex;
    private TextView tv_new_name;
    private RelativeLayout my_setting_head;
    private RoundedImageView my_head;
    private File file;

    private RelativeLayout my_setting_phone_number;
    private TextView tv_phone_number;

    private RelativeLayout my_setting_modify_password;

    private String resultStr;
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
        //my_setting_sex = (Button) findViewById(R.id.my_setting_sex);


        my_setting_phone_number = (RelativeLayout) findViewById(R.id.my_setting_phone_number);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_phone_number.setText(AccountManager.sharedInstance().getUser());


        my_setting_modify_password = (RelativeLayout) findViewById(R.id.my_setting_modify_password);

        my_head.setOnClickListener(mOnClickListener);
        my_head_portrait.setOnClickListener(mOnClickListener);
        my_setting_name.setOnClickListener(mOnClickListener);
        my_setting_phone_number.setOnClickListener(mOnClickListener);
        my_setting_modify_password.setOnClickListener(mOnClickListener);
        //my_setting_sex.setOnClickListener(mOnClickListener);

        String avatar = AccountManager.sharedInstance().getAvatarName();
        if(avatar != null && avatar.length()>0){
            avatar = avatar.substring(avatar.lastIndexOf("/")+1,avatar.length());
            File picfile = new File(picPath,avatar);
            if (picfile.exists()) {
                Bitmap bitmap = decodeUriAsBitmap(Uri.fromFile(picfile));
                Bitmap roundBitMap = getRoundedCornerBitmap(bitmap, 1.0f);
                my_head.setImageBitmap(roundBitMap);
            }
        }

    }
    private String findFile (File file, String keyword)
    {
        String res = "";
        if (!file.isDirectory())
        {
            res = "不是目录";
            return res;
        }
        File[] files = new File(file.getPath()).listFiles();

        for (File f : files)
        {
            if (f.getName().indexOf(keyword) >= 0)
            {
                res += f.getPath() + "\n";
            }
        }

        if (res.equals(""))
        {
            res = "没有找到相关文件";
        }

        return res;

    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == my_head_portrait || v == my_head) {
                showDialog();
            } else if (v == my_setting_name) {
                ActivityJumpHelper.startActivityForResule(MySetting.this, SettingName.class, 20);
            }else if(v == my_setting_modify_password){
                ActivityJumpHelper.startActivity(MySetting.this,ChangePassWord.class);
            }
//            else if (v == my_setting_sex) {
//                my_setting_sex.setText(AccountManager.sharedInstance().getSex());
//                AlertDialog ad = new AlertDialog.Builder(MySetting.this).setTitle("选择性别").
//                        setSingleChoiceItems(sex, radioOnClick.getIndex(), radioOnClick).create();
//                sexRadioListView = ad.getListView();
//                ad.show();
//            }
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
                        Bitmap roundBitMap = getRoundedCornerBitmap(bitmap, 1.0f);
                        my_head.setImageBitmap(roundBitMap);

                        new Thread(uploadImageRunnable).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

//    class RadioOnClick implements DialogInterface.OnClickListener {
//        private int index;
//
//        public RadioOnClick(int index) {
//            this.index = index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public int getIndex() {
//            return index;
//        }
//
//        @Override
//        public void onClick(DialogInterface dialogInterface, int i) {
//            setIndex(i);
//            Toast.makeText(MySetting.this, sex[index], Toast.LENGTH_LONG).show();
//            AccountManager.sharedInstance().setSex(sex[index]);
//            my_setting_sex.setText(sex[index]);
//            dialogInterface.dismiss();
//        }
//    }

    /**
     * 根据不同方式选择图片设置ImageView
     *
     * @param type 0-本地相册选择，非0为拍照
     */
    private void doHandlerPhoto(int type) {
        try {
            //保存裁剪后的图片文件
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/xbird/pic");
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
        intent.setType("image/*");//设置数据类型.比如要限定上传到服务器的的图片类型可以直接加"image/jpeg"活着"image/png"等类型
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
     *
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
    /**
     * 使用HttpUrlConnection模拟post表单进行文件
     * 上传平时很少使用，比较麻烦
     * 原理是： 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {
            String imgUrl = NetworkConfig.SERVER_ADDRESS_DEV + "?r=user/changeavatar&token="+AccountManager.sharedInstance().getToken();
            String urlpath = picPath+"/upload.jpg";
            if(TextUtils.isEmpty(imgUrl)){
               // Toast.makeText(mContext, 还没有设置上传服务器的路径！, Toast.LENGTH_SHORT).show();
                return;
            }
            //Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileparams = new HashMap<String, File>();
            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                //textParams = new HashMap<String, String>();
                fileparams = new HashMap<String, File>();
                // 要上传的图片文件
                File file = new File(urlpath);
                fileparams.put("pic", file);
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");//设置编码
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeFileParams(fileparams, ds);
                //NetUtil.writeStringParams(textParams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                     resultStr = NetUtil.readString(is);
                    JSONObject result = new JSONObject(resultStr);

                                        if (result.getString("error").equals("0")) {
                                            String picName = result.getString("avatar");
                                            AccountManager.sharedInstance().setAvatarName(picName);
                                            picName = picName.substring(picName.lastIndexOf("/")+1,picName.length());
                                            File picfile = new File(photoUri.getPath());
                                            picfile.renameTo(new File(picPath,picName));

                                        } else {
                                                //toast("图片本地记录失败！");
                                        }
                } else {
                    InputStream is = conn.getErrorStream();
                    resultStr = NetUtil.readString(is);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //pd.dismiss();

                    try {
//                        // 返回数据示例，根据需求和后台数据灵活处理
//                        // {status:1,statusMessage:上传成功,imageUrl:http://120.24.219.49/726287_temphead.jpg}
//                        JSONObject jsonObject = new JSONObject(resultStr);
//
//                        // 服务端以字符串“1”作为操作成功标记
//                        if (jsonObject.optString(status).equals(1)) {
//                            BitmapFactory.Options option = new BitmapFactory.Options();
//                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
//                            option.inSampleSize = 1;
//
//                            // 服务端返回的JsonObject对象中提取到图片的网络URL路径
//                            String imageUrl = jsonObject.optString(imageUrl);
//                            Toast.makeText(mContext, imageUrl, Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(mContext, jsonObject.optString(statusMessage), Toast.LENGTH_SHORT).show();
//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
            return false;
        }
    });
}
