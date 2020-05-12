package com.ztmg.cicmorgan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * dongdong on 2018/6/4.
 * <p>
 * 自定义seek的方法
 */

public class MySeekBar extends SeekBar {
    /**
     * 是否支持拖动进度
     */
    private boolean touch = false;

    public MySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //LogUtil.getLog().d("voice progressbar onDraw");
        super.onDraw(canvas);

    }

    public void setTouch(boolean touch) {
        this.touch = touch;
    }

    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touch) {
            return super.onTouchEvent(event);
        }
        return false;
    }
}