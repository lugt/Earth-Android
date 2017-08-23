package com.earth.define;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * ScrollView
 * 滑动
 */
public class BounceScrollView extends ScrollView {
	private View inner;// 

	private float y;// 

	private Rect normal = new Rect();

	private boolean isCount = false;

	public BounceScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	//触摸
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner != null) {
			commOnTouchEvent(ev);
		}

		return super.onTouchEvent(ev);
	}

	
	//通过滑动来进行判断
	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:

			if (isNeedAnimation()) {
				animation();
				isCount = false;
			}
			break;
		/***
		 *
		 */
		case MotionEvent.ACTION_MOVE:
			final float preY = y;// 
			float nowY = ev.getY();// 
			int deltaY = (int) (preY - nowY);// 
			if (!isCount) {
				deltaY = 0; //
			}

			y = nowY;
		
			if (isNeedMove()) {
				
				if (normal.isEmpty()) {
					
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
				}

		
				inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,
						inner.getRight(), inner.getBottom() - deltaY / 2);
			}
			isCount = true;
			break;

		default:
			break;
		}
	}


	public void animation() {
	
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
				normal.top);
		ta.setDuration(200);
		inner.startAnimation(ta);
		
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);

		normal.setEmpty();

	}


	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	
	public boolean isNeedMove() {
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		Log.e("jj", "scrolly=" + scrollY);

		return scrollY == 0 || scrollY == offset;
	}

}
