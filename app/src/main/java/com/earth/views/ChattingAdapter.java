package com.earth.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.lang.ref.WeakReference;
import java.util.List;

import earth.client.Long.LongClient;
import earth.client.Util.MessageBean;
import earth.client.Util.Monitor;
import earth.seagate.handler.CrashHandler;
import earth.seagate.handler.DateUtil;
import earth.seagate.handler.FriendManager;
import earth.seagate.handler.UserInterfaces;

/**
 * Created by God on 2017/2/9.
 */

class ChattingAdapter extends BaseAdapter {

    private WeakReference<ListView> listView;
    private UserInfo user;

    public void setUser(UserInfo u){
        user=u;
    }

    class ViewHolderLeftText {
        TextView timeTV;
        ImageView leftAvatar;
        TextView leftNickname;
        TextView leftText;
    }

    class ViewHolderRightText {
        TextView timeTV;
        ImageView rightAvatar;
        TextView rightNickname;
        TextView rightText;
        ProgressBar rightProgress;
    }

    private List<MessageBean> items;
    private Context context;
    private ListView adapterList;
    private LayoutInflater inflater;

    DisplayImageOptions options;
    DisplayImageOptions photooptions;

    public ChattingAdapter(Context context, List<MessageBean> items,
                           ListView adapterList) {
        this.context = context;
        this.items = items;
        this.adapterList = adapterList;
        this.user = user;
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avatar_placeholder)
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        photooptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void refreshList(List<MessageBean> items) {
        this.items = items;
        this.notifyDataSetChanged();
        if (this.items.size() > 1) {
            adapterList.setSelection(items.size() - 1);
        }
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderRightText holderRightText;
        ViewHolderLeftText holderLeftText;
        try {
            if(user == null){
                user = FriendManager.get(LongClient.etid);
            }
            MessageBean message = items.get(position);
            if (convertView == null) {
                switch (message.getType()) {
                    case MessageBean.TXT:
                        if(isLeft(message)) {
                            holderLeftText = new ViewHolderLeftText();
                            convertView = inflater.inflate(R.layout.chat_left_text, null);
                            holderLeftText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
                            holderLeftText.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
                            holderLeftText.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
                            holderLeftText.leftText = (TextView) convertView.findViewById(R.id.textview_content_l);
                            displayLeftText(message.msg, holderLeftText, position);
                            convertView.setTag(holderLeftText);
                        }else{
                            holderRightText = new ViewHolderRightText();
                            convertView = inflater.inflate(R.layout.chat_right_text, null);
                            holderRightText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
                            holderRightText.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
                            holderRightText.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
                            holderRightText.rightText = (TextView) convertView.findViewById(R.id.textview_content_r);
                            displayRightText(message.msg, holderRightText, position);
                            convertView.setTag(holderRightText);
                        }
                        break;
                }
            } else {
                switch (message.getType()) {
                    case MessageBean.TXT:
                        if(!isLeft(message)) {
                            if (convertView.getTag() instanceof ViewHolderRightText) {
                                holderRightText = (ViewHolderRightText) convertView.getTag();
                                displayRightText(message.msg, holderRightText, position);
                            }
                            else {
                                holderRightText = new ViewHolderRightText();
                                convertView = inflater.inflate(R.layout.chat_right_text, null);
                                holderRightText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
                                holderRightText.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
                                holderRightText.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
                                holderRightText.rightText = (TextView) convertView.findViewById(R.id.textview_content_r);
                                displayRightText(message.msg, holderRightText, position);
                                convertView.setTag(holderRightText);
                            }
                            break;
                        }else {
                            if (convertView.getTag() instanceof ViewHolderLeftText) {
                                holderLeftText = (ViewHolderLeftText) convertView.getTag();
                                displayLeftText(message.msg, holderLeftText, position);
                            } else {
                                holderLeftText = new ViewHolderLeftText();
                                convertView = inflater.inflate(R.layout.chat_left_text, null);
                                holderLeftText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
                                holderLeftText.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
                                holderLeftText.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
                                holderLeftText.leftText = (TextView) convertView.findViewById(R.id.textview_content_l);
                                displayLeftText(message.msg, holderLeftText, position);
                                convertView.setTag(holderLeftText);
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Monitor.logger(e.getMessage());
        }
        return convertView;
    }

    private boolean isLeft(MessageBean message) {
        try{
            // 正在和自己聊天
//所有消息显示在右侧
            if(!UserInterfaces.getEtid().equals(user.etid)) {
                // 和他人聊天
                if(user.etid.equals(message.getFromId())){
                    // 对方说的，对面用户的id == message的来源
                  return true;
                }else if(!UserInterfaces.getEtid().equals(message.getFromId())){
                  UserInterfaces.showMsgDialog(message,context);
                }
            }
        }catch (Exception e){
            CrashHandler.innerCall(e);
        }
        return false;
    }


    private void displayLeftText(String msg, ViewHolderLeftText viewHolderLeftText, int position) {
        //imageLoader.displayImage(user, viewHolderLeftText.leftAvatar, options);
        viewHolderLeftText.leftText.setText(msg);
        displayTime(position, viewHolderLeftText.timeTV);
    }

    private void displayRightText(String msg, ViewHolderRightText viewHolderRightText, int position) {
        //imageLoader.displayImage(CommonValue.BASE_URL+ appContext.getLoginUserHead(), viewHolderRightText.rightAvatar, options);
        viewHolderRightText.rightText.setText(msg);
        displayTime(position, viewHolderRightText.timeTV);
    }

    private void displayTime(int position, TextView timeTV) {
        Long currentTime = items.get(position).getTime();
        Long previewTime = (position - 1) >= 0 ? items.get(position - 1).getTime() : 0L;
        try {
            long time1 = Long.valueOf(currentTime);
            long time2 = Long.valueOf(previewTime);
            if ((time1 - time2) >= 5 * 60) {
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(DateUtil.formatLong(currentTime));
            } else {
                timeTV.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Monitor.logger(e.getMessage());
            e.printStackTrace();
        }
    }

}
