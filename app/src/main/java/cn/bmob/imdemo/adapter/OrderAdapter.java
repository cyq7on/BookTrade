package cn.bmob.imdemo.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.CartOrOrderBean;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.ChatActivity;
import cn.bmob.imdemo.util.ShoppingCartBiz;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;

public class OrderAdapter extends CartAdapter {

    public OrderAdapter(BaseActivity context) {
        super(context);
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
        holder.tvEdit.setVisibility(View.GONE);
        holder.ivCheckGroup.setVisibility(View.GONE);
        return convertView;
    }
}
