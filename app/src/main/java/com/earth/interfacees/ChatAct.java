package com.earth.interfacees;

import android.view.View;

import com.earth.views.UserInfo;

import earth.client.Util.MessageBean;

/**
 * Created by Frapo on 2017/4/18.
 * Earth 23:52
 */

public interface ChatAct {
    public void receiveNewMessage(MessageBean msB);
    void sentMsg(Object obj);
    boolean isFinishing();
    boolean isDestroyed();
    int onUserInfo(UserInfo ui);
    void onBack(View v);
}
