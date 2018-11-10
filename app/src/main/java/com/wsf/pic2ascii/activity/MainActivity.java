package com.wsf.pic2ascii.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.wsf.pic2ascii.R;
import com.wsf.pic2ascii.rxpermissions.RxPermissions;
import com.wsf.pic2ascii.utils.CommUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.luck.picture.lib.config.PictureConfig.CHOOSE_REQUEST;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_show)
    public ImageView ivShow;

    @BindView(R.id.btn_convert)
    public Button btnConvert;

    @BindView(R.id.btn_select)
    public Button btnSelect;

    @BindView(R.id.tv_path)
    public TextView tvPath;

    private static final String basePath=Environment.getExternalStorageDirectory().getPath()+"/com.wsf.pic2ascii";
    private static final String picListPath=basePath+"/images";
    private String mediaPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermissionsAndMakeFile();

    }

    private void checkPermissionsAndMakeFile(){
        new RxPermissions(this)
                .request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if(granted){
                            File file=new File(picListPath);
                            if(!file.exists()){
                                file.mkdirs();

                            }else if(file.isFile()){
                                file.mkdir();
                            }else {
                                File[] files = file.listFiles();
                                for (File file1 : files) {
                                    file1.delete();
                                }
                            }
                        }else {
                            Toast.makeText(MainActivity.this,"无权限",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void selectMedia(){
        CommUtil.choosePhoto(this,CHOOSE_REQUEST);
    }

    private void convertPic(String path){
        Bitmap pic=CommUtil.createAsciiPic(path,this);
        ivShow.setImageBitmap(pic);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_REQUEST:
                if(resultCode==RESULT_OK){
                    List<LocalMedia> lms=PictureSelector.obtainMultipleResult(data);
                    String path="";
                    LocalMedia lm=null;
                    if(lms!=null&&lms.size()>0){
                        lm=lms.get(0);
                        path=lm.getPath();
                    }
                    if(lm.isCompressed()){
                        path=lm.getCompressPath();
                    }
                    if(lm.isCut()){
                        path=lm.getCutPath();
                    }
                    if(TextUtils.isEmpty(path)){
                        Toast.makeText(MainActivity.this,"请选择有效文件",Toast.LENGTH_LONG).show();
                        return;
                    }
                    showMedia(path);
                }
                break;

        }
    }

    private void showMedia(final String path) {
        Glide.with(this).load(path).into(ivShow);
        mediaPath=path;
    }

    @OnClick({R.id.btn_convert,R.id.btn_select})
    public void onBtnConvertClick(View view){

        Button btn=(Button)view;
        switch (btn.getId()){
            case R.id.btn_select:
                selectMedia();
            break;
            case R.id.btn_convert:
                convertPic(mediaPath);
            break;
        }

    }
}
