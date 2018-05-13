package cn.bmob.imdemo.bean;

import java.util.ArrayList;

/**
 * @Description: 
 * @author: cyq7on
 * @date: 18-5-13 下午4:00
 * @version: V1.0
 */

public class FatherData {
    private String title;
    private ArrayList<Book> list;// 二级列表数据
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ArrayList<Book> getList() {
        return list;
    }
    public void setList(ArrayList<Book> list) {
        this.list = list;
    }
}

