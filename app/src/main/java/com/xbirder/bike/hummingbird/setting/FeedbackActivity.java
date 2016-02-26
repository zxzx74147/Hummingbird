package com.xbirder.bike.hummingbird.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/27.
 */
public class FeedbackActivity extends BaseActivity {
    private EditText feedback_edit;
    private Button feedback_commit;
    private FeedbackReuest feedbackReuest;
    private boolean iscommitting = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedback_edit = (EditText) findViewById(R.id.feedback_edit);

        feedback_commit = (Button) findViewById(R.id.feedback_commit);
        feedback_commit.setOnClickListener(mOnClickListener);

    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == feedback_commit) {
                if(iscommitting){
                    return;
                }
                feedbackReuest = new FeedbackReuest(new HttpResponse.Listener<JSONObject>() {
                    @Override
                    public void onResponse(HttpResponse<JSONObject> response) {
                        if (response.isSuccess()) {
                            iscommitting = false;
                            try {
                                if (response.result.getString("error").equals("0")) {
//                                    AccountManager.sharedInstance().setUserName(et_new_name);
//                                    Intent data = new Intent();
//                                    data.putExtra("str", et_new_name);
//                                    AccountManager.sharedInstance().setUserName(et_new_name);
//                                    setResult(20, data);
//                                    finish();
                                    feedback_edit.setText("");
                                    toast("提交成功");
                                } else {
                                        toast("提交失败");
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                String commitStr = feedback_edit.getText().toString();
                if(commitStr==null || commitStr.length()<=0){
                    toast("请先输入内容！");
                    return;
                }
                String commitVersion = AccountManager.sharedInstance().getBikeCurrentVersion();
                if(commitVersion==null || commitStr.length()<=0){
                    commitVersion = "未知版本";
                }
                feedbackReuest.setParam(feedback_edit.getText().toString(),commitVersion);
               // System.out.print("et_new_name : " + et_new_name);
                iscommitting = true;
                sendRequest(feedbackReuest);

            }

        }

    };
}
