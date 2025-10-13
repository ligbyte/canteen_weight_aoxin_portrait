package com.stkj.aoxin.weight.home.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.stkj.aoxin.weight.AppApplication;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.ui.adapter.GoodsCountAdapter;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.home.model.CategoryItem;
import com.stkj.aoxin.weight.home.ui.adapter.CategoryAdapter;
import com.stkj.aoxin.weight.home.ui.widget.SuccessDialog;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.callback.OnHandListener;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;
import com.stkj.aoxin.weight.pay.model.TTSSpeakEvent;
import com.stkj.aoxin.weight.pay.service.PayService;
import com.stkj.aoxin.weight.pay.ui.weight.HandCalculator;
import com.stkj.aoxin.weight.utils.OccurrenceConverter;
import com.stkj.aoxin.weight.weight.Weight;
import com.stkj.aoxin.weight.weight.WeightCallback;
import com.stkj.aoxin.weight.weight.WeightUtils;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.RoundImageView;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class CheckActivity extends BaseActivity {

    private static final String TAG = "CheckActivity";
    private static final int REQUEST_CODE_CAPTURE = 1001;

    private RadioGroup radioGroup,radioGroupQuPi;
    private RadioButton rbSingleWeight, rbMultipleWeight, rbQuantitySign;

    double totalGrossWeight = 0;
    double totalTareWeight = 0;

    private RadioButton rb_qupi_shoudong, rb_qupi_0, rb_qupi_package;

    public static String globalUnit = "公斤";

    private int pageTotal = 1;
    private int pageIndex = 1;

    // UI 组件
    private ImageView btnBack;
    private TextView tvTitle;

    // 分类菜单
    private RecyclerView rvCategory;

    private RecyclerView rv_goods_count;
    private LinearLayout ll_goods_count;
    private CategoryAdapter categoryAdapter;
    private List<CategoryItem> categoryList;

    private String createTime = "";

    private String orderID = "";

    private String supplierName = "";

    private int selectedCategoryIndex = 2; // 默认选中五花肉

    // 商品信息

    private ImageView ivProduct;
    private TextView tvProductName;

    private TextView tvProductType;

    private TextView tvProductBrand;

    private TextView tvProductAddr;

    private TextView tv_price;

    private TextView tv_price_unit;
    private TextView tvProductLevel;

    List<OrderInfoBean.SupplyProductOrderDetailListBean> supplyProductOrderDetailList;

    // 重量输入
    private TextView tvReweight;

    private TextView tv_check_maozhong;

    private TextView tv_review_tare_summary;

    private TextView tvGrossWeight;
    private LinearLayout ll_gross_weight;

    private LinearLayout ll_trae_weight;
//    private TextView tvPhotoMissing;
    private TextView tvTareWeight;

    private String selectedOptionQupi = "";


    // 拍照区域
    private RoundImageView btnTakeProductPhoto;
    private RoundImageView btnTakePackagePhoto;

    private ShapeLinearLayout ll_keyboard;

    // 复核详情

    private TextView product_weight_summary;
    private TextView tv_net_weight_diff_summary;
    private TextView tv_order_weight_summary;

    private TextView tv_review_gross_summary;
    private TextView tv_review_trae_summary;

    private TextView tvOrderWeightValue;
    private TextView tvReviewGrossValue;
    private TextView tvReviewTareValue;
    private TextView tvNetWeightDiffValue;
    private TextView tvOrderAmountValue;
    private TextView tvMissingAmountValue;
    private TextView tvReviewAmountValue;
    private TextView tvWeight0;

    public static int currentMode = 0;


    // 操作按钮
    private Button btnCancel;
    private Button btnReturnExchange;
    private Button btn_check_ruku;
    private Button btn_check_use;

    private HandCalculator handCalculator;

    // 数据变量
    private double orderWeight = 0.00; // 订单重量
    private double grossWeight = 0.00; // 复核毛重
    private double tareWeight = 0.00; // 复核皮重
    private double unitPrice = 0.00; // 单价
    private double orderAmount = 0.00; // 订单金额

    private long netWeightTime = 0L;

    private double weightMultiplier = 1;


    private DecimalFormat weightFormat = new DecimalFormat("0.00");
    private DecimalFormat amountFormat = new DecimalFormat("0.00");
    private Uri photoUri;
    private File photoFile;

    public static int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        initViews();
//        initCategoryData();
        initData();
        setupListeners();
//        updateReviewDetails();

        initWeightght();

        orderInfo(getIntent().getStringExtra("orderID"));
//        var scaleWeightModule= uni.requireNativePlugin('RUICLOUD-SCALE ScaleWeightModule');
    }

    private void initWeightght() {
        WeightUtils.start("/dev/ttyS0", new WeightCallback() {
            @Override
            public void callback(final Weight weight, final String cache) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String pattern = "重量:%.3f    皮重:%.3f    稳定:%s    皮重:%s    零位:%s";
//                        if (weight != null) {
//                            String text = String.format(
//                                    pattern,
//                                    weight.netWeight,
//                                    weight.tareWeight,
//                                    weight.stabled ? "是" : "否",
//                                    weight.netFlag ? "是" : "否",
//                                    weight.zeroFlag ? "是" : "否"
//                            );
//                            binding.tvWeight.setText(text);
//                        }

                        if (System.currentTimeMillis() - netWeightTime < 100) {
                            return  ;
                        }
                        netWeightTime = System.currentTimeMillis();

                        Log.d(TAG, "limerbQuantitySign: " + 224);
                        if(rbQuantitySign.isChecked()){
                            return;
                        }
                        Log.d(TAG, "limerbQuantitySign: " + 228);
                        if (weight != null) {

                            if(weight.netWeight > 0){

                                if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl())) {
                                    if (weight.netWeight > 149.999 ){
                                        ll_gross_weight.setBackgroundResource(R.drawable.weight_input_red_bg);
                                        tvGrossWeight.setText("超出称的量程");
                                        tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));
                                        return;
                                    }

                                    tvGrossWeight.setText(PriceUtils.formatPrice(weight.netWeight * weightMultiplier));
                                    btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_begin);
                                    ll_gross_weight.setBackgroundResource(R.drawable.weight_input_bg);
                                    tvGrossWeight.setTextColor(Color.parseColor("#1D2129"));
                                    tvReviewGrossValue.setText(PriceUtils.formatPrice(weight.netWeight * weightMultiplier ) + globalUnit);
                                    return;
                                }

                                if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getPassImageUrl()) && rb_qupi_package.isChecked()) {
                                    if (!supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                                        if (weight.netWeight > 149.999 ){
                                            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_red_bg);
                                            tvTareWeight.setText("超出称的量程");
                                            tvTareWeight.setTextColor(Color.parseColor("#FFFFFF"));
                                            return;
                                        }

                                            tvTareWeight.setText(PriceUtils.formatPrice(weight.netWeight * weightMultiplier));
                                            btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_begin);
                                            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
                                            tvTareWeight.setTextColor(Color.parseColor("#1D2129"));
                                            tvReviewTareValue.setText(PriceUtils.formatPrice(weight.netWeight * weightMultiplier ) + globalUnit);
                                            calculateWeightMoney();
                                            Log.d(TAG, "limecalculateWeightMoney: " + 260);
                                    }
                                }

                            } else {


                                if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl())) {
                                btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
                                ll_gross_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
                                tvGrossWeight.setText("请放置商品");
                                tvReviewTareValue.setText("--" + globalUnit);
                                tvReviewGrossValue.setText("--" + globalUnit);
                                tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));
                                    return;
                                }


                                if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getPassImageUrl()) && rb_qupi_package.isChecked()) {
                                    btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
                                    ll_trae_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
                                    tvTareWeight.setText("请放置包装");
                                    tvReviewTareValue.setText("0.00" + globalUnit);
                                    tvNetWeightDiffValue.setText("--" + globalUnit);
                                    tvMissingAmountValue.setText("0.00元");
                                    tvReviewAmountValue.setText("0.00元");
                                    tvTareWeight.setTextColor(Color.parseColor("#FFFFFF"));
                                    return;
                                }

                            }


                        }else {
//                            btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
//                            tvGrossWeight.setText("0.00");
                            if (tvGrossWeight.getText().toString().trim().equals("请放置商品")){
                                tvReviewGrossValue.setText("--" + globalUnit);
                            }

                        }
                    }
                });

            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        // 头部导航
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);

        tv_review_tare_summary = findViewById(R.id.tv_review_tare_summary);

        handCalculator = findViewById(R.id.hc_input);

        // 分类菜单
        rvCategory = findViewById(R.id.rv_category);

        rv_goods_count = findViewById(R.id.rv_goods_count);

        ll_goods_count = findViewById(R.id.ll_goods_count);

        // 商品信息
        product_weight_summary = findViewById(R.id.product_weight_summary);
        tv_net_weight_diff_summary = findViewById(R.id.tv_net_weight_diff_summary);
        tv_review_gross_summary = findViewById(R.id.tv_review_gross_summary);
        tv_review_trae_summary = findViewById(R.id.tv_review_trae_summary);
        tv_order_weight_summary = findViewById(R.id.tv_order_weight_summary);
        ivProduct = findViewById(R.id.iv_product);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductType = findViewById(R.id.tv_product_type);
        tvProductBrand = findViewById(R.id.tv_product_brand);
        tvProductAddr = findViewById(R.id.tv_product_addr);
        tvProductLevel = findViewById(R.id.tv_product_level);

        ll_keyboard = findViewById(R.id.ll_keyboard);

        tv_price = findViewById(R.id.tv_price);

        tv_price_unit = findViewById(R.id.tv_price_unit);
        // 重量输入
        ll_gross_weight = findViewById(R.id.ll_gross_weight);
        ll_trae_weight = findViewById(R.id.ll_trae_weight);
        tvReweight = findViewById(R.id.tv_reweight);
        tv_check_maozhong = findViewById(R.id.tv_check_maozhong);
        tvGrossWeight = findViewById(R.id.tv_gross_weight);
