package com.ztmg.cicmorgan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 提供禁止滑动功能的自定义ViewPager
 */
public class MyViewPager extends android.support.v4.view.ViewPager {
    /**
     * 1 默认true 可以滑动; * 2 只需要将返回值改为false，那么ViewPager就不会消耗掉手指滑动的事件了，转而传递给上层View去处理或者该事件就直接终止了。
     */
    private boolean isScrollable = true;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) { /* return false;//super.onTouchEvent(arg0); */
        if (isScrollable)
            return super.onTouchEvent(arg0);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isScrollable)
            return super.onInterceptTouchEvent(arg0);
        else
            return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // try { // return super.onTouchEvent(ev);
    // } catch (IllegalArgumentException ex) {
    // ex.printStackTrace();
    // }
    // return false;
    // }
    //
    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev) {
    // try {
    // return super.onInterceptTouchEvent(ev);
    // } catch (IllegalArgumentException ex) {
    // ex.printStackTrace();
    // }
    // return false;
    // }


}