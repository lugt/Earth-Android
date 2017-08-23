package com.earth.define;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;


//橢圓  图形处理
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

	public RoundImageView(Context context, AttributeSet attribute) {
		super(context, attribute);
		init();
	}

	public RoundImageView(Context context) {
		super(context);
		init();
	}

	private final RectF roundrect = new RectF();
	private final Paint maskpaint = new Paint();
	private final Paint zonepaint = new Paint();
	private float rect = 110;

	private void init() {
		// TODO Auto-generated method stub
		maskpaint.setAntiAlias(true);//抗锯齿
		maskpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//分辨率
		// maskPaint.setXfermode(new
		// PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		zonepaint.setAntiAlias(true);
		zonepaint.setColor(Color.WHITE);
		float density = getResources().getDisplayMetrics().density;
		rect = rect * density;

	}

	public void setRectAdius(float adius) {
		rect = adius;
		invalidate();
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int width = getWidth();
		int height = getHeight();
		roundrect.set(0, 0, width, height);
	}

	/**
	 * 生成新的涂成，draw画的图在这上面进行 最后一定要用drawRoundRect形成一个新的涂成。
	 */

	public void draw(Canvas canvas) {
		canvas.saveLayer(roundrect, zonepaint, Canvas.ALL_SAVE_FLAG);
		canvas.drawRoundRect(roundrect, rect, rect, zonepaint);
		canvas.saveLayer(roundrect, maskpaint, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
		canvas.restore();
	}
}
