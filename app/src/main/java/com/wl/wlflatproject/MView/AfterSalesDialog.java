package com.wl.wlflatproject.MView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import com.wl.wlflatproject.R;

public class AfterSalesDialog extends Dialog {

    private Bitmap towCode;
    public AfterSalesDialog(Context context) {
        super(context);
        init();
    }

    private void init() {
        setContentView(R.layout.after_code_layout);
    }

}