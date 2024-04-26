package com.wl.wlflatproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wl.wlflatproject.Bean.OpenRecordMsgBean;
import com.wl.wlflatproject.Bean.OpenRecordType;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * 开门记录二级适配器
 * @Author zhuobaolian
 * @Date 15:17
 */
 
public class OpenRecordChildViewAdapter extends RecyclerView.Adapter<OpenRecordChildViewAdapter.ViewHolder> {


    /**
     * 开门记录二级数据
     */
    private final List<OpenRecordMsgBean.OpenRecordMsgDataBean.UnlockMsgListDTO> subListData;

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

        //开锁方式
        switch (data.getUnlockMode()){
            case OpenRecordType.DOOR_OPEN:
            case OpenRecordType.IN_DOOR_OPEN:
                //门开
                holder.typeImg.setImageResource(R.mipmap.ic_door_open);
                break;
            case OpenRecordType.PSW_OPEN:
                //密码
                holder.typeImg.setImageResource(R.mipmap.ic_psw);
                break;
            case OpenRecordType.FINGERPRINT_OPEN:
                //指纹
                holder.typeImg.setImageResource(R.mipmap.ic_fingerprint);
                break;
            case OpenRecordType.FACE_OPEN:
                //人脸
                holder.typeImg.setImageResource(R.mipmap.ic_face);
                break;
            case OpenRecordType.SENSING_OPEN:
                //遥感
                holder.typeImg.setImageResource(R.mipmap.ic_sensing);
                break;
            case OpenRecordType.REMOTE_OPEN:
                //远程
                holder.typeImg.setImageResource(R.mipmap.ic_remote);
                break;
            case OpenRecordType.COERCION_PSW_OPEN:
                //胁迫密码
                holder.typeImg.setImageResource(R.mipmap.ic_coercion_psw);
                break;
            case OpenRecordType.COERCION_FINGERPRINT_OPEN:
                //胁迫指纹
                holder.typeImg.setImageResource(R.mipmap.ic_coercion_fingerprint);
                break;
            case OpenRecordType.DOUBLE_MODE_OPEN:
                //双人
                holder.typeImg.setImageResource(R.mipmap.ic_double_mode);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return subListData==null?0:subListData.size();
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
