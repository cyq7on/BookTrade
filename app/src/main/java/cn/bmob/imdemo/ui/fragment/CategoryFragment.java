package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CategoryAdapter;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.Book;
import cn.bmob.imdemo.bean.FatherData;
import cn.bmob.imdemo.ui.UploadCookBookActivity;
import cn.bmob.imdemo.ui.UserInfoActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @Description: 图书分类，home页
 * @author: cyq7on
 * @date: 18-5-13 下午4:08
 * @version: V1.0
 */

public class CategoryFragment extends ParentWithNaviFragment {
    @Bind(R.id.expand_list)
    ExpandableListView expandList;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    private ArrayList<FatherData> list = new ArrayList<>();
    private CategoryAdapter adapter;

    @Override
    protected String title() {
        return "主页";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(UploadCookBookActivity.class,null);
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CategoryAdapter(getActivity(),list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        createData();
        expandList.setAdapter(adapter);
        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Bundle bundle = new Bundle();
                Book book = adapter.getChild(i,i1);
                bundle.putSerializable("b", book);
                startActivity(UserInfoActivity.class, bundle);
                return true;
            }
        });
        expandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if(adapter.getChildrenCount(i) == 0){
                    toast("暂无该类图书");
                    return true;
                }
                return false;
            }
        });
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createData();
            }
        });
        return rootView;
    }

    private void query(String keyWord) {
        BmobQuery<Book> query = new BmobQuery<>();
        query.order("-updatedAt");
        //免费版不支持模糊查询
//        query.addWhereContains("name",keyWord);
        if(TextUtils.isEmpty(keyWord)){
            query.findObjects(new FindListener<Book>() {
                @Override
                public void done(List<Book> books, BmobException e) {
                    swRefresh.setRefreshing(false);
                    if(e == null){
                        for (Book book : books) {
                            list.get(book.categoryId).getList().add(book);
                        }
                        adapter.notifyDataSetChanged();
                    }else {
                        Logger.e(e);
                        toast("获取信息出错");
                    }
                }
            });
        }else {
            query.findObjects(new FindListener<Book>() {
                @Override
                public void done(List<Book> books, BmobException e) {
                    swRefresh.setRefreshing(false);
                    if(e == null){
                        for (Book book : books) {
                            list.get(book.categoryId).getList().add(book);
                        }
                        adapter.notifyDataSetChanged();
                    }else {
                        Logger.e(e);
                        toast("获取信息出错");
                    }
                }
            });
        }

    }

    private void createData() {
        swRefresh.setRefreshing(true);
        for (int i = 0; i < CategoryAdapter.category.length; i++) {
            FatherData fatherData = new FatherData();
            fatherData.setList(new ArrayList<Book>());
            fatherData.setTitle(CategoryAdapter.category[i]);
            list.add(fatherData);
        }
        query(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
