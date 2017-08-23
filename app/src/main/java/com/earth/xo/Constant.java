package com.earth.xo;


import android.database.Cursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import earth.client.Util.Monitor;

public class Constant {
	//Btn的标识
	public static final int BTN_FLAG_MESSAGE = 0x01;
	public static final int BTN_FLAG_CONTACTS = 0x01 << 1;
	public static final int BTN_FLAG_NEWS = 0x01 << 2;
	public static final int BTN_FLAG_SETTING = 0x01 << 3;
	
	//Fragment的标识
	public static final String FRAGMENT_FLAG_MESSAGE = "消息"; 
	public static final String FRAGMENT_FLAG_CONTACTS = "联系人"; 
	public static final String FRAGMENT_FLAG_NEWS = "新闻"; 
	public static final String FRAGMENT_FLAG_SETTING = "设置"; 
	public static final String FRAGMENT_FLAG_SIMPLE = "simple";
	private static String filesDir;

	private static String usname,etid;
    public static boolean isMainLoaded = false;

    public static String getFilesDir() {
		return filesDir;
	}

    public static void loadConf(Cursor cur) {
        if(cur.moveToFirst()){
            for (int i = 0; i < cur.getCount(); i++) {
                String name = cur.getString(0);
                String val = cur.getString(1);
                parseConf(name,val);
                cur.moveToNext();
            }
        }
    }

    private static Map<String,String> conf_local= new HashMap<String,String>();
    private static int parseConf(String name, String val) {
        conf_local.put(name,val);
        //switch (name){
        // HOOK
        //}
        return 100;
    }

    public static String getConf(String name){
        return conf_local.get(name);
    }

    public static String getConfStr() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> entry : conf_local.entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" ,");
        }
        return sb.toString();
    }

    public enum TAB_SPEC_TAG {

		MAIN_SPEC_TAG("1"), PLAYLIST_SPEC_TAG("2"), AUDIO_LIST_SPEC_TAG("3"), PLAYER_SPEC_TAG(
				"4");

		TAB_SPEC_TAG(String id) {
			this.id = id;

		}

		private String id;

		public String getId() {
			return this.id;
		}

	}

}