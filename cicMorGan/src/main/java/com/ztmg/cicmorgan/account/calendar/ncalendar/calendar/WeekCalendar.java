package com.ztmg.cicmorgan.account.calendar.ncalendar.calendar;

import org.joda.time.DateTime;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.calendar.ncalendar.adapter.CalendarAdapter;
import com.ztmg.cicmorgan.account.calendar.ncalendar.adapter.WeekAdapter;
import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnClickWeekViewListener;
import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnWeekCalendarChangedListener;
import com.ztmg.cicmorgan.account.calendar.ncalendar.utils.Attrs;
import com.ztmg.cicmorgan.account.calendar.ncalendar.utils.Utils;
import com.ztmg.cicmorgan.account.calendar.ncalendar.view.CalendarView;
import com.ztmg.cicmorgan.account.calendar.ncalendar.view.WeekView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;


/**
 * Created by necer on 2017/8/30.
 * QQ群:127278900
 */

public class WeekCalendar extends CalendarPager implements OnClickWeekViewListener {

    private OnWeekCalendarChangedListener onWeekCalendarChangedListener;

    public WeekCalendar(Context context) {
        super(context);
    }

    public WeekCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CalendarAdapter getCalendarAdapter() {

        mPageSize = Utils.getIntervalWeek(startDateTime, endDateTime, Attrs.firstDayOfWeek) + 1;
        mCurrPage = Utils.getIntervalWeek(startDateTime, mInitialDateTime, Attrs.firstDayOfWeek);

        return new WeekAdapter(getContext(), mPageSize, mCurrPage, mInitialDateTime, this);
    }


    private int lastPosition = -1;

    @Override
    protected void initCurrentCalendarView(int position) {

        WeekView currView = (WeekView) calendarAdapter.getCalendarViews().get(position);
        WeekView lastView = (WeekView) calendarAdapter.getCalendarViews().get(position - 1);
        WeekView nextView = (WeekView) calendarAdapter.getCalendarViews().get(position + 1);
        if (currView == null)
            return;

        if (lastView != null)
            lastView.clear();

        if (nextView != null)
            nextView.clear();

        //只处理翻页
        if (lastPosition == -1) {
            currView.setDateTimeAndPoint(mInitialDateTime, pointList, true);
            mSelectDateTime = mInitialDateTime;
            lastSelectDateTime = mInitialDateTime;
            if (onWeekCalendarChangedListener != null) {
                onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDateTime);
            }
        } else if (isPagerChanged) {
            int i = position - lastPosition;
            mSelectDateTime = mSelectDateTime.plusWeeks(i);

            if (isDefaultSelect) {
                //日期越界
                if (mSelectDateTime.getMillis() > endDateTime.getMillis()) {
                    mSelectDateTime = endDateTime;
                } else if (mSelectDateTime.getMillis() < startDateTime.getMillis()) {
                    mSelectDateTime = startDateTime;
                }

                currView.setDateTimeAndPoint(mSelectDateTime, pointList, false);
                if (onWeekCalendarChangedListener != null) {
                    onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDateTime);
                }
            } else {
                if (Utils.isEqualsMonth(lastSelectDateTime, mSelectDateTime)) {
                    currView.setDateTimeAndPoint(lastSelectDateTime, pointList, false);
                }
            }

        }
        lastPosition = position;
    }

    public void setOnWeekCalendarChangedListener(OnWeekCalendarChangedListener onWeekCalendarChangedListener) {
        this.onWeekCalendarChangedListener = onWeekCalendarChangedListener;
    }


    @Override
    protected void setDateTime(DateTime dateTime) {

        if (dateTime.getMillis() > endDateTime.getMillis() || dateTime.getMillis() < startDateTime.getMillis()) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseArray<CalendarView> calendarViews = calendarAdapter.getCalendarViews();
        if (calendarViews.size() == 0) {
            return;
        }

        isPagerChanged = false;

        WeekView currectWeekView = (WeekView) calendarViews.get(getCurrentItem());

        //不是当周
        if (!currectWeekView.contains(dateTime)) {

            DateTime initialDateTime = currectWeekView.getInitialDateTime();
            int weeks = Utils.getIntervalWeek(initialDateTime, dateTime, Attrs.firstDayOfWeek);
            int i = getCurrentItem() + weeks;
            setCurrentItem(i, Math.abs(weeks) < 2);
            currectWeekView = (WeekView) calendarViews.get(getCurrentItem());
        }

        currectWeekView.setDateTimeAndPoint(dateTime, pointList, false);
        mSelectDateTime = dateTime;
        lastSelectDateTime = dateTime;

        isPagerChanged = true;

        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDateTime);
        }
    }


    @Override
    public void onClickCurrentWeek(DateTime dateTime) {

        if (dateTime.getMillis() > endDateTime.getMillis() || dateTime.getMillis() < startDateTime.getMillis()) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        WeekView weekView = (WeekView) calendarAdapter.getCalendarViews().get(getCurrentItem());
        weekView.setDateTimeAndPoint(dateTime, pointList, false);
        mSelectDateTime = dateTime;
        lastSelectDateTime = dateTime;
        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(dateTime);
        }

    }
}
