package com.earth.data.down;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 16:17
 */

public class FileDaoImp{

    private DbHelper dbHelper = null;
    public FileDaoImp(Context context){
        dbHelper = new DbHelper(context, "file_upload");
    }

    //@Override
    public void insertFileUrl(String url,long length,String md5,String file_name) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        //将文件的状态存入到数据库，默认为0表示未完成上传
        db.execSQL("insert into file_info(fileUrl,file_state,file_length,file_md5," +
                "file_progress,file_name)values(?,?,?,?,?,?)",new Object[]{url,0,length,md5,0,file_name
        });
        db.close();
    }

    //根据文件的md5值去除已经上传的文件
    public boolean deleteFilebyMd5(String md5) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
//        db.execSQL("delete from file_info  where file_md5 = ?",new String[]{md5});
        int file_info = db.delete("file_info", "file_md5=?", new String[]{md5});
        db.close();
        if(file_info>0){
            return true;
        }
        return false;
    }


    public void deleteFileUrl() {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        db.execSQL("delete from file_info");
        db.close();
    }
    //删除数据
    public  boolean deletDateFileTask( ) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        //int shebei_info = db.delete("shebei_info", "_id= ?", new String[]{""+id});
        int shebei_info = db.delete("fileTask", null, null);   //全部删除数据
        //sql中含有自增序列时，会自动建立一个名为sqlite_sequence的表，其中包含name与seq
        //name记录自增所在的表，seq记录当前的序号。删除数据后想要将自增id置为0只需upadtaseq即可
        db.execSQL(" update sqlite_sequence set seq=0 where name='fileTask'");
        return false;
    }

    public List<String> queryFileUrl() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from file_info", null);
        while (cursor.moveToNext()){
            String url = cursor.getString(cursor.getColumnIndex("fileUrl"));
            list.add(url);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<FileInfo> queryFileByState() {
        List<FileInfo> list_fileInfo = new ArrayList<>();
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from file_info where file_state = 0", null);
        while (cursor.moveToNext()){
            String url = cursor.getString(cursor.getColumnIndex("fileUrl"));
            int  file_length = cursor.getInt(cursor.getColumnIndex("file_length"));
            String file_md5 = cursor.getString(cursor.getColumnIndex("file_md5"));
            String file_name = cursor.getString(cursor.getColumnIndex("file_name"));
            FileInfo fileInfo = new FileInfo(url,file_name,file_length,file_md5);
            //fileInfo.setUrl(url);
            //fileInfo.setMd5(file_md5);
            //fileInfo.setLength(file_length);
            //fileInfo.setFileName(file_name);
            list_fileInfo.add(fileInfo);
        }
        cursor.close();
        db.close();
        return list_fileInfo;
    }

    //根据文件的url来更新文件的状态，将文件状态更新为已经上传
  //  @Override
    public void updateFile(String md5) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        db.execSQL("update  file_info set file_state = ? where file_md5 = ?",new Object[]{
                1,md5
        });
        db.close();
    }

    //得到文件的总长度
//    @Override
    public long getLengthByState(int state) {
        long length = 0;
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from file_info where file_state = ?", new String[]{state+""});
        while (cursor.moveToNext()){
            String url = cursor.getString(cursor.getColumnIndex("fileUrl"));
            length+=new File(url).length();
        }
        cursor.close();
        db.close();
        Log.i("FileUploader","----------length--------"+length);
        return length;
    }

//    @Override
    public void upDateProgress(String md5,int progress) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        db.execSQL("update  file_info set file_progress = ? where file_md5 = ?",new Object[]{
                progress,md5
        });
        db.close();
    }

    public int getFileFinishedProgress(String md5) {
        int  length = 0;
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from file_info where file_md5 =?", new String[]{md5});
        while (cursor.moveToNext()){
            length = cursor.getInt(cursor.getColumnIndex("file_progress"));
        }
        cursor.close();
        db.close();
        Log.i("main","------------fileProgress-------"+length);
        return length;

    }

    public int getFileSumLength(String file_length) {
        int  sum = 0;
//        SQLiteDatabase db  = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select sum(file_length) from file_info ", null);
//        cursor.close();
//        db.close();
//        Log.i("main","------------fileProgress-------"+sum);
        return sum;
    }

//-------------------------------离线---------------

    //增加数据
    public  boolean insertData( String values) {
       /* ContentValues values  = new ContentValues();
        values.put("title", title);
        values.put("content", content);*/
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put("filepath",values);
        long shebei_info = db.insert("fileTask", null, contentValues);
        contentValues.clear();
        if (shebei_info > 0) {
            Log.i("main","---------shebei_info-----------------");
            return true;
        }
        return false;
    }

    //删除数据
    public  boolean deletelian_Date( ) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        //int shebei_info = db.delete("shebei_info", "_id= ?", new String[]{""+id});
        int shebei_info = db.delete("fileTask", null, null);   //全部删除数据
        //sql中含有自增序列时，会自动建立一个名为sqlite_sequence的表，其中包含name与seq
        //name记录自增所在的表，seq记录当前的序号。删除数据后想要将自增id置为0只需upadtaseq即可
        db.execSQL(" update sqlite_sequence set seq=0 where name='fileTask'");
        return false;
    }

    public  List<String> query_lianxian_data( ) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        List<String> list_db = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from fileTask", null);
        if (cursor != null) {
            String columns[] = cursor.getColumnNames();   //得到对应的字段
            while (cursor.moveToNext()) {
                String file_path = cursor.getString(cursor.getColumnIndex(columns[1]));
                list_db.add(file_path);

            }
            cursor.close();
        }
        return  list_db;
    }
    public boolean deleteFilebyPath(String path) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
//        db.execSQL("delete from file_info  where file_md5 = ?",new String[]{md5});
        int file_info = db.delete("fileTask", "filepath=?", new String[]{path});
        db.close();
        if(file_info>0){
            return true;
        }
        return false;

    }
}