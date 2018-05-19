package cn.bmob.imdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * @Description: 购物车以及订单
 * @author: cyq7on
 * @date: 18-5-19 上午10:03
 * @version: V1.0
 */
public class CartOrOrderBean extends BmobObject {
    public Book book;
    public int count = 1;
    //卖家
    public User fromUser;
    //买家
    public User toUser;
    //是否购物车数据，false为订单
    public boolean isCart = true;
    //是否选中
    public boolean isChecked = false;
    public boolean isEdit = false;
}
