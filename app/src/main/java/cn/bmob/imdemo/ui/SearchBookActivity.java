package cn.bmob.imdemo.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.BookAdapter;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.bean.Book;
import cn.bmob.imdemo.bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchBookActivity extends ParentWithNaviActivity {

    @Bind(R.id.rc_view)
    RecyclerView rcView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.btn_search)
    Button btnSearch;
    private BookAdapter adapter;

    @Override
    protected String title() {
        return "搜索";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        initNaviView();
        swRefresh.post(new Runnable() {
            @Override
            public void run() {
                swRefresh.setRefreshing(true);
            }
        });
        query();
        IMutlipleItem<Book> mutlipleItem = new IMutlipleItem<Book>() {

            @Override
            public int getItemViewType(int postion, Book book) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_child;
            }

            @Override
            public int getItemCount(List<Book> list) {
                return list.size();
            }
        };
        adapter = new BookAdapter(this, mutlipleItem, null);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
    }

    private void query() {
        BmobQuery<Book> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.include("user");
        BmobQuery<User> innerQuery = new BmobQuery<>();
        String keyWord = etSearch.getText().toString();
        //免费版不支持模糊查询,故采用全匹配
        if (!TextUtils.isEmpty(keyWord)) {
//            query.addWhereContains("name",keyWord);
            query.addWhereEqualTo("name", keyWord);
        }
        query.addWhereMatchesQuery("user", "_User", innerQuery);
        query.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> books, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    adapter.bindDatas(books);
                } else {
                    Logger.e(e);
                    toast("获取信息出错");
                }
            }
        });

    }

    @OnClick(R.id.btn_search)
    public void onViewClicked() {
        query();
    }
}
