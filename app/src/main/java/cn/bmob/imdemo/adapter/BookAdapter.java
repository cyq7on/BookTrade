package cn.bmob.imdemo.adapter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.util.Collection;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseRecyclerAdapter;
import cn.bmob.imdemo.adapter.base.BaseRecyclerHolder;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.Book;
import cn.bmob.imdemo.bean.CartOrOrderBean;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.ChatActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 * @author :smile
 * @project:ContactNewAdapter
 * @date :2016-04-27-14:18
 */
public class BookAdapter extends BaseRecyclerAdapter<Book> {

    private BaseActivity context;

    public BookAdapter(BaseActivity context, IMutlipleItem<Book> items, Collection<Book> datas) {
        super(context,items,datas);
        this.context = context;
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, final Book book, int position) {
        // 设置标题上的文本信息
        holder.setText(R.id.tv_name,book.name);
        holder.setText(R.id.tv_info,book.info);
        holder.setText(R.id.tvPrice,String.format("￥%s",book.price));
        holder.setImageView(book.imageUrl, R.mipmap.ic_launcher, R.id.iv_book);
        ImageView ivCart = holder.getView(R.id.ivCart);
        ImageView ivChat = holder.getView(R.id.ivChat);
        final User user = book.user;
        final User currentUser = BmobUser.getCurrentUser(User.class);
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getObjectId().equals(currentUser.getObjectId())){
                    toast("这是你自己的书哦");
                    return;
                }
                CartOrOrderBean bean = new CartOrOrderBean();
                bean.book = book;
                bean.fromUser = user;
                bean.toUser = currentUser;
                bean.count = 1;
                bean.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            toast("成功加入购物车");
                        }else {
                            Logger.e(e);
                            toast("加入购物车出错");
                        }
                    }
                });
            }
        });
    }

}
