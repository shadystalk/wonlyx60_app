package com.wl.wlflatproject.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wl.wlflatproject.Bean.OpenRecordMsgBean;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.R;

import org.json.JSONException;
import org.json.JSONObject;

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


    public void initData() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorName", "wja");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        OkGo.<String>post(ApiSrevice.queryUnlockRecord).headers(ApiSrevice.getHeads(getContext())).upJson(requestBody).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if(msgRecyclerView==null){
                    return;
                }
                String s = response.body();

                Log.e("XXXXXX", "onSuccess==" + s);
                OpenRecordMsgBean infoBean = GsonUtils.GsonToBean(s, OpenRecordMsgBean.class);
                if (infoBean.getCode() == 200 && infoBean.getData() != null) {
                    List<OpenRecordMsgBean.OpenRecordMsgDataBean> data = infoBean.getData();
                    if (data != null) {
                        // 创建主RecyclerView的适配器
                        OpenRecordParentViewAdapter adapter = new OpenRecordParentViewAdapter(getActivity(), data);

                        // 设置主RecyclerView的布局管理器
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        };
                        layoutManager.setAutoMeasureEnabled(true);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        msgRecyclerView.setLayoutManager(layoutManager);

                        // 设置主RecyclerView的适配器
                        msgRecyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), infoBean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<String> response) {
                Log.e("XXXXXX", "onError==" + response.body());
                super.onError(response);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
