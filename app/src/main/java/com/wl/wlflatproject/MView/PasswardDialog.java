package com.wl.wlflatproject.MView;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wl.wlflatproject.MUtils.MTextUtils;
import com.wl.wlflatproject.R;


public class PasswardDialog extends Dialog {

    private OnResultListener resultListener;
    private EditText password;

    public PasswardDialog(Context context) {
        super(context);
        initView(context);
    }

    public PasswardDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public void initView(Context context) {
        View inflate = View.inflate(context, R.layout.device_password_dialog, null);
        TextView one = inflate.findViewById(R.id.password_dialog_one);
        TextView two = inflate.findViewById(R.id.password_dialog_two);
        TextView three = inflate.findViewById(R.id.password_dialog_three);
        TextView four = inflate.findViewById(R.id.password_dialog_four);
        TextView five = inflate.findViewById(R.id.password_dialog_five);
        TextView six = inflate.findViewById(R.id.password_dialog_six);
        TextView seven = inflate.findViewById(R.id.password_dialog_seven);
        TextView eight = inflate.findViewById(R.id.password_dialog_eight);
        TextView nine = inflate.findViewById(R.id.password_dialog_nine);
        TextView zero = inflate.findViewById(R.id.password_dialog_zero);
        TextView cancel = inflate.findViewById(R.id.password_dialog_cancel);
        TextView sure = inflate.findViewById(R.id.password_dialog_sure);
//        ImageView clear = inflate.findViewById(R.id.password_dialog_clear);
        password = inflate.findViewById(R.id.password_dialog_password);
        setContentView(inflate);
        sure.setOnClickListener(view -> {
            String s = password.getText().toString();
            if (s.length() == 6) {
                resultListener.setOnResultListener(s);
            } else if (TextUtils.isEmpty(s)) {
                Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
                //  Toast.makeText(context,"密码不能为空!",Toast.LENGTH_SHORT).show();
            } else if (s.length() < 7) {
                Toast.makeText(context, "请输入6位密码", Toast.LENGTH_SHORT).show();
            }
        });
        MTextUtils.setEditTextInhibitInputSpaChat(password, 1);
//        clear.setOnClickListener(view -> password.setText(""));
        cancel.setOnClickListener(view -> dismiss());
        one.setOnClickListener(v -> {
            // 对话框第1个按钮
            password.append(one.getText().toString());
        });
        two.setOnClickListener(v -> {
            // 对话框第2个按钮
            password.append(two.getText().toString());
        });
        three.setOnClickListener(v -> {
            // 对话框第3个按钮
            password.append(three.getText().toString());
        });
        four.setOnClickListener(v -> {
            // 对话框第4个按钮
            password.append(four.getText().toString());
        });
        five.setOnClickListener(v -> {
            // 对话框第5个按钮
            password.append(five.getText().toString());
        });
        six.setOnClickListener(v -> {
            // 对话框第6个按钮
            password.append(six.getText().toString());
        });
        seven.setOnClickListener(v -> {
            // 对话框第7个按钮
            password.append(seven.getText().toString());
        });
        eight.setOnClickListener(v -> {
            // 对话框第8个按钮
            password.append(eight.getText().toString());
        });
        nine.setOnClickListener(v -> {
            // 对话框第9个按钮
            password.append(nine.getText().toString());
        });
        zero.setOnClickListener(v -> {
            // 对话框第10个按钮
            password.append(zero.getText().toString());
        });
    }

    public void setListener(OnResultListener listener) {
        resultListener = listener;
    }

    public interface OnResultListener {
        void setOnResultListener(String password);
    }

    public void setEdit(String s) {
        password.setText(s);
    }
}
