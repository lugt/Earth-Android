package com.earth.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.earth.interfacees.AuthenticateAct;
import com.earth.xo.Constant;
import com.earth.xo.NetworkUtil;

import earth.client.LocalHandler;
import earth.seagate.handler.CrashHandler;
import earth.seagate.handler.LoginActivityHandler;
import earth.seagate.handler.UserInterfaces;

public class WelcomeBack extends Activity implements AuthenticateAct {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //CrashHandler.getInstance().init(this);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.activity_welcome);

        Handler hdl = new LoginActivityHandler(this);
        LocalHandler.setHandler(hdl);

        s1 = Constant.getConf("usname");
        s2 = Constant.getConf("uspwd");

        if(s1 == null || s2 == null){
            // Error;
            Intent intent = new Intent();
            intent.setClass(this,PassAuthAct.class);
            startActivity(intent);
            finish();
            return ;
        }

        new Thread() {
            public void run(){
                try {
                   Thread.sleep(700L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!NetworkUtil.isNetworkConnected(getApplicationContext())){
                    ((TextView)findViewById(R.id.loginanswer)).setText("请检查网络连接");
                }

                UserInterfaces.getApplicationInfo();
            }
        }.start();
	}

    private String s1;
    private String s2;

    public boolean isNew = false;
    public void NewUser(View v){
        // OK
        isNew = !isNew;
        if(!isNew){
            Button btn = (Button) v;
            btn.setText("新用户");
            ((Button)findViewById(R.id.loginbtn)).setText("登录");
        }else {
            Button btn = (Button) v;
            btn.setText("已有账号");
            ((Button)findViewById(R.id.loginbtn)).setText("创建账号");
        }
    }


	public void Login(View v) {
        UserInterfaces.authLogin(s1, s2);
	}

    public void onFinishLogin(){
        //Toast.makeText(getApplicationContext(), "欢迎登陆" + s1 + "\n" + s2,
        //        Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WelcomeBack.this, BoardActivity.class);
        startActivity(intent);
        finish();
        System.gc();
    }

    @Override
    public void onFinishReg() {
        // not carable
    }

    public void ChangeAcc(View v){
        //
        SharedPreferences share=getSharedPreferences("conf", Context. MODE_PRIVATE);
        share.edit().putBoolean(MainActivity.IS_FIRST,false).apply();
        Intent intent = new Intent(WelcomeBack.this, PassAuthAct.class);
        startActivity(intent);
        finish();
        System.gc();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

    private Handler mResetHand = new Handler();
    private Runnable clearRun = new Runnable() {
        public void run() {
            //execute the task
            ((TextView) findViewById(R.id.loginanswer)).setText("");
        }
    };
    public void setAlert(String txt) {
        ((TextView)findViewById(R.id.loginanswer)).setText(txt);
        mResetHand.removeCallbacks(clearRun);
        mResetHand.postDelayed(clearRun,5000);
    }
}
