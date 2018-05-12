package cn.bmob.imdemo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.bean.DietPlan;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class UploadDietActivity extends ParentWithNaviActivity {

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_breakfast)
    EditText etBreakfast;
    @Bind(R.id.et_launch)
    EditText etLaunch;
    @Bind(R.id.et_dinner)
    EditText etDinner;
    @Bind(R.id.et_nutrient)
    TextView etNutrient;
    @Bind(R.id.btn_upload)
    Button btnUpload;
    private String[] items = {"能量", "蛋白质", "维生素A", "维生素B", "维生素C", "维生素D", "维生素E", "钙铁锌硒"};
    private DietPlan dietPlan = new DietPlan();

    @Override
    protected String title() {
        return "上传饮食计划";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_diet);
        ButterKnife.bind(this);
        etNutrient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                if(TextUtils.isEmpty(name)){
                    toast("请填写计划名");
                    return;
                }
                String breakfast = etBreakfast.getText().toString();
                if(TextUtils.isEmpty(breakfast)){
                    toast("请填写早餐餐品");
                    return;
                }
                String launch = etLaunch.getText().toString();
                if(TextUtils.isEmpty(launch)){
                    toast("请填写午餐餐品");
                    return;
                }
                String dinner = etDinner.getText().toString();
                if(TextUtils.isEmpty(dinner)){
                    toast("请填写晚餐餐品");
                    return;
                }
                String nutrient = etNutrient.getText().toString();
                if(TextUtils.isEmpty(nutrient)){
                    toast("请选择营养成分");
                    return;
                }
                dietPlan.createUserId = user.getObjectId();
                dietPlan.name = name;
                dietPlan.diet.add(breakfast);
                dietPlan.diet.add(launch);
                dietPlan.diet.add(dinner);
                dietPlan.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            toast("上传成功");
                            finish();
                        } else {
                            toast("上传失败");
                            Logger.d(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void showDialog() {
        dietPlan.nutrientList.clear();
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("营养成分")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dietPlan.nutrientList.clear();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < dietPlan.nutrientList.size(); j++) {
                            stringBuilder.append(dietPlan.nutrientList.get(j)).append("\t");
                            //textView 设置省略号无效
                            if (j > 3) {
                                stringBuilder.append("...");
                                break;
                            }
                        }
                        etNutrient.setText(stringBuilder);

                    }
                })
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Logger.d(which + "\n" + isChecked);
                        if (isChecked) {
                            dietPlan.nutrientList.add(items[which]);
                        } else {
                            dietPlan.nutrientList.remove(items[which]);
                        }
                    }
                }).create();
        dialog.show();
    }
}
