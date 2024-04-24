package com.wl.wlflatproject.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wl.wlflatproject.Adapter.OpenRecordParentViewAdapter;
import com.wl.wlflatproject.Bean.InfoBean;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.DpUtils;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OpenRecordFragment extends Fragment {
    @BindView(R.id.recycler_view_open_record)
    RecyclerView msgRecyclerView;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_record_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void initData(){
        OkGo.<String>get(ApiSrevice.queryUnlockRecord).headers(ApiSrevice.getHeads(getContext())).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
//                String s = response.toString();
//                InfoBean infoBean = GsonUtils.GsonToBean(s, InfoBean.class);
//                if(infoBean.getCode()==200  &&  infoBean.getData()!=null){
//                    bindNum.setText(infoBean.getData().getPhone());
//                    bindVisi(true);
//                }else{
//                    Toast.makeText(getContext(),infoBean.getMsg(),Toast.LENGTH_SHORT).show();
//                }
            }
        });

        // 创建主列表数据
        List<List<String>> mainListData = new ArrayList<>();
        List<String> subListData1 = new ArrayList<>();
        subListData1.add("Item 1");
        subListData1.add("Item 2");
        subListData1.add("Item 3");
        mainListData.add(subListData1);

        List<String> subListData2 = new ArrayList<>();
        subListData2.add("Item 4");
        subListData2.add("Item 5");
        mainListData.add(subListData2);

        // 创建主RecyclerView的适配器
        OpenRecordParentViewAdapter adapter = new OpenRecordParentViewAdapter(getActivity(),mainListData);

        // 设置主RecyclerView的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setAutoMeasureEnabled(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        msgRecyclerView.setLayoutManager(layoutManager);

        // 设置主RecyclerView的适配器
        msgRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
