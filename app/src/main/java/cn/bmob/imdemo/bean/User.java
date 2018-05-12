package cn.bmob.imdemo.bean;

import cn.bmob.imdemo.db.NewFriend;
import cn.bmob.v3.BmobUser;

/**
 * @Description: 
 * @author: cyq7on
 * @date: 2018/4/25 16:51
 * @version: V1.0
 */
public class User extends BmobUser {

    private String avatar;
    public String age;
    public int sex;
    public int role;


    public User() {
    }

    public User(NewFriend friend) {
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(friend.getAvatar());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        User user = (User) o;
        return getObjectId().equals(user.getObjectId());
    }
}
