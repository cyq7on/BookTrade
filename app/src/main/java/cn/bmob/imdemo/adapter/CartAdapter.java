package cn.bmob.imdemo.adapter;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.CartOrOrderBean;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.ChatActivity;
import cn.bmob.imdemo.ui.UIAlertView;
import cn.bmob.imdemo.util.ShoppingCartBiz;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * * ---------神兽保佑 !---------
 * <p/>
 * ... ┏┓        ┏┓
 * ..┏┛┻━━━━┛┻┓
 * .┃              ┃
 * ┃      ━       ┃
 * ┃  ┳┛  ┗┳   ┃
 * ┃              ┃
 * ┃      ┻      ┃
 * ┃              ┃
 * ┗━┓      ┏━┛
 * ... ┃      ┃
 * .. ┃      ┃
 * . ┃      ┗━━━┓
 * ┃              ┣┓
 * ┃             ┏┛
 * ┗┓┓┏━┳┓┏┛
 * . ┃┫┫  ┃┫┫
 * .┗┻┛  ┗┻┛
 * <p/>
 */
public class CartAdapter extends BaseExpandableListAdapter {
    private BaseActivity mContext;
    private List<CartOrOrderBean> mListGoods = new ArrayList<>();
    private OnShoppingCartChangeListener mChangeListener;
    private boolean isSelectAll = false;

    public interface OnShoppingCartChangeListener {
        void onDataChange(String selectCount, String selectMoney);
        void onSelectItem(boolean isSelectedAll);
    }

    public CartAdapter(BaseActivity context) {
        mContext = context;
    }

    public void setList(List<CartOrOrderBean> mListGoods) {
        this.mListGoods = mListGoods;
        setSettleInfo();
    }

    public void setOnShoppingCartChangeListener(OnShoppingCartChangeListener changeListener) {
        this.mChangeListener = changeListener;
    }

    public View.OnClickListener getAdapterListener() {
        return listener;
    }

    @Override
    public int getGroupCount() {
        return mListGoods.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return mListGoods.get(groupPosition).getGoods().size();
//        return mListGoods.size();
        return mListGoods.size() == 0 ? 0 : 1;
    }

    @Override
    public CartOrOrderBean getGroup(int groupPosition) {
        return mListGoods.get(groupPosition);
    }

