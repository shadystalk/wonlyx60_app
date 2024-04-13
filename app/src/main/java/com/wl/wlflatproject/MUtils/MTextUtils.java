package com.wl.wlflatproject.MUtils;

import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTextUtils {
    /**
     * 禁止EditText输入空格
     *
     * @param editText
     * @param type     1是密码框
     */
    public static void setEditTextInhibitInputSpaChat(EditText editText, int type) {
        InputFilter filter_space = (source, start, end, dest, dstart, dend) -> {
            if (" ".equals(source)) {
                return "";
            } else {
                return null;
            }
        };
        InputFilter filter_speChat = (charSequence, i, i1, spanned, i2, i3) -> {
            String speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(speChat);
            Matcher matcher = pattern.matcher(charSequence.toString());
            if (matcher.find()) {
                return "";
            } else {
                return null;
            }
        };
        if (type == 1) {
            editText.setFilters(new InputFilter[]{filter_space, filter_speChat, new InputFilter.LengthFilter(6)});
        } else {
            editText.setFilters(new InputFilter[]{filter_space, filter_speChat, new InputFilter.LengthFilter(12)});
        }

    }
}
