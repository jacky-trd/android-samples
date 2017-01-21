package com.jikexueyuan.remindernotebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 该类为自定义的adapter类，配合ListView使用
 */
public class EventAdapter extends BaseAdapter {

    private List<RemindEventInfo> lists;
    private Context context;

    private static class ViewHolder{
        TextView tvEventTime;
        TextView tvEventContent;
    }

    public EventAdapter(List<RemindEventInfo> lists,Context context)
    {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.reminder_list_item,null);
            holder = new ViewHolder();
            holder.tvEventTime = (TextView) view.findViewById(R.id.tvTime);
            holder.tvEventContent = (TextView) view.findViewById(R.id.tvContent);
            view.setTag(holder);
        }
        else{
            holder=(ViewHolder)view.getTag();
        }
        holder.tvEventTime.setText(String.valueOf(lists.get(i).getEventTime()));
        holder.tvEventContent.setText(lists.get(i).getEventContent());
        return view;
    }
}