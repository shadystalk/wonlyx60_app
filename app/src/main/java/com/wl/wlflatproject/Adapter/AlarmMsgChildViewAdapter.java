package com.wl.wlflatproject.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.AlarmMsgBean;
import com.wl.wlflatproject.MView.MediaDialog;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * 告警消息二级列表适配器
 * @Author zhuobaolian
 * @Date 15:17
 */
 
public class AlarmMsgChildViewAdapter extends RecyclerView.Adapter<AlarmMsgChildViewAdapter.ViewHolder> {

    /**
     * 告警消息列表
     */
    private final List<AlarmMsgBean.AlarmMsgDataDTO.AlarmMsgListDTO> subListData;
    private final Context context;

    public AlarmMsgChildViewAdapter(Context context,List<AlarmMsgBean.AlarmMsgDataDTO.AlarmMsgListDTO> subListData) {
        this.context=context;
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
        //有图片的时候显示图片图标、反之隐藏
        if(TextUtils.isEmpty(data.getPicPath())){
            holder.picImg.setVisibility(View.GONE);
        }else {
            holder.picImg.setVisibility(View.VISIBLE);
            holder.picImg.setOnClickListener(view -> {
                int imgOrientation=0;
                //弹窗查看图片
                try {
                    imgOrientation=Integer.parseInt(data.getImgOrientation());
                }catch (Exception e){
                    e.printStackTrace();
                }
                MediaDialog dialog = new MediaDialog(context, data.getPicPath(), true, R.style.mDialog,imgOrientation);
                dialog.show();
            });
        }
        //有视频的时候显示视频图标、反之隐藏
        if(TextUtils.isEmpty(data.getVideoPath())){
            holder.videoImg.setVisibility(View.GONE);
        }else {
            holder.videoImg.setVisibility(View.VISIBLE);
            holder.videoImg.setOnClickListener(view -> {
                //弹窗查看视频
                MediaDialog dialog = new MediaDialog(context, data.getVideoPath(), false, R.style.mDialog);
                dialog.show();
            });
        }
    }

    @Override
    public int getItemCount() {

        return subListData==null?0:subListData.size();
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
