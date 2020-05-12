package com.ztmg.cicmorgan.account.calendar.ncalendar.listener;

import org.joda.time.DateTime;

/**
 * Created by necer on 2017/7/4.
 */

public interface OnCalendarChangedListener {
    void onCalendarChanged(DateTime dateTime);
    void onCalendarClickChanged(DateTime dateTime);
}
