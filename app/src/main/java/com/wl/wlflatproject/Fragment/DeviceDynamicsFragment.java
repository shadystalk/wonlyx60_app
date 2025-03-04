package com.wl.wlflatproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.wl.wlflatproject.Adapter.PagerAdapter;
import com.wl.wlflatproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 设备动态页面
 *  * @Author zhuobaolian
 *  * @Date 15:17
 */
public class DeviceDynamicsFragment extends Fragment {
    @BindView(R.id.device_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager2 viewPager;

    private final static String[] FRAGMENT_TITLE={"开门记录","告警消息"};
    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_dynamic_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText(FRAGMENT_TITLE[0]));
        tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText(FRAGMENT_TITLE[1]));

        // 创建适配器
        PagerAdapter adapter = new PagerAdapter(getActivity());

        // 添加 Fragment 到适配器
        adapter.addFragment( new OpenRecordFragment());
        adapter.addFragment(new AlarmMsgFragment());
        // 设置适配器给 ViewPager
        viewPager.setAdapter(adapter);
        //预加载
        viewPager.setOffscreenPageLimit(1);
        viewPager.setSaveEnabled(false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //切换顶部tab的时候 切换fragment
                int pos = tabLayout.getSelectedTabPosition();
                viewPager.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //禁止fragment滑动
        viewPager.setUserInputEnabled(false);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        //因为设置页面fragment的替换方式是replace，设备动态里面的两个子fragment用了viewPaper，且pagerAdapter用了FragmentStateAdapter，状态会被保存
        // 这里是为了解决设置tab来回切换导致状态异常的问题
        viewPager.setCurrentItem(0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
