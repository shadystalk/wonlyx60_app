package com.wl.wlflatproject.Activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.wl.wlflatproject.Adapter.PagerAdapter;
import com.wl.wlflatproject.Fragment.AlarmMsgFragment;
import com.wl.wlflatproject.Fragment.OpenRecordFragment;
import com.wl.wlflatproject.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDynamicsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    View backIv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_dynamic_fragment);
        viewPager=findViewById(R.id.view_pager);
        backIv=findViewById(R.id.back_iv);
        tabLayout=findViewById(R.id.device_tablayout);
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText(FRAGMENT_TITLE[0]));
        tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText(FRAGMENT_TITLE[1]));

        // 创建适配器
        PagerAdapter adapter = new PagerAdapter(this);

        // 添加 Fragment 到适配器
        adapter.addFragment( new OpenRecordFragment());
        adapter.addFragment(new AlarmMsgFragment());
        // 设置适配器给 ViewPager
        viewPager.setAdapter(adapter);
        //预加载
        viewPager.setOffscreenPageLimit(1);
        viewPager.setSaveEnabled(false);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
    }

    private final static String[] FRAGMENT_TITLE={"开门记录","告警消息"};


    @Override
    public void onPause() {
        super.onPause();
        //因为设置页面fragment的替换方式是replace，设备动态里面的两个子fragment用了viewPaper，且pagerAdapter用了FragmentStateAdapter，状态会被保存
        // 这里是为了解决设置tab来回切换导致状态异常的问题
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 16) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
}
