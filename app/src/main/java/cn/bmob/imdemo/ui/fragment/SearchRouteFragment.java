package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.StationAdapter;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.Route;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchRouteFragment extends ParentWithNaviFragment {
    protected StationAdapter adapter;
    @Bind(R.id.et_start)
    EditText etStart;
    @Bind(R.id.et_end)
    EditText etEnd;
    @Bind(R.id.btn_search)
    Button btnSearch;
    @Bind(R.id.rc_view)
    RecyclerView rcView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.cb)
    CheckBox cb;
    @Bind(R.id.et_bus)
    EditText etBus;
    @Bind(R.id.ll_no)
    LinearLayout ll;

    @Override
    protected String title() {
        return "公交线路搜索";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        IMutlipleItem<String> mutlipleItem = new IMutlipleItem<String>() {

            @Override
            public int getItemViewType(int postion, String s) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_father;
            }

            @Override
            public int getItemCount(List<String> list) {
                return list.size();
            }
        };
        adapter = new StationAdapter(getActivity(), mutlipleItem, null);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swRefresh.setRefreshing(true);
                query();
            }
        });
        return rootView;
    }

    protected void query() {
        BmobQuery<Route> query = new BmobQuery<>();
        if(cb.isChecked()){
            String bus = etBus.getText().toString();
            if (TextUtils.isEmpty(bus)) {
                toast("请输入公交");
                return;
            }
            query.addWhereEqualTo("name", bus);
        }else {
            String start = etStart.getText().toString();
            String end = etEnd.getText().toString();
            if (TextUtils.isEmpty(start)) {
                toast("请输入起点站");
                return;
            }
            if (TextUtils.isEmpty(end)) {
                toast("请输入终点站");
                return;
            }
            query.addWhereEqualTo("start", start);
            query.addWhereEqualTo("end", end);
        }
        query.order("-updatedAt");
        query.findObjects(new FindListener<Route>() {
            @Override
            public void done(List<Route> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        Route route = list.get(0);
                        StringBuilder stringBuilder = new StringBuilder(route.name).
                                append("\t").append(route.time).
                                append("\t").append(route.other == null ? "" : route.other);
                        route.station.add(0, stringBuilder.toString());
                        adapter.bindDatas(route.station);
                    } else {
                        if (getUserVisibleHint()) {
                            toast("暂无信息");
                        }
                        adapter.bindDatas(null);
                    }
                } else {
                    if (getUserVisibleHint()) {
                        toast("获取信息出错");
                    }
                    Logger.e(e);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_search)
    public void onViewClicked() {
        swRefresh.setRefreshing(true);
        query();
    }
}
