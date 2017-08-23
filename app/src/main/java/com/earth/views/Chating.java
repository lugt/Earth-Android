package com.earth.views;

/**
 * Created by God on 2017/2/9.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.earth.data.Initialize;
import com.earth.interfacees.ChatAct;
import com.earth.interfacees.ChatActHandler;

import java.util.ArrayList;
import java.util.List;

import earth.client.LocalHandler;
import earth.client.Util.MessageBean;
import earth.client.Util.Monitor;
import earth.seagate.handler.CrashHandler;
import earth.seagate.handler.FriendManager;
import earth.seagate.handler.MainActivityHandler;
import earth.seagate.handler.UserInterfaces;
/**
 * wechatdonal
 */
/**
 * wechat
 *
 * @author donal
 *
 */
public class Chating extends Activity implements ChatAct,OnItemClickListener,OnTouchListener,OnEditorActionListener,FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "Chating-Easy";
    private boolean isFace = false;

    private Button voiceOrTextButton;
    private Button faceOrTextButton;
    private Button voiceButton;
    private ChattingAdapter adapter = null;
    private EditText messageInput = null;

    private ListView listView;

    private int recordCount;
    private UserInfo user;// 聊天人

    private int firstVisibleItem;
    private int currentPage = 1;
    private int objc;

    private AnimationDrawable leftAnimationDrawable;
    private AnimationDrawable rightAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.getInstance().init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chating);

        initUI();

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
            if(adapter!=null){
                adapter.setUser(user);
            }
        }catch (Exception e){
            e.printStackTrace();
            LocalHandler.innerCall(-3400,null);
            finish();
            return;
        }

        MainActivityHandler.seHandler(new ChatActHandler(this),user.etid);
        loadAllMsgs();
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
                Initialize.addMsg(user.etid, msB , getApplicationContext());

            } catch (SQLiteException e) {
                e.printStackTrace();
                Monitor.logger(TAG,"Unexpected cannot save a message.");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "消息已发送，显示可能延迟",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int onUserInfo(UserInfo ui) {
        //处理UserInfo
        return 0;
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    private void initUI() {
        faceOrTextButton = (Button) findViewById(R.id.faceOrTextButton);
        voiceOrTextButton = (Button) findViewById(R.id.voiceOrTextButton);
        voiceButton = (Button) findViewById(R.id.voiceButton);
        voiceButton.setOnTouchListener(this);
        listView = (ListView) findViewById(R.id.chat_list);
        listView.setCacheColorHint(0);
        adapter = new ChattingAdapter(Chating.this, getMessages(),
                listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        if (firstVisibleItem == 0) {
                            int num = addNewMessage(++currentPage);
                            if (num > 0) {
                                adapter.refreshList(getMessages());
                                listView.setSelection(num - 1);
                            }
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        closeInput();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Chating.this.firstVisibleItem = firstVisibleItem;
            }

        });

        messageInput = (EditText) findViewById(R.id.chat_content);
        messageInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listView.setSelection(getMessages().size() - 1);
            }
        });
        messageInput.setOnEditorActionListener(this);
    }

    private int addNewMessage(int i) {
        // Please Get more older messages.
        return 0;
    }


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


    public void touchSpeak(View v){
        try {
            if(!NoDoubleClickUtils.isDoubleClick()) {
                Initialize.clearMsg(user.etid, getApplicationContext());
            }
        }catch (Exception e){
            // Already Not Exist
            e.printStackTrace();
        }
    }

    public void ButtonClick(View v) {

        if(!NoDoubleClickUtils.isDoubleClick()) {
            return;
        }

        switch (v.getId()) {
            case R.id.leftBarButton:
                finish();
                closeInput();
                break;
            case R.id.voiceOrTextButton:
                if (messageInput.getVisibility() == View.VISIBLE) {
                    closeInput();
                    messageInput.setVisibility(View.INVISIBLE);
                    voiceButton.setVisibility(View.VISIBLE);
                    voiceOrTextButton.setBackgroundResource(R.drawable.keyborad);
                    faceOrTextButton.setBackgroundResource(R.drawable.face);
                } else if (messageInput.getVisibility() == View.INVISIBLE) {
                    messageInput.setVisibility(View.VISIBLE);
                    voiceButton.setVisibility(View.INVISIBLE);
                    voiceOrTextButton.setBackgroundResource(R.drawable.voice);
                }
                break;
            case R.id.faceOrTextButton:
                if (messageInput.getVisibility() == View.VISIBLE) {
                    if (!isFace) {
                        isFace = true;
                        closeInput();
                        faceOrTextButton.setBackgroundResource(R.drawable.keyborad);
                        //show face key borad
                    } else {
                        isFace = false;
                        faceOrTextButton.setBackgroundResource(R.drawable.face);
                        //hide face key borad
                    }
                    voiceOrTextButton.setBackgroundResource(R.drawable.voice);
                } else if (messageInput.getVisibility() == View.INVISIBLE) {
                    messageInput.setVisibility(View.VISIBLE);
                    voiceButton.setVisibility(View.INVISIBLE);
                    voiceOrTextButton.setBackgroundResource(R.drawable.voice);
                    faceOrTextButton.setBackgroundResource(R.drawable.keyborad);
                }
                break;
        }
    }

    private void closeInput() {
        // close Input
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(messageInput.getWindowToken(), 0);
    }


    public void receiveNewMessage(MessageBean message) {
        // receive New Message
        MessageList.add(message);
        refreshMessage(MessageList);
        // 本地保存
        try {
            Initialize.addMsg(user.etid, message , getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        MainActivityHandler.seHandler(null,0L);
        super.onStop();
        // The Application has been closed!
    }


    protected void refreshMessage(List<MessageBean> messages) {
        adapter.refreshList(messages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatActHandler mH = new ChatActHandler(this);
        MainActivityHandler.seHandler(mH,user.etid);
//		recordCount = MessageManager.getInstance(context)
//				.getChatCountWithSb(to);
        adapter.refreshList(getMessages());
        listView.setSelection(getMessages().size() - 1);
    }

    private List<MessageBean> MessageList = new ArrayList<>();

    private List<MessageBean> getMessages() {
        return MessageList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
        }
    }

    @Override
    public void onBackStackChanged() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
        MainActivityHandler.seHandler(null,0L);
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View convertView, int position, long arg3) {

    }


    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent arg2) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEND:
                String message = messageInput.getText().toString();
                if ("".equals(message)) {
                    Toast.makeText(Chating.this, "不能为空",
                            Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        UserInterfaces.sendMsg(user.etid, message);
                        messageInput.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "信息發送失敗", Toast.LENGTH_LONG).show();
                        messageInput.setText(message);
                    }
                    //showToast("信息发送失败");
                    //}
                    closeInput();
                }
                listView.setSelection(getMessages().size() - 1);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}