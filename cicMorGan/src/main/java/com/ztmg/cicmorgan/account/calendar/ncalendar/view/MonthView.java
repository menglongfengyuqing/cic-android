package com.ztmg.cicmorgan.account.calendar.ncalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnClickMonthViewListener;
import com.ztmg.cicmorgan.account.calendar.ncalendar.utils.Attrs;
import com.ztmg.cicmorgan.account.calendar.ncalendar.utils.Utils;
import com.ztmg.cicmorgan.net.Common;


import org.joda.time.DateTime;

import java.util.LinkedHashSet;
import java.util.List;


/**
 * Created by necer on 2017/8/25.
 * QQ群:127278900
 */

public class MonthView extends CalendarView {

    private List<String> lunarList;
    private int mRowNum;
    private OnClickMonthViewListener mOnClickMonthViewListener;


    public MonthView(Context context, DateTime dateTime, OnClickMonthViewListener onClickMonthViewListener) {
        super(context);
        this.mInitialDateTime = dateTime;

        //0周日，1周一
        Utils.NCalendar nCalendar2 = Utils.getMonthCalendar2(dateTime, Attrs.firstDayOfWeek);
        mOnClickMonthViewListener = onClickMonthViewListener;

        lunarList = nCalendar2.lunarList;
        dateTimes = nCalendar2.dateTimeList;

        mRowNum = dateTimes.size() / 7;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        //绘制高度
        mHeight = getDrawHeight();
        mRectList.clear();
        for (int i = 0; i < mRowNum; i++) {
            for (int j = 0; j < 7; j++) {
                Rect rect = new Rect(j * mWidth / 7, i * mHeight / mRowNum, j * mWidth / 7 + mWidth / 7, i * mHeight / mRowNum + mHeight / mRowNum);
                mRectList.add(rect);
                DateTime dateTime = dateTimes.get(i * 7 + j);
                Paint.FontMetricsInt fontMetrics = mSorlarPaint.getFontMetricsInt();

                int baseline;//让6行的第一行和5行的第一行在同一直线上，处理选中第一行的滑动
                if (mRowNum == 5) {
                    baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                } else {
                    baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2 + (mHeight / 5 - mHeight / 6) / 2;
                }

                //当月和上下月的颜色不同
                if (Utils.isEqualsMonth(dateTime, mInitialDateTime)) {
                    //当天和选中的日期不绘制农历
                    if (Utils.isToday(dateTime) && isInit) {//默认显示当前天
                        //mSorlarPaint.setColor(mSelectCircleColor);
                        mSorlarPaint.setColor(mSelectTodayCircleColor);
                        int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
                        canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);

                        //此段代码注释掉，显示今日的颜色就是实心圆，放开就是空心圆
                        //mSorlarPaint.setColor(mHollowCircleColor);
                        //canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);

                        //mSorlarPaint.setColor(mSolarTextColor);
                        mSorlarPaint.setColor(getResources().getColor(R.color.white));
                        canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                    } else if (Utils.isToday(dateTime) && !isInit) {//点击到当前这一天的时候
                        mSorlarPaint.setColor(mSelectTodayCircleColor);
                        int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
                        canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);
                        //mSorlarPaint.setColor(mHollowCircleColor);
                        //canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);

                        mSorlarPaint.setColor(mHollowCircleColor);
                        canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);

                    } else if (mSelectDateTime != null && dateTime.equals(mSelectDateTime)) {//点击时间的状态
                        String mSelect = mSelectDateTime.toString("yyyy-MM-dd");
                        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>(Common.dateList.size());
                        linkedHashSet.clear();
                        linkedHashSet.addAll(Common.dateList);
                        if (linkedHashSet.contains(mSelect)) {
                            mSorlarPaint.setColor(mSelectTodayCircleColor);//包含时间显示跟默认颜色一致
                            int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
                            canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);
                        } else {
                            mSorlarPaint.setColor(mClickSelectCircleColor);
                            int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
                            canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);
                            //mSorlarPaint.setColor(mHollowCircleColor);
                            //canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);
                        }
                        mSorlarPaint.setColor(mHollowCircleColor);
                        canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                    } else {//其他没有状态的显示
                        mSorlarPaint.setColor(mSolarTextColor);
                        canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                        //绘制农历
                        drawLunar(canvas, rect, baseline, mLunarTextColor, i, j);
                        //绘制节假日
                        drawHolidays(canvas, rect, dateTime, baseline);
                        //绘制圆点
                        drawPoint(canvas, rect, dateTime, baseline);
                    }

                } else {
                    mSorlarPaint.setColor(mHintColor);
                    canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                    //绘制农历
                    drawLunar(canvas, rect, baseline, mHintColor, i, j);
                    //绘制节假日
                    drawHolidays(canvas, rect, dateTime, baseline);
                    //绘制圆点
                    //drawPoint(canvas, rect, dateTime, baseline);//上个月 下个月不显示圆点 ,放开就显示
                }
            }
        }
    }

    /**
     * 月日历高度
     *
     * @return
     */
    public int getMonthHeight() {
        return Attrs.monthCalendarHeight;
    }

    /**
     * 月日历的绘制高度，
     * 为了月日历6行时，绘制农历不至于太靠下，绘制区域网上压缩一下
     *
     * @return
     */
    public int getDrawHeight() {
        return (int) (getMonthHeight() - Utils.dp2px(getContext(), 10));
    }


    private void drawLunar(Canvas canvas, Rect rect, int baseline, int color, int i, int j) {
        if (isShowLunar) {
            mLunarPaint.setColor(color);
            String lunar = lunarList.get(i * 7 + j);
            canvas.drawText(lunar, rect.centerX(), baseline + getMonthHeight() / 20, mLunarPaint);
        }
    }

    private void drawHolidays(Canvas canvas, Rect rect, DateTime dateTime, int baseline) {
        if (isShowHoliday) {
            if (holidayList.contains(dateTime.toLocalDate().toString())) {
                mLunarPaint.setColor(mHolidayColor);
                canvas.drawText("休", rect.centerX() + rect.width() / 4, baseline - getMonthHeight() / 20, mLunarPaint);

            } else if (workdayList.contains(dateTime.toLocalDate().toString())) {
                mLunarPaint.setColor(mWorkdayColor);
                canvas.drawText("班", rect.centerX() + rect.width() / 4, baseline - getMonthHeight() / 20, mLunarPaint);
            }
        }
    }

    //绘制圆点
    public void drawPoint(Canvas canvas, Rect rect, DateTime dateTime, int baseline) {
        if (pointList != null && pointList.contains(dateTime.toLocalDate().toString())) {

            mSorlarPaint.setColor(mSelectCircleColor);
            int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
            canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);
            mSorlarPaint.setColor(mHollowCircleColor);
            canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);

            mSorlarPaint.setColor(mSolarTextColor);
            canvas.drawText(dateTime.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);

            //mLunarPaint.setColor(mPointColor);
            //canvas.drawCircle(rect.centerX(), baseline - getMonthHeight() / 15, mPointSize, mLunarPaint);
        }
    }


    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < mRectList.size(); i++) {
                Rect rect = mRectList.get(i);
                if (rect.contains((int) e.getX(), (int) e.getY())) {
                    DateTime selectDateTime = dateTimes.get(i);
                    if (Utils.isLastMonth(selectDateTime, mInitialDateTime)) {
                        // mOnClickMonthViewListener.onClickLastMonth(selectDateTime);//点击上个月
                    } else if (Utils.isNextMonth(selectDateTime, mInitialDateTime)) {
                        //  mOnClickMonthViewListener.onClickNextMonth(selectDateTime);//点击下个月
                    } else {
                        mOnClickMonthViewListener.onClickCurrentMonth(selectDateTime);
                    }
                    break;
                }
            }
            return true;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public int getRowNum() {
        return mRowNum;
    }

    public int getSelectRowIndex() {
        if (mSelectDateTime == null) {
            return 0;
        }
        int indexOf = dateTimes.indexOf(mSelectDateTime);
        return indexOf / 7;
    }


}
