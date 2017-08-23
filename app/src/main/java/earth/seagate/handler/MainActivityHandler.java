package earth.seagate.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.earth.data.Initialize;
import com.earth.views.BoardActivity;
import com.earth.interfacees.ChatActHandler;
import com.earth.views.MainActivity;

import java.lang.ref.WeakReference;

import earth.client.Util.MessageBean;
import earth.client.Util.Monitor;

/**
 * Created by Frapo on 2017/1/31.
 * Earth 15:29
 */

public class MainActivityHandler extends Handler {

    WeakReference<BoardActivity> mActivity;

    public MainActivityHandler(BoardActivity con){
        this.mActivity = new WeakReference<>(con);
    }

    public void handleMessage (Message msg) {
        //此方法不在ui线程运行
        String txt;
        int displ = 0;
        switch(msg.what) {
            case 2501:
                txt = "好友操作成功";displ = 1;
                return;
            case 2502:
                txt = "好友信息已加载"; displ = 2;
                try {
                    mActivity.get().setFriendInfo(msg.obj);
                    return;
                } catch (Exception e) {
                    txt = "好友信息显示可能延迟";
                    e.printStackTrace();
                }
                break;
            case 3410:
                txt = "消息发送成功"; displ = 4;
                if(msg.arg1 != 0){
                    String a = UserInterfaces.queries.get(msg.arg1);
                    if(a != null && hdds != null) {
                        hdds.obtainMessage(2300, a).sendToTarget();
                        return;
                    }
                }
                break;
            case -3412:
                txt = "消息发送失败";
                break;
            case -3400:
                txt = "启动聊天界面失败";
                break;
            case 2000:
                deliverMsg(msg);
                return;
            case 1000:
                if("long".equals(msg.obj)){
                    txt = "已经连接到消息服务"; displ = 7;
                }else{
                    txt = "操作成功-"+msg.obj;
                }
                break;
            case -201:
                txt = "好友操作失败";break;
            case -202:
                txt = "获取好友信息失败,请重新登录";
                break;
            case -180:
                txt = "无法连接到服务器";break;
            case -170:
                txt = "网路连接不畅";break;
            case -150:
                txt = "长连接建立失败";break;
            case -130:
                txt = "正在发送SendMsg";break;
            case -120:
                txt = "正在发送Ping";break;
            case -110:
                txt = "您尚未连接消息服务";break;
            case -100:
                txt = "启动长连接服务失败";break;
            case -40:
                txt = "服务器响应失败";
                break;
            case -517:
                mActivity.get().onExpire();
                return;
            default:
                if(mActivity.get() != null) {
                    Toast.makeText(mActivity.get().getApplicationContext(),
                            "有情况:"+msg.arg1+"-"+msg.obj,
                            Toast.LENGTH_SHORT).show();
                }
                return;
        }
        if(mActivity.get() != null) {
            Monitor.logger("主页处理模块: "+txt);
            if(displ <= 0) {
                Toast.makeText(mActivity.get().getApplicationContext(),
                        txt,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deliverMsg(Message msg) {
        if(msg.obj instanceof MessageBean) {
            if (hdds == null) {
                UserInterfaces.showMsgDialog((MessageBean) msg.obj, mActivity.get());
                try {
                    Initialize.addMsg(((MessageBean) msg.obj).getFromId(), (MessageBean) msg.obj, mActivity.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg.obj = null;
            } else {
                if (hdds.isAlive() && currenthddstarget.equals(((MessageBean) msg.obj).getFromId())) {
                    hdds.obtainMessage(2000, msg.obj).sendToTarget();
                } else {
                    UserInterfaces.showMsgDialog((MessageBean) msg.obj, mActivity.get());
                    try {
                        Initialize.addMsg(((MessageBean) msg.obj).getFromId(), (MessageBean) msg.obj, mActivity.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            CrashHandler.innerCall(new Exception("Unexpected msg was given at deliverMsg " + msg.what + "="+msg.obj));
        }
    }

    private static Long currenthddstarget;
    /**
     * 设置二级处理函数，负责分管消息(消息抵达\消息发送\转账等)
     *
     * */
    public static void seHandler(ChatActHandler hdl, long t) {
        //Message Handler
        hdds = hdl;
        currenthddstarget = t;
    }

    private static ChatActHandler hdds;

    public boolean isOfAct(Activity activity) {
        return mActivity.get() == activity;
    }
}
