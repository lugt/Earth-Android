package com.earth.adapter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.RadioGroup;

import java.util.List;

public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {
	private List<Fragment> fragments;
	private RadioGroup rgs;
	private int fragmentContentId;
	private Fragment fragment;
	private int currentTab;
	private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener;

	public FragmentTabAdapter(Fragment fragment, List<Fragment> fragments,
			int fragmentContentId, RadioGroup rgs) {
		this.fragments = fragments;
		this.rgs = rgs;
		this.fragment = fragment;
		this.fragmentContentId = fragmentContentId;

		FragmentTransaction ft = fragment.getFragmentManager()
				.beginTransaction();
		ft.add(fragmentContentId, fragments.get(0));
		ft.commit();

		rgs.setOnCheckedChangeListener(this);

	}

	/**
	 * 这种情况，在切换Fragment的时候，需要在Add Fragment的时候，设置一个TAG；
	 */

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		for (int i = 0; i < rgs.getChildCount(); i++) {
			if (rgs.getChildAt(i).getId() == checkedId) {
				Fragment fragment = fragments.get(i);
				FragmentTransaction ft = obtainFragmentTransaction(i);

				getCurrentFragment().onPause();

				if (fragment.isAdded()) {

					fragment.onResume();
				} else {
					ft.add(fragmentContentId, fragment);
				}
				showTab(i);
				ft.commit();

			}
		}

	}

	/**
	 * 
	 * @param idx
	 */
	private void showTab(int idx) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
		currentTab = idx; //
	}

	/**
	 * 
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fragment.getFragmentManager()
				.beginTransaction();

		return ft;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public Fragment getCurrentFragment() {
		return fragments.get(currentTab);
	}

	public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
		return onRgsExtraCheckedChangedListener;
	}

	public void setOnRgsExtraCheckedChangedListener(
			OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
		this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
	}

	/**
	     *  
	     */
	static class OnRgsExtraCheckedChangedListener {
		public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
				int checkedId, int index) {
		}
	}
}