//        tvPhotoMissing = findViewById(R.id.tv_photo_missing);
        tvTareWeight = findViewById(R.id.tv_tare_weight);

        // 拍照区域
        btnTakeProductPhoto = findViewById(R.id.riv_goods);
        btnTakePackagePhoto = findViewById(R.id.riv_package);

        // 复核详情
        tvOrderWeightValue = findViewById(R.id.tv_order_weight_value);
        tvReviewGrossValue = findViewById(R.id.tv_review_gross_value);
        tvReviewTareValue = findViewById(R.id.tv_review_tare_value);
        tvNetWeightDiffValue = findViewById(R.id.tv_net_weight_diff_value);
        tvOrderAmountValue = findViewById(R.id.tv_order_amount_value);
        tvMissingAmountValue = findViewById(R.id.tv_missing_amount_value);
        tvReviewAmountValue = findViewById(R.id.tv_review_amount_value);

        tvWeight0 = findViewById(R.id.tv_weight_0);
        // 操作按钮
        btnCancel = findViewById(R.id.btn_cancel);
        btnReturnExchange = findViewById(R.id.btn_return_exchange);
        btn_check_ruku = findViewById(R.id.btn_check_ruku);
        btn_check_use = findViewById(R.id.btn_check_use);

        radioGroup = findViewById(R.id.radioGroup);
        rbSingleWeight = findViewById(R.id.rb_single_weight);
        rbMultipleWeight = findViewById(R.id.rb_multiple_weight);
        rbQuantitySign = findViewById(R.id.rb_quantity_sign);

        radioGroupQuPi = findViewById(R.id.radioGroupQuPi);
        rb_qupi_shoudong = findViewById(R.id.rb_qupi_shoudong);
        rb_qupi_0 = findViewById(R.id.rb_qupi_0);
        rb_qupi_package = findViewById(R.id.rb_qupi_package);

        handCalculator.getTvConsume().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (rbQuantitySign.isChecked()) {
                    if (s.toString().contains(".")){
                        handCalculator.getTvConsume().setText(s.toString().substring(0,s.length()-1));
                        tvGrossWeight.setText(s.toString().substring(0,s.length()-1));
                    }else {
                        tvGrossWeight.setText(s.toString());
                    }

                    if (StringUtils.isNumeric(s.toString())){
                        tvReviewGrossValue.setText(s.toString() + globalUnit);
                        calculateWeightMoney();
                        Log.d(TAG, "limecalculateWeightMoney: " + 403);
                    }else {
                        tvNetWeightDiffValue.setText("--" + globalUnit);
                        tvMissingAmountValue.setText("0.00元");
                        tvReviewAmountValue.setText("0.00元");
                        tvReviewTareValue.setText("按包装计量");
                        tvReviewGrossValue.setText("--" + globalUnit);
                    }


                    return;
                }

                if (rb_qupi_shoudong.isChecked()) {
                    tvTareWeight.setText(s.toString());
                }

