package com.xbirder.bike.hummingbird.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;

/**
 * Created by Administrator on 2015/8/27.
 */
public class CustomAlertDialog {

    Dialog mDialog = null;
    private Context mContext = null;
    private IHintDialog mIDialogInstance = null;

    public CustomAlertDialog(Context context) {
        mContext = context;
        mDialog = new AlertDialog(mContext) {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && mIDialogInstance != null) {
                    mIDialogInstance.onKeyDown(keyCode, event);
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }
        };
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void showDialog(int iLayoutResId, IHintDialog interfaceInstance){
        if (mDialog == null || iLayoutResId == 0){
            return;
        }

        mIDialogInstance = interfaceInstance;
        mDialog.show();
        Window window = mDialog.getWindow();
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog.setContentView(iLayoutResId);
        if (mIDialogInstance != null){
            mIDialogInstance.showWindowDetail(window);
        }
    }

    public void dismissDialog(){
        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    public boolean isShowing(){
        if (mDialog != null && mDialog.isShowing()){
            return mDialog.isShowing();
        }
        return false;
    }

    public interface IHintDialog{
        public void onKeyDown(int keyCode,KeyEvent event);
        public void showWindowDetail(Window window);
    }
}
