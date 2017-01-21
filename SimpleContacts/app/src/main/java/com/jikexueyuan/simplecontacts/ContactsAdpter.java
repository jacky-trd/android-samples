package com.jikexueyuan.simplecontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 该类配合ListView控件使用： listView.setAdapter(adapter)
 */
public class ContactsAdpter extends BaseAdapter {

    private List<PersonalPhoneInfo> lists;
    private Context context;

    private static class ViewHolder{
        TextView tvName;
        TextView tvNumber;
    }

    public ContactsAdpter(List<PersonalPhoneInfo> lists,Context context)
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
        if(view==null) {
            view = LayoutInflater.from(context).inflate(R.layout.persional_item,null);
            holder = new ViewHolder();
            holder.tvName =  (TextView)view.findViewById(R.id.tvName);
            holder.tvNumber =  (TextView)view.findViewById(R.id.tvNumber);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder)view.getTag();
        }
        holder.tvName.setText(lists.get(i).getPhoneName());
        holder.tvNumber.setText(lists.get(i).getPhoneNumber());
        return view;
    }
}
