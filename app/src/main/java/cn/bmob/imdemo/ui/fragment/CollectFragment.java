package cn.bmob.imdemo.ui.fragment;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.bean.CookBook;
import cn.bmob.imdemo.bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CollectFragment extends RecommendFragment {
    @Override
    protected String title() {
        return "收藏";
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
            }
        };
    }

    @Override
    protected void query(String category) {
        BmobQuery<CookBook> query = new BmobQuery<>();
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",user.getObjectId());
        query.addWhereMatchesQuery("collectUsers", "_User", innerQuery);
        query.order("-updatedAt");
        query.findObjects(new FindListener<CookBook>() {
            @Override
            public void done(List<CookBook> list, BmobException e) {
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
}
