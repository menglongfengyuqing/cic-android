package com.ztmg.cicmorgan.view;

import com.ztmg.cicmorgan.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Config;
import android.widget.ProgressBar;
/**
 * 投资列表的进度条
 * @author pc
 *
 */
public class TextProgressBar extends ProgressBar{
	
	private String str;
	private Paint mPaint;
	private Canvas mCanvas;

	public TextProgressBar(Context context)
	{
		super(context);
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initText();
	}

	@Override
	public void setProgress(int progress)
	{
		setText(progress);
		super.setProgress(progress);

	}

	@Override
	protected synchronized void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.str, 0, this.str.length(), rect);
		int x = (getWidth() / 2) - rect.centerX();// 让现实的字体处于中心位置;;
		int y = (getHeight() / 2) - rect.centerY();// 让显示的字体处于中心位置;;
		canvas.drawText(this.str, x, y, this.mPaint);
		
	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);   
	    canvas.drawBitmap(bitmap, 250,360, this.mPaint); 
	}

	// 初始化，画笔
	private void initText()
	{
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);// 设置抗锯齿;;;;
		this.mPaint.setColor(Color.WHITE);
	}

	// 设置文字内容
	private void setText(int progress)
	{
		int i = (int) ((progress * 1.0f / this.getMax()) * 100);
		this.str = String.valueOf(i) + "%";
	}
}
