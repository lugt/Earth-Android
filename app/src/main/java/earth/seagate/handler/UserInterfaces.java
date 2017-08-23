package earth.seagate.handler;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.earth.data.Initialize;
import com.earth.views.R;
import com.earth.xo.NetworkUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import earth.Connection.PingMsg;
import earth.Connection.SendMsg;
import earth.client.LocalHandler;
import earth.client.Long.LongClient;
import earth.client.Short.ShortClient;
import earth.client.Util.Constant;
import earth.client.Util.MessageBean;
import earth.client.Util.Monitor;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Frapo on 2017/1/29.
 * Earth 15:43
 */
public class UserInterfaces {

    public static int authReg(String cell,String ps) {
        try {
            if(Long.valueOf(cell) <= 13000000000L || Long.valueOf(cell) >= 20000000000L){
                LocalHandler.innerCall(-510,"请输入正确的手机号");
                return -510;
            }
            byte[] bT = ps.getBytes("UTF-8");
            ps = Base64.encodeToString(bT,Base64.NO_WRAP);
            ShortClient.syncReg(cell,ps);
            return 1000;
        }catch (Exception e){
            Monitor.except("auth_Reg",e);
            LocalHandler.innerCall(-515,"加载异常");
            return -515;
        }
    }

    public static void authLogin(String cell,String ps) {
        try {
            if(Long.valueOf(cell) <= 13000000000L || Long.valueOf(cell) >= 20000000000L){
                LocalHandler.innerCall(-500, "请输入正确的手机号");
                return;
            }
            byte[] bT = ps.getBytes("UTF-8");
            ps = Base64.encodeToString(bT,Base64.NO_WRAP);
            int i = ShortClient.syncLogin(cell,ps);
            if(i != 1000){
                LocalHandler.innerCall(-501, "网络无法连接");
            }
        }catch (Exception e){
            LocalHandler.innerCall(-501, "网络无法连接");
            Monitor.except("auth_login",e);
        }
    }

    public static void getFriend() {
        ShortClient.syncFriend(LongClient.ssid);
    }

    public static void addFriend(long tar) {
        ShortClient.addFriend(LongClient.ssid,String.valueOf(tar));
    }

    public static void removeFriend(long tar) {
        ShortClient.rmFriend(LongClient.ssid, String.valueOf(tar));
    }

    public static int sendPing() throws Exception {
        PingMsg ping = new PingMsg(LongClient.getNewSessionCount());
        return LongClient.sendRequest(ping);
    }

    public static SparseArray<String> queries = new SparseArray<>();

    public static int sendMsg(long target, String msg) {
        try {
            String out = Base64.encodeToString(msg.getBytes(),Base64.NO_WRAP);
            int sess = LongClient.getNewSessionCount();
            SendMsg send = new SendMsg(sess, out,target);
            queries.put(sess,target+","+out);
            int i;
            if((i = LongClient.sendRequest(send)) != 1000){
                // 异常
                return i;
            }
            return 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return -3002;
        }
    }

    public static void getApplicationInfo() {
        try {
            if(ShortClient.establish()<0){
                Monitor.error("Fail on Establish Short Connection");
            }
        }catch (Exception e){
            Monitor.except("auth_Reg",e);
        }
    }

    public static void initLong(){
        new Thread(){
            public void run(){
                LongClient.init(Constant.LONG_SERVER,Constant.LONGPORT);
            }
        }.start();
    }

    public static Long getEtid() {
        return LongClient.etid;
    }

    public static String getDisplayName(Long fromId) {
        return "Earth_ID_"+fromId;
    }

    public static void showMsgDialog(MessageBean msB,Context ctx) {
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setContentTitle(UserInterfaces.getDisplayName(msB.getFromId()))//设置通知栏标题
                .setContentText(msB.getInlineMsg())
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS,ctx)) //设置通知栏点击意图
//  .setNumber(number) //设置通知集合的数量
                .setTicker("有人给你发送了消息") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis()+100)//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                //.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setLights(0x2222f0ff, 1000, 5000)
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON

        Notification nf = mBuilder.build();
        mNotificationManager.notify(getNotifyId(), nf);
    }

    public static String getSsid() {
        return LongClient.ssid;
    }

    /**
     * 带按钮的通知栏
     */
    public void showButtonNotify(Context ctx){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        RemoteViews mRemoteViews = new RemoteViews(ctx.getPackageName(), R.layout.pop);
        /*
        mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.drawable.add);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, "周杰伦");
        mRemoteViews.setTextViewText(R.id.tv_custom_song_name, "七里香");
        //如果版本号低于（3。0），那么不显示按钮
        mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
        if(true){
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        /* 上一首按钮 * /
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
        / * 播放/暂停  按钮 * /
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
        /  * 下一首 按钮  *   /
        //buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.sing_icon);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(notifyId, notify);
        */
    }

    public static PendingIntent getDefalutIntent(int flags, Context ctx){
        return PendingIntent.getActivity(ctx, 1, new Intent(), flags);
    }

    private static int notifyId = 1;
    public static int getNotifyId() {
        return notifyId++;
    }

    public static Object getUserDetailInfo(Long etid) {
        // 获取指定联系人的名称，备注，图片, 人像, 背景，聊天记录，事项
        //
        return null;
    }

    public static boolean isLongConnected() {
        return LongClient.isConnected();
    }
}
