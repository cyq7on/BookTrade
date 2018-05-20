package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CartAdapter;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.CartOrOrderBean;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.util.ShoppingCartBiz;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CartFragment extends ParentWithNaviFragment {

    @Bind(R.id.expandableListView)
    ExpandableListView expandableListView;
    @Bind(R.id.ivSelectAll)
    ImageView ivSelectAll;
    @Bind(R.id.btnSettle)
    TextView btnSettle;
    @Bind(R.id.tvCountMoney)
    TextView tvCountMoney;
    /* @Bind(R.id.tvTitle)
     TextView tvTitle;*/
    @Bind(R.id.rlShoppingCartEmpty)
    RelativeLayout rlShoppingCartEmpty;
    protected List<CartOrOrderBean> mListGoods = new ArrayList<>();
    protected CartAdapter adapter;
    @Bind(R.id.rlBottomBar)
    RelativeLayout rlBottomBar;

    @Override
    protected String title() {
        return "购物车";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        setAdapter();
        initView();
        query();
        return rootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            return;
        }
        query();
    }

    protected void setAdapter() {
        adapter = new CartAdapter((BaseActivity) getActivity());
        expandableListView.setAdapter(adapter);
        adapter.setOnShoppingCartChangeListener(new CartAdapter.OnShoppingCartChangeListener() {
            @Override
            public void onDataChange(String selectCount, String selectMoney) {
//                int goodsCount = ShoppingCartBiz.getGoodsCount();
                int goodsCount = mListGoods.size();
//                if (!isNetworkOk) {//网络状态判断暂时不显示
//                }
                if (goodsCount == 0) {
                    showEmpty(true);
                } else {
                    showEmpty(false);//其实不需要做这个判断，因为没有商品的时候，必须退出去添加商品；
                }
                String countMoney = String.format(getResources().getString(R.string.count_money), selectMoney);
                String countGoods = String.format(getResources().getString(R.string.count_goods), selectCount);
                String title = String.format(getResources().getString(R.string.shop_title), goodsCount + "");
                tvCountMoney.setText(countMoney);
                btnSettle.setText(countGoods);
                tv_title.setText(title);
            }

            @Override
            public void onSelectItem(boolean isSelectedAll) {
                ShoppingCartBiz.checkItem(isSelectedAll, ivSelectAll);
            }
        });
        //通过监听器关联Activity和Adapter的关系，解耦；
        View.OnClickListener listener = adapter.getAdapterListener();
        if (listener != null) {
            //即使换了一个新的Adapter，也要将“全选事件”传递给adapter处理；
            ivSelectAll.setOnClickListener(adapter.getAdapterListener());
            //结算时，一般是需要将数据传给订单界面的
            btnSettle.setOnClickListener(adapter.getAdapterListener());
        }
    }

    public void showEmpty(boolean isEmpty) {
        if (isEmpty) {
            expandableListView.setVisibility(View.GONE);
            rlShoppingCartEmpty.setVisibility(View.VISIBLE);
        } else {
            expandableListView.setVisibility(View.VISIBLE);
            rlShoppingCartEmpty.setVisibility(View.GONE);
        }
    }

    private void initView() {
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
    }


    protected void updateListView() {
        adapter.setList(mListGoods);
        adapter.notifyDataSetChanged();
        expandAllGroup();
    }

    /**
     * 展开所有组
     */
    private void expandAllGroup() {
        for (int i = 0; i < mListGoods.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    /**
     * 测试添加数据 ，添加的动作是通用的，但数据上只是添加ID而已，数据非通用
     */
    private void testAddGood() {
        ShoppingCartBiz.addGoodToCart("279457f3-4692-43bf-9676-fa9ab9155c38", "6");
        ShoppingCartBiz.addGoodToCart("95fbe11d-7303-4b9f-8ca4-537d06ce2f8a", "8");
        ShoppingCartBiz.addGoodToCart("8c6e52fb-d57c-45ee-8f05-50905138801b", "9");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801d", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801e", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801f", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801g", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801h", "3");
    }

    protected void query() {
        BmobQuery<CartOrOrderBean> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.addWhereEqualTo("isCart",true);
        query.include("fromUser,toUser,book");
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", user.getObjectId());
        query.addWhereMatchesQuery("toUser", "_User", innerQuery);
        query.findObjects(new FindListener<CartOrOrderBean>() {
            @Override
            public void done(List<CartOrOrderBean> list, BmobException e) {
//                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (mListGoods.isEmpty()) {
                        mListGoods.addAll(list);
                    } else {
                        mListGoods.clear();
                        mListGoods.addAll(list);
                    }
                    updateListView();
                } else {
                    Logger.e(e);
                    toast("获取信息出错");
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
