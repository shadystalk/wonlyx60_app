package com.wl.wlflatproject.MView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wl.wlflatproject.R;


public class SetDialog extends Dialog {
    private NumberPickerView start;
    private String s[] = new String[]{"-20", "-19", "-18", "-17", "-16", "-15", "-14", "-13", "-12", "-11", "-10", "-9", "-8",
            "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
            , "12", "13", "14", "15", "16", "17", "18", "19","20"};//修正角度
    private String s1[] = new String[]{"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};//速度
//    private String s2[] = new String[]{"72", "77", "82", "87"};//开门角度
    private String s2[] = new String[]{"小", "适中", "大", "最大"};//开门角度
    private String s3[] = new String[]{"1","2","3", "4", "5","6", "7",  "8","9", "10","11","12","13", "14", "15","16",
            "17", "18","19","20","21", "22","13", "24", "25","26","27", "28","29", "30"};//等待时间
    private String s4[] = new String[]{"减速一档", "减速二档", "减速三档", "减速关闭"};//关门力度
    private String s5[] = new String[]{"低", "中", "高"};//设置防夹检测范围
    private TextView name;
    private Button back;
    private Button complete;
    private String value;
    private ResultListener listener;
    private int flag;

    public SetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initData(context);
    }

    private void initData(Context context) {
        View inflate = View.inflate(context, R.layout.set_dialog_layout, null);
        start = inflate.findViewById(R.id.start);
        name = inflate.findViewById(R.id.dialog_name);
        back = inflate.findViewById(R.id.back);
        complete = inflate.findViewById(R.id.complete);
        setContentView(inflate);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = start.getContentByCurrValue();
                if(flag==3){
                    switch (value){
                        case "小":
                            value="72";
                            break;
                        case "适中":
                            value="77";
                            break;
                        case "大":
                            value="82";
                            break;
                        case "最大":
                            value="87";
                            break;
                    }
                }
                listener.onResult(value, flag);
                dismiss();
            }
        });
    }


    public int getIndex(String ss[], String value) {
        int index=0;
        for(int x=0;x< ss.length;x++){
            if(ss[x].equals(value)){
                index=x;
            }
        }
        return index;
    }

    public void show(int flag, String value) {
        this.flag = flag;
        switch (flag) {
            case 1://左角度修正
                start.refreshByNewDisplayedValues(s);
                start.setValue(getIndex(s,value));
                name.setText("左角度修复值");
                break;
            case 2://右角度修正
                start.refreshByNewDisplayedValues(s);
                start.setValue(getIndex(s,value));
                name.setText("右角度修复值");
                break;
            case 3://开门角度
                start.refreshByNewDisplayedValues(s2);
                start.setValue(getIndex(s2,value));
                name.setText("开门角度");
                break;
            case 4://等待时间
                start.refreshByNewDisplayedValues(s3);
                start.setValue(getIndex(s3,value));
                name.setText("等待时间");
                break;
            case 5://开门速度
                start.refreshByNewDisplayedValues(s1);
                start.setValue(getIndex(s1,value));
                name.setText("开门速度");
                break;
            case 6://关门速度
                start.refreshByNewDisplayedValues(s1);
                start.setValue(getIndex(s1,value));
                name.setText("关门速度");
                break;
            case 8://关门力度
                start.refreshByNewDisplayedValues(s4);
                start.setValue(getIndex(s4,value));
                name.setText("关门力度");
                break;
            case 12://开门角度修复值
                start.refreshByNewDisplayedValues(s);
                start.setValue(getIndex(s,value));
                name.setText("开门角度修复值");
                break;
            case 22://防夹等级
                start.refreshByNewDisplayedValues(s5);
                start.setValue(getIndex(s5,value));
                name.setText("设置防夹检测范围");
                break;
        }
        show();
    }


    public void setListener(ResultListener listener) {
        this.listener = listener;
    }

    public interface ResultListener {
        public void onResult(String value, int flag);
    }
}
