package cn.bmob.imdemo.adapter;

import android.content.Context;

import java.util.Collection;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseRecyclerAdapter;
import cn.bmob.imdemo.adapter.base.BaseRecyclerHolder;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;

/**联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 * @author :smile
 * @project:ContactNewAdapter
 * @date :2016-04-27-14:18
 */
public class StationAdapter extends BaseRecyclerAdapter<String> {


    public StationAdapter(Context context, IMutlipleItem<String> items, Collection<String> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, String s, int position) {
        holder.setText(R.id.tv_name,s);
    }

}
