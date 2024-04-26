package com.wl.wlflatproject.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.annotation.NonNull;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * viewPaper搭配fragment的适配器
 * @Author zhuobaolian
 * @Date 17:23
 */
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
        if(fragmentList==null){
            return 0;
        }
        return fragmentList.size();
    }
}
