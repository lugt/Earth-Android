package com.earth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.earth.fragment.SwipeListView;
import com.earth.interfacees.DeleteClickListener;
import com.earth.views.R;
import com.earth.xo.FriendsMessage;

import java.util.List;


public class SwipeAdapter extends BaseAdapter{
    private Context context=null;
    private List<FriendsMessage> data;
    private SwipeListView swipelistview;

    public SwipeAdapter(Context Mcontext, List<FriendsMessage> data, SwipeListView swipeListView) {
        context = Mcontext;
        this.data = data;
        this.swipelistview = swipeListView;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.list_item, arg2, false);
            holder = new ViewHolder();
            holder.item_left = (RelativeLayout)convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout)convertView.findViewById(R.id.item_right);

            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.msg = (TextView)convertView.findViewById(R.id.tv_msg);
            holder.time = (TextView)convertView.findViewById(R.id.tv_time);

            holder.item_right_txt = (TextView)convertView.findViewById(R.id.item_right_txt);
            convertView.setTag(holder);
        }else{
        holder=(ViewHolder)convertView.getTag();
        }
        LinearLayout.LayoutParams p1=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(p1);

        LinearLayout.LayoutParams p2 = new LayoutParams(15, LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(p2);

       FriendsMessage msg = data.get(position);

         holder.title.setText(msg.getTitle());
         holder.msg.setText(msg.getMsg());
         holder.time.setText(msg.getTime());
         holder.icon.setImageResource(msg.getIcon_id());
         holder.item_right.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                if (swipelistview instanceof DeleteClickListener) {
                    data.remove(position);
                    swipelistview.onDeleteItemClick();
                    notifyDataSetChanged();
                }
             }
         });
         return convertView;

    }
    public class ViewHolder{
        RelativeLayout item_left;
        RelativeLayout item_right;

        TextView title;
        TextView msg;
        TextView time;
        ImageView icon;

        TextView item_right_txt;
    }


}