//                if (StringUtils.isNumeric(s.toString()) && ( Double.parseDouble(s.toString()) > 0)){
//                    tvTareWeight.setTextColor(Color.parseColor("#1D2129"));
//                } else {
//                    tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
//                }


            }
        });

        handCalculator.setOnCalculateListener(new OnHandListener() {
            @Override
            public void onConfirmMoney(String payMoney) {
                if (rbQuantitySign.isChecked()) {

                    if (TextUtils.isEmpty(payMoney)){
                        ToastUtils.toastMsgWarning("请输入正确数量");
                        EventBus.getDefault().post(new TTSSpeakEvent("请输入正确数量"));
                        return;
                    }

                    tvReviewGrossValue.setText((int)Double.parseDouble(payMoney) + globalUnit);
                    calculateWeightMoney();
                    Log.d(TAG, "limecalculateWeightMoney: " + 444);
                    ll_keyboard.setVisibility(View.GONE);
                    return;
                }

                tvReviewTareValue.setText(PriceUtils.formatPrice(payMoney) + globalUnit);
                calculateWeightMoney();
                Log.d(TAG, "limecalculateWeightMoney: " + 451);
                ll_keyboard.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
              ll_keyboard.setVisibility(View.GONE);
            }

            @Override
            public void onDeleteLongClickListener() {
                //ll_keyboard.setVisibility(View.GONE);
                handCalculator.getTvConsume().setText("");
                handCalculator.getTvConsume().setHint("0.00");
                if (!rbQuantitySign.isChecked()) {
                    tvReviewTareValue.setText("0.00" + globalUnit);
                }else {
                    tvMissingAmountValue.setText("0.00元");
                    tvReviewAmountValue.setText("0.00元");
                    tvReviewGrossValue.setText("--" + globalUnit);
                    tvNetWeightDiffValue.setText("--" + globalUnit);

                }
                calculateWeightMoney();
                Log.d(TAG, "limecalculateWeightMoney: " + 470);

            }

            @Override
            public void onClickDisableConfirm() {
                OnHandListener.super.onClickDisableConfirm();
            }
        });

        tvTareWeight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (rb_qupi_shoudong.isChecked()) {
                    ll_keyboard.setVisibility(View.VISIBLE);
                }
            }
        });

        ll_trae_weight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (rb_qupi_shoudong.isChecked()) {
                    ll_keyboard.setVisibility(View.VISIBLE);
                }
            }
        });

        ll_gross_weight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (rbQuantitySign.isChecked()) {
                    if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && !TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) && !supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                        ToastUtils.toastMsgWarning("禁止修改数量");
                        EventBus.getDefault().post(new TTSSpeakEvent("禁止修改数量"));
                        return;
                    }
                    ll_keyboard.setVisibility(View.VISIBLE);
                }
            }
        });



    }

    private void initCategoryData() {

        // 设置RecyclerView
        categoryAdapter = new CategoryAdapter(categoryList);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(categoryAdapter);

        // 设置点击监听
        categoryAdapter.setOnCategoryClickListener((position, item) -> {

            if (currentIndex == position){
                ToastUtils.toastMsgWarning("已选中该商品");
                EventBus.getDefault().post(new TTSSpeakEvent("已选中该商品"));
                return;
            }

            if (categoryList.get(position).getStatus() == 2){
                ToastUtils.toastMsgWarning("该商品已复核");
                EventBus.getDefault().post(new TTSSpeakEvent("该商品已复核"));
                return;
            }


            if (categoryList.get(currentIndex).getStatus() != 2){
                categoryList.get(currentIndex).setStatus(0);
            }

            handCalculator.getTvConsume().setText("");
            currentIndex = position;
            categoryList.get(currentIndex).setStatus(1);
            categoryAdapter.notifyDataSetChanged();
            rb_qupi_package.setChecked(false);
            rb_qupi_0.setChecked(false);
            rb_qupi_shoudong.setChecked(false);
            setContentData(supplyProductOrderDetailList.get(currentIndex), currentIndex,false);
            tvNetWeightDiffValue.setText("--" + globalUnit);
            tvMissingAmountValue.setText("0.00元");
            tvReviewAmountValue.setText("0.00元");
            tvReviewTareValue.setText("--" + globalUnit);
            tvReviewGrossValue.setText("--" + globalUnit);
            btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
            tvTareWeight.setText("请放置包装");
            ll_keyboard.setVisibility(View.GONE);
            if (rbQuantitySign.isChecked()){
                ll_keyboard.setVisibility(View.VISIBLE);
            }
            tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
            hideSuccessButton();

            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
            supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            checkByCount(rbQuantitySign.isChecked());
        });
    }

    private void initData() {
        // 设置初始显示数据
        tvGrossWeight.setText(weightFormat.format(grossWeight));
    }

    private void setupListeners() {

        rbMultipleWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupQuPi.clearCheck();
            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
            supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
            tvTareWeight.setText("请放置包装");
            tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
            }
        });

        rbSingleWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupQuPi.clearCheck();
                supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
                supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
                tvTareWeight.setText("请放置包装");
                tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
            }
        });


        rbQuantitySign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupQuPi.clearCheck();
                supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
                supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
                tvTareWeight.setText("请放置包装");
                tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedOption = "";
                AppApplication.supplyProductOrderDetailList.clear();
                ll_goods_count.setVisibility(View.GONE);
                totalGrossWeight = 0;
                totalTareWeight = 0;
                if (checkedId == R.id.rb_single_weight) {
                    selectedOption = "单次称重签收";
                    tv_review_gross_summary.setText("复核毛重");
                    tv_review_trae_summary.setText("复核皮重");
                    handCalculator.getStv_spot().setClickable(true);
                    supplyProductOrderDetailList.get(currentIndex).setCheckType(0);
                    checkByCount(false);
                } else if (checkedId == R.id.rb_multiple_weight) {
                    selectedOption = "分次称重签收";
                    tv_review_gross_summary.setText("首次毛重");
                    tv_review_trae_summary.setText("首次皮重");
                    handCalculator.getStv_spot().setClickable(true);
                    supplyProductOrderDetailList.get(currentIndex).setCheckType(1);
                    checkByCount(false);
                } else if (checkedId == R.id.rb_quantity_sign) {
                    selectedOption = "按数量签收";
                    tv_review_gross_summary.setText("复核毛重");
                    tv_review_trae_summary.setText("复核皮重");
                    handCalculator.getStv_spot().setClickable(false);
                    supplyProductOrderDetailList.get(currentIndex).setCheckType(2);
                    checkByCount(true);
                }

            }
        });

        radioGroupQuPi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (rbQuantitySign.isChecked()){
                    rb_qupi_package.setChecked(false);
                    rb_qupi_0.setChecked(false);
                    rb_qupi_shoudong.setChecked(false);
                    ToastUtils.toastMsgWarning("无需去皮");
                    EventBus.getDefault().post(new TTSSpeakEvent("无需去皮"));
                    return;
                }

                if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
//                    ToastUtils.toastMsgWarning("请先放置商品");
//                    EventBus.getDefault().post(new TTSSpeakEvent("请先放置商品"));
                    if (checkedId == R.id.rb_qupi_package) {
                        tvTareWeight.setText("请放置包装");
                    }else {
                        tvTareWeight.setText("0.00");
                    }
                    return;
                }

                if (checkedId == R.id.rb_qupi_shoudong) {
                    if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                        rb_qupi_shoudong.setChecked(false);
                        ToastUtils.toastMsgWarning("请先称重商品");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先称重商品"));
                        return;
                    }

                    if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) || supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                        rb_qupi_shoudong.setChecked(false);
                        ToastUtils.toastMsgWarning("请先给商品拍照");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先给商品拍照"));
                        Log.d(TAG, "limephoto  :  " + 468);
                        rb_qupi_0.setChecked(false);
                        return;
                    }


                    selectedOptionQupi = "手动去皮";
                    supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                    btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
                    ll_keyboard.setVisibility(View.VISIBLE);
                    tvTareWeight.setText("0.00");
                    tvReviewTareValue.setText("--" + globalUnit);
                    tvNetWeightDiffValue.setText("--" + globalUnit);
                    hideSuccessButton();
                    calculateWeightMoney();
                    Log.d(TAG, "limecalculateWeightMoney: " + 652);
                } else if (checkedId == R.id.rb_qupi_0) {

                    if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                        rb_qupi_0.setChecked(false);
                        ToastUtils.toastMsgWarning("请先称重商品");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先称重商品"));
                        return;
                    }

                    if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) || supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                        rb_qupi_0.setChecked(false);
                        ToastUtils.toastMsgWarning("请先给商品拍照");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先给商品拍照"));
                        Log.d(TAG, "limephoto  :  " + 497);
                        rb_qupi_0.setChecked(false);
                        return;
                    }

                    ll_keyboard.setVisibility(View.GONE);
                    selectedOptionQupi = "免皮重";
                    supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                    btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
                    ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
                    tvTareWeight.setText("0.00");
                    tvTareWeight.setTextColor(Color.parseColor("#333333"));
                    tvReviewTareValue.setText("0.00" + globalUnit);
                    handCalculator.getTvConsume().setText("");
                    supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                    supplyProductOrderDetailList.get(currentIndex).setTareWeight(0);
                    calculateWeightMoney();

                    Log.d(TAG, "limecalculateWeightMoney: " + 683);

                } else if (checkedId == R.id.rb_qupi_package) {
                    Log.d(TAG, "limeradioGroupQuPi  :  " + 517);
                    if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                        Log.d(TAG, "limeradioGroupQuPi  :  " + 520);
                        rb_qupi_package.setChecked(false);
                        Log.d(TAG, "limerb_qupi_package  :  false " + 517);
                        ToastUtils.toastMsgWarning("请先称重商品");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先称重商品"));
                        return;
                    }

                    if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) || supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                        Log.d(TAG, "limeradioGroupQuPi  :  " + 528);
                        rb_qupi_package.setChecked(false);
                        Log.d(TAG, "limerb_qupi_package  :  false " + 525);
                        ToastUtils.toastMsgWarning("请先给商品拍照");
                        EventBus.getDefault().post(new TTSSpeakEvent("请先给商品拍照"));
                        Log.d(TAG, "limephoto  :  " + 529);
                        rb_qupi_0.setChecked(false);
                        return;
                    }

                    Log.d(TAG, "limeradioGroupQuPi  :  " + 538);
                    ll_keyboard.setVisibility(View.GONE);
                    selectedOptionQupi = "整装去皮";
                    supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                    btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
                    ll_trae_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
                    tvTareWeight.setText("请放置包装");
                    handCalculator.getTvConsume().setText("");
                    tvTareWeight.setTextColor(Color.parseColor("#FFFFFF"));
                    hideSuccessButton();
                    calculateWeightMoney();
                    Log.d(TAG, "limecalculateWeightMoney: " + 718);
                }

                // Show a toast message with the selected option
                //ToastUtils.toastMsgWarning(selectedOptionQupi);
                EventBus.getDefault().post(new TTSSpeakEvent(selectedOptionQupi));

            }
        });

        // 返回按钮
        btnBack.setOnClickListener(v -> {
//            startActivity(new Intent(CheckActivity.this, MainActivity.class));
            finish();
        });

        tvTitle.setOnClickListener(v -> {
//            startActivity(new Intent(CheckActivity.this, MainActivity.class));
            finish();
        });

        // 重新称重
        tvReweight.setOnClickListener(v -> {

            if (rbQuantitySign.isChecked()) {
                ToastUtils.toastMsgWarning("重新输入");
                EventBus.getDefault().post(new TTSSpeakEvent("重新输入"));
                supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
                supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
                btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
                btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);

                tvNetWeightDiffValue.setText("--" + globalUnit);
                tvMissingAmountValue.setText("0.00元");
                tvReviewAmountValue.setText("0.00元");
                tvReviewGrossValue.setText("--" + globalUnit);

                handCalculator.getTvConsume().setText("");
                handCalculator.getTvConsume().setHint("0.00");

                tvGrossWeight.setText("");

                hideSuccessButton();
                return;
            }

            ToastUtils.toastMsgWarning("重新称重");
            EventBus.getDefault().post(new TTSSpeakEvent("重新称重"));

            categoryList.get(currentIndex).setStatus(1);
            categoryAdapter.notifyDataSetChanged();
            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
            supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            setContentData(supplyProductOrderDetailList.get(currentIndex), currentIndex,true);
            tvNetWeightDiffValue.setText("--" + globalUnit);
            tvMissingAmountValue.setText("0.00元");
            tvReviewAmountValue.setText("0.00元");
            btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
            tvTareWeight.setText("请放置包装");
            radioGroupQuPi.clearCheck();
            //rb_qupi_package.setChecked(true);
            Log.d(TAG, "limerb_qupi_package  :  true " + 573);
            hideSuccessButton();
            tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
        });

        // 缺失去皮
