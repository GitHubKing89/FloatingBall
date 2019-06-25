package com.floating.qihang.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.floating.qihang.R;


/**
 * view动画操作工具
 */
public class AnimatorUtils {

    private static   ObjectAnimator mFloatBallanim = null;

    /**
     * 小球位置状态改变后监听一段时间是否有操作，
     * 无操作判断靠左还是靠右，自动贴边渐变半透明效果
     *
     * @param view
     * @param isRight
     * @param status
     */
    public static void floatBallLocationChangeAnimator(View view, boolean isRight, int status) {
        if (mFloatBallanim!=null){
            mFloatBallanim.cancel();
        }
        if (view != null) {
            ImageView mFloatBallIcon = (ImageView) view.findViewById(R.id.ball_icon);
            if (view.isShown()) {
                if (status == CustomerUtils.FLOAT_HIDE) {
                    mFloatBallIcon.setColorFilter(Color.parseColor("#80c6c5c6"), PorterDuff.Mode.MULTIPLY);
                    if (isRight) {
                        mFloatBallanim = ObjectAnimator.ofFloat(view, "translationX", 0, view.getWidth() / 2);
                    } else {
                        mFloatBallanim = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth() / 2);
                    }
                    mFloatBallanim.setDuration(1000);
                } else {
                    mFloatBallIcon.clearColorFilter();
                    if (isRight) {
                        mFloatBallanim = ObjectAnimator.ofFloat(view, "translationX", 0, 0);
                    } else {
                        mFloatBallanim = ObjectAnimator.ofFloat(view, "translationX", 0, 0);
                    }
                    mFloatBallanim.setDuration(1);
                }
                mFloatBallanim.setInterpolator(new LinearInterpolator());
                mFloatBallanim.start();

            }
        }
    }


    private static final int ANIMATION_TIME = 300;

    public static void floatBallClickChangedAnimator(final View floatView, final View expandView, boolean isExpand) {
        if (isExpand) {
            if (expandView != null) {
                if (!expandView.isShown()) {
                    expandView.setVisibility(View.VISIBLE);
                    alphaAnim(expandView, 0.2f, 1, 10, false);
                }
            }
            if (floatView != null) {
                if (floatView.isShown()) {
                    alphaAnim(floatView, 1f, 0.3f, 30, true);
                }
            }
            //mExpandImp.openView(mExpandView,mParamx,mParamy,mBallSize,windowManager);
            //mExpandImp.openView(mExpandView,mParamx,mParamy,mBallSize);

        } else {
            //closeView();
            // mExpandImp.closeView(mExpandView,mHandler);
            if (floatView != null) {
                if (!floatView.isShown()) {
                    floatView.setVisibility(View.VISIBLE);
                    alphaAnim(floatView, 0.3f, 1f, ANIMATION_TIME, false);
                }
            }
            if (expandView != null) {
                if (expandView.isShown()) {
                    //delayShowView(ANIMATION_TIME+30,mExpandView,View.GONE);
                    alphaAnim(expandView, 1, 0.3f, ANIMATION_TIME + 30, true);
                }
            }
        }

    }

    /**
     * 这个方法用来执行隐藏于显示的动画
     */
    private static void alphaAnim(final View view, float fvalue, float tvalue, int time, final boolean isGone) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", fvalue, tvalue);
        animator.setDuration(time);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isGone) {
                    view.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * 打开所有的子控件
     */
    public static void openViewAnimator(final View floatView, final View expandView, int paramx, int paramy, int ballSize, final boolean isExpand) {
   /*     if (floatView != null) {
            ImageView mFloatBallIcon = (ImageView) floatView.findViewById(R.id.ball_icon);
            mFloatBallIcon.clearColorFilter();
            mFloatBallIcon.clearAnimation();
            floatView.clearAnimation();
        }*/
        ViewGroup viewGroup = (ViewGroup) expandView.findViewById(R.id.expand_ly);
        if (viewGroup != null) {
            int count = viewGroup.getChildCount() - 1;
            float degree = (float) (2 * Math.PI / count); // 360°/个数
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) viewGroup.getLayoutParams();
            int expandSize = expandView.getContext().getResources().getDimensionPixelSize(R.dimen.dpi_390px);
            int radius = expandSize / 3 - 10;//图标距离中位置的系数
            params.x = paramx - (expandSize - ballSize) / 2;//贴边的算法会有问题
            params.y = paramy;
            viewGroup.setLayoutParams(params);
            ProgressWindowManager.windowManagerInstance(expandView.getContext()).updateViewLayout(expandView, params);//移动的是整个展开盒子的view
            AnimatorSet pSet = new AnimatorSet();
            ObjectAnimator pScalex = ObjectAnimator.ofFloat(viewGroup, "scaleX", 0.2f, 1f);
            ObjectAnimator pScaley = ObjectAnimator.ofFloat(viewGroup, "scaleY", 0.2f, 1f);
            ObjectAnimator palpha = ObjectAnimator.ofFloat(viewGroup, "alpha", 0.3f, 1);
            pSet.playTogether(pScalex, pScaley, palpha);
            pSet.setDuration(CustomerUtils.ANIMATION_TIME);
            pSet.setInterpolator(new AccelerateDecelerateInterpolator());
            pSet.start();

            pSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    floatBallClickChangedAnimator(floatView, expandView, isExpand);
                }
            });


            for (int i = 0; i < count; i++) {
                View view = viewGroup.getChildAt(i);
                view.setVisibility(View.VISIBLE);
                //用三角函数自己调角度
                float x = (float) (radius * Math.sin(degree * i));
                float y = -(float) (radius * Math.cos(degree * i));

                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator transxanim = ObjectAnimator.ofFloat(view, "translationX", 0, x);
                ObjectAnimator transyanim = ObjectAnimator.ofFloat(view, "translationY", 0, y);
                ObjectAnimator scalexanim = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 1);
                ObjectAnimator scaleyanim = ObjectAnimator.ofFloat(view, "scaleY", 0.2f, 1);
                animatorSet.playTogether(transxanim, transyanim, scalexanim, scaleyanim);
                animatorSet.setDuration(CustomerUtils.ANIMATION_TIME);
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.start();
            }
        }
    }


    /**
     * 扩展view的关闭效果
     */
    public static void closeViewAnimator(final View floatView, final View expandView, final boolean isExpand, final Handler handler) {
        final ViewGroup viewGroup = (ViewGroup) expandView.findViewById(R.id.expand_ly);

        if (viewGroup != null) {
            int count = viewGroup.getChildCount() - 1;
            AnimatorSet pSet = new AnimatorSet();
            ObjectAnimator pScalex = ObjectAnimator.ofFloat(viewGroup, "scaleX", 1f, 0.4f);
            ObjectAnimator pScaley = ObjectAnimator.ofFloat(viewGroup, "scaleY", 1f, 0.4f);
            ObjectAnimator palpha = ObjectAnimator.ofFloat(viewGroup, "alpha", 1, 0f);
            pSet.playTogether(pScalex, pScaley, palpha);
            pSet.setDuration(CustomerUtils.ANIMATION_TIME);
            pSet.setInterpolator(new AccelerateDecelerateInterpolator());
            pSet.start();
            pSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    AnimatorUtils.floatBallClickChangedAnimator(floatView, expandView, isExpand);
                }
            });

            int radius = CustomerUtils.EXPAND_ANIM_LENGTH;
            float degree = (float) (2 * Math.PI / count); // 360°/个数
            for (int i = 0; i < count; i++) {
                final View view = viewGroup.getChildAt(i);
                view.setVisibility(View.VISIBLE);
                float x = (float) (radius * Math.sin(degree * i));
                float y = -(float) (radius * Math.cos(degree * i));

                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator transxanim = ObjectAnimator.ofFloat(view, "translationX", x, 0);
                ObjectAnimator transyanim = ObjectAnimator.ofFloat(view, "translationY", y, 0);
                ObjectAnimator scalexanim = ObjectAnimator.ofFloat(view, "scaleX", 1, 0.2f);
                ObjectAnimator scaleyanim = ObjectAnimator.ofFloat(view, "scaleY", 1, 0.2f);
                animatorSet.playTogether(transxanim, transyanim, scalexanim, scaleyanim);
                animatorSet.setDuration(CustomerUtils.ANIMATION_TIME);
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        viewGroup.setBackgroundResource(R.mipmap.circle_bg);
                        view.setVisibility(View.GONE);
                        //3s内没操作
                        handler.removeMessages(CustomerUtils.START_TO_MOVE);
                        handler.sendEmptyMessageDelayed(CustomerUtils.START_TO_MOVE, 3000);
                    }
                });

            }

        }
    }
}
