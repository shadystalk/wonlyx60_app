package com.wl.wlflatproject.Adapter;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wl.wlflatproject.R;

import java.util.List;

public class SettingGuideAdapter extends BaseQuickAdapter<SettingGuideAdapter.GuideBean, BaseViewHolder> {

    public SettingGuideAdapter(@Nullable List<GuideBean> data) {
        super(R.layout.item_setting_guide, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, GuideBean guideBean) {
        baseViewHolder.setText(R.id.item_title, guideBean.getTitle())
//                .setBackgroundRes(R.id.item_bg, guideBean.isSelect() ? R.drawable.bg_guide_select : R.color.transparent)
                .setImageResource(R.id.item_image, guideBean.getImage());
    }

    public static class GuideBean {
        @DrawableRes
        private int image;
        private String title;
        private boolean isSelect;

        public GuideBean(int image, String title) {
            this.image = image;
            this.title = title;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
