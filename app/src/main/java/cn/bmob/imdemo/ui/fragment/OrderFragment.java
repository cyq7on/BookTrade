package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CartAdapter;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.CartOrOrderBean;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.util.ShoppingCartBiz;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OrderFragment extends CartFragment {

    public static OrderFragment getInstance(Bundle bundle) {
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected String title() {
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();
        rlBottomBar.setVisibility(View.GONE);
    }

    @Override
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
                String title = String.format(getArguments().getString("title" ,"")
                        + "（%s）", goodsCount + "");
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

    @Override
    public void showEmpty(boolean isEmpty) {
        if(isEmpty){
            toast("还没有订单哦");
        }
    }

    @Override
    protected void query() {
        BmobQuery<CartOrOrderBean> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.include("fromUser,toUser,book");
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", user.getObjectId());
        query.addWhereMatchesQuery(getArguments().getString("key",""), "_User", innerQuery);
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
}
