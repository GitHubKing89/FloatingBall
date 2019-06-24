package com.floating.qihang;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

private static final int WINDOW_REQUEST_CODE=0x111;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*动态申请权限*/
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,WINDOW_REQUEST_CODE);
            }
        }
        if (Settings.canDrawOverlays(getApplicationContext())){
            Intent intent = new Intent(this,FloatManagerService.class);
            startService(intent);
            finish();
        }
        //setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (WINDOW_REQUEST_CODE==requestCode){
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                Toast.makeText(getApplicationContext(),"请到设置里打开权限在使用！",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}
