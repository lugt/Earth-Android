package com.earth.xo;

public class FriendsMessage {
private int icon_id;
private String msg;
private String title;
private String time;
public FriendsMessage(String title, String msg, String time) {
	this.title = title;
	this.msg = msg;
	this.time = time;
}
public int getIcon_id() {
	return icon_id;
}
public void setIcon_id(int icon_id) {
	this.icon_id = icon_id;
}
public String getMsg() {
	return msg;
}
public void setMsg(String msg) {
	this.msg = msg;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
}
