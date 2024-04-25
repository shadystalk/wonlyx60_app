package com.wl.wlflatproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.OpenRecordMsgBean;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * @Author zhuobaolian
 * @Date 15:17
 */
 
public class OpenRecordChildViewAdapter extends RecyclerView.Adapter<OpenRecordChildViewAdapter.ViewHolder> {

    private List<OpenRecordMsgBean.OpenRecordMsgDataBean.UnlockMsgListDTO> subListData;

    public OpenRecordChildViewAdapter(List<OpenRecordMsgBean.OpenRecordMsgDataBean.UnlockMsgListDTO> subListData) {
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
        OpenRecordMsgBean.OpenRecordMsgDataBean.UnlockMsgListDTO data = subListData.get(position);
        // 设置TextView的值
        holder.timeTv.setText(data.getShowTime());
        holder.titleTv.setText(data.getUnlockDescribe());
        holder.nameTv.setText(data.getUserNote());
        switch (data.getUnlockMode()){
            case 0:
                holder.typeImg.setImageResource(R.mipmap.ic_door_open);
                break;
            case 1:
                //密码
                holder.typeImg.setImageResource(R.mipmap.ic_psw);
                break;
            case 3:
                //指纹
                holder.typeImg.setImageResource(R.mipmap.ic_fingerprint);
                break;
            case 5:
                //人脸
                holder.typeImg.setImageResource(R.mipmap.ic_face);
                break;
            case 12:
                //遥感
                holder.typeImg.setImageResource(R.mipmap.ic_sensing);
                break;
            case 13:
                //门内
                holder.typeImg.setImageResource(R.mipmap.ic_door_open);
                break;
            case 14:
                //远程
                holder.typeImg.setImageResource(R.mipmap.ic_remote);
                break;
            case 15:
                //胁迫密码
                holder.typeImg.setImageResource(R.mipmap.ic_coercion_psw);
                break;
            case 16:
                //胁迫指纹
                holder.typeImg.setImageResource(R.mipmap.ic_coercion_fingerprint);
                break;
            case 100:
                //双人
                holder.typeImg.setImageResource(R.mipmap.ic_double_mode);
                break;
            default:
                break;
        }
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
