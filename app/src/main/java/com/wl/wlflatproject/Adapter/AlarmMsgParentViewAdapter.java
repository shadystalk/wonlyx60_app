package com.wl.wlflatproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.AlarmMsgBean;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * 告警消息一级适配器，设置中的设备动态与首页的共享
 * @Author zhuobaolian
 * @Date 15:15
 */
public class AlarmMsgParentViewAdapter extends RecyclerView.Adapter<AlarmMsgParentViewAdapter.ViewHolder> {

    /**
     * 告警消息数据
     */
    private final List<AlarmMsgBean.AlarmMsgDataDTO> mainListData;
    private final Context context;
    public AlarmMsgParentViewAdapter(Context context, List<AlarmMsgBean.AlarmMsgDataDTO> mainListData) {
        this.mainListData = mainListData;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_msg_parent_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 获取当前位置的子列表
        AlarmMsgBean.AlarmMsgDataDTO subListData = mainListData.get(position);
        holder.dateTv.setText(subListData.getShowDate());
        // 设置子RecyclerView的适配器及数据
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.subRecyclerView.setLayoutManager(layoutManager);
        holder.subRecyclerView.setAdapter(new AlarmMsgChildViewAdapter(context,subListData.getAlarmMsgList()));
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
