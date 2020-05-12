package com.ztmg.cicmorgan.account.picture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.ToastUtils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 作者：bcq on 2017/3/17 17:23
 */

public class PicGridActivity extends BaseActivity implements PicGridViewAdapter.OnPicListening {

    GridView gridView;
    TextView txt_picnum, tv_finish;
    List<ImgEntity> imageBeanList;
    List<ImgEntity> backImageBeanList;
    List<ImgEntity> preImgList;
    private int imgSize;
    PicGridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pic_gridview);
        initView();
        initData();
        initLintening();
    }

    @Override
    protected void initView() {
        imgSize = getIntent().getIntExtra("ImgSize", 0);
        imageBeanList = new ArrayList<ImgEntity>();
        backImageBeanList = new ArrayList<ImgEntity>();
        gridView = (GridView) findViewById(R.id.gr_view);
        txt_picnum = (TextView) findViewById(R.id.txt_picnum);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
    }

    @Override
    protected void initData() {
        preImgList = (List<ImgEntity>) getIntent().getSerializableExtra("ImgList");
        imageBeanList = CheckRootPic.getRootPic(this);
        if (preImgList != null && preImgList.size() > 0) {
            for (ImgEntity baseE : imageBeanList) {
                for (ImgEntity en : preImgList) {
                    if (baseE.getName().equals(en.getName())) {
                        baseE.setBln(true);
                    }
                }

            }
            backImageBeanList.addAll(preImgList);
        }
        gridViewAdapter = new PicGridViewAdapter(this, imageBeanList, imgSize, preImgList);
        gridViewAdapter.setOnPicListening(this);
        gridView.setAdapter(gridViewAdapter);
    }

    public void initLintening() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //				ToastUtils.show(PicGridActivity.this,"gridview的pos：：：：：：：：" + position);

            }
        });
        txt_picnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("PIC", (Serializable) backImageBeanList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("PIC", (Serializable) backImageBeanList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void setPicNum(int size) {
        txt_picnum.setText("图片共：" + size + "张");
    }

    @Override
    public void getImgList(List<ImgEntity> map) {
        backImageBeanList.clear();
        //	        for (Integer key:map.keySet()){
        backImageBeanList.addAll(map);
        //	        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void startAct() {

    }
}
