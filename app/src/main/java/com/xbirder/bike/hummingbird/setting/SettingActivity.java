package com.xbirder.bike.hummingbird.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.LogoActivity;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothManager;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import cn.smssdk.SMSSDK;

public class SettingActivity extends AppCompatActivity {
    private Button quitBtn = null;

    private TextView resetView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        quitBtn = (Button)findViewById(R.id.quit_login);
        quitBtn.setOnClickListener(mOnClickListener);

        resetView = (TextView)findViewById(R.id.resetXBird);
        resetView.setOnClickListener(mOnClickListener);
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
            } else if (v == resetView) {
                byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.RESET, XBirdBluetoothConfig.END};
                XBirdBluetoothManager.sharedInstance().sendToBluetooth(value);
            }
        }
    };
}
