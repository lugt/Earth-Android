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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.earth.data.Initialize;
import com.earth.interfacees.AuthenticateAct;
import com.earth.xo.NetworkUtil;

import earth.client.LocalHandler;
import earth.seagate.handler.CrashHandler;
import earth.seagate.handler.LoginActivityHandler;
import earth.seagate.handler.UserInterfaces;

public class PassAuthAct extends Activity implements AuthenticateAct {

	private EditText name;
	private EditText password;
    private String s13,s14;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        CrashHandler.getInstance().init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		 //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.activity_login);


        name = (EditText) findViewById(R.id.name);
		password = (EditText) findViewById(R.id.loginpassword);

        Handler hdl = new LoginActivityHandler(this);

        LocalHandler.setHandler(hdl);
        if(!NetworkUtil.isNetworkConnected(getApplicationContext())){
            ((TextView)findViewById(R.id.loginanswer)).setText("请检查网络连接");
        }
        new Thread() {
            public void run(){
                try {
                   Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserInterfaces.getApplicationInfo();
            }
        }.start();
	}


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
        Toast.makeText(getApplicationContext(),"正在加载",Toast.LENGTH_LONG).show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        final String s11 = name.getText().toString();
        final String s21 = password.getText().toString();

        s13 = s11;
        s14 = s21;

        if(!isNew) {
            new Thread() {
                public void run() {
                    UserInterfaces.authLogin(s11, s21);
                }
            }.start();
        }else{
            new Thread() {
                public void run() {
                    UserInterfaces.authReg(s11, s21);
                }
            }.start();
        }
	}


    private static final String IS_FIRST = "isFirstLabel";

    public void onFinishLogin(){
        //Toast.makeText(getApplicationContext(), "欢迎登陆" + s1 + "\n" + s2,
        //        Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PassAuthAct.this, BoardActivity.class);
        intent.putExtra("first",true);

        try {
            Initialize.insertConf("usname", s13);
            Initialize.insertConf("uspwd", s14);
        } catch (Exception e) {
            try {
                Initialize.updateConf("usname", s13);
                Initialize.updateConf("uspwd", s14);
            }catch (Exception es){
                // ...
                e.printStackTrace();
                es.printStackTrace();
            }
            //e.printStackTrace();
        }

        SharedPreferences share=getSharedPreferences("conf",Context. MODE_PRIVATE);
        share.edit().putBoolean(IS_FIRST,false).apply();

        startActivity(intent);
        finish();
    }

    @Override
    public void onFinishReg() {
        isNew = false;
        Button btn = (Button) findViewById(R.id.createuser);
        btn.setText("新用户");
        ((Button)findViewById(R.id.loginbtn)).setText("登录");
        ((TextView)findViewById(R.id.loginanswer)).setText("注册成功");
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
