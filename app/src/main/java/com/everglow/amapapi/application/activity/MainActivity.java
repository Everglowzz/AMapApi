package com.everglow.amapapi.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.everglow.amapapi.R;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.textView3)
    TextView mTextView3;
    @BindView(R.id.textView8)
    TextView mTextView8;
    @BindView(R.id.tv_location)
    TextView mTvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!mayMultiplePermission()) return;
        initData();
    }

    private void initData() {
        RxView.clicks(mTvLocation)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(aVoid -> startActivity(new Intent(MainActivity.this, LocationActivity.class)));
        RxView.clicks(mTextView3)
                .throttleFirst(1,TimeUnit.SECONDS)
                .subscribe(aVoid -> startActivity(new Intent(MainActivity.this,Map3dActivity.class)));
        RxView.clicks(mTextView8)
                .throttleFirst(1,TimeUnit.SECONDS)
                .subscribe(aVoid -> startActivity(new Intent(MainActivity.this,PoiKeyWordSearchActivity.class)));

    }

    @Override
    protected void permissionGranted(String permission, boolean complete) {
        super.permissionGranted(permission, complete);
        if (complete) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