    @Override
    public CartOrOrderBean getChild(int groupPosition, int childPosition) {
        return mListGoods.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_elv_group_test, parent, false);
            holder.tvGroup = (TextView) convertView.findViewById(R.id.tvShopNameGroup);
            holder.tvEdit = (TextView) convertView.findViewById(R.id.tvEdit);
            holder.ivCheckGroup = (ImageView) convertView.findViewById(R.id.ivCheckGroup);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        CartOrOrderBean group = getGroup(groupPosition);
        holder.tvGroup.setText(group.fromUser.getUsername());
        holder.tvGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = getGroup(groupPosition).fromUser;
                BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(),
                        user.getAvatar());
                BmobIMConversation conversationEntrance = BmobIM.getInstance().
                        startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", conversationEntrance);
                mContext.startActivity(ChatActivity.class, bundle, false);
            }
        });
        ShoppingCartBiz.checkItem(group.isChecked, holder.ivCheckGroup);
        boolean isEditing = group.isEdit;
        if (isEditing) {
            holder.tvEdit.setText("完成");
        } else {
            holder.tvEdit.setText("编辑");
        }
        holder.ivCheckGroup.setTag(groupPosition);
        holder.ivCheckGroup.setOnClickListener(listener);
        holder.tvEdit.setTag(groupPosition);
        holder.tvEdit.setOnClickListener(listener);
        return convertView;
    }

    /**
     * child view
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_elv_child_test, parent, false);
            holder.tvChild = (TextView) convertView.findViewById(R.id.tvItemChild);
            holder.tvDel = (TextView) convertView.findViewById(R.id.tvDel);
            holder.ivCheckGood = (ImageView) convertView.findViewById(R.id.ivCheckGood);
            holder.ivGoods = (ImageView) convertView.findViewById(R.id.ivGoods);
            holder.rlEditStatus = (RelativeLayout) convertView.findViewById(R.id.rlEditStatus);
            holder.llGoodInfo = (LinearLayout) convertView.findViewById(R.id.llGoodInfo);
            holder.ivAdd = (ImageView) convertView.findViewById(R.id.ivAdd);
            holder.ivReduce = (ImageView) convertView.findViewById(R.id.ivReduce);
            holder.tvGoodsParam = (TextView) convertView.findViewById(R.id.tvGoodsParam);
            holder.tvPriceNew = (TextView) convertView.findViewById(R.id.tvPriceNew);
            holder.tvPriceOld = (TextView) convertView.findViewById(R.id.tvPriceOld);
            holder.tvPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//数字被划掉效果
            holder.tvNum = (TextView) convertView.findViewById(R.id.tvNum);
            holder.tvNum2 = (TextView) convertView.findViewById(R.id.tvNum2);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        CartOrOrderBean child = getGroup(groupPosition);
        boolean isChildSelected = child.isChecked;
        boolean isEditing = child.isEdit;
        String priceNew = "¥" + child.book.price;
//        String priceOld = "¥" + goods.getMkPrice();
        String num = child.count + "";
        String pdtDesc = child.book.info;
        String goodName = child.book.name;

        holder.ivCheckGood.setTag(groupPosition + "," + childPosition);
        holder.tvChild.setText(goodName);
        holder.tvPriceNew.setText(priceNew);
//        holder.tvPriceOld.setText(priceOld);
        holder.tvPriceOld.setVisibility(View.GONE);
        holder.tvNum.setText("X " + num);
        holder.tvNum2.setText(num);
        holder.tvGoodsParam.setText(pdtDesc);

        holder.ivAdd.setTag(child);
        holder.ivReduce.setTag(child);
        holder.tvDel.setTag(groupPosition + "," + childPosition);
        holder.tvDel.setTag(groupPosition + "," + childPosition);

        ShoppingCartBiz.checkItem(isChildSelected, holder.ivCheckGood);
        if (isEditing) {
            holder.llGoodInfo.setVisibility(View.GONE);
            holder.rlEditStatus.setVisibility(View.VISIBLE);
        } else {
            holder.llGoodInfo.setVisibility(View.VISIBLE);
            holder.rlEditStatus.setVisibility(View.GONE);
        }

//        holder.ivCheckGood.setOnClickListener(listener);
        holder.ivCheckGood.setVisibility(View.GONE);
        holder.tvDel.setOnClickListener(listener);
        holder.ivAdd.setOnClickListener(listener);
        holder.ivReduce.setOnClickListener(listener);
        holder.llGoodInfo.setOnClickListener(listener);
        ImageLoaderFactory.getLoader().loadAvator(holder.ivGoods,child.book.imageUrl,R.mipmap.ic_launcher);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //main
                case R.id.ivSelectAll:
                    isSelectAll = ShoppingCartBiz.selectAll(mListGoods, isSelectAll, (ImageView) v);
                    setSettleInfo();
                    notifyDataSetChanged();
                    break;
                /*case R.id.tvEditAll:
                    break;*/
                case R.id.btnSettle:
                    if (ShoppingCartBiz.hasSelectedGoods(mListGoods)) {
                        toast("结算跳转");
                    } else {
                        toast("亲，先选择商品！");
                    }
                    //group
                    break;
                case R.id.tvEdit://切换界面，属于特殊处理，假如没打算切换界面，则不需要这块代码
                    int groupPosition2 = Integer.parseInt(String.valueOf(v.getTag()));
                    boolean isEditing = !(mListGoods.get(groupPosition2).isEdit);
                    mListGoods.get(groupPosition2).isEdit = isEditing;
                    TextView textView = (TextView) v;
                    if(textView.getText().toString().contains("完成")){
                        mListGoods.get(groupPosition2).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e != null){
                                    toast("商品数量更新失败");
                                    Logger.e(e);
                                }
                            }
                        });
                    }
                    for (CartOrOrderBean bean : mListGoods) {
                        Logger.d(bean.count + "");
                    }
                    /*for (int i = 0; i < mListGoods.get(groupPosition2).getGoods().size(); i++) {
                        mListGoods.get(groupPosition2).getGoods().get(i).setIsEditing(isEditing);
                    }*/
                    notifyDataSetChanged();
                    break;
                case R.id.ivCheckGroup:
                    int groupPosition3 = Integer.parseInt(String.valueOf(v.getTag()));
                    isSelectAll = ShoppingCartBiz.selectGroup(mListGoods, groupPosition3);
                    selectAll();
                    setSettleInfo();
                    notifyDataSetChanged();
                    break;
                //child
                case R.id.ivCheckGood:
                    String tag = String.valueOf(v.getTag());
                    if (tag.contains(",")) {
                        String s[] = tag.split(",");
                        int groupPosition = Integer.parseInt(s[0]);
                        int childPosition = Integer.parseInt(s[1]);
                        isSelectAll = ShoppingCartBiz.selectOne(mListGoods, groupPosition, childPosition);
                        selectAll();
                        setSettleInfo();
                        notifyDataSetChanged();
                    }
                    break;
                case R.id.tvDel:
                    String tagPos = String.valueOf(v.getTag());
                    if (tagPos.contains(",")) {
                        String s[] = tagPos.split(",");
                        int groupPosition = Integer.parseInt(s[0]);
                        int childPosition = Integer.parseInt(s[1]);
                        showDelDialog(groupPosition, childPosition);
                    }
                    break;
                case R.id.ivAdd:
                    ShoppingCartBiz.addOrReduceGoodsNum(true, (CartOrOrderBean) v.getTag(), ((TextView) (((View) (v.getParent())).findViewById(R.id.tvNum2))));
                    setSettleInfo();
                    break;
                case R.id.ivReduce:
                    ShoppingCartBiz.addOrReduceGoodsNum(false, (CartOrOrderBean) v.getTag(), ((TextView) (((View) (v.getParent())).findViewById(R.id.tvNum2))));
                    setSettleInfo();
                    break;
                case R.id.llGoodInfo:
