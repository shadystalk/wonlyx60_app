package com.wl.wlflatproject.MView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.wl.wlflatproject.MUtils.CodeUtils;
import com.wl.wlflatproject.MUtils.DeviceUtils;
import com.wl.wlflatproject.MUtils.DpUtils;
import com.wl.wlflatproject.R;

import java.util.Set;

public class CodeDialog extends Dialog {

    private Bitmap towCode;

    private void init(Context context,String id,String devType) {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.code_dialog_layout, null);
        ImageView iv=mView.findViewById(R.id.dialog_code_iv);
        if(towCode==null){
            towCode = DpUtils.getTowCode(context, devType+id);
//            towCode = DpUtils.getTowCode(context, "WL025S1-"+ DeviceUtils.getSerialNumber(context));
            iv.setImageBitmap(towCode);
            setContentView(mView);
        }
    }

    public CodeDialog(Context context, int themeResId,String id,String devType) {
        super(context, themeResId);
        init(context,id,devType);
    }

}
