package com.floating.qihang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhengshaorui on 2018/2/5.
 */

@SuppressLint("AppCompatCustomView")
public class LoopText extends TextView {
    public LoopText(Context context) {
        this(context,null);
    }

    public LoopText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoopText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine();
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(true);
    }

    @Override
    public boolean isFocused() {

        return true;
    }
}
