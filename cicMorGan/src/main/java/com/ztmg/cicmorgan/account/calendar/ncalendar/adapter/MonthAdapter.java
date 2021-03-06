package com.ztmg.cicmorgan.account.calendar.ncalendar.adapter;


import org.joda.time.DateTime;

import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnClickMonthViewListener;
import com.ztmg.cicmorgan.account.calendar.ncalendar.view.MonthView;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by necer on 2017/8/28.
 * QQ群:127278900
 */

public class MonthAdapter extends CalendarAdapter {

    private OnClickMonthViewListener mOnClickMonthViewListener;

    public MonthAdapter(Context mContext, int count, int curr, DateTime dateTime, OnClickMonthViewListener onClickMonthViewListener) {
        super(mContext, count, curr, dateTime);
        this.mOnClickMonthViewListener = onClickMonthViewListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        MonthView nMonthView = (MonthView) mCalendarViews.get(position);
        if (nMonthView == null) {
            int i = position - mCurr;
            DateTime dateTime = this.mDateTime.plusMonths(i);
            nMonthView = new MonthView(mContext, dateTime, mOnClickMonthViewListener);
            mCalendarViews.put(position, nMonthView);
        }
        container.addView(nMonthView);
        return nMonthView;
    }
}
