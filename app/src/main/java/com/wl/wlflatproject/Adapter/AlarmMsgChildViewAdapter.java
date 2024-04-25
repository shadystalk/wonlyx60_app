package com.wl.wlflatproject.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.AlarmMsgBean;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * @Author zhuobaolian
 * @Date 15:17
 */
 
public class AlarmMsgChildViewAdapter extends RecyclerView.Adapter<AlarmMsgChildViewAdapter.ViewHolder> {

    private List<AlarmMsgBean.AlarmMsgDataDTO.AlarmMsgListDTO> subListData;

    public AlarmMsgChildViewAdapter(List<AlarmMsgBean.AlarmMsgDataDTO.AlarmMsgListDTO> subListData) {
        this.subListData = subListData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_msg_child_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 获取当前位置的数据
        AlarmMsgBean.AlarmMsgDataDTO.AlarmMsgListDTO data = subListData.get(position);
        // 设置TextView的值
        holder.timeTv.setText(data.getShowTime());
        holder.titleTv.setText(data.getAlarmDescribe());
        if(TextUtils.isEmpty(data.getPicPath())){
            holder.picImg.setVisibility(View.GONE);
        }else {
            holder.picImg.setVisibility(View.VISIBLE);
            holder.picImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        if(TextUtils.isEmpty(data.getVideoPath())){
            holder.videoImg.setVisibility(View.GONE);
        }else {
            holder.videoImg.setVisibility(View.VISIBLE);
            holder.videoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeTv;
        public TextView titleTv;
        public ImageView videoImg;
        public ImageView picImg;
        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.tv_time);
            titleTv=itemView.findViewById(R.id.tv_title);
            videoImg=itemView.findViewById(R.id.img_video);
            picImg=itemView.findViewById(R.id.img_pic);
        }
    }
}