//                    toast("商品详情，暂未实现");
                    break;
                /*case R.id.tvShopNameGroup:

                    break;*/
            }
        }
    };

    private void toast(String info) {
        Toast.makeText(mContext,info,
                Toast.LENGTH_SHORT).show();
    }

    private void selectAll() {
        if (mChangeListener != null) {
            mChangeListener.onSelectItem(isSelectAll);
        }
    }

    private void setSettleInfo() {
        String[] infos = ShoppingCartBiz.getShoppingCount(mListGoods);
        //删除或者选择商品之后，需要通知结算按钮，更新自己的数据；
        if (mChangeListener != null && infos != null) {
            mChangeListener.onDataChange(infos[0], infos[1]);
        }
    }

    private void showDelDialog(final int groupPosition, final int childPosition) {
        final UIAlertView delDialog = new UIAlertView(mContext, "温馨提示", "确认删除该商品吗?",
                "取消", "确定");
        delDialog.show();

        delDialog.setClicklistener(new UIAlertView.ClickListenerInterface() {

                                       @Override
                                       public void doLeft() {
                                           delDialog.dismiss();
                                       }

                                       @Override
                                       public void doRight() {
                                           /*String productID = mListGoods.get(groupPosition).getGoods().get(childPosition).getProductID();
                                           ShoppingCartBiz.delGood(productID);*/
                                           delGoods(groupPosition, childPosition);
                                           setSettleInfo();
                                           delDialog.dismiss();
                                       }
                                   }
        );
    }

    private void delGoods(final int groupPosition, int childPosition) {
        /*mListGoods.get(groupPosition).getGoods().remove(childPosition);
        if (mListGoods.get(groupPosition).getGoods().size() == 0) {
            mListGoods.remove(groupPosition);
        }*/
        mListGoods.get(groupPosition).delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    mListGoods.remove(groupPosition);
                    notifyDataSetChanged();
                }else {
                    Logger.e(e);
                    toast("删除失败");
                }
            }
        });
    }

    class GroupViewHolder {
        TextView tvGroup;
        TextView tvEdit;
        ImageView ivCheckGroup;
    }

    class ChildViewHolder {
        /** 商品名称 */
        TextView tvChild;
        /** 商品规格 */
        TextView tvGoodsParam;
        /** 选中 */
        ImageView ivCheckGood;
        //图书照片
        ImageView ivGoods;
        /** 非编辑状态 */
        LinearLayout llGoodInfo;
        /** 编辑状态 */
        RelativeLayout rlEditStatus;
        /** +1 */
        ImageView ivAdd;
        /** -1 */
        ImageView ivReduce;
        /** 删除 */
        TextView tvDel;
        /** 新价格 */
        TextView tvPriceNew;
        /** 旧价格 */
        TextView tvPriceOld;
        /** 商品状态的数量 */
        TextView tvNum;
        /** 编辑状态的数量 */
        TextView tvNum2;
    }
}
