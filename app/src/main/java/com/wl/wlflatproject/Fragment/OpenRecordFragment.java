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
import com.wl.wlflatproject.Bean.OpenRecordMsgBean;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.R;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 开门记录页面
 *  * @Author zhuobaolian
 *  * @Date 15:17
 */
public class OpenRecordFragment extends Fragment {
    @BindView(R.id.recycler_view_open_record)
    RecyclerView msgRecyclerView;
    private Unbinder unbinder;
    private final static int SUCCESS_CODE=200;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_record_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }


    /**
     * 请求开门记录数据
     */
    public void initData() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorName", "wja");
        } catch (Exception e) {
        e.printStackTrace();
        }
        OkGo.<String>post(ApiSrevice.queryUnlockRecord).headers(ApiSrevice.getHeads(getContext())).upJson(requestBody).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if(msgRecyclerView==null){
                    //拿到结果的时候，页面可能已经销毁了，
                    return;
                }
                String s = response.body();
                OpenRecordMsgBean infoBean = GsonUtils.GsonToBean(s, OpenRecordMsgBean.class);
                if (infoBean.getCode() == SUCCESS_CODE && infoBean.getData() != null) {
                    //请求成功、解析数据
                    List<OpenRecordMsgBean.OpenRecordMsgDataBean> data = infoBean.getData();
                    if (data != null) {
                        // 创建主RecyclerView的适配器
                        OpenRecordParentViewAdapter adapter = new OpenRecordParentViewAdapter(getActivity(), data);

                        // 设置主RecyclerView的布局管理器，这里是为了保证二级的recycleView能够正常展示
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
                Toast.makeText(getContext(), "数据请求失败，请稍后再试", Toast.LENGTH_SHORT).show();
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
