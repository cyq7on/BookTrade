package cn.bmob.imdemo.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.bean.Route;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchStationFragment extends SearchRouteFragment {
    @Override
    protected String title() {
        return "公交站点搜索";
    }

    @Override
    public void onStart() {
        super.onStart();
        ll.setVisibility(View.GONE);
        etStart.setHint("请输入站点");
        etStart.setText("句容市");
        etEnd.setVisibility(View.GONE);
    }

    @Override
    protected void query() {
        BmobQuery<Route> query = new BmobQuery<>();
        final String station = etStart.getText().toString();
        if (TextUtils.isEmpty(station)) {
            toast("请输入站点");
            return;
        }
        query.order("-updatedAt");
        query.findObjects(new FindListener<Route>() {
            @Override
            public void done(List<Route> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        List<String> routes = new ArrayList<>(list.size());
                        for (Route route : list) {
                            if(route.station.contains(station)){
                                String s = route.name +
                                        "\t" + route.time +
                                        "\t" + (route.other == null ? "" : route.other);
                                routes.add(s);
                                routes.addAll(route.station);
                            }
                        }
                        adapter.bindDatas(routes);
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
}
