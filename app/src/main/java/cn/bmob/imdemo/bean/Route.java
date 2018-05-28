package cn.bmob.imdemo.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Route extends BmobObject {
    public String name;
    public String start;
    public String end;
    public String time;
    public String other;
    public List<String> station;
}
