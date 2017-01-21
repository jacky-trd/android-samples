package com.jikexueyuan.cloudnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 继承自RecyclerView.Adapter类，用于配合RecyclerView使用
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private LayoutInflater mInflater;
    //笔记集合
    private ArrayList<NoteEntity> mDataList = new ArrayList<>();

    public NoteRecyclerAdapter(Context context){
        //构造函数获取context和inflater
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建并返回ViewHolder
        return new ViewHolder(mInflater.inflate(R.layout.item_note,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NoteEntity noteEntity = mDataList.get(position);
        //给ViewHolder赋值
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvTitle.setText(noteEntity.getTitle());
        viewHolder.tvDate.setText(noteEntity.getDate());
        viewHolder.tvSummary.setText(noteEntity.getSummary());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<NoteEntity> getDataList(){
        //返回笔记集合
        return mDataList;
    }

    public void setDataList(Collection<NoteEntity> list){
        //设置笔记集合
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvSummary;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvSummary = (TextView) itemView.findViewById(R.id.tvSummary);
        }
    }
}
