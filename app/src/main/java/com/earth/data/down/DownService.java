package com.earth.data.down;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.earth.views.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 16:17
 */

public class DownService extends Service{

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    downLoadTask = new DownloadTask(DownService.this);
                    downLoadTask.download();
                    break;
            }
        }
    };

    public static final String RECEIVI = "UPDATEPROGRESS";
    //下载文件的线程
    private DownloadTask downLoadTask = null;
    //文件断点上传的数据库管理类
    FileDaoImp fileDaoImp = new FileDaoImp(DownService.this);
    boolean isFirst = true;
    List<String> list_file_path = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (MainActivity.ACTION_START.equals(intent.getAction())) {
                downLoadTask.isPause = false;
                String loading_shangchuan = intent.getStringExtra("loading_shangchuan");
                if (loading_shangchuan != null && loading_shangchuan.equals("loading_shangchuan")) {
                    isFirst = false;
                    new InitThread().start();
                    return super.onStartCommand(intent, flags, startId);
                }
                list_file_path = (List<String>) intent.getSerializableExtra("file_list");
                isFirst = true;
                Log.i("main", "--------list---Service--------------" + list_file_path.size());
                //初始化线程
                new InitThread().start();
            } else if (MainActivity.ACTION_STOP.equals(intent.getAction())) {
                if (downLoadTask != null) {
                    downLoadTask.isPause = true;
                    downLoadTask = null;
                }
            } else if (MainActivity.ACTION_CANCEL.equals(intent.getAction())) {
                downLoadTask.isPause = true;
                downLoadTask = null;
                fileDaoImp.deletDateFileTask();
                fileDaoImp.deleteFileUrl();
            }
        }
//        START_NO_STICKY
//         START_STICKY   默认调用
        return super.onStartCommand(intent, flags, startId);
    }
    //初始话文件线程
    class InitThread extends Thread {
        @Override
        public void run() {
            if (isFirst) {
                for (int i = 0; i < list_file_path.size(); i++) {
                    File file = new File(list_file_path.get(i));
//                    L.i("-------file-------------" + file.length());
                    FileInfo fileInfo2 = null;
                    try {
                        if (!file.isDirectory()) {
                            //将选中的文件存入数据库
                            fileInfo2 = new FileInfo(file.getAbsolutePath(), file.getName(), file.length(),
                                    MD5Util.getFileMD5String(file));
                            fileDaoImp.insertFileUrl(fileInfo2.getUrl(), fileInfo2.getLength(),
                                    fileInfo2.getMd5(), fileInfo2.getFileName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            handler.obtainMessage(1).sendToTarget();

        }
    }
}