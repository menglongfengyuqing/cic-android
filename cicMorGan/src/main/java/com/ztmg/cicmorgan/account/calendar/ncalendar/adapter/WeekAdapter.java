package com.ztmg.cicmorgan.account.calendar.ncalendar.adapter;

import org.joda.time.DateTime;

import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnClickWeekViewListener;
import com.ztmg.cicmorgan.account.calendar.ncalendar.view.WeekView;

import android.content.Context;
import android.view.ViewGroup;


/**
 * Created by necer on 2017/8/30.
 * QQ群:127278900
 */

public class WeekAdapter extends CalendarAdapter {

    private OnClickWeekViewListener mOnClickWeekViewListener;

    public WeekAdapter(Context mContext, int count, int curr, DateTime dateTime, OnClickWeekViewListener onClickWeekViewListener) {
        super(mContext, count, curr, dateTime);
        this.mOnClickWeekViewListener = onClickWeekViewListener;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        WeekView nWeekView = (WeekView) mCalendarViews.get(position);
        if (nWeekView == null) {
            nWeekView = new WeekView(mContext, mDateTime.plusDays((position - mCurr) * 7),mOnClickWeekViewListener);
            mCalendarViews.put(position, nWeekView);
        }
        container.addView(mCalendarViews.get(position));
        return mCalendarViews.get(position);
    }
}
