package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.DietPlanAdapter;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.DietPlan;
import cn.bmob.imdemo.ui.UploadDietActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DietPlanFragment extends ParentWithNaviFragment {
    @Bind(R.id.rc_view)
    RecyclerView rcView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    private DietPlanAdapter adapter;
    @Override
    protected String title() {
        return "饮食计划";
    }

    @Override
    public Object right() {
        return user.role == 1 ? R.drawable.base_action_bar_add_bg_selector : null;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(UploadDietActivity.class,null);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        IMutlipleItem<DietPlan> mutlipleItem = new IMutlipleItem<DietPlan>() {

            @Override
            public int getItemViewType(int postion, DietPlan dietPlan) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_dietplan;
            }

            @Override
            public int getItemCount(List<DietPlan> list) {
                return list.size();
            }
        };
        adapter = new DietPlanAdapter(getActivity(), mutlipleItem, null);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        swRefresh.setRefreshing(true);
        query();
    }


    protected void query() {
        BmobQuery<DietPlan> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.findObjects(new FindListener<DietPlan>() {
            @Override
            public void done(List<DietPlan> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        adapter.bindDatas(list);
                    } else {
                        if(getUserVisibleHint()){
                            toast("暂无信息");
                        }
                        adapter.bindDatas(list);

                    }
                } else {
                    if(getUserVisibleHint()){
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
}
