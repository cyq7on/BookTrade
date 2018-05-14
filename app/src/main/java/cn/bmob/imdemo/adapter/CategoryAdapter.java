package cn.bmob.imdemo.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.Book;
import cn.bmob.imdemo.bean.FatherData;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.ChatActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.v3.BmobUser;

/**
 * Created by cyq7on on 18-3-24.
 */

public class CategoryAdapter extends BaseExpandableListAdapter {

    public static final String[] category = new String[] {
            "专业必修","人文历史","语言文化","计算机","其他"
    } ;
    // 定义一个Context
    private BaseActivity context;
    // 定义一个LayoutInflater
    private LayoutInflater mInflater;
    // 定义一个List来保存列表数据
    private ArrayList<FatherData> data_list;

    // 定义一个构造方法
    public CategoryAdapter(BaseActivity context, ArrayList<FatherData> datas) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data_list = datas;
    }

    // 刷新数据
    public void flashData(ArrayList<FatherData> datas) {
        this.data_list.clear();
        this.data_list.addAll(datas);
        this.notifyDataSetChanged();
    }

    // 获取二级列表的内容
    @Override
    public Book getChild(int arg0, int arg1) {
        return data_list.get(arg0).getList().get(arg1);
    }

    // 获取二级列表的ID
    @Override
    public long getChildId(int arg0, int arg1) {
        return arg1;
    }

    // 定义二级列表中的数据
    @Override
    public View getChildView(final int arg0, final int arg1, boolean arg2, View arg3, ViewGroup arg4) {
        // 定义一个二级列表的视图类
        final HolderView childrenView;
        if (arg3 == null) {
            childrenView = new HolderView();
            // 获取子视图的布局文件
            arg3 = mInflater.inflate(R.layout.item_child, arg4, false);
            childrenView.titleView = (TextView) arg3.findViewById(R.id.tv_name);
            childrenView.tvInfo = (TextView) arg3.findViewById(R.id.tv_info);
            childrenView.ivBook = (ImageView) arg3.findViewById(R.id.iv_book);
            childrenView.ivChat = (ImageView) arg3.findViewById(R.id.ivChat);
            childrenView.ivCart = (ImageView) arg3.findViewById(R.id.ivCart);
            // 这个函数是用来将holderview设置标签,相当于缓存在view当中
            arg3.setTag(childrenView);
        } else {
            childrenView = (HolderView) arg3.getTag();
        }

        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        childrenView.titleView.setText(getChild(arg0,arg1).name);
        childrenView.tvInfo.setText(getChild(arg0,arg1).info);
        ImageLoaderFactory.getLoader().loadAvator(childrenView.ivBook,getChild(arg0,arg1).imageUrl,
                R.mipmap.ic_launcher);
        childrenView.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = getChild(arg0,arg1);
                User user = book.user;
                User currentUser = BmobUser.getCurrentUser(User.class);
                if(user.getObjectId().equals(currentUser.getObjectId())){
                    toast("这是你自己的书哦");
                    return;
                }
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    toast("尚未连接IM服务器");
                    return;
                }
                BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(),
                        user.getAvatar());
                BmobIMConversation conversationEntrance = BmobIM.getInstance().
                        startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", conversationEntrance);
                context.startActivity(ChatActivity.class, bundle, false);
            }
        });
        childrenView.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return arg3;
    }

    private void toast(String info) {
        Toast.makeText(context,info,
                Toast.LENGTH_SHORT).show();
    }

    // 保存二级列表的视图类
    private class HolderView {
        TextView titleView;
        TextView tvInfo;
        ImageView ivBook;
        ImageView ivChat;
        ImageView ivCart;
    }

    // 获取二级列表的数量
    @Override
    public int getChildrenCount(int arg0) {
        return data_list.get(arg0).getList().size();
    }

    // 获取一级列表的数据
    @Override
    public String getGroup(int arg0) {
        return data_list.get(arg0).getTitle();
    }

    // 获取一级列表的个数
    @Override
    public int getGroupCount() {
        return data_list.size();
    }

    // 获取一级列表的ID
    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    // 设置一级列表的view
    @Override
    public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
        HodlerViewFather hodlerViewFather;
        if (arg2 == null) {
            hodlerViewFather = new HodlerViewFather();
            arg2 = mInflater.inflate(R.layout.item_father, arg3, false);
            hodlerViewFather.titlev = (TextView) arg2.findViewById(R.id.tv_name);
            arg2.setTag(hodlerViewFather);
        } else {
            hodlerViewFather = (HodlerViewFather) arg2.getTag();
        }

        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        hodlerViewFather.titlev.setText(data_list.get(arg0).getTitle());

        // 返回一个布局对象
        return arg2;
    }

    // 定义一个 一级列表的view类
    private class HodlerViewFather {
        TextView titlev;
        ImageView group_state;
    }

    /**
     * 指定位置相应的组视图
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 当选择子节点的时候，调用该方法(点击二级列表)
     */
    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }
}

