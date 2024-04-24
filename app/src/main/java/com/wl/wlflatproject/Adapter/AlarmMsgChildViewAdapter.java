package com.wl.wlflatproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.R;

import java.util.List;

/**
 * @Author zhuobaolian
 * @Date 15:17
 */
 
public class AlarmMsgChildViewAdapter extends RecyclerView.Adapter<AlarmMsgChildViewAdapter.ViewHolder> {

    private List<String> subListData;

    public AlarmMsgChildViewAdapter(List<String> subListData) {
        this.subListData = subListData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_record_child_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 获取当前位置的数据
        String data = subListData.get(position);
        // 设置TextView的值
        holder.timeTv.setText(data);
    }

    @Override
    public int getItemCount() {
        return subListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeTv;
        public TextView titleTv;
        public TextView nameTv;
        public ImageView typeImg;
        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.tv_time);
            titleTv=itemView.findViewById(R.id.tv_title);
            nameTv=itemView.findViewById(R.id.tv_name);
            typeImg=itemView.findViewById(R.id.img_type);
        }
    }
}
