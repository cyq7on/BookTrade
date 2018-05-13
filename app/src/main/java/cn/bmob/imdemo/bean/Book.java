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
    public String category;
    public int categoryId;
    public String createUserId;
    public String imageUrl;
    public BmobRelation collectUsers = new BmobRelation();
    public List<User> collectList = new ArrayList<>();

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", collectUsers=" + collectUsers +
                '}';
    }
}
