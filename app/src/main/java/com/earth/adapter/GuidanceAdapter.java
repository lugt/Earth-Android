package com.earth.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class GuidanceAdapter extends PagerAdapter{
	private Context context;
	private List<View> mList=new ArrayList<View>();
public GuidanceAdapter(Context context,List<View> mList){
	this.context=context;
	this.mList=mList;
}
	@Override
	public int getCount() {

		return mList.size();
	}
	public int getItemPosition(Object object) {

		return super.getItemPosition(object);
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
@Override
public void destroyItem(ViewGroup container, int position, Object object) {
	// TODO Auto-generated method stub
	container.removeView(mList.get(position));
}
@Override
public Object instantiateItem(ViewGroup container, int position) {
	// TODO Auto-generated method stub
	container.addView(mList.get(position));
	return mList.get(position);
}
}
