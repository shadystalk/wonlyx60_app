package com.wl.wlflatproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.OpenRecordMsgBean;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * 开门记录一级适配器
 * @Author zhuobaolian
 * @Date 15:15
 */
public class OpenRecordParentViewAdapter extends RecyclerView.Adapter<OpenRecordParentViewAdapter.ViewHolder> {

    /**
     * 开门记录数据
     */
    private List<OpenRecordMsgBean.OpenRecordMsgDataBean> mainListData;

    private Context context;
    public OpenRecordParentViewAdapter(Context context,List<OpenRecordMsgBean.OpenRecordMsgDataBean> mainListData) {
        this.mainListData = mainListData;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_record_parent_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 获取当前位置的子列表
        OpenRecordMsgBean.OpenRecordMsgDataBean data = mainListData.get(position);
        holder.dateTv.setText(data.getShowDate());
        // 设置子RecyclerView的适配器
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.subRecyclerView.setLayoutManager(layoutManager);
        holder.subRecyclerView.setAdapter(new OpenRecordChildViewAdapter(data.getUnlockMsgList()));
    }

    @Override
    public int getItemCount() {
        return mainListData==null?0:mainListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView subRecyclerView;
        public TextView dateTv;

        public ViewHolder(View itemView) {
            super(itemView);
            subRecyclerView = itemView.findViewById(R.id.sub_recyclerview);
            dateTv=itemView.findViewById(R.id.tv_date);
        }
    }
}