//        tvPhotoMissing.setOnClickListener(v -> {
//            Toast.makeText(this, "缺失去皮功能", Toast.LENGTH_SHORT).show();
//            // 这里可以实现去皮功能
//        });

        // 拍照功能
        btnTakeProductPhoto.setOnClickListener(v -> {



            if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && !TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) && !supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                ToastUtils.toastMsgWarning("禁止重复拍照");
                EventBus.getDefault().post(new TTSSpeakEvent("禁止重复拍照"));
                return;
            }

            if (rbQuantitySign.isChecked() && (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim()))){
                ToastUtils.toastMsgWarning("请输入商品数量");
                EventBus.getDefault().post(new TTSSpeakEvent("请输入商品数量"));
                return ;
            }

            if (StringUtils.isNumeric(tvGrossWeight.getText().toString().trim()) && (Double.parseDouble(tvGrossWeight.getText().toString()) > 0)) {
                // 创建拍照意图
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 确保有相机应用可以处理这个意图
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // 创建临时文件存储照片
                    try {


                        photoFile = File.createTempFile(
                                "product_photo_" +  System.currentTimeMillis(),  /* prefix */
                                ".jpg",         /* suffix */
                                getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
                        );

                        // 保存文件路径，使用FileProvider确保兼容性
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            photoUri = FileProvider.getUriForFile(
                                    CheckActivity.this,
                                    getPackageName() + ".fileprovider",
                                    photoFile
                            );
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        } else {
                            photoUri = Uri.fromFile(photoFile);
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAPTURE);
                        supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("--");
                    } catch (Exception ex) {
                        ToastUtils.toastMsgWarning("无法创建照片文件");
                        ex.printStackTrace();
                    }
                }
            } else {
                if (rbQuantitySign.isChecked()){
                    ToastUtils.toastMsgWarning("请输入商品数量");
                    EventBus.getDefault().post(new TTSSpeakEvent("请输入商品数量"));
                }else {
                    ToastUtils.toastMsgWarning("请先放置商品");
                    EventBus.getDefault().post(new TTSSpeakEvent("请先放置商品"));
                }


            }

        });

        btnTakePackagePhoto.setOnClickListener(v -> {

            if (rbQuantitySign.isChecked()){
                ToastUtils.toastMsgWarning("无需拍照");
                EventBus.getDefault().post(new TTSSpeakEvent("无需拍照"));
                return;
            }

            if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                ToastUtils.toastMsgWarning("请先称重商品");
                EventBus.getDefault().post(new TTSSpeakEvent("请先称重商品"));
                return;
            }

            if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) || supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                ToastUtils.toastMsgWarning("请先给商品拍照");
                EventBus.getDefault().post(new TTSSpeakEvent("请先给商品拍照"));
                Log.d(TAG, "limephoto  :  " + 650);
                return;
            }

            if (rb_qupi_0.isChecked()){
                ToastUtils.toastMsgWarning("不需要拍照");
                EventBus.getDefault().post(new TTSSpeakEvent("不需要拍照"));
                return;
            }


            if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && !TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getPassImageUrl()) && !supplyProductOrderDetailList.get(currentIndex).getPassImageUrl().equals("--")) {
                ToastUtils.toastMsgWarning("禁止重复拍照");
                EventBus.getDefault().post(new TTSSpeakEvent("禁止重复拍照"));
                return;
            }


            if (StringUtils.isNumeric(tvTareWeight.getText().toString().trim()) && !tvTareWeight.getText().toString().equals("请输入") && (Double.parseDouble(tvTareWeight.getText().toString()) > 0)) {
                // 创建拍照意图
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 确保有相机应用可以处理这个意图
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // 创建临时文件存储照片
                    try {
                        photoFile = File.createTempFile(
                                "package_photo_" +  System.currentTimeMillis(),  /* prefix */
                                ".jpg",         /* suffix */
                                getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
                        );

                        // 保存文件路径，使用FileProvider确保兼容性
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            photoUri = FileProvider.getUriForFile(
                                    CheckActivity.this,
                                    getPackageName() + ".fileprovider",
                                    photoFile
                            );
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        } else {
                            photoUri = Uri.fromFile(photoFile);
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAPTURE);
                        supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("--");
                    } catch (Exception ex) {
                        ToastUtils.toastMsgWarning("无法创建照片文件");
                        ex.printStackTrace();
                    }
                }
            } else {
                ToastUtils.toastMsgWarning("请先放置包装");
                EventBus.getDefault().post(new TTSSpeakEvent("请先放置包装"));
            }
        });

        // 底部按钮
        btnCancel.setOnClickListener(v -> {
            ToastUtils.toastMsgError("取消操作");
//            startActivity(new Intent(CheckActivity.this, MainActivity.class));
            finish();
        });

        btnReturnExchange.setOnClickListener(v -> {
            ToastUtils.toastMsgError("暂未开放");
            // 这里可以实现退换逻辑
        });


        btn_check_ruku.setOnClickListener(v -> {

            if (rb_qupi_package.isChecked() && !StringUtils.isNumeric(tvTareWeight.getText().toString().trim())){
                ToastUtils.toastMsgWarning("请先放置包装");
                EventBus.getDefault().post(new TTSSpeakEvent("请先放置包装"));
                return;
            }

            double tareWeight = 0;
            if (!StringUtils.isNumeric(tvTareWeight.getText().toString().trim())){
                tareWeight = 0.00;
            } else {
                tareWeight = Double.parseDouble(tvTareWeight.getText().toString().trim());
            }

            if (rbQuantitySign.isChecked() && !StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                ToastUtils.toastMsgWarning("请输入商品数量");
                EventBus.getDefault().post(new TTSSpeakEvent("请输入商品数量"));
                return;
            }

            double grossWeight = Double.parseDouble(tvGrossWeight.getText().toString().trim());

            if (grossWeight < tareWeight){
                ToastUtils.toastMsgWarning("皮重不能大于毛重");
                EventBus.getDefault().post(new TTSSpeakEvent("皮众不能大于毛重"));
                return;
            }


            calculateWeightMoney();
            Log.d(TAG, "limecalculateWeightMoney: " + 969);

            currentMode = 0;

            if(rbMultipleWeight.isChecked()){

                SuccessDialog dialog = new SuccessDialog(this, "称重完成", "分次称重",currentIndex == (supplyProductOrderDetailList.size() - 1) ? "订单确认" : "结束称重");
                dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                    @Override
                    public void onSingleButtonClicked() {

                    }

                    @Override
                    public void onLeftButtonClicked() {
                        AppApplication.supplyProductOrderDetailList.add(JSON.parseObject(JSON.toJSONString(supplyProductOrderDetailList.get(currentIndex)), OrderInfoBean.SupplyProductOrderDetailListBean.class));
                        multipleWeight();
                    }



                    @Override
                    public void onRightButtonClicked() {
                        supplyProductOrderDetailList.get(currentIndex).setReviewNumber(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight() - totalTareWeight - supplyProductOrderDetailList.get(currentIndex).getTareWeight() );
                        supplyProductOrderDetailList.get(currentIndex).setGrossWeight(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight());
                        supplyProductOrderDetailList.get(currentIndex).setTareWeight(totalTareWeight + supplyProductOrderDetailList.get(currentIndex).getTareWeight());
                        handleOrder();
                    }

                    @Override
                    public void onDialogDismissed() {
                        supplyProductOrderDetailList.get(currentIndex).setReviewNumber(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight() - totalTareWeight - supplyProductOrderDetailList.get(currentIndex).getTareWeight() );
                        supplyProductOrderDetailList.get(currentIndex).setGrossWeight(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight());
                        supplyProductOrderDetailList.get(currentIndex).setTareWeight(totalTareWeight + supplyProductOrderDetailList.get(currentIndex).getTareWeight());
                        handleOrder();
                    }
                });
                dialog.show();
                return;
            }

            int totalCountTemp = 0;
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getStatus() == 2){
                    totalCountTemp += 1;
                }
            }

            Log.i(TAG, "limesetupListeners totalCountTemp: " + totalCountTemp + "  supplyProductOrderDetailList.size() - 1: " + (supplyProductOrderDetailList.size() - 1));
            SuccessDialog dialog = new SuccessDialog(this, "复核完成",  (supplyProductOrderDetailList.size() - 1) == totalCountTemp ? "订单确认" : "验收下个商品");
            dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                @Override
                public void onSingleButtonClicked() {
                    handleOrder();
                }

                @Override
                public void onLeftButtonClicked() {
                    Toast.makeText(CheckActivity.this, "Left button clicked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightButtonClicked() {
                    Toast.makeText(CheckActivity.this, "Right button clicked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDialogDismissed() {

                    handleOrder();

                }
            });
            dialog.show();
        });

        btn_check_use.setOnClickListener(v -> {
            if (rb_qupi_package.isChecked() && !StringUtils.isNumeric(tvTareWeight.getText().toString().trim())){
                ToastUtils.toastMsgWarning("请先放置包装");
                EventBus.getDefault().post(new TTSSpeakEvent("请先放置包装"));
                return;
            }


            double tareWeight = 0;
            if (!StringUtils.isNumeric(tvTareWeight.getText().toString().trim())){
                tareWeight = 0.00;
            } else {
                tareWeight = Double.parseDouble(tvTareWeight.getText().toString().trim());
            }

            double grossWeight = Double.parseDouble(tvGrossWeight.getText().toString().trim());

            if (grossWeight < tareWeight){
                ToastUtils.toastMsgWarning("皮重不能大于毛重");
                EventBus.getDefault().post(new TTSSpeakEvent("皮众不能大于毛重"));
                return;
            }
            calculateWeightMoney();
            Log.d(TAG, "limecalculateWeightMoney: " + 1054);

            currentMode = 1;

            if(rbMultipleWeight.isChecked()){

                SuccessDialog dialog = new SuccessDialog(this, "称重完成", "分次称重",currentIndex == (supplyProductOrderDetailList.size() - 1) ? "订单确认" : "结束称重");
                dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                    @Override
                    public void onSingleButtonClicked() {

                    }

                    @Override
                    public void onLeftButtonClicked() {
                        AppApplication.supplyProductOrderDetailList.add(JSON.parseObject(JSON.toJSONString(supplyProductOrderDetailList.get(currentIndex)), OrderInfoBean.SupplyProductOrderDetailListBean.class));
                        multipleWeight();
                    }



                    @Override
                    public void onRightButtonClicked() {
                        supplyProductOrderDetailList.get(currentIndex).setReviewNumber(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight() - totalTareWeight - supplyProductOrderDetailList.get(currentIndex).getTareWeight() );
                        supplyProductOrderDetailList.get(currentIndex).setGrossWeight(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight());
                        supplyProductOrderDetailList.get(currentIndex).setTareWeight(totalTareWeight + supplyProductOrderDetailList.get(currentIndex).getTareWeight());
                        handleOrder();
                    }

                    @Override
                    public void onDialogDismissed() {
                        supplyProductOrderDetailList.get(currentIndex).setReviewNumber(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight() - totalTareWeight - supplyProductOrderDetailList.get(currentIndex).getTareWeight() );
                        supplyProductOrderDetailList.get(currentIndex).setGrossWeight(totalGrossWeight + supplyProductOrderDetailList.get(currentIndex).getGrossWeight());
                        supplyProductOrderDetailList.get(currentIndex).setTareWeight(totalTareWeight + supplyProductOrderDetailList.get(currentIndex).getTareWeight());
                        handleOrder();
                    }
                });
                dialog.show();
                return;
            }

            int totalCountTemp = 0;
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getStatus() == 2){
                    totalCountTemp += 1;
                }
            }

            Log.d(TAG, "limesetupListeners totalCountTemp: " + totalCountTemp + "  supplyProductOrderDetailList.size() - 1: " + (supplyProductOrderDetailList.size() - 1));
            SuccessDialog dialog = new SuccessDialog(this, "复核完成", (supplyProductOrderDetailList.size() - 1) == totalCountTemp ? "订单确认" : "验收下个商品");
            dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                @Override
                public void onSingleButtonClicked() {
                    handleOrder();
                }

                @Override
                public void onLeftButtonClicked() {
                    Toast.makeText(CheckActivity.this, "Left button clicked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightButtonClicked() {
                    Toast.makeText(CheckActivity.this, "Right button clicked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDialogDismissed() {
                    handleOrder();

                }
            });
            dialog.show();
        });


        // 毛重点击事件（模拟输入）
        tvGrossWeight.setOnClickListener(v -> {
            if (rbQuantitySign.isChecked()) {
                if (supplyProductOrderDetailList != null && supplyProductOrderDetailList.get(currentIndex) != null && !TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) && !supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                    ToastUtils.toastMsgWarning("禁止修改数量");
                    EventBus.getDefault().post(new TTSSpeakEvent("禁止修改数量"));
                    return;
                }
                ll_keyboard.setVisibility(View.VISIBLE);
            }
        });

        // 皮重点击事件（模拟输入）
        tvTareWeight.setOnClickListener(v -> {
            if (rb_qupi_shoudong.isChecked()) {
                ll_keyboard.setVisibility(View.VISIBLE);
            }
        });


        // 置零
        tvWeight0.setOnClickListener(v -> {
            // 添加确认弹窗，按确认键执行置零操作，按取消键弹窗消失
            CommonAlertDialogFragment dialogFragment = CommonAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("请确保称上没有商品，是否确认置零？")
                    .setLeftNavTxt("确认")
                    .setRightNavTxt("取消")
                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            // TODO: 执行置零操作
                            try {
                                WeightUtils.zero();
                                ToastUtils.toastMsgSuccess("校准成功");
                                tvNetWeightDiffValue.setText("--" + globalUnit);
                                tvMissingAmountValue.setText("0.00元");
                                tvReviewAmountValue.setText("0.00元");
                                tvReviewTareValue.setText("--" + globalUnit);
                                tvReviewGrossValue.setText("--" + globalUnit);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtils.toastMsgError("置零失败");
                            }
                        }
                    })
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            // 取消键：弹窗消失（默认行为，无需额外处理）
                        }
                    });
            dialogFragment.show(CheckActivity.this);
        });

    }

    /**
     * 计算并设置 ll_goods_count 的高度
     * 如果真实高度超过230dp，则限制为230dp；否则显示真实高度
     */
    private void adjustGoodsCountLayoutHeight() {
        ll_goods_count.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = ll_goods_count.getMeasuredHeight();

        // 将100dp转换为像素
        float density = getResources().getDisplayMetrics().density;
        int maxHeight = (int) (240 * density + 0.5f);

        // 获取当前布局参数
        ViewGroup.LayoutParams layoutParams = ll_goods_count.getLayoutParams();

        // 根据测量高度决定最终高度
        if (measuredHeight > maxHeight) {
            layoutParams.height = maxHeight;
        } else {
            layoutParams.height = measuredHeight;
        }

        // 应用新的布局参数
        ll_goods_count.setLayoutParams(layoutParams);
    }

    /**
     * 监听布局完成并调整 ll_goods_count 高度
     */
    private void observeGoodsCountLayout() {
        ll_goods_count.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听器避免重复调用
                ll_goods_count.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 调整高度
                adjustGoodsCountLayoutHeight();
            }
        });
    }

    private void multipleWeight() {

        ToastUtils.toastMsgWarning("分次称重");
        EventBus.getDefault().post(new TTSSpeakEvent("分次称重"));


          if (AppApplication.supplyProductOrderDetailList.size() > 0){
              ll_goods_count.setVisibility(View.VISIBLE);
              observeGoodsCountLayout();
              GoodsCountAdapter adapter = new GoodsCountAdapter(this);
              rv_goods_count.setAdapter(adapter);
              rv_goods_count.setItemAnimator(null);
              rv_goods_count.setItemViewCacheSize(4);
              rv_goods_count.setHasFixedSize(true);
              adapter.addData(AppApplication.supplyProductOrderDetailList);
              rv_goods_count.scrollToPosition(AppApplication.supplyProductOrderDetailList.size() - 1);
          }

        categoryList.get(currentIndex).setStatus(1);
        categoryAdapter.notifyDataSetChanged();
        supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
        supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
//        setContentData(supplyProductOrderDetailList.get(currentIndex), currentIndex);

        btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
        ll_gross_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
        tvGrossWeight.setText("请放置商品");
        tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));


        btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
        ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
        tvTareWeight.setText("请放置包装");
        tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));

        tvProductName.setText(supplyProductOrderDetailList.get(currentIndex).getProductName());
        tv_price.setText(PriceUtils.formatPrice(supplyProductOrderDetailList.get(currentIndex).getUnitPrice()/100.0));
        tv_price_unit.setText("元/" + supplyProductOrderDetailList.get(currentIndex).getPackageUnit());

        tvProductBrand.setText("品牌：" + (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getProductBranch()) ? "--" :supplyProductOrderDetailList.get(currentIndex).getProductBranch()));
        tvProductAddr.setText("产地：" + (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getProductAddr()) ? "--" : supplyProductOrderDetailList.get(currentIndex).getProductAddr()));
        tvProductLevel.setText("质量等级:：" + "--");
        tvProductType.setText("包装规格：" + supplyProductOrderDetailList.get(currentIndex).getSpecification());

        tvOrderWeightValue.setText(PriceUtils.formatPrice(supplyProductOrderDetailList.get(currentIndex).getPurchaseNumber() ) + globalUnit);
        tvOrderAmountValue.setText("￥" + PriceUtils.formatPrice(supplyProductOrderDetailList.get(currentIndex).getOrderFee()/100.0));


        if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getProductImageUrl())){
            ivProduct.setImageResource(R.mipmap.ic_cai_default);
        } else {
            GlideApp.with(CheckActivity.this).load(supplyProductOrderDetailList.get(currentIndex).getProductImageUrl()).placeholder(R.mipmap.ic_cai_default)
                    .into(ivProduct);
        }
        tvNetWeightDiffValue.setText("--" + globalUnit);
        tvMissingAmountValue.setText("0.00元");
        tvReviewAmountValue.setText("0.00元");
        btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
        ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
        tvTareWeight.setText("请放置包装");
        rb_qupi_package.setChecked(true);
        Log.d(TAG, "limerb_qupi_package  :  true " + 573);
        hideSuccessButton();
        tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));

        if (rbMultipleWeight.isChecked() && AppApplication.supplyProductOrderDetailList.size() == 0) {
            tv_review_gross_summary.setText("首次毛重");
            tv_review_trae_summary.setText("首次皮重");
        }else {
            tv_review_gross_summary.setText(OccurrenceConverter.getOccurrence(AppApplication.supplyProductOrderDetailList.size()) + "毛重");
            tv_review_trae_summary.setText(OccurrenceConverter.getOccurrence(AppApplication.supplyProductOrderDetailList.size()) +  "皮重");
        }
    }

    private void checkByCount(boolean b) {
        handCalculator.getTvConsume().setText("");

        weightMultiplier = StringUtils.getWeightMultiplier(supplyProductOrderDetailList.get(currentIndex).getPackageUnit());

        if (b){
            globalUnit = supplyProductOrderDetailList.get(currentIndex).getPackageUnit();
            rb_qupi_package.setChecked(false);
            rb_qupi_0.setChecked(false);
            rb_qupi_shoudong.setChecked(false);
            radioGroupQuPi.setClickable(false);
            tvReweight.setText("重新输入");
            ll_gross_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
            tv_check_maozhong.setText("商品数量 (" + globalUnit + ")");
            tvGrossWeight.setText("请输入");
            tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));
            ll_keyboard.setVisibility(View.VISIBLE);


            tvOrderWeightValue.setText((int)supplyProductOrderDetailList.get(currentIndex).getPurchaseNumber() + globalUnit);
            tv_price_unit.setText("元/" + globalUnit);
            product_weight_summary.setText("商品数量");
            tv_order_weight_summary.setText("订单数量");
            tv_review_gross_summary.setText("复核数量");
            tv_net_weight_diff_summary.setText("数量差额");
            tvReviewTareValue.setText("按包装计量");
            tvReviewGrossValue.setText("--" + globalUnit);
            tvNetWeightDiffValue.setText("--" + globalUnit);
            tvMissingAmountValue.setText("0.00元");
            tvReviewAmountValue.setText("0.00元");
