package com.ztmg.cicmorgan.account.picture;

import com.ztmg.cicmorgan.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 作者：bcq on 2017/3/20 10:19
 */

public class MyImageView extends ImageView {
	private boolean isClick;
	private static final int cx = 15;// 半径
	private static final int cdp = 5;// 间隔
	Paint mPaint;
	Bitmap bmp;
	Matrix mMatrix = new Matrix();

	public boolean isClick() {
		return isClick;
	}

	public void setClick(boolean click) {
		isClick = click;
		invalidate();
	}

	public MyImageView(Context context) {
		this(context, null);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, R.drawable.vip);

	}

	private void initView(Context ctx, int resId) {
		mPaint = new Paint();
		bmp = BitmapFactory.decodeResource(ctx.getResources(), resId);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		int mWidth = bmp.getWidth() / 2;
		int l = width - cdp - mWidth;
		int top = cdp;
		int r = width - cdp;
		int b = mWidth + cdp;
		super.onDraw(canvas);
		if (isClick) {
			 canvas.save();
	            mMatrix.setScale(0.5f, 0.5f);
	            canvas.drawColor(0x86555555);
	            canvas.translate(l, top);
	            canvas.drawBitmap(bmp, mMatrix, null);
	            canvas.restore();

		} else {
			canvas.drawColor(0x00000000);
		}
	}
}
