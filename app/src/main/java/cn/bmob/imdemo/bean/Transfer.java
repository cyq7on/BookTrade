package cn.bmob.imdemo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Transfer extends BmobObject {
    public String start;
    public String end;
    public BmobRelation route;
}