//            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
//            supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
            btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
            tvTareWeight.setText("请放置包装");
//            radioGroupQuPi.clearCheck();
            Log.d(TAG, "limerb_qupi_package  :  true " + 573);
            hideSuccessButton();
            tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));

        } else {
            globalUnit = supplyProductOrderDetailList.get(currentIndex).getPackageUnit();

            tv_price_unit.setText("元/" + globalUnit);
            product_weight_summary.setText("商品重量");
            tv_order_weight_summary.setText("订单重量");
            tv_review_gross_summary.setText("复核毛重");
            tv_net_weight_diff_summary.setText("净重差额");
            if (rbMultipleWeight.isChecked() ) {
                if (AppApplication.supplyProductOrderDetailList.size() == 0) {
                    tv_review_gross_summary.setText("首次毛重");
                    tv_review_trae_summary.setText("首次皮重");
                } else {
                    tv_review_gross_summary.setText(OccurrenceConverter.getOccurrence(AppApplication.supplyProductOrderDetailList.size()) + "毛重");
                    tv_review_trae_summary.setText(OccurrenceConverter.getOccurrence(AppApplication.supplyProductOrderDetailList.size()) + "皮重");
                }
            }
//            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
//            supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
            btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);

            tvReviewGrossValue.setText("--" + globalUnit);
            tvNetWeightDiffValue.setText("--" + globalUnit);
            tvOrderWeightValue.setText(supplyProductOrderDetailList.get(currentIndex).getPurchaseNumber() + globalUnit);
            tvReweight.setText("重新称重");
            ll_gross_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
            tv_check_maozhong.setText("复核毛重（" + globalUnit + "）");
            tv_review_tare_summary.setText("复核皮重（" + globalUnit + "）");
            tvReviewTareValue.setText("--" + globalUnit);
            tvGrossWeight.setText("请放置商品");
            tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));
            ll_keyboard.setVisibility(View.GONE);
            radioGroupQuPi.setClickable(true);
        }

    }

    private void gotoOrderActivity(){
        ll_goods_count.setVisibility(View.GONE);
        Intent intent = new Intent(CheckActivity.this, OrderActivity.class);
        OrderActivity.orderID = orderID;
        OrderActivity.createTime = createTime;
        OrderActivity.supplierName = supplierName;
        OrderActivity.supplyProductOrderDetailList = supplyProductOrderDetailList;
        startActivity(new Intent(CheckActivity.this, OrderActivity.class));
        finish();
    }

    private void handleOrder() {

        Log.d(TAG, "limehandleOrder: " + JSON.toJSONString(supplyProductOrderDetailList.get(currentIndex)));

        categoryList.get(currentIndex).setStatus(2);
        int totalCount = 0;
        int minIndex = -1;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getStatus() == 2){
                totalCount += 1;
            }

            if(minIndex < 0 && categoryList.get(i).getStatus() == 0){
                minIndex = i;
            }
        }

        if (currentIndex >= (supplyProductOrderDetailList.size() - 1)) {

            if (totalCount == categoryList.size()){
                gotoOrderActivity();
            }else {
                nextCategoryData(minIndex);
            }

        } else {



            if (totalCount == categoryList.size()){
                gotoOrderActivity();
                return;
            }

            if (categoryList.get(currentIndex + 1).getStatus() != 2){
                nextCategoryData(currentIndex + 1);
            }else {
                int maxIndex = -1;
                for (int i = currentIndex + 1; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getStatus() == 0){
                        maxIndex = i;
                        break;
                    }
                }

                if (maxIndex >= 0){
                    nextCategoryData(maxIndex);
                }else {
                    nextCategoryData(minIndex);
                }

            }


        }


    }

    private void nextCategoryData(int  index) {
        ll_goods_count.setVisibility(View.GONE);
        radioGroup.clearCheck();
        rbSingleWeight.setChecked(true);
        currentIndex = index;
        categoryList.get(currentIndex).setStatus(1);
        categoryAdapter.notifyDataSetChanged();
        rb_qupi_package.setChecked(false);
        rb_qupi_0.setChecked(false);
        rb_qupi_shoudong.setChecked(false);
        setContentData(supplyProductOrderDetailList.get(currentIndex), currentIndex,false);
        supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
        supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
        tvNetWeightDiffValue.setText("--" + globalUnit);
        tvMissingAmountValue.setText("0.00元");
        tvReviewAmountValue.setText("0.00元");
        tvReviewTareValue.setText("--" + globalUnit);
        tvReviewGrossValue.setText("--" + globalUnit);
        btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
        ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
        tvTareWeight.setText("请放置包装");
        ll_keyboard.setVisibility(View.GONE);
        if (rbQuantitySign.isChecked()){
            ll_keyboard.setVisibility(View.VISIBLE);
        }
        tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));
        hideSuccessButton();
    }

    private void calculateWeightMoney() {

        Log.d(TAG, "limecalculateWeightMoney: " + 1570);

        try {

            if (rbMultipleWeight.isChecked()){
                totalGrossWeight = 0;
                totalTareWeight = 0;
                for (int i = 0; i < AppApplication.supplyProductOrderDetailList.size(); i++) {
                    totalGrossWeight += AppApplication.supplyProductOrderDetailList.get(i).getGrossWeight();
                    totalTareWeight += AppApplication.supplyProductOrderDetailList.get(i).getTareWeight();
                }

            }

            if (!StringUtils.isNumeric(tvGrossWeight.getText().toString().trim())){
                return;
            }

            double tareWeight = 0;
            if (!StringUtils.isNumeric(tvTareWeight.getText().toString().trim())){
                tareWeight = 0.00;
            } else {
                tareWeight = Double.parseDouble(tvTareWeight.getText().toString().trim());
            }


            double grossWeight = Double.parseDouble(tvGrossWeight.getText().toString().trim());


            double missWeight = (grossWeight + totalGrossWeight) - supplyProductOrderDetailList.get(currentIndex).getPurchaseNumber()   - (tareWeight + totalTareWeight);

            if (rbQuantitySign.isChecked()){
                if (missWeight < 0) {
                    tvNetWeightDiffValue.setText((int)missWeight + globalUnit);
                }else if (missWeight > 0){
                    tvNetWeightDiffValue.setText( "+" + (int)missWeight + globalUnit);
                }else {
                    tvNetWeightDiffValue.setText("0" + globalUnit);
                }
            }else {
                if (missWeight < 0) {
                    tvNetWeightDiffValue.setText(PriceUtils.formatPrice(missWeight) + globalUnit);
                }else if (missWeight > 0){
                    tvNetWeightDiffValue.setText( "+" + PriceUtils.formatPrice(missWeight) + globalUnit);
                }else {
                    tvNetWeightDiffValue.setText("0.00" + globalUnit);
                }
            }



            supplyProductOrderDetailList.get(currentIndex).setReviewNumber( supplyProductOrderDetailList.get(currentIndex).getPurchaseNumber() + missWeight);

            double missMoney = missWeight * (supplyProductOrderDetailList.get(currentIndex).getUnitPrice()/100.0);

            tvMissingAmountValue.setText(PriceUtils.formatPrice(missMoney) + "元");

            double reviewMoney = 0.00;

            if (missWeight > 0){
                reviewMoney = supplyProductOrderDetailList.get(currentIndex).getOrderFee() / 100.0 + Math.abs(missMoney);
            }else {
                reviewMoney = supplyProductOrderDetailList.get(currentIndex).getOrderFee() / 100.0 - Math.abs(missMoney);
            }

            tvReviewAmountValue.setText(PriceUtils.formatPrice(reviewMoney)+ "元");

            supplyProductOrderDetailList.get(currentIndex).setReviewFee(reviewMoney);

            supplyProductOrderDetailList.get(currentIndex).setGrossWeight(grossWeight);
            supplyProductOrderDetailList.get(currentIndex).setTareWeight(tareWeight);


            supplyProductOrderDetailList.get(currentIndex).setNetWeightDifference(missWeight);

            supplyProductOrderDetailList.get(currentIndex).setDifferenceAmount(missMoney);

            if (!rbQuantitySign.isChecked()) {
                showSuccessButton();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }

    private void showSuccessButton() {
        btnReturnExchange.setVisibility(View.GONE);
        btn_check_ruku.setVisibility(View.VISIBLE);
        btn_check_use.setVisibility(View.VISIBLE);
    }

    private void hideSuccessButton() {
        btnReturnExchange.setVisibility(View.VISIBLE);
        btn_check_ruku.setVisibility(View.GONE);
        btn_check_use.setVisibility(View.GONE);
    }


    private Bitmap addWatermarkToBitmap(Bitmap originalBitmap) {
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String copyrightText = "新安县奥新餐饮服务有限公司 ©" + currentTime.substring(0, 4) + " 版权所有";

        // 创建新的Bitmap用于绘制水印
        Bitmap watermarkedBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                originalBitmap.getConfig());
        Canvas canvas = new Canvas(watermarkedBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        // 设置水印画笔
        Paint paint = new Paint();
        // 设置白色字体并调整透明度为70% (透明度值为255*0.7≈179)
        paint.setColor(Color.argb(179, 255, 255, 255));
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK); // 添加阴影使文字更清晰

        // 增加行间距
        float lineHeight = paint.getTextSize() * 1.5f; // 行间距为文字高度的1.5倍

        // 绘制第一行时间水印
        canvas.drawText(currentTime, 20, originalBitmap.getHeight() - lineHeight * 2 + 10, paint);
        // 绘制第二行版权信息
        canvas.drawText(copyrightText, 20, originalBitmap.getHeight() - lineHeight + 10, paint);

        return watermarkedBitmap;
    }




    private File saveBitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getCacheDir(), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
            // 将拍好的照片设置到btnTakeProductPhoto上
// 将拍好的照片设置到btnTakeProductPhoto上
            if (photoFile != null && photoFile.exists()) {
                Log.d(TAG, "limeonActivityResult: " + 857);
                // 先添加水印
                Bitmap originalBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap bitmap = addWatermarkToBitmap(originalBitmap);

                // 保存带水印的图片
                File watermarkedFile = saveBitmapToFile(bitmap, "watermarked_" + System.currentTimeMillis() + ".jpg");

                if (TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) || supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnTakeProductPhoto.setImageBitmap(bitmap);
                            supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl(watermarkedFile.getAbsolutePath());
                            if (rbQuantitySign.isChecked()) {
                                showSuccessButton();
                                return;
                            }

                            tvTareWeight.setText("请放置包装");
                            tvTareWeight.setTextColor(Color.parseColor("#FFFFFF"));
                            ll_trae_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
                            radioGroupQuPi.clearCheck();
                            rb_qupi_package.setChecked(true);

                            Log.d(TAG, "limerb_qupi_package  :  true " + 941);
                        }
                    });


                }else {
                    btnTakePackagePhoto.setImageBitmap(bitmap);
                    supplyProductOrderDetailList.get(currentIndex).setPassImageUrl(watermarkedFile.getAbsolutePath());
                    calculateWeightMoney();
                    Log.d(TAG, "limecalculateWeightMoney: " + 1514);
                }
            }

        }else {
            Log.d(TAG, "limeonActivityResult: " + 875);
            if (!TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl()) && supplyProductOrderDetailList.get(currentIndex).getReviewImageUrl().equals("--")) {
                supplyProductOrderDetailList.get(currentIndex).setReviewImageUrl("");
            }

            if ( !TextUtils.isEmpty(supplyProductOrderDetailList.get(currentIndex).getPassImageUrl()) && supplyProductOrderDetailList.get(currentIndex).getPassImageUrl().equals("--")) {
                supplyProductOrderDetailList.get(currentIndex).setPassImageUrl("");
            }
        }
    }


    private void orderInfo(String id) {
        Log.d(TAG, "limegetOrderCompleteList pageIndex: " + pageIndex);
        if (pageIndex > 1 && pageIndex  > pageTotal){
//            hideLoadingDialog();
//            srlRecordList.finishRefresh();
//            srlRecordList.finishLoadMore();
            AppToast.toastMsg("没有更多数据了!");
            return;
        }

        Log.d(TAG, "limeorderInfo id: " + id);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("orderInfo");
        paramsMap.put("id",id);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .orderInfo(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<OrderInfoBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OrderInfoBean> responseBaseResponse) {
                        try {
//                            Log.d(TAG, "limeFoodConsumePageResponse 1397  pageIndex: " + pageIndex +  "  "+ JSON.toJSONString(responseBaseResponse));

                            if (responseBaseResponse.isTokenInvalid()){
                                CommonAlertDialogFragment.build()
                                        .setAlertTitleTxt("提示")
                                        .setAlertContentTxt("登录失效，请重新登录")
                                        .setLeftNavTxt("确定")
                                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                            @Override
                                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                                Intent loginIntent = new Intent(CheckActivity.this, LoginLandActivity.class);
                                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(loginIntent);
                                                finish();
                                            }
                                        })
                                        .show(CheckActivity.this);
                                return;
                            }
                            Log.d(TAG, "limeFoodConsumePageResponse 1416  : ");
                            //FileUtils.saveContentToSdcard("foodConsumePage.txt",JSON.toJSONString(responseBaseResponse) + "\n\n");
                            OrderInfoBean responseData = responseBaseResponse.getData();
                            if (responseBaseResponse.isSuccess() && responseData != null) {
                                Log.d(TAG, "limeFoodConsumePageResponse 1420  : ");
                                Log.d(TAG, "limeFoodConsumePageResponse 155  responseData.getSupplierName(): " + responseData.getSupplierName() +  "  "+ responseData.getSupplierName());
                                orderID = responseData.getId();
                                createTime = responseData.getCreateTime();
                                supplierName = responseData.getSupplierName();
                                categoryList = new ArrayList<>();
                                for (int i = 0; i < responseData.getSupplyProductOrderDetailList().size(); i++) {
                                    categoryList.add(new CategoryItem(responseData.getSupplyProductOrderDetailList().get(i).getProductName(), 0));
                                }
                                Log.d(TAG, "limeFoodConsumePageResponse 1429  : ");
                                initCategoryData();

                                categoryList.get(0).setStatus(1);
                                categoryAdapter.notifyDataSetChanged();
                                supplyProductOrderDetailList = responseData.getSupplyProductOrderDetailList();
                                rb_qupi_package.setChecked(false);
                                Log.d(TAG, "limerb_qupi_package  :  false " + 1030);
                                setContentData(responseData.getSupplyProductOrderDetailList().get(0),0,false);

                            } else {
                                if (pageIndex == 1) {
//                                    orderCompleteListAdapter.getData().clear();
//                                    orderCompleteListAdapter.notifyDataSetChanged();

                                }else {
                                    AppToast.toastMsg("没有更多数据了");
                                }

                            }

                        }catch (Exception e){
                            Log.e(TAG, "limeFoodConsumePageResponse 1451: " + e.getMessage());
                            AppToast.toastMsg("获取数据异常");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        hideLoadingDialog();
//                        srlRecordList.finishRefresh();
//                        srlRecordList.finishLoadMore();
//                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeightUtils.stop();
    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_check_content;
    }

    private void setContentData(OrderInfoBean.SupplyProductOrderDetailListBean supplyProductOrderDetailListBean,int index,boolean isNewWeight){
        this.currentIndex = index;

        Log.d(TAG, "limesetContentData: " + JSON.toJSONString(supplyProductOrderDetailListBean));

        totalGrossWeight = 0;
        totalTareWeight = 0;
        btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
        ll_gross_weight.setBackgroundResource(R.drawable.weight_input_blue_bg);
        tvGrossWeight.setText("请放置商品");
        tvGrossWeight.setTextColor(Color.parseColor("#FFFFFF"));

        globalUnit = supplyProductOrderDetailList.get(currentIndex).getPackageUnit();

        btnTakePackagePhoto.setImageResource(R.mipmap.take_photo_default);
        ll_trae_weight.setBackgroundResource(R.drawable.weight_input_bg);
        tvTareWeight.setText("请放置包装");
        tvTareWeight.setTextColor(Color.parseColor("#C9CDD4"));

        tvProductName.setText(supplyProductOrderDetailListBean.getProductName());
        tv_price.setText(PriceUtils.formatPrice(supplyProductOrderDetailListBean.getUnitPrice()/100.0));
        tv_price_unit.setText("元/" + supplyProductOrderDetailListBean.getPackageUnit());

        tvProductBrand.setText("品牌：" + (TextUtils.isEmpty(supplyProductOrderDetailListBean.getProductBranch()) ? "--" :supplyProductOrderDetailListBean.getProductBranch()));
        tvProductAddr.setText("产地：" + (TextUtils.isEmpty(supplyProductOrderDetailListBean.getProductAddr()) ? "--" : supplyProductOrderDetailListBean.getProductAddr()));
        tvProductLevel.setText("质量等级:：" + "--");
        tvProductType.setText("包装规格：" + supplyProductOrderDetailListBean.getSpecification());

        weightMultiplier = StringUtils.getWeightMultiplier(supplyProductOrderDetailListBean.getPackageUnit());

        tvOrderWeightValue.setText(PriceUtils.formatPrice(supplyProductOrderDetailListBean.getPurchaseNumber() ) + globalUnit);
        tvOrderAmountValue.setText("￥" + PriceUtils.formatPrice(supplyProductOrderDetailListBean.getOrderFee()/100.0));

        tv_check_maozhong.setText("复核毛重（" + globalUnit + "）");
        tv_review_tare_summary.setText("复核皮重（" + globalUnit + "）");
        tvReviewGrossValue.setText("--" + globalUnit);
        tvNetWeightDiffValue.setText("--" + globalUnit);

        if (!isNewWeight) {
            if (supplyProductOrderDetailListBean.getPackageUnit().contains("公斤") || supplyProductOrderDetailListBean.getPackageUnit().contains("斤") || supplyProductOrderDetailListBean.getPackageUnit().contains("kg") || supplyProductOrderDetailListBean.getPackageUnit().contains("g") || supplyProductOrderDetailListBean.getPackageUnit().contains("克") || supplyProductOrderDetailListBean.getPackageUnit().contains("千克")) {
                if (rbMultipleWeight.isChecked()) {

                } else {
                    rbSingleWeight.setChecked(true);
                }

            } else {
                tvGrossWeight.setText("请输入");
                rbQuantitySign.setChecked(true);
                ll_keyboard.setVisibility(View.VISIBLE);
            }
        }



        if (TextUtils.isEmpty(supplyProductOrderDetailListBean.getProductImageUrl())){
            ivProduct.setImageResource(R.mipmap.ic_cai_default);
        } else {
            GlideApp.with(CheckActivity.this).load(supplyProductOrderDetailListBean.getProductImageUrl()).placeholder(R.mipmap.ic_cai_default)
                    .into(ivProduct);
        }


    }


}