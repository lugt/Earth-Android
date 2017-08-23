package com.earth.views;

/**
 * Created by Frapo on 2017/4/17.
 * Earth 11:02
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import com.earth.adapter.SwipeAdapter;
import com.earth.data.Initialize;
import com.earth.fragment.SwipeListView;
import com.earth.interfacees.ChatAct;
import com.earth.interfacees.ChatActHandler;
import com.earth.xo.FriendsMessage;

import java.util.ArrayList;
import java.util.List;

import earth.client.LocalHandler;
import earth.client.Util.MessageBean;
import earth.seagate.handler.CrashHandler;
import earth.seagate.handler.FriendManager;
import earth.seagate.handler.MainActivityHandler;
import earth.seagate.handler.UserInterfaces;

public class EngageActivity extends AppCompatActivity implements ChatAct {

    private String TAG = "EngageAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //CrashHandler.getInstance().init(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_engage);

        initUser();
        MainActivityHandler.seHandler(new ChatActHandler(this), user.etid);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initUI();
            }
        }).start();
    }

    private UserInfo user;// 聊天人

    private void initUser() {
        Intent si = getIntent();
        String x = si.getStringExtra("etid");
        if (x == null) {
            finish();
            return;
        }
        Long a = 0L;
        try{
            a = Long.parseLong(x,16);
            user = FriendManager.get(a);
            // Set User
        }catch (Exception e){
            e.printStackTrace();
            LocalHandler.innerCall(-3400,null);
            finish();
            return;
        }
    }

    private void initUI() {
        loadAllMsgs();
    }

    private List<MessageBean> MessageList = new ArrayList<>();

    private int loadAllMsgs(){
        try {
            List<MessageBean> mBs = Initialize.loadMsg(this.user,getApplicationContext());
            this.MessageList.addAll(mBs);
            mBs = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"Error Occured in loading Msgs");
        }
        return 0;
    }

    public int onUserInfo(UserInfo ui){
        // 处理获得的UserInfo更新
        return 0;
    }

    public void onClickRight(View v){
        //
        Intent in = new Intent(getApplicationContext(), Chating.class);
        in.putExtra("etid", user.etid);
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receiveNewMessage(MessageBean msB) {
        // Rcvd new Msg
        //
    }

    public void sentMsg(Object obj) {
        if(obj instanceof String){
            String s= (String) obj;
            String[] x = s.split(",");
            MessageBean msB;
            try {
                int time = ((Long) ((System.currentTimeMillis() / 1000L))).intValue();
                byte[] bs = Base64.decode(x[1],Base64.NO_WRAP);
                String plain = new String(bs);
                msB = new MessageBean(UserInterfaces.getEtid(),plain,1,time);
                MessageList.add(msB);
                refreshMessage(MessageList);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "消息已发送，显示可能延迟",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onBack(View v){
        finish();
    }

    private void refreshMessage(List<MessageBean> messageList) {
        // 更新列表
    }

}
