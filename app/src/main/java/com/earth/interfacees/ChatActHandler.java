package com.earth.interfacees;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.earth.views.Chating;
import com.earth.views.MainChat;

import java.lang.ref.WeakReference;

import earth.client.Util.MessageBean;

/**
 * Created by God on 2017/2/12.
 */

public class ChatActHandler extends Handler {

    WeakReference<ChatAct> chat;

    public ChatActHandler(ChatAct ch){
        chat = new WeakReference<ChatAct>(ch);
    }

    public void handleMessage (Message msg) {
        switch (msg.what){
            case 2000:
                chat.get().receiveNewMessage(
                        (MessageBean) msg.obj);
                break;
            case 2300:
                chat.get().sentMsg(msg.obj);
        }
    }

    public boolean isAlive(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return chat.get() == null || !chat.get().isFinishing() || ! chat.get().isDestroyed();
        }else{
            return chat.get() == null || !chat.get().isFinishing();
        }
    }
}

