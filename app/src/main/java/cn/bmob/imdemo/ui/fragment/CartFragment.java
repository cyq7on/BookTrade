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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CartAdapter;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.Book;
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
    private List<CartOrOrderBean> mListGoods = new ArrayList<>();
    private CartAdapter adapter;

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
        requestShoppingCartList();
        initView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        query();
    }

    private void setAdapter() {
        adapter = new CartAdapter(getContext());
        expandableListView.setAdapter(adapter);
        adapter.setOnShoppingCartChangeListener(new CartAdapter.OnShoppingCartChangeListener() {
            @Override
            public void onDataChange(String selectCount, String selectMoney) {
                int goodsCount = ShoppingCartBiz.getGoodsCount();
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

    /** 获取购物车列表的数据（数据和网络请求也是非通用部分） */
    private void requestShoppingCartList() {
        ShoppingCartBiz.delAllGoods();
//        testAddGood();
        //使用本地JSON，作测试用。本来应该是将商品ID发送的服务器，服务器返回对应的商品信息；
        /*ShoppingCartHttpBiz.requestOrderList(this, new VollyHelperNew.ResponseCallBack() {//requestOrderList(list, new VollyHelperNew.ResponseCallBack())
            @Override
            public void handleResponse(JSONObject response, int errCode) {
                mListGoods = ShoppingCartHttpBiz.handleOrderList(response, errCode);
                ShoppingCartBiz.updateShopList(mListGoods);
                updateListView();
            }
        });*/
        InputStream is = null;
        try {
            is = getContext().getAssets().open("firm_order.json");
        } catch (IOException e) {
            Logger.e(e);
        }
        String s = readJson(is);
    }

    //从给定位置读取Json文件
    private String readJson(InputStream is) {
        //从给定位置获取文件
//        File file = new File(path);
        BufferedReader reader = null;
        //返回值,使用StringBuffer
        StringBuffer data = new StringBuffer();
        //
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            //每次读取文件的缓存
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭文件流
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }

    private void updateListView() {
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

    /** 测试添加数据 ，添加的动作是通用的，但数据上只是添加ID而已，数据非通用 */
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

    private void query() {
        BmobQuery<Book> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.include("user");
        BmobQuery<User> innerQuery = new BmobQuery<>();
        //免费版不支持模糊查询
//        query.addWhereContains("name",keyWord);
        query.addWhereMatchesQuery("user", "_User", innerQuery);
        query.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> books, BmobException e) {
//                swRefresh.setRefreshing(false);
                if(e == null){
//                    clearData();
                    for (Book book : books) {
                        Logger.d(book.toString());
                        CartOrOrderBean cartOrOrderBean = new CartOrOrderBean();
                        cartOrOrderBean.book = book;
                        cartOrOrderBean.fromUser = book.user;
                        ShoppingCartBiz.addGoodToCart(book.getObjectId(),cartOrOrderBean.count + "");
                        mListGoods.add(cartOrOrderBean);
                    }
                    updateListView();
                }else {
                    Logger.e(e);
                    toast("获取信息出错");
                }
            }
        });
    }
}
