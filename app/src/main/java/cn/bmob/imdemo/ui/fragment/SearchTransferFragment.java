package cn.bmob.imdemo.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.bean.Route;
import cn.bmob.imdemo.bean.Transfer;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchTransferFragment extends SearchRouteFragment {

    @Override
    public void onStart() {
        super.onStart();
        ll.setVisibility(View.GONE);
        etStart.setText("大市口");
        etEnd.setText("南京市");
    }

    @Override
    protected String title() {
        return "换乘方案搜索";
    }

    @Override
    protected void query() {
        String start = etStart.getText().toString();
        if (TextUtils.isEmpty(start)) {
            toast("请输入起点站");
            return;
        }
        String end = etEnd.getText().toString();
        if (TextUtils.isEmpty(end)) {
            toast("请输入终点站");
            return;
        }
        BmobQuery<Transfer> transferBmobQuery = new BmobQuery<>();
        transferBmobQuery.order("-updatedAt");
        transferBmobQuery.addWhereEqualTo("start", start);
        transferBmobQuery.addWhereEqualTo("end", end);
        transferBmobQuery.findObjects(new FindListener<Transfer>() {
            @Override
            public void done(List<Transfer> list, BmobException e) {
                if(e == null && !list.isEmpty()){
                    BmobQuery<Route> query = new BmobQuery<>();
                    query.order("-updatedAt");
                    query.addWhereRelatedTo("route", new BmobPointer(list.get(0)));
                    query.findObjects(new FindListener<Route>() {
                        @Override
                        public void done(List<Route> list, BmobException e) {
                            swRefresh.setRefreshing(false);
                            if (e == null) {
                                if (list != null && list.size() > 0) {
                                    List<String> stationList = new ArrayList<>();
                                    for (Route route : list) {
                                        StringBuilder stringBuilder = new StringBuilder(route.name).
                                                append("\t").append(route.time).
                                                append("\t").append(route.other == null ? "" : route.other);
                                        route.station.add(0,stringBuilder.toString());
                                        stationList.addAll(route.station);
                                    }
                                    adapter.bindDatas(stationList);
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
                }else {
                    if (getUserVisibleHint()) {
                        toast("获取信息出错");
                    }
                    Logger.e(e);
                }
            }
        });

    }
}
