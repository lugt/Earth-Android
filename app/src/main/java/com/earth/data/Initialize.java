package com.earth.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.earth.views.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import earth.client.Util.MessageBean;

/**
 * Created by Frapo on 2017/4/19.
 * Earth 0:03
 */

public class Initialize {
    private static File fileDir;

    //public static void InitInOrder(Context ctx){
    //    initSqlite(ctx);
    //}

    static SqliteCore core = null;
    private static void initSqlite(Context ctx) {
        core = new SqliteCore(ctx,"e_local",SqliteCore.VERSION);
    }

    private static void starDB() {
        //throws Exception {
        /*if(core == null) {
            throw new Exception("Core Initialize Error");
        }*/

        if(DB == null) {
            DB = core.getReadableDatabase();
            //
            // DB = SQLiteDatabase.openOrCreateDatabase(fileDir,null);
            //DB = core.getWritableDatabase();
        }
    }

    private static SQLiteDatabase DB;

    /**
     * 关闭程序关闭数据库
     */
    protected static void onDestroy() {
        DB.close();
    }


    /**
     * 建表
    */
    public static void createConf() throws Exception{
        starDB();
        String createSql = "create table e_local_conf(name primary key,val)";
        DB.execSQL(createSql);
    }

    /**
     * 插入
     * @param name
     * @param val
     */
    public static  void insertConf(String name, String val) throws Exception{
            starDB();
            String insertSql = "insert into e_local_conf(name,val) values(?,?)";
            DB.execSQL(insertSql, new String[] {name, val});
    }

    /**
     * 查询
     */
    public static  Cursor listConf() throws Exception{
        starDB();
        String selectSql = "select name,val from e_local_conf";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }

    /**
     * 删除
     */
    public static  void deleteConf(String id) throws Exception{
        starDB();
        String deleteSql = "delete from e_local_conf where name=?";
        DB.execSQL(deleteSql, new String[] { id });
    }

    /**
     * 更新
     */
    public static  void updateConf(String name,String value) throws Exception{
        starDB();
        String updataSql = "update e_local_conf set val=? where name=?";
        DB.execSQL(updataSql, new String[] { value , name });
    }

    public static void InitInOrder(Context applicationContext) {
        core = new SqliteCore(applicationContext,"e_local",SqliteCore.VERSION);
        fileDir = applicationContext.getFilesDir();
    }
    public static void checkConfTable(){

    }

    public static List<MessageBean> loadMsg(UserInfo user,Context ctx) throws Exception {
        SqliteCore sqcore = new SqliteCore(ctx,"single_msg");
        SQLiteDatabase DB = sqcore.getReadableDatabase();
        String timeDif = String.valueOf(System.currentTimeMillis()-1000*3600*24*5);
        List<MessageBean> mB = new ArrayList<MessageBean>();
        Cursor cur = DB.rawQuery("select msg,timeprint from m_"+user.etid +" where timeprint > ?",new String[] {timeDif});
        if(cur.moveToFirst()){
            for(int i = 0;i<cur.getCount();i++){
                String msg = cur.getString(0);
                // 处理msg
                mB.add(new MessageBean(msg));
                cur.moveToNext();
            }
        }
        cur.close();
        DB.close();
        return mB;
    }

    public static void addMsg(long etid, MessageBean message,Context ctx) throws Exception{
        SqliteCore sqcore = new SqliteCore(ctx,"single_msg");
        SQLiteDatabase DB = sqcore.getReadableDatabase();
        //String timeDif = String.valueOf(System.currentTimeMillis()-1000*3600*24*5);
        String time = String.valueOf(message.getTime());

        // if exist
        if(!checkIsMsgTblExist(DB,"m_"+etid)){
            String createSql = "create table if not exists m_"+etid+" (msg text, timeprint long);";
            DB.execSQL(createSql);
        }

        String insertSql = "insert into m_"+etid+" (msg,timeprint) values(?,?)";
        DB.execSQL(insertSql, new String[] {message.toString(), time});
        DB.close();
    }

    private static boolean checkIsMsgTblExist(SQLiteDatabase db, String name) throws Exception{
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+name+"';";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            cursor.close();
            if(count>0){
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public static void clearMsg(long etid,Context ctx) {
        SqliteCore sqcore = new SqliteCore(ctx,"single_msg");
        SQLiteDatabase DB = sqcore.getReadableDatabase();
        String sql = "drop table m_"+etid + " ;";
        DB.execSQL(sql);
        DB.close();
    }
}
