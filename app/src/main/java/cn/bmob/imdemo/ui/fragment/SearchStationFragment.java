package cn.bmob.imdemo.ui.fragment;

import android.view.View;

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
        etEnd.setVisibility(View.GONE);
    }
}
