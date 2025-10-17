package com.stkj.aoxin.weight.home.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.data.AppCommonMMKV;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.tts.TTSVoiceHelper;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.upload.UploadFileHelper;
import com.stkj.aoxin.weight.base.utils.DESUtil;
import com.stkj.aoxin.weight.home.ui.adapter.ProductOrderAdapter;
import com.stkj.aoxin.weight.home.ui.widget.SuccessDialog;
import com.stkj.aoxin.weight.login.helper.LoginHelper;
import com.stkj.aoxin.weight.login.model.UserInfo;
import com.stkj.aoxin.weight.login.service.LoginService;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.goods.model.RecheckOrderParams;
import com.stkj.aoxin.weight.pay.goods.model.RecheckOrderResult;
import com.stkj.aoxin.weight.pay.model.LogoutEvent;
import com.stkj.aoxin.weight.pay.model.OrderCheckSuccesskEvent;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;
import com.stkj.aoxin.weight.pay.model.OrderItemBean;
import com.stkj.aoxin.weight.pay.model.TTSSpeakEvent;
import com.stkj.aoxin.weight.pay.service.PayService;
import com.stkj.aoxin.weight.setting.data.PaymentSettingMMKV;
import com.stkj.aoxin.weight.setting.model.ResumeFacePassDetect;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.utils.NetworkUtils;
import com.wind.dialogtiplib.dialog_tip.TipLoadDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.rxjava3.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class OrderActivity extends BaseActivity {

    private static final String TAG = "OrderActivity";
    public static String orderID = "";

    public static String createTime = "";
    public static String supplierName = "";

    private int onSuccessCount = 0;
    private int onErrorCount = 0;
    private int totalCount = 0;

    public static  List<OrderInfoBean.SupplyProductOrderDetailListBean> supplyProductOrderDetailList;


    // UI Components
    private TextView tvTitle;


    private TextView tvOrderTime;
    private TextView tvSupplier;
    private RecyclerView rvProducts;
    private TextView tvProductCount;
    private TextView tvVerifiedCount;
    private TextView tvReturnCount;
    private TextView tvOrderTotal;
    private TextView tvVerifiedTotal;

    private ImageView iv_preview;
    private ImageView iv_back;
    private TextView tv_back;
    private AppCompatButton btnConfirm;

    private RelativeLayout rl_preview;

    private TipLoadDialog tipLoadDialog;

    // Data
    private ProductOrderAdapter adapter;
    private List<OrderInfoBean.SupplyProductOrderDetailListBean> productList;
    private OrderSummary orderSummary;
    
    // Constants
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        EventBus.getDefault().register(this);
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        updateSummary();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        ToastUtils.toastMsgWarning("不可返回");
        EventBus.getDefault().post(new TTSSpeakEvent("不可返回"));
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvSupplier = findViewById(R.id.tv_supplier);
        rvProducts = findViewById(R.id.rv_products);
        tvProductCount = findViewById(R.id.tv_product_count);
        tvVerifiedCount = findViewById(R.id.tv_verified_count);
        tvReturnCount = findViewById(R.id.tv_return_count);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvVerifiedTotal = findViewById(R.id.tv_verified_total);
        btnConfirm = findViewById(R.id.btn_confirm);
        rl_preview = findViewById(R.id.rl_preview);
        iv_preview = findViewById(R.id.iv_preview);
        tv_back    = findViewById(R.id.tv_back);
        iv_back = findViewById(R.id.iv_back);
    }
    
    private void initData() {
        // Initialize order summary
        orderSummary = new OrderSummary();
        orderSummary.setOrderTime("2025-09-22 18:35:36");
        orderSummary.setSupplier("河南中农食品集团有限公司");
        
        // Initialize product list with sample data
        productList = new ArrayList<>();
        

        productList.addAll(supplyProductOrderDetailList);


        Log.d(TAG, "limeproductList: " + JSON.toJSONString(productList));

        // Set order info
        tvOrderTime.setText("订单时间：" + createTime);
        tvSupplier.setText("供货单位：" + supplierName);
    }
    
    private void setupRecyclerView() {
        adapter = new ProductOrderAdapter(productList, new ProductOrderAdapter.OnItemClickListener() {
            @Override
            public void onPhotoClick(int position, int photoType) {

                String imgUrl = (photoType == ProductOrderAdapter.PHOTO_TYPE_PRODUCT) ? productList.get(position).getReviewImageUrl() : productList.get(position).getPassImageUrl();

                if (!TextUtils.isEmpty(imgUrl)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);
                    iv_preview.setImageBitmap(bitmap);
                    rl_preview.setVisibility(View.VISIBLE);
                }else {
                    EventBus.getDefault().post(new TTSSpeakEvent("暂无照片，不可预览"));
                    ToastUtils.toastMsgWarning("暂无照片，不可预览");
                }

            }
            
            @Override
            public void onQuantityChange(int position, double newQuantity) {

            }
            
            @Override
            public void onRestockClick(int position) {
                //handleRestockClick(position);
            }
        });
        
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupClickListeners() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_preview.setVisibility(View.GONE);
                iv_preview.setImageBitmap(null);
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_preview.setVisibility(View.GONE);
                iv_preview.setImageBitmap(null);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }
    
    private void capturePhoto(int productPosition, int photoType) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Store the position and photo type for later use
            takePictureIntent.putExtra("product_position", productPosition);
            takePictureIntent.putExtra("photo_type", photoType);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "无法打开相机", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            
            // Get the product position and photo type
            int productPosition = data.getIntExtra("product_position", -1);
            int photoType = data.getIntExtra("photo_type", -1);
            
            if (productPosition >= 0 && photoType >= 0) {
                // Update the adapter with the new photo
                adapter.setProductPhoto(productPosition, photoType, imageBitmap);
                Toast.makeText(this, "照片已保存", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateProductQuantity(int position, double newQuantity) {
        if (position >= 0 && position < productList.size()) {
            OrderInfoBean.SupplyProductOrderDetailListBean product = productList.get(position);

            // Recalculate verified amount
            double verifiedAmount = newQuantity * product.getUnitPrice();

            // Update the adapter
            adapter.notifyItemChanged(position);
            
            // Update summary
            updateSummary();
        }
    }
    
    private void updateSummary() {
        int productCount = productList.size();
        int verifiedCount = 0;
        int returnCount = 0;
        double orderTotal = 0.0;
        double verifiedTotal = 0.0;
        
        for (OrderInfoBean.SupplyProductOrderDetailListBean product : productList) {
            orderTotal += product.getOrderFee()/100.0;
            verifiedTotal += product.getReviewFee();

//            if (product.isReturned()) {
//                returnCount++;
//            } else if (product.getVerifiedQuantity() > 0) {
//                verifiedCount++;
//            }
        }
        
        // Update summary views
        tvProductCount.setText("商品数量：" + productCount);
        tvVerifiedCount.setText("签收数量：" + productCount);
        tvReturnCount.setText("退货数量：" + 0);
        tvOrderTotal.setText("订单总额：¥" + DECIMAL_FORMAT.format(orderTotal));
        tvVerifiedTotal.setText("复核总额：¥" + DECIMAL_FORMAT.format(verifiedTotal));
        
        // Update order summary object
        orderSummary.setProductCount(productCount);
        orderSummary.setVerifiedCount(verifiedCount);
        orderSummary.setReturnCount(returnCount);
        orderSummary.setOrderTotal(orderTotal);
        orderSummary.setVerifiedTotal(verifiedTotal);
    }
    
    private void confirmOrder() {
        // Validate that all required photos are taken
        boolean allPhotosValid = validatePhotos();
        
        if (!allPhotosValid) {
            Toast.makeText(this, "请为所有商品拍摄必要的照片", Toast.LENGTH_LONG).show();
            return;
        }


        if (NetworkUtils.isConnected()){
            showLoadingDialog("复核中", "Laoding");

            uploadPhotos();
        } else {

            EventBus.getDefault().post(new TTSSpeakEvent("网络未连接"));
            ToastUtils.toastMsgError("网络未连接");
        }


    }
    
    private boolean validatePhotos() {
        // Check if all non-returned products have required photos
//        for (OrderInfoBean.SupplyProductOrderDetailListBean product : productList) {
//            if (!product.isReturned()) {
//                if (product.getProductPhoto() == null || product.getPackagePhoto() == null) {
//                    return false;
//                }
//            }
//        }
        return true;
    }

    private void compressImageOnly(int position, String imagePath) {
        File originalFile = new File(imagePath);

        // 使用Luban进行图片压缩
        Luban.with(this)
                .load(originalFile)
                .ignoreBy(100)
                .setTargetDir(getCacheDir().getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功，直接上传压缩后的文件
                        Log.d(TAG, "limeLuban: " + 380);
                        uploadCompressedImage(position, file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 压缩失败处理
                        Log.e(TAG, "图片压缩失败: " + e.getMessage());
                        onErrorCount += 1;
                        if ((onSuccessCount + onErrorCount) == totalCount) {
                            finishOrder();
                        }
                    }
                })
                .ignoreBy(100)
                .launch();
    }

    private void uploadCompressedImage(int position, File compressedFile) {
        UploadFileHelper uploadFileHelper = new UploadFileHelper(OrderActivity.this);
        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
            @Override
            public void onStart() {
                // 上传开始
            }

            @Override
            public void onSuccess(String fileUrl) {
                Log.d(TAG, "上传成功: " + fileUrl);
                // 更新图片URL
                supplyProductOrderDetailList.get(position).setReviewImageUrl(fileUrl);
                onSuccessCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "上传失败: " + msg);
                onErrorCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }
        });

        uploadFileHelper.uploadFile(compressedFile);
    }


    private void uploadPhotos() {

        try {
            Log.i(TAG, "limeLuban  435:  " + supplyProductOrderDetailList.size());
            for (int i = 0; i < supplyProductOrderDetailList.size(); i++) {
                Log.i(TAG, "limeLuban: --------  " + JSON.toJSONString(supplyProductOrderDetailList.get(i)));
                if (!TextUtils.isEmpty(supplyProductOrderDetailList.get(i).getReviewImageUrl()) && !supplyProductOrderDetailList.get(i).getReviewImageUrl().equals("--")) {
                    totalCount += 1;
                    handlePhotoProduct(i);
                    Log.d(TAG, "limeLuban: ===========" + 439);
                }

                if (!TextUtils.isEmpty(supplyProductOrderDetailList.get(i).getPassImageUrl()) && !supplyProductOrderDetailList.get(i).getPassImageUrl().equals("--")) {
                    totalCount += 1;
                    handlePhotoPackage(i);
                    Log.i(TAG, "limeLuban: ==========" + 445);
                }

            }

        }catch (Exception e){
            Log.e(TAG, "limeLuban: " + e.getMessage());
        }



    }


    private void handlePhotoProduct(int position) {
        String imagePath = supplyProductOrderDetailList.get(position).getReviewImageUrl();
        if (!TextUtils.isEmpty(imagePath) && !imagePath.equals("--")) {
            // 先检查文件是否存在
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // 进行压缩处理
                compressImageOnly(position, imagePath);
            } else {
                onErrorCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }
        } else {
            onErrorCount += 1;
            if ((onSuccessCount + onErrorCount) == totalCount) {
                finishOrder();
            }
        }
    }






    private void handlePhotoPackage(int position) {
        String imagePath = supplyProductOrderDetailList.get(position).getPassImageUrl();
        if (!TextUtils.isEmpty(imagePath) && !imagePath.equals("--")) {
            // 先检查文件是否存在
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // 进行压缩处理
                compressImageOnlyForPackage(position, imagePath);
            } else {
                onErrorCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }
        } else {
            onErrorCount += 1;
            if ((onSuccessCount + onErrorCount) == totalCount) {
                finishOrder();
            }
        }
    }

    private void compressImageOnlyForPackage(int position, String imagePath) {
        File originalFile = new File(imagePath);

        // 使用Luban进行图片压缩
        Luban.with(this)
                .load(originalFile)
                .ignoreBy(100)
                .setTargetDir(getCacheDir().getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功，直接上传压缩后的文件
                        uploadCompressedImageForPackage(position, file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 压缩失败处理
                        Log.e(TAG, "图片压缩失败: " + e.getMessage());
                        onErrorCount += 1;
                        if ((onSuccessCount + onErrorCount) == totalCount) {
                            finishOrder();
                        }
                    }
                })
                .ignoreBy(100)// 压缩到100KB以内
                .launch();
    }

    private void uploadCompressedImageForPackage(int position, File compressedFile) {
        UploadFileHelper uploadFileHelper = new UploadFileHelper(OrderActivity.this);
        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
            @Override
            public void onStart() {
                // 上传开始
            }

            @Override
            public void onSuccess(String fileUrl) {
                Log.d(TAG, "上传成功: " + fileUrl);
                // 更新图片URL
                supplyProductOrderDetailList.get(position).setPassImageUrl(fileUrl);
                onSuccessCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "上传失败: " + msg);
                onErrorCount += 1;
                if ((onSuccessCount + onErrorCount) == totalCount) {
                    finishOrder();
                }
            }
        });

        uploadFileHelper.uploadFile(compressedFile);
    }


    private void finishOrder() {


//        HashMap<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("id", orderID);
//        for (int i = 0; i < supplyProductOrderDetailList.size(); i++) {
//            supplyProductOrderDetailList.get(i).setPassImageUrl("");
//            supplyProductOrderDetailList.get(i).setReviewImageUrl("");
//        }
//        paramsMap.put("supplyProductRecheckData", JSON.toJSONString(supplyProductOrderDetailList));


        RecheckOrderParams recheckOrderParams = new RecheckOrderParams(orderID,supplyProductOrderDetailList);

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .recheckOrder(recheckOrderParams)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<RecheckOrderResult>>() {
                    @Override
                    protected void onSuccess(BaseResponse<RecheckOrderResult> userInfoBaseResponse) {

                        if (userInfoBaseResponse.isTokenInvalid()){
                            CommonAlertDialogFragment.build()
                                    .setAlertTitleTxt("提示")
                                    .setAlertContentTxt("登录失效，请重新登录")
                                    .setLeftNavTxt("确定")
                                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                        @Override
                                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {

                                            Intent loginIntent = new Intent(OrderActivity.this, LoginLandActivity.class);
                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(loginIntent);
                                            if (OrderActivity.this instanceof Activity) {
                                                 finish();
                                            }

                                        }
                                    })
                                    .show(OrderActivity.this);
                            return;
                        }

                        if (!userInfoBaseResponse.isSuccess()) {
                            showLoadingDialog("订单复核失败","FAIL");
                            ToastUtils.toastMsgError(userInfoBaseResponse.getMsg());
                            return;
                        } else {
                            dismissLoadingDialog();
                            ToastUtils.toastMsgSuccess("订单复核成功");
                            for (int i = 0; i < supplyProductOrderDetailList.size(); i++) {
                                if (!TextUtils.isEmpty(AppCommonMMKV.getOrderData(supplyProductOrderDetailList.get(i).getId()))){
                                    AppCommonMMKV.removeOrderData(supplyProductOrderDetailList.get(i).getId());
                                }
                            }
                            EventBus.getDefault().post(new OrderCheckSuccesskEvent());
                            finish();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        showLoadingDialog("订单复核失败","FAIL");
                        //订单复核失败
                        LoginHelper.INSTANCE.onLoginError("订单复核失败:" + e.getMessage());
                    }
                });


    }
    
    private void handleRestockClick(int position) {
        if (position >= 0 && position < productList.size()) {
            OrderInfoBean.SupplyProductOrderDetailListBean product = productList.get(position);
            
            // Show restock confirmation dialog
            SuccessDialog dialog = SuccessDialog.createWithTwoButtons(this, "取消", "确认补货");
//            dialog.setTitle("是否确认补货：" + product.getName() + "?");
            
            dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                @Override
                public void onSingleButtonClicked() {
                    // Not used
                }
                
                @Override
                public void onLeftButtonClicked() {
                    // Cancel - do nothing
                }
                
                @Override
                public void onRightButtonClicked() {
                    // Confirm restock
//                    product.setIsReturned(false);
//                    product.setVerifiedQuantity(product.getOrderQuantity());
//                    product.setVerifiedAmount(product.getOrderAmount());
                    
                    adapter.notifyItemChanged(position);
                    updateSummary();
                    
//                    Toast.makeText(OrderActivity.this, "已确认补货：" + product.getName(), Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onDialogDismissed() {
                    // Auto dismiss - do nothing
                }
            });
            
            dialog.show();
        }
    }
    
    // Data Models
    public static class ProductOrder {
        private String name;
        private String brand;
        private String quality;
        private double unitPrice;
        private String unit;
        private String origin;
        private String packaging;
        private double orderQuantity;
        private double verifiedQuantity;
        private double orderAmount;
        private double verifiedAmount;
        private boolean isReturned;
        private Bitmap productPhoto;
        private Bitmap packagePhoto;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
        
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        
        public String getPackaging() { return packaging; }
        public void setPackaging(String packaging) { this.packaging = packaging; }
        
        public double getOrderQuantity() { return orderQuantity; }
        public void setOrderQuantity(double orderQuantity) { this.orderQuantity = orderQuantity; }
        
        public double getVerifiedQuantity() { return verifiedQuantity; }
        public void setVerifiedQuantity(double verifiedQuantity) { this.verifiedQuantity = verifiedQuantity; }
        
        public double getOrderAmount() { return orderAmount; }
        public void setOrderAmount(double orderAmount) { this.orderAmount = orderAmount; }
        
        public double getVerifiedAmount() { return verifiedAmount; }
        public void setVerifiedAmount(double verifiedAmount) { this.verifiedAmount = verifiedAmount; }
        
        public boolean isReturned() { return isReturned; }
        public void setIsReturned(boolean returned) { isReturned = returned; }
        
        public Bitmap getProductPhoto() { return productPhoto; }
        public void setProductPhoto(Bitmap productPhoto) { this.productPhoto = productPhoto; }
        
        public Bitmap getPackagePhoto() { return packagePhoto; }
        public void setPackagePhoto(Bitmap packagePhoto) { this.packagePhoto = packagePhoto; }
    }
    
    public static class OrderSummary {
        private String orderTime;
        private String supplier;
        private int productCount;
        private int verifiedCount;
        private int returnCount;
        private double orderTotal;
        private double verifiedTotal;
        
        // Getters and Setters
        public String getOrderTime() { return orderTime; }
        public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
        
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        
        public int getProductCount() { return productCount; }
        public void setProductCount(int productCount) { this.productCount = productCount; }
        
        public int getVerifiedCount() { return verifiedCount; }
        public void setVerifiedCount(int verifiedCount) { this.verifiedCount = verifiedCount; }
        
        public int getReturnCount() { return returnCount; }
        public void setReturnCount(int returnCount) { this.returnCount = returnCount; }
        
        public double getOrderTotal() { return orderTotal; }
        public void setOrderTotal(double orderTotal) { this.orderTotal = orderTotal; }
        
        public double getVerifiedTotal() { return verifiedTotal; }
        public void setVerifiedTotal(double verifiedTotal) { this.verifiedTotal = verifiedTotal; }
    }


    public void showLoadingDialog(String msg, String tag) {
        if (tag == "SUCCESS") {
            if (tipLoadDialog != null) {
                tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_SUCCESS).show();
                tipLoadDialog = null;
            }
        } else if (tag == "FAIL") {
            if (tipLoadDialog == null) {
                tipLoadDialog = new TipLoadDialog(OrderActivity.this);
            }
            tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_FAIL).show();
            tipLoadDialog = null;
        } else {
            if (tipLoadDialog == null) {
                tipLoadDialog = new TipLoadDialog(OrderActivity.this);
            }
            tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_LOADING2).show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent eventBus) {

        dismissLoadingDialog();
        Log.d(TAG, "limeuploadFile: onLogoutEvent ");

        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("登录失效，请重新登录")
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {

                        Intent loginIntent = new Intent(OrderActivity.this, LoginLandActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();

                    }
                })
                .show(OrderActivity.this);


    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_check_content;
    }

    private void dismissLoadingDialog() {
        if (tipLoadDialog != null) {
            tipLoadDialog.dismiss();
        }
    }
}