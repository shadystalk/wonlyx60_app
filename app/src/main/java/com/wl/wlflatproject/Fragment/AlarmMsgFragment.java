package com.wl.wlflatproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wl.wlflatproject.Adapter.AlarmMsgParentViewAdapter;
import com.wl.wlflatproject.Bean.AlarmMsgBean;
import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.R;

import org.json.JSONObject;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/***
 * 告警消息页面
 *  @Author zhuobaolian
 *  * @Date 17:23
 */
public class AlarmMsgFragment extends Fragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view_alarm_msg)
    RecyclerView alarmMsgRy;
    @BindView(R.id.tv_empty)
    TextView emptyTv;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_msg_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 执行刷新操作，比如重新加载数据
                initData();
            }
        });
        return view;
    }

    /**
     * 请求服务器 获取告警消息记录
     */
    public void initData(){
        JSONObject requestBody = new JSONObject();
//        try {
//            //供应商
//            requestBody.put("vendorName","wja");
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
        OkGo.<String>post(ApiSrevice.queryAlarmMsg).headers(ApiSrevice.getHeads(getContext())).upJson(requestBody).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if(alarmMsgRy==null){
                    //拿到结果的时候，页面可能已经销毁了，
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                String s = response.body();
                AlarmMsgBean infoBean = GsonUtils.GsonToBean(s, AlarmMsgBean.class);
                if (infoBean.getCode() == Constant.SUCCESS_CODE && infoBean.getData() != null) {
                    List<AlarmMsgBean.AlarmMsgDataDTO> data = infoBean.getData();
                    if (data != null) {
                        if(data.size()!=0) {
                            alarmMsgRy.setVisibility(View.VISIBLE);
                            // 创建主RecyclerView的适配器
                            AlarmMsgParentViewAdapter adapter = new AlarmMsgParentViewAdapter(getActivity(), data);

                            // 设置主RecyclerView的布局管理器,这里是为了保证二级的recycleView能够正常展示
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
                                @Override
                                public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                }
                            };
                            layoutManager.setAutoMeasureEnabled(true);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            alarmMsgRy.setLayoutManager(layoutManager);
                            emptyTv.setVisibility(View.GONE);
                            // 设置主RecyclerView的适配器
                            alarmMsgRy.setAdapter(adapter);
                        }else {
                            alarmMsgRy.setVisibility(View.GONE);
                            emptyTv.setVisibility(View.VISIBLE);
                        }
                    }else {
                        alarmMsgRy.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), infoBean.getMsg(), Toast.LENGTH_SHORT).show();
                    alarmMsgRy.setVisibility(View.GONE);
                    emptyTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Response<String> response) {
                swipeRefreshLayout.setRefreshing(false);
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
