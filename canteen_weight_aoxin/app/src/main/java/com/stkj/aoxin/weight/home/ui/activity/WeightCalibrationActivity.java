package com.stkj.aoxin.weight.home.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.stkj.aoxin.weight.R;
import com.stkj.common.ui.activity.BaseActivity;

public class WeightCalibrationActivity extends BaseActivity {

    private ImageView iv_back;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_calibration);

        initViews();
    }

    private void initViews() {
        tv_back    = findViewById(R.id.tv_back);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}