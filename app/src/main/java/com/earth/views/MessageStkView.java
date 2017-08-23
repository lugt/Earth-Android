package com.earth.views;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Frapo on 2017/6/13.
 * Earth 18:02
 */


public class MessageStkView extends Fragment {

    private static final String EXTRA_COLOR = "com.bartoszlipinski.flippablestackview.fragment.ColorFragment.EXTRA_COLOR";

    RelativeLayout mMainLayout;

    public static MessageStkView newInstance(int backgroundColor) {
        MessageStkView fragment = new MessageStkView();
        Bundle bdl = new Bundle();
        bdl.putInt(EXTRA_COLOR, backgroundColor);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.act_engage, container, false);
        Bundle bdl = getArguments();

        mMainLayout = (RelativeLayout) v.findViewById(R.id.actege);

        LayerDrawable bgDrawable = (LayerDrawable) mMainLayout.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.background_shape);
        shape.setColor(bdl.getInt(EXTRA_COLOR));

        return v;
    }
}

