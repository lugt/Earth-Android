package earth.seagate.handler;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.earth.interfacees.AuthenticateAct;

import java.lang.ref.WeakReference;

import earth.client.Util.Monitor;

/**
 * Created by Frapo on 2017/1/31.
 * Earth 15:29
 */

public class LoginActivityHandler extends Handler {

    WeakReference<AuthenticateAct> mActivity;

    public LoginActivityHandler(AuthenticateAct con){
        this.mActivity = new WeakReference<>(con);
    }

    public void handleMessage (Message msg) {//此方法在ui线程运行
        String txt;
        if(msg.what == 1000){
            //OK
            Monitor.logger("LoginActHandler",msg.what+"-成功");
            if("login".equals(msg.obj)){
                if(mActivity.get() != null) {
                    mActivity.get().onFinishLogin();
                }else{
                    //队列
                }
            }
            if("reg".equals(msg.obj)) {
                txt = "注册成功";
                if(mActivity.get() != null) {
                    mActivity.get().onFinishReg();
                }else{
                    //队列
                }
            }
            //if("update".equals(msg.obj)) txt = "欢迎回来";
            return;
        }

        switch(msg.what) {
            case 4000:
                txt = (String) msg.obj;
                break;
            case -3200:
                txt = "注册失败";
                if(msg.obj instanceof String[]) {
                    if ("repeatcell".equals(((String[])msg.obj)[2])) {
                        //缺少参数
                        txt = "手机号已存在";
                    }
                    if ("pass".equals(((String[])msg.obj)[2])) {
                        //缺少参数
                        txt = "密码格式不对";
                    }
                }
                break;
            case -20:
                txt="登陆失败";
                if("notfound".equals(((String[])msg.obj) [2])) txt = "用户不存在";
                if("password".equals(((String[])msg.obj) [2])) txt = "密码不正确";
                break;
            case -40:
                txt = "连接失败";
                break;
            case -9999:
                txt = "有可用更新";
                break;
            case -501:
            case -500:
            case -513:
            case -510:
            case -515:
                txt = ((String)msg.obj);
                break;
            case -517:
                txt = "上次登录已退出";
                // 应该要求重新登录
                break;
            default:
                if(mActivity.get() != null) {
                    mActivity.get().setAlert("操作失败");
                }
                return;
        }
        if(mActivity.get() != null) {
            mActivity.get().setAlert(txt);
        }
    }
}
