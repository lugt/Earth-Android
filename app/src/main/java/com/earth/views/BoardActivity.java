package com.earth.views;

/**
 * Created by Frapo on 2017/4/17.
 * Earth 11:02
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.earth.adapter.SwipeAdapter;
import com.earth.data.Initialize;
import com.earth.data.SqliteCore;
import com.earth.fragment.SwipeListView;
import com.earth.xo.FriendsMessage;

import java.util.ArrayList;
import java.util.List;

import earth.client.LocalHandler;
import earth.seagate.handler.MainActivityHandler;
import earth.seagate.handler.UserInterfaces;

public class BoardActivity extends AppCompatActivity {

    private static boolean loaded = false;

    private static MainActivityHandler hdl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        initDate();
        mListView=(SwipeListView)findViewById(R.id.list_contact);
        SwipeAdapter adapter =new SwipeAdapter(this, data, mListView);
        mListView.setAdapter(adapter);

        if(loaded || (hdl != null && hdl.isOfAct(this))){
            finish();
            return;
        }

        if(savedInstanceState == null || !savedInstanceState.getBoolean("loaded")){

            if(hdl == null) {
                hdl = new MainActivityHandler(this);
                LocalHandler.setHandler(hdl);
            }

            if(!UserInterfaces.isLongConnected()){
                UserInterfaces.initLong();
                UserInterfaces.getFriend();
            }

            loaded = true;

        }else{

            if(!UserInterfaces.isLongConnected()){
                UserInterfaces.initLong();
                UserInterfaces.getFriend();
            }

            loaded = true;
        }

    }

    public void setFriendInfo(Object obj) throws Exception{
        if(obj instanceof String){
            // OK
            String m = ((String) obj);
            String[] friends = m.split("-");
            setAllFriends(friends);
        }
    }


    private void initDate() {
        // TODO Auto-generated method stub
        FriendsMessage message=null;
        message=new FriendsMessage("请稍候", "正在读取好友信息", "...");
        message.setIcon_id(R.drawable.round_1);
        data.add(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        //
        loaded = false;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle menu) {
        //
        if(menu != null) {
            menu.putBoolean("loaded", true);
        }
        loaded = false;
        super.onSaveInstanceState(menu);
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



    public void setAllFriends(String[] etids) {
        data = new ArrayList<>();
        FriendList = new ArrayList<>();
        for(int i=0;i<etids.length;i++){
            FriendsMessage message=null;
            String s = etids[i];
            String zp = "陌生人";
            if('+' == s.charAt(s.length()-1)){
                zp = "好友";
            }
            FriendList.add(s.substring(0,s.length()-1));
            message=new FriendsMessage("伙伴_"+s.substring(0,s.length()-1), zp, "默认分组");
            if(Math.random() >= 0.6) {
                message.setIcon_id(R.drawable.tab_per_1);
            }else if(Math.random() > 0.5){
                message.setIcon_id(R.drawable.tab_per_1);
            }else{
                message.setIcon_id(R.drawable.tab_per_2);
            }
            data.add(message);
        }
        SwipeAdapter adapter =new SwipeAdapter(this, data, mListView);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (!NoDoubleClickUtils.isDoubleClick()) {
                        Intent in = new Intent(getApplicationContext(), EngageActivity.class);
                        in.putExtra("etid", FriendList.get(position));
                        startActivity(in);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                // Toast.makeText(getActivity(), "item onclick " + position, Toast.LENGTH_SHORT)
                //         .show();
            }
        });


        System.gc();

    }

    public void onClick(View v) {
        if(NoDoubleClickUtils.isDoubleClick()){
            return;
        }

        if(v.getId() == R.id.button10){
            runShowEtid();
        }

        if(v.getId() == R.id.button11){
            runAddFriendSc(false);
        }


        if(v.getId() == R.id.button12){
            runAddFriendSc(true);
        }
    }

    private void runShowEtid() {
        Intent intt = new Intent();
        intt.setClass(this,MessageStack.class);
        startActivity(intt);
    }

    public void runAddFriendSc(final boolean isDel){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialogue, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialogue);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.et_content);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            private boolean isLong(String str) {
                try {
                    Long t = Long.getLong(str);
                }catch (Exception e){
                    return false;
                }
                return true;
            }
            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str) || !isLong(str)) {
                    etContent.setError("找不到指定的联系人");
                } else {
                    dialog.dismiss();
                    try {
                        long t = Long.parseLong(str);
                        if(!isDel) {
                            UserInterfaces.addFriend(t);
                        }else{
                            UserInterfaces.removeFriend(t);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(BoardActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }


    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

    public static ArrayList<String> FriendList = new ArrayList<String>();

    private List<FriendsMessage> data=new ArrayList<FriendsMessage>();
    private SwipeListView mListView;

    public void onExpire() {
        String txt = "您的登录已失效,请重新登录";
        Toast.makeText(getApplicationContext(), txt,
                Toast.LENGTH_SHORT).show();
        Intent intt = new Intent();
        intt.setClass(this,MainActivity.class);
        startActivity(intt);
        finish();
    }
}
