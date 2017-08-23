package earth.client.Util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Frapo on 2017/1/23.
 */
public class Monitor {

    static {
    }

    public static void setLog() {
        try {
            Runtime.getRuntime().exec("logcat -f "+ Environment.getExternalStorageDirectory().getAbsolutePath()+"/earth/"+System.currentTimeMillis()+"-logcat.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setInnerLog() {
        try {
            Runtime.getRuntime().exec("logcat -f "+ Environment.getDataDirectory().getAbsolutePath()+"/"+System.currentTimeMillis()+".log");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendlf(String i) {
        String filePath = Environment.getExternalStorageDirectory()+"/Earth/";
        String fileName = "log-url.txt";
        writeTxtToFile(i, filePath, fileName);
        android.util.Log.i("Monitor",i);
    }

    private static class Log{

        public void info(String i) {
        }

        public void warn(String i) {
            android.util.Log.w("Monitor",i);
        }
        public void error(String i) {
            android.util.Log.e("Monitor",i);
        }
        public void logger(String a,String i) {
            android.util.Log.i(a,i);
        }

        public void trace(String s) {
            android.util.Log.v("Monitor Trace:",s);
        }

        public void info(String name, String message) {
            android.util.Log.i(name,message);
        }
    }

    static Log log = new Log();

    public static void alert(String msg) {
        log.warn(msg);
        // Alert;
    }

    public static void debug(String s) {
        log.info(s);
    }

    public static void logger(String s) {
        log.info(s);
    }

    public static void access(String s) {
        log.trace("{" + s + "}");
    }

    public static void response(String res) {
        log.trace("[" + res + "]");
    }

    public static void exp(Exception e, Class s) {
        log.info(s.getName(),e.getMessage());
        e.printStackTrace();
    }

    public static void error(String s) {
        log.error(s);
    }

    public static void logger(String tag, String s) {
        log.info(tag+s);
    }

    public static void except(String a, Exception e) {
        log.info(a + e.getMessage());
    }



    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            android.util.Log.i("Monitor", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            android.util.Log.i("Monitor","failonRootDir");
        }
    }
}
