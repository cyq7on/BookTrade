package cn.bmob.imdemo.ui;

import android.os.Bundle;

import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.ui.fragment.OrderFragment;

public class OrderActivity extends ParentWithNaviActivity {

    @Override
    protected String title() {
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        Bundle bundle = getBundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

}
