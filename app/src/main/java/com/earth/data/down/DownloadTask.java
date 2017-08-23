package com.earth.data.down;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 16:16
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.earth.xo.Constant;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import earth.client.Long.LongClient;
import earth.seagate.handler.UserInterfaces;

/**
 * Created by zhoukai on 2016/5/3.
 * //                    int fixedLength = (int) fStream.getChannel().size();
 * //                    已知输出流的长度用setFixedLengthStreamingMode()
 * //                    位置输出流的长度用setChunkedStreamingMode()
 * //                    con.setChunkedStreamingMode(块的大小);
 * //                    如果没有用到以上两种方式，则会在本地缓存后一次输出，那么当向输出流写入超过40M的大文件时会导致OutOfMemory
 * //设置固定流的大小
 * //                    con.setFixedLengthStreamingMode(fixedLength);
 * //                    con.setFixedLengthStreamingMode(1024 * 1024*20);
 */
public class DownloadTask {
    private Context context;
    private FileDaoImp fileDaoImp;
    public static boolean isPause = false;
    private long file_sum = 0;
    String isExistUrl = "http://123.56.15.30:8080/upload/isExistFile";
    String actionUrl = "http://123.56.15.30:8080/upload/uploadFile";
    private int finishedLength;
    public DownloadTask(Context context) {
        this.context = context;
        fileDaoImp = new FileDaoImp(context);
    }
    public void download() {
        new DownThread().start();
    }
    class DownThread extends Thread {
        private double load_lenth = 0;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        @Override
        public void run() {
            //未上传的文件
            List<FileInfo> list;
            list = fileDaoImp.queryFileByState();
            Log.i("main", "--------list--数据库---------------" + list.size());
            int sum_filelength = (int) fileDaoImp.getLengthByState(0);
            if (list.size() == 0) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction(DownService.RECEIVI);
            int nSplitter_length = 1024 * 1024 * 5;
            for (int i = 0; i < list.size(); i++) {
                int file_length = (int) list.get(i).getLength();
                int count = file_length / nSplitter_length + 1;
//                L.i("-------------------md5------------" + list.get(i).getMd5());
//                L.i("------------------fileName------------" + list.get(i).getFileName());

//---------------------验证文件--------------------------------------------------
                URL realurl = null;
                InputStream in = null;
                HttpURLConnection conn = null;
                try {
                    realurl = new URL(isExistUrl);
                    conn = (HttpURLConnection) realurl.openConnection();
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setRequestMethod("POST");
                    conn.setChunkedStreamingMode(1024 * 1024 * 10);
                    //无穷大超时
                    conn.setReadTimeout(0);
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    PrintWriter pw = new PrintWriter(conn.getOutputStream());
                    pw.print("userId=" + UserInterfaces.getEtid() + "&md5=" + list.get(i).getMd5()
                            + "&ssid=" + UserInterfaces.getSsid() + "&name=" + list.get(i).getFileName() + "&size=" + list.get(i).getLength());
                    Log.i("main", "-------------userId---------" + UserInterfaces.getEtid());
//                    Log.i("main", "-------------md5---------" + list.get(i).getMd5());
//                    Log.i("main", "-------------did---------" + getDid());
//                    Log.i("main", "-------------name---------" + list.get(i).getFileName());
//                    Log.i("main","-------------size---------"+list.get(i).getLength());
                    pw.flush();
                    pw.close();
                       /* 取得Response内容 */
                    in = conn.getInputStream();
                    int ch;
                    StringBuffer stringBuffer = new StringBuffer();
                    while ((ch = in.read()) != -1) {
                        stringBuffer.append((char) ch);
                    }
                    String json = stringBuffer.toString();
                    JSONObject jsonObject = new JSONObject(json);
                    boolean isSuccess = jsonObject.optBoolean("success");
                    if (isSuccess) {
                        int lengths = jsonObject.optJSONObject("info").optJSONObject("file").optInt("length");
                        finishedLength = lengths;
                        if (finishedLength == list.get(i).getLength()) {
                            fileDaoImp.deleteFilebyMd5(list.get(i).getMd5());
                            fileDaoImp.deleteFilebyPath(list.get(i).getUrl());
                            if (i == list.size() - 1) {
                                intent.putExtra("progress", (load_lenth * 100 / ((double) sum_filelength)));
                                intent.putExtra("state", "success");
                                context.sendBroadcast(intent);
                            }
                            continue;
                        }
                        Log.i("main", "-----length_finished------" + finishedLength);
                    }
                } catch (Exception eio) {
                    Log.i("main", "-----Exception------" + eio.toString());

                }

                //---------------------上传文件--------------------------------------------------
                for (int j = 0; j < count; j++) {
                    try {
                        File file = new File(list.get(i).getUrl());
                        URL url = new URL(actionUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setChunkedStreamingMode(1024 * 1024 * 10);
                        //无穷大超时
                        con.setReadTimeout(0);
                        con.setConnectTimeout(0);
            /* 允许Input、Output，不使用Cache */
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        con.setUseCaches(false);
            /* 设置传送的method=POST */
                        con.setRequestMethod("POST");
            /* setRequestProperty */
                        con.setRequestProperty("Connection", "Keep-Alive");//建立长连接
                        con.setRequestProperty("Charset", "UTF-8");        //编码格式
                        con.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);//表单提交文件
                        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
                        //添加参数
                        StringBuffer sb = new StringBuffer();
                        Map<String, String> params_map = new HashMap<>();
                        params_map.put("nSplitter", "3");
                        params_map.put("md5", list.get(i).getMd5());
                        params_map.put("etid", UserInterfaces.getEtid() + "");
                        params_map.put("ssid", UserInterfaces.getSsid());
                        params_map.put("name", file.getName());
                        params_map.put("from", finishedLength + "");
                        Log.i("main", "-------------userId----上传-----" + UserInterfaces.getEtid());
                        if (finishedLength + nSplitter_length > file_length) {
                            params_map.put("to", file_length + "");
                        } else {
                            params_map.put("to", (finishedLength + nSplitter_length) + "");
                        }
                        params_map.put("size", list.get(i).getLength() + "");
                        //添加参数
                        for (Map.Entry<String, String> entries : params_map.entrySet()) {
                            sb.append(twoHyphens).append(boundary).append(end);//分界符
                            sb.append("Content-Disposition: form-data; name=" + entries.getKey() + end);
                            sb.append("Content-Type: text/plain; charset=UTF-8" + end);
                            sb.append("Content-Transfer-Encoding: 8bit" + end);
                            sb.append(end);
                            sb.append(entries.getValue());
                            Log.i("main", "-----------params----------" + entries.getValue());
                            sb.append(end);//换行！
                        }
                        ds.writeBytes(sb.toString());
                        //添加文件
                        ds.writeBytes(twoHyphens + boundary + end);
                        ds.writeBytes("Content-Disposition: form-data; "
                                + "name=\"file" + "\";filename=\"" + file.getName() + "\"" + end);
                        ds.writeBytes(end);
            /* 设置每次写入1024bytes */
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        int length = -1;
                        long time = System.currentTimeMillis();
            /* 从文件读取数据至缓冲区 */
                        file_sum = file.length();
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                        randomAccessFile.seek(finishedLength);
                        load_lenth = finishedLength;
                        double current_lenth = load_lenth;
                        while ((length = randomAccessFile.read(buffer)) != -1) {
             /* 将资料写入DataOutputStream中 */
                            ds.write(buffer, 0, length);
                            load_lenth += length;
                            if (load_lenth - current_lenth > nSplitter_length) {
                                current_lenth = load_lenth;
                                break;
                            }
                            if (System.currentTimeMillis() - time > 500) {
                                time = System.currentTimeMillis();
                                //使用广播发送上传百分比
//                                intent.putExtra("progress", (load_lenth * 100 / ((double) file_sum)));
                                intent.putExtra("progress", (load_lenth * 100 / ((double) sum_filelength)));
                                context.sendBroadcast(intent);
                            }
                            if (isPause) {
                                //将文件的进度修改
                                ds.writeBytes(end);
                                randomAccessFile.close();
                                ds.flush();
                                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* 取得Response内容 */
                                InputStream is = con.getInputStream();
                                int ch;
                                StringBuffer b = new StringBuffer();
                                while ((ch = is.read()) != -1) {
                                    b.append((char) ch);
                                }
                                String json = b.toString();
                                JSONObject jsonObject = new JSONObject(json);
                                boolean isSuccess = jsonObject.optBoolean("success");
                                if (isSuccess) {
                                    int lengths = jsonObject.optJSONObject("info").optJSONObject("file0").optInt("length");
                                    if (lengths == list.get(i).getLength()) {
                                        Log.i("main", "----文件上传-lengths------" + lengths);
                                    }
                                    //更新进度
                                    fileDaoImp.upDateProgress(list.get(i).getMd5(), lengths);
                                }
                                ds.close();
                                con.disconnect();
                                return;
                            }
                        }
                        ds.writeBytes(end);
                        randomAccessFile.close();
                        ds.flush();
                        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* 取得Response内容 */
                        InputStream is = con.getInputStream();
                        int ch;
                        StringBuffer b = new StringBuffer();
                        while ((ch = is.read()) != -1) {
                            b.append((char) ch);
                        }
                        String json = b.toString();
                        JSONObject jsonObject = new JSONObject(json);
                        boolean isSuccess = jsonObject.optBoolean("success");
                        Log.i("main", "----文件分片------" + json);
                        if (isSuccess) {
                            int lengths = jsonObject.optJSONObject("info").optJSONObject("file0").optInt("length");
                            finishedLength = lengths;  //更新跳过的字节数
                            if (lengths == list.get(i).getLength()) {
                                boolean b1 = fileDaoImp.deleteFilebyMd5(list.get(i).getMd5());
                                //删除离线文件
                                boolean b2 = fileDaoImp.deleteFilebyPath(list.get(i).getUrl());
                                Log.i("main", "----文件上传-成功------" + lengths);
                                //当最后一个文件
                                if (i == list.size() - 1) {
                                    intent.putExtra("progress", (load_lenth * 100 / ((double) sum_filelength)));
                                    intent.putExtra("state", "success");
                                    context.sendBroadcast(intent);
                                }
                                break;
                            }
                        }
                        ds.close();
                        con.disconnect();
                        Log.i("main", "--------end----------");
                    } catch (Exception e) {
                        Log.i("main", "---------e------------" + e.toString());
                    }
                }
            }
        }
    }
}