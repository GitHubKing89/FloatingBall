package com.floating.qihang;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

private static final int WINDOW_REQUEST_CODE=0x111;
    private TextView open;
    private TextView close;


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
        //应用启动就添加悬浮球并关闭界面
/*        if (Settings.canDrawOverlays(getApplicationContext())){
                boolean existed= isServiceExisted(MainActivity.this,FloatManagerService.class.getName());
                if (!existed){
                    Intent intent = new Intent(MainActivity.this,FloatManagerService.class);
                    startService(intent);
                }
            finish();
        }*/

        setContentView(R.layout.activity_main);

        // 点击按钮控制打开和关闭小球
        open =(TextView)findViewById(R.id.open);
        close =(TextView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean existed= isServiceExisted(MainActivity.this,FloatManagerService.class.getName());
                if (existed){
                    Intent intent = new Intent();
                    intent.setAction("com.floating.qihang.FloatManagerService");
                    sendBroadcast(intent);
                }
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.canDrawOverlays(getApplicationContext())){
                    boolean existed= isServiceExisted(MainActivity.this,FloatManagerService.class.getName());
                    if (!existed){
                        Intent intent = new Intent(MainActivity.this,FloatManagerService.class);
                        startService(intent);
                        finish();
                    }
                }
            }
        });


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
        }else {

        }
    }


    /**
     * 获取指定的还在运行的service
     * @param context
     * @param className
     * @return
     */
    private  boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
