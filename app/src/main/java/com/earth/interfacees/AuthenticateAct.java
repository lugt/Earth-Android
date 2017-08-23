package com.earth.interfacees;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Frapo on 2017/4/17.
 * Earth 11:58
 */

public interface AuthenticateAct {
    Context getApplicationContext();
    void setAlert(String txt);
    void onFinishLogin();
    void onFinishReg();
}
