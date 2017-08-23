package com.earth.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.earth.data.Initialize;

import java.io.IOException;

import earth.client.LocalHandler;
import earth.client.Short.ShortClient;
import earth.client.Util.Constant;
import earth.client.Util.Monitor;
import earth.seagate.handler.CrashHandler;

public class MainActivity extends Activity {

    public static final String IS_FIRST = "isFirstLabel";
    public static final String ACTION_START = "astart";
    public static final String ACTION_STOP = "astop";
    public static final String ACTION_CANCEL = "acancel";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1032;

    private void initials() {
        LocalHandler handler = new LocalHandler();
        Constant.setHandler(handler);
        ShortClient.init(Constant.SHORTSERVER,Constant.SHORTPORT);
        //CrashHandler crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());  //传入参数必须为Activity，否则AlertDialog将不显示。
        Initialize.InitInOrder(this.getApplicationContext());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.initial);
        if(!com.earth.xo.Constant.isMainLoaded || savedInstanceState.getBoolean("loaded")){
            // Init / or / Reuse;
            com.earth.xo.Constant.isMainLoaded = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            startAsNew();
        }else{
            // 判断Short / Long 是否在线
            // how to call to the board;
            calltheboard();
        }
    }


    public void onSaveInstanceState(Bundle out){
        super.onSaveInstanceState(out);
        out.putBoolean("loaded",true);
        /*

        onSaveInstanceState并不是像文档里说的，是在内存不够时，系统要强制杀掉这个Activity时，进行调用的。在这几钟情况下会被调用：

        1.当用户按下HOME键时。

        2.从任务管理器切换其他应用的时候

        3.关闭屏幕的时候

        4.跳转到另一个Activity的时候

        5.横竖屏切换的时候

        如果执行了finish()方法onSaveInstanceState就不会执行了，比如按返回键

        */
    }

    private void calltheboard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent in = new Intent();
                    SharedPreferences share = getSharedPreferences("conf", Context.MODE_PRIVATE);
                    boolean isfirst = share.getBoolean(IS_FIRST, true);
                    if (!isfirst) {
                        // is First Time = IMEI Register
                        in.setClass(MainActivity.this, WelcomeBack.class);
                    } else {
                        in.setClass(MainActivity.this, PassAuthAct.class);
                    }
                    startActivity(in);
                    finish();
                }
            }
        }, 100);
    }

    private void startAsNew() {

        getLogPermit();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Monitor.setLog();
        }else{
            //
            Toast.makeText(this,"暂时关闭了体验改进功能",Toast.LENGTH_SHORT).show();
            Monitor.setInnerLog();
        }

        initials();

        calltheboard();

        try {
            Cursor cur = Initialize.listConf();
            int ans = cur.getCount();
            com.earth.xo.Constant.loadConf(cur);
            Monitor.logger("Conf-load-list: " + ans);
            Log.i("CONF-LOAD", com.earth.xo.Constant.getConfStr());
            if(cur.getCount() <= 0) {
                Initialize.insertConf("last_start",System.currentTimeMillis() + "");
            }else{
                Initialize.updateConf("last_start",System.currentTimeMillis() + "");
            }
        }catch (Exception e){
            //Initialize.createConf();
            e.printStackTrace();
        }
    }

    private void getLogPermit() {

        //hisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //Monitor.sendlf("http://lllkkk.kkkj");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //

                //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
              //  calltheboard();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            //Granted
            //calltheboard();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Monitor.setLog();

                } else {
                    Monitor.setInnerLog();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
        calltheboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
    }


}
