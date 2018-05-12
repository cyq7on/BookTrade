package cn.bmob.imdemo.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class DietPlan extends BmobObject{
    public String name;
    public String createUserId;
    //早中午三餐餐品
    public List<String> diet = new ArrayList<>(3);
    public List<String> nutrientList = new ArrayList<>();
}
