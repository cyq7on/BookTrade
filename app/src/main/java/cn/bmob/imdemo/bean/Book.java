package cn.bmob.imdemo.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @Description:
 * @author: cyq7on
 * @date: 18-5-13 下午4:03
 * @version: V1.0
 */
public class Book extends BmobObject {
    public String name;
    public String info;
    public String price;
    public String category;
    public int categoryId;
    public User user;
    public String imageUrl;
    public BmobRelation collectUsers = new BmobRelation();
    public List<User> collectList = new ArrayList<>();

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", user='" + user.getUsername() + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", collectUsers=" + collectUsers +
                '}';
    }
}
