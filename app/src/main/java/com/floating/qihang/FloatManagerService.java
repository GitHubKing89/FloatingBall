package com.floating.qihang;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.floating.qihang.utils.AnimatorUtils;
import com.floating.qihang.utils.CustomerUtils;
import com.floating.qihang.utils.ProgressWindowManager;

public class FloatManagerService extends Service implements View.OnClickListener, View.OnTouchListener {
    private ImageView mFloatBallIcon;
    private ObjectAnimator mFloatBallanim = null;
    private static final int HIDE = 3;
    private static final int OPEN = 4;
    private boolean isMoveFinish;
    private static final int START_TO_MOVE = 0X001;
    private static final int START_TO_HIDE = 0X002;
    private static final int MOVE_STEP = 6;
    private static final int MOVE_TIME = 3;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mRootView != null) {
                if (mRootView.isShown()) {
                    if (msg.what == START_TO_MOVE) {
                        if (isRight) {
                            ProgressWindowManager.paramsInstance().x += MOVE_STEP;
                            if (ProgressWindowManager.paramsInstance().x >= mWidth - mRootView.getWidth()) {
                                isMoveFinish = true;
                            }
                        } else {
                            ProgressWindowManager.paramsInstance().x -= MOVE_STEP;
                            if (ProgressWindowManager.paramsInstance().x <= 0) {
                                isMoveFinish = true;
                            }
                        }
                           ProgressWindowManager.updateViewLayoutLocationParams(mRootView);
                        if (!isMoveFinish) {
                            mHandler.sendEmptyMessageDelayed(START_TO_MOVE, MOVE_TIME);
                        } else {
                            mHandler.sendEmptyMessageDelayed(START_TO_HIDE, 500);
                        }
                    }

                    if (msg.what == START_TO_HIDE) {
                        //changeFloatBall(HIDE);
                        AnimatorUtils.floatBallLocationChangeAnimator(mRootView,isRight, CustomerUtils.FLOAT_HIDE);
                    }


                }
            }
        }
    };
    private BroadcastReceiver stopServiceReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        registStopServiceReceiver();
        mBallSize = getResources().getDimensionPixelSize(R.dimen.dpi_80px);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mRootView==null){
            addWindowView();
        }
        mExpandView.setVisibility(View.GONE);
        return super.onStartCommand(intent, flags, startId);
    }

    private View mRootView;
    private View mExpandView;
    private void addWindowView(){
        /**
         * add展开功能的view
         */
        mExpandView = ProgressWindowManager.windowAddFixedSizeView(this, R.layout.expand_layout, 0, 300);
        ImageView imageview = (ImageView) mExpandView.findViewById(R.id.expand_icon);
        //我们把功能实现放在另外一个类中
        ViewGroup viewGroup = (ViewGroup) mExpandView.findViewById(R.id.expand_ly);
        for (int i = 0; i < viewGroup.getChildCount() - 1; i++) {
            View view = viewGroup.getChildAt(i);
            view.setOnClickListener(this);
        }
        imageview.setOnClickListener(this);
        mExpandView.setOnTouchListener(this);
        //获取屏幕宽度
        mWidth=ProgressWindowManager.getDisplaySize(this)[0];
        /**
         * add移动小球的view
         */
        mRootView = ProgressWindowManager.windowAddCommonView(this, R.layout.ball_layout, 0, 300);
        mFloatBallIcon = (ImageView) mRootView.findViewById(R.id.ball_icon);
        mFloatBallIcon.setOnTouchListener(this);
        mFloatBallIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.ball_icon){
            AnimatorUtils.openViewAnimator(mRootView,mExpandView,mParamx,mParamy,mBallSize,true);
            return;
        }else if (view.getId()==R.id.expand_icon){
            AnimatorUtils.closeViewAnimator(mRootView,mExpandView,false,mHandler);
            mHandler.sendEmptyMessageDelayed(START_TO_MOVE, 3000);
            return;
        }
        AnimatorUtils.floatBallClickChangedAnimator(mRootView,mExpandView,false);

        //下面就是点击调用自己想要具体处理的事情
        switch (view.getId()){
            case R.id.item_1_ly:
                Toast.makeText(this,getResources().getString(R.string.mem_speed),Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_2_ly:
                Toast.makeText(this,getResources().getString(R.string.hardware_check),Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_3_ly:
                Toast.makeText(this,getResources().getString(R.string.apk_lock),Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_4_ly:
                Toast.makeText(this,getResources().getString(R.string.system_lock),Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_5_ly:
                Toast.makeText(this,getResources().getString(R.string.home),Toast.LENGTH_SHORT).show();
                break;
        }

    }
    private float mLastx, mlasty;
    private long mDownTime;
    private int mParamx, mParamy;
    private int mWidth;
    private boolean isRight = false;
    private int mBallSize;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == R.id.ball_icon) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastx =  event.getRawX();
                    mlasty =  event.getRawY();
                    int[] locationParams = ProgressWindowManager.getViewLocationParams();
                    mParamx = locationParams[0];
                    mParamy = locationParams[1];
                    mDownTime = System.currentTimeMillis();
                     AnimatorUtils.floatBallLocationChangeAnimator(mRootView,isRight,OPEN);
                    mHandler.removeMessages(START_TO_HIDE);
                    mHandler.removeMessages(START_TO_MOVE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) (event.getRawX() - mLastx);
                    int dy = (int) (event.getRawY() - mlasty);
                    ProgressWindowManager.updateViewLayoutLocationParams(mRootView,dx+mParamx,dy+mParamy);
                    break;
                case MotionEvent.ACTION_UP:
                    isMoveFinish = false;
                    //根据小球的坐标来控制小球左右贴边的方向
                    isRight = ProgressWindowManager.getViewLocationParams()[0] - (mWidth / 2 - mRootView.getWidth()) > 0;
                    //3s内没操作

                    if (System.currentTimeMillis() - mDownTime >= 200) {
                     mHandler.sendEmptyMessageDelayed(START_TO_MOVE, 3000);
                        return true;
                    } else {
                        return false;
                    }
                default:
                    break;
            }
        }

        if (view.getId() == R.id.expand_ly) {
            /**
             * 点击局部view尺寸以外的位置会触发 ACTION_OUTSIDE
             */
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                AnimatorUtils.floatBallClickChangedAnimator(mRootView,mExpandView,false);
                isMoveFinish = false;
                isRight = ProgressWindowManager.getViewLocationParams()[0] - (mWidth / 2 - mRootView.getWidth()) > 0;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(stopServiceReceiver);
        super.onDestroy();
    }

    /**
     * 注册移除Window上添加的view的广播
     */
    private void registStopServiceReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.floating.qihang.FloatManagerService");
        stopServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if ("com.floating.qihang.FloatManagerService".equals(intent.getAction())){
                    if (mExpandView!=null){
                        ProgressWindowManager.windowManagerInstance(ctx).removeViewImmediate(mExpandView);
                        mExpandView=null;
                    }
                    if (mRootView!=null){
                        ProgressWindowManager.windowManagerInstance(ctx).removeViewImmediate(mRootView);
                        mRootView=null;
                    }
                   stopSelf();
                }

            }
        };
        registerReceiver(stopServiceReceiver, intentFilter);
    }
}
