package cn.bmob.imdemo.adapter;

import android.content.Context;

import java.util.Collection;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseRecyclerAdapter;
import cn.bmob.imdemo.adapter.base.BaseRecyclerHolder;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.bean.DietPlan;

/**联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 * @author :smile
 * @project:ContactNewAdapter
 * @date :2016-04-27-14:18
 */
public class DietPlanAdapter extends BaseRecyclerAdapter<DietPlan> {


    public DietPlanAdapter(Context context, IMutlipleItem<DietPlan> items, Collection<DietPlan> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, final DietPlan dietPlan, int position) {
        holder.setText(R.id.tv_name,"计划：" + dietPlan.name);
        StringBuilder stringBuilder = new StringBuilder()
                .append("早餐：").append(dietPlan.diet.get(0)).append("\n")
                .append("午餐：").append(dietPlan.diet.get(1)).append("\n")
                .append("晚餐：").append(dietPlan.diet.get(2)).append("\n");
        int size = dietPlan.nutrientList.size();
        if(size > 0){
            stringBuilder.append("\n").append("营养成分：").append("\n");
        }
        for (int i = 0; i < size; i++) {
            stringBuilder.append(dietPlan.nutrientList.get(i)).append("\n");
        }

        holder.setText(R.id.tv_info,stringBuilder.substring(0,stringBuilder.length() - 1));

    }

}
