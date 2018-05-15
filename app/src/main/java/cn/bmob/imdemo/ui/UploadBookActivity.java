package cn.bmob.imdemo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.CategoryAdapter;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.bean.Book;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UploadBookActivity extends ParentWithNaviActivity {

    private static final int REQUEST_CODE = 0x1001;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_info)
    EditText etInfo;
    @Bind(R.id.et_image)
    TextView etImage;
    @Bind(R.id.tv_category)
    TextView tvCategory;
    @Bind(R.id.btn_upload)
    Button btnUpload;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.et_price)
    EditText etPrice;
    private Book book = new Book();

    private BmobFile bmobFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        ButterKnife.bind(this);
        initNaviView();
        etImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(UploadBookActivity.this)
                        .setTitle("图书分类")
                        .setItems(CategoryAdapter.category, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tvCategory.setText(CategoryAdapter.category[i]);
                                book.categoryId = i;
                            }
                        }).create();
                dialog.show();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String info = etInfo.getText().toString();
                final String category = tvCategory.getText().toString();
                final String price = etPrice.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    toast("请填写书名");
                    return;
                }
                if (TextUtils.isEmpty(info)) {
                    toast("请填写图书简介");
                    return;
                }
                if (TextUtils.isEmpty(price)) {
                    toast("请填写图书价格");
                    return;
                }
                if (TextUtils.isEmpty(category)) {
                    toast("请选择图书分类");
                    return;
                }
                if (bmobFile == null) {
                    toast("请选择图片");
                    return;
                }
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Logger.d(bmobFile.getFileUrl());
                            book.imageUrl = bmobFile.getFileUrl();
                            book.user = user;
                            book.name = name;
                            book.info = info;
                            book.price = price;
                            book.category = category;
                            book.save(new SaveListener<String>() {
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
                        } else {
                            toast("上传失败");
                            Logger.d(e.getMessage());
                        }
                    }
                });
            }

        });
    }


    @Override
    protected String title() {
        return "上传图书";
    }

    private void selectPhoto() {
        int color = getResources().getColor(R.color.colorPrimaryDark);
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(color)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(color)
                // 裁剪大小。needCrop为true的时候配置
//                .cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();

        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra("result");
            for (String path : pathList) {
                Logger.d(path + "\n");
            }
            if (pathList.size() > 0) {
                File file = new File(pathList.get(0));
                bmobFile = new BmobFile(file);
                image.setImageURI(Uri.fromFile(file));
            }
        }
    }
}
