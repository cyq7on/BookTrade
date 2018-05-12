package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CookBookAdapter;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.CookBook;
import cn.bmob.imdemo.util.BMIUtils;
import cn.bmob.imdemo.util.SPUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RecommendFragment extends ParentWithNaviFragment {
    @Bind(R.id.rc_view)
    RecyclerView rcView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    protected CookBookAdapter adapter;
    @Bind(R.id.weight)
    EditText weight;
    @Bind(R.id.height)
    EditText height;
    @Bind(R.id.btn_generate)
    Button btnGenerate;
    private String category = "全部";
    private PopupMenu popup;
    public static final String STATUS_INT = "status_int";

    @Override
    protected String title() {
        return "推荐";
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
                popup.show();
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        IMutlipleItem<CookBook> mutlipleItem = new IMutlipleItem<CookBook>() {

            @Override
            public int getItemViewType(int postion, CookBook cookBook) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_cookbook;
            }

            @Override
            public int getItemCount(List<CookBook> list) {
                return list.size();
            }
        };
        adapter = new CookBookAdapter(getActivity(), mutlipleItem, null);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swRefresh.setRefreshing(true);
                query(category);
            }
        });

        popup = new PopupMenu(getActivity(), tv_right);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = (String) item.getTitle();
                if (TextUtils.isEmpty(title)) {
                    return false;
                } else {
                    swRefresh.setRefreshing(true);
                    query(title);
                    category = title;
                    return true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        swRefresh.setRefreshing(true);
        query(category);
    }


    protected void query(final String category) {
        BmobQuery<CookBook> query = new BmobQuery<>();
        query.order("-updatedAt");
        /*if(!category.contains("全部")){
//            query.addWhereContains("category","test");
            query.addWhereEqualTo("category","test");
        }*/
        final int status = (int) SPUtil.get(getContext(), STATUS_INT, 1);
        query.findObjects(new FindListener<CookBook>() {
            @Override
            public void done(List<CookBook> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        List<CookBook> cookBookList = new ArrayList<>();
                        if (category.contains("全部")) {
                            for (CookBook cookBook : list) {
                                if (isNeedCook(status,cookBook.nutrientList)) {
                                    cookBookList.add(cookBook);
                                }
                                Logger.d(cookBook.toString());
                            }
                            list = cookBookList;
                        }else {
                            for (CookBook cookBook : list) {
                                if (cookBook.category.contains(category) &&
                                        isNeedCook(status,cookBook.nutrientList)) {
                                    cookBookList.add(cookBook);
                                }
                                Logger.d(cookBook.toString());
                            }
                            list = cookBookList;
                        }
                        if (list.isEmpty()) {
                            if (getUserVisibleHint()) {
                                toast("暂无信息");
                            }

                        }
                        adapter.bindDatas(list);
                    } else {
                        if (getUserVisibleHint()) {
                            toast("暂无信息");
                        }
                        adapter.bindDatas(list);

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

    private boolean isNeedCook(int status,List<String> nutrientList) {
        String keyWord = "能量";
        switch (status) {
            case 0:
                for (String nutrient : nutrientList) {
                    if(nutrient.contains(keyWord)){
                        return true;
                    }
                }
                return false;
            case 1:
               return true;
            case 2:
                for (String nutrient : nutrientList) {
                    if(nutrient.contains(keyWord)){
                        return false;
                    }
                }
                return true;
            case 3:
                for (String nutrient : nutrientList) {
                    if(nutrient.contains(keyWord)){
                        return false;
                    }
                }
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_generate)
    public void onViewClicked() {
        String weight = this.weight.getText().toString();
        String height = this.height.getText().toString();
        if(TextUtils.isEmpty(weight) || TextUtils.isEmpty(height)){
            toast("请输入完整信息");
            return;
        }
        BMIUtils utils = new BMIUtils(Double.parseDouble(weight),Double.parseDouble(height));
        toast(utils.getStatus());
        int statusInt = utils.getStatusInt();
        SPUtil.putAndApply(getContext(),STATUS_INT,statusInt);
        query("全部");
    }
}
