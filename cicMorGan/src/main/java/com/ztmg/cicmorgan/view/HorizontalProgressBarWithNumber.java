package com.ztmg.cicmorgan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;


public class HorizontalProgressBarWithNumber extends ProgressBar {

	private static final int DEFAULT_TEXT_SIZE = 10;
	private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#E5343A");

	private static final int DEFAULT_COLOR_UNREACHED_COLOR = Color.parseColor("#B5B3B4");
	private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
	private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
	private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
	/**
	 * painter of all drawing things
	 */
	protected Paint mPaint = new Paint();
	/**
	 * color of progress number
	 */
	protected int mTextColor = DEFAULT_TEXT_COLOR;
	/**
	 * size of text (sp)
	 */
	protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

	/**
	 * offset of draw progress
	 */
	protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

	/**
	 * height of reached progress bar
	 */
	protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

	/**
	 * color of reached bar
	 */
	protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
	/**
	 * color of unreached bar
	 */
	protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
	/**
	 * height of unreached progress bar
	 */
	protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
	/**
	 * view width except padding
	 */
	protected int mRealWidth;

	protected boolean mIfDrawText = true;

	protected static final int VISIBLE = 0;

	protected Paint xPaint = new Paint();

	protected  double currentProgress=0;
	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);

		xPaint.setTextSize(mTextSize);
		xPaint.setColor(mTextColor);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);

		mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			float textHeight = (mPaint.descent() - mPaint.ascent());
			result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
					Math.max(mReachedProgressBarHeight,
							mUnReachedProgressBarHeight), Math.abs(textHeight)));
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}




	@Override
	protected synchronized void onDraw(Canvas canvas) {

		canvas.save();
		canvas.translate(getPaddingLeft(), getHeight() / 2);

		boolean noNeedBg = false;
		//	float radio = getProgress() * 1.0f / getMax();
		float radio = (float) (getDoubleProgress() * 1.0f / getMax());

		float progressPosX = (int) (mRealWidth * radio);
		//	String text = getProgress() + "%";
		String text=getDoubleProgress()+ "%";
		// mPaint.getTextBounds(text, 0, text.length(), mTextBound);
		if(getDoubleProgress()==100){
			mPaint.setColor(mUnReachedBarColor);
		}else{
			mPaint.setColor(mTextColor);
		}

		if(getDoubleProgress()==100){
			xPaint.setColor(mUnReachedBarColor);
		}else{
			xPaint.setColor(mTextColor);
		}

		float textWidth = mPaint.measureText(text);
		float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

		if (progressPosX + textWidth > mRealWidth) {
			progressPosX = mRealWidth - textWidth;
			noNeedBg = true;
		}
		if(currentProgress==100){
			// draw reached bar
			float endX = progressPosX - mTextOffset / 2;
			if (endX > 0) {
				mPaint.setColor(mUnReachedBarColor);
				mPaint.setStrokeWidth(mReachedProgressBarHeight);
				canvas.drawLine(0, 0, endX, 0, mPaint);
			}

		}else{
			// draw reached bar
			float endX = progressPosX - mTextOffset / 2;
			if (endX > 0) {
				mPaint.setColor(mReachedBarColor);
				mPaint.setStrokeWidth(mReachedProgressBarHeight);
				canvas.drawLine(0, 0, endX, 0, mPaint);
			}
		}
		//draw progress bar
		// measure text bound
		if (mIfDrawText) {
			float length=mPaint.measureText(text);
			RectF rect;
			if(currentProgress<=100&&currentProgress>=90){
				rect=new RectF(progressPosX-10, textHeight-15,progressPosX+length, -textHeight+15);
			}else if(currentProgress<=1&&currentProgress>=0){
				rect=new RectF(progressPosX, textHeight-15,progressPosX+length+10, -textHeight+15);
			}else{
				rect=new RectF(progressPosX-10, textHeight-15,progressPosX+length+10, -textHeight+15);
			}
			canvas.drawRoundRect(rect,

					30, //x轴的半径

					30, //y轴的半径

					xPaint);
			//canvas.drawRect(rf,xPaint);
			mPaint.setColor(Color.WHITE);
			canvas.drawText(text, progressPosX, -textHeight, mPaint);

		}

		// draw unreached bar
		if (!noNeedBg) {
			float start = progressPosX + mTextOffset / 2 + textWidth;
			mPaint.setColor(mUnReachedBarColor);
			mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
			canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
		}


		canvas.restore();

	}

	/**
	 * dp 2 px
	 * 
	 * @param dpVal
	 */
	protected int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	/**
	 * sp 2 px
	 * 
	 * @param spVal
	 * @return
	 */
	protected int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, getResources().getDisplayMetrics());

	}

	public void setDoubleProgress(double num){
		currentProgress=num;
		invalidate();
	}
	public double getDoubleProgress(){
		return  currentProgress;
	}

}
