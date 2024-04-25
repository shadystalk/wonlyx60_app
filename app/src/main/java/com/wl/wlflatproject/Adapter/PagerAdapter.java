package com.wl.wlflatproject.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wl.wlflatproject.Fragment.AlarmMsgFragment;
import com.wl.wlflatproject.Fragment.OpenRecordFragment;

/**
 * @Author zhuobaolian
 * @Date 17:23
 */
import androidx.annotation.NonNull;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
