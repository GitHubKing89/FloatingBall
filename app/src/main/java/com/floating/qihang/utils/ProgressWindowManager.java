package com.floating.qihang.utils;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.floating.qihang.R;

/**
 * 管理window窗应用的界面和事件
 */
public class ProgressWindowManager {

    private static WindowManager windowManager;
    private static WindowManager.LayoutParams standardParams;

    /**
     *添加view到屏幕上
     */
    private static void windowAddView(Context context,int locationX,int locationY){
       /* if (windowManager == null) {
            windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }*/
        windowManagerInstance(context);
        /**
         * 注意每个View必须用自己的LayoutParams，不可以复用同一个；
         */
        standardParams = new WindowManager.LayoutParams();
        standardParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        standardParams.format = PixelFormat.RGBA_8888;
        // 设置标志,这个两个标志为了让就算出现了悬浮窗，不影响其他区域，只在悬浮窗的区域受干扰
        standardParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        standardParams.x=locationX;
        standardParams.y = locationY;//初始的位置
        //坐标原点定位属性值（以屏幕左边正中间为坐标原点，往上坐标为负值，往下坐标为正值；例如要初始化view在屏幕左上角（standardParams.x=0；standardParams.y =-(1080/2)））
        standardParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

    }

    /**
     * 具体添加那个view
     * 设置添加的view具体的信息（位置，尺寸等）
     *
     * @param context
     * @param layoutId
     */
    public static View windowAddFixedSizeView(Context context, int layoutId, int locationX, int locationY){
        windowAddView(context,locationX,locationY);
        standardParams.width = context.getResources().getDimensionPixelSize(R.dimen.dpi_390px);
        standardParams.height = context.getResources().getDimensionPixelSize(R.dimen.dpi_390px);
        View view  = LayoutInflater.from(context).inflate(layoutId, null);
        windowManager.addView(view, standardParams);
        return view;
    }

    /**
     * 注意：RelativeLayout.LayoutParams
     * 不同的布局要给对应的LayoutParams
     * @param context
     * @param layoutId
     * @param locationX 初始的X坐标
     * @param locationY 初始的Y坐标
     * @return
     */
    public static View windowAddCommonView(Context context, int layoutId,int locationX, int locationY){
        windowAddView(context,locationX,locationY);
        standardParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        standardParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        View view  = LayoutInflater.from(context).inflate(layoutId, null);
        windowManager.addView(view, standardParams);
        return view;
    }

    /**
     * 获取屏幕尺寸
     * @return
     */
    public static int[] getDisplaySize(Context context){
        if (windowManager == null) {
            windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
       return new int[]{dm.widthPixels,dm.heightPixels};
    }


    /**
     * 获取当前view的坐标参数
     * @return
     */
    public static int[] getViewLocationParams(){
        if (standardParams == null) {
            standardParams = new WindowManager.LayoutParams();
        }
        return new int[]{standardParams.x,standardParams.y};
    }

    /**
     * 更新view在屏幕中的坐标
     * @param view
     * @param locationX
     * @param locationY
     */
    public static void updateViewLayoutLocationParams(View view,int locationX,int locationY){
        standardParams.x = locationX;
        standardParams.y = locationY;
        windowManager.updateViewLayout(view, standardParams);
    }
    /**
     * 更新view在屏幕中的坐标
     * @param view
     */
    public static void updateViewLayoutLocationParams(View view){
        windowManager.updateViewLayout(view, standardParams);
    }


    /**
     * 获取 WindowManager
     * @param context
     * @return
     */
    public static WindowManager windowManagerInstance(Context context){
        if (windowManager==null){
            windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }
    public static WindowManager.LayoutParams paramsInstance(){
        if (standardParams == null) {
            standardParams = new WindowManager.LayoutParams();
        }
        return standardParams;
    }


}
