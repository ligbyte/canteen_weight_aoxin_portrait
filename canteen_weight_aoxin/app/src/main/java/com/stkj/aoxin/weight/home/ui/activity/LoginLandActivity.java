package com.stkj.aoxin.weight.home.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.lztek.toolkit.Lztek;
import com.stkj.aoxin.weight.AppApplication;
import com.stkj.aoxin.weight.base.net.AppNetManager;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.utils.DESUtil;
import com.stkj.aoxin.weight.home.ui.widget.NetDialog;
import com.stkj.aoxin.weight.home.ui.widget.PicCapchaDialog;
import com.stkj.aoxin.weight.home.ui.widget.SuccessDialog;
import com.stkj.aoxin.weight.login.model.GetPhonevalidcode;
import com.stkj.aoxin.weight.login.model.GetPicCaptchaInfo;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.model.TTSSpeakEvent;
import com.stkj.aoxin.weight.setting.data.PaymentSettingMMKV;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.device.DeviceManager;
import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.login.helper.LoginHelper;
import com.stkj.aoxin.weight.login.model.UserInfo;
import com.stkj.aoxin.weight.login.service.LoginService;
import com.stkj.common.utils.NetworkUtils;
import com.stkj.common.utils.TimeUtils;
import com.wind.dialogtiplib.dialog_tip.TipLoadDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LoginLandActivity extends AppCompatActivity {

    private static final String TAG = "LoginLandActivity";
    private EditText etUsername, etPassword, etCaptcha;

    private LinearLayout ll_login;

    private TextView tv_account_login;
    private ImageView ivPasswordToggle;

    private TextView tvCaptcha, tvForgotPassword,tv_sn;
    private CheckBox cbRemember;
    private Button btnLogin;
    
    private boolean isPasswordVisible = false;
    private int captchaAnswer;

    private long beforeTime = 0;

    private TipLoadDialog tipLoadDialog;

    private PicCapchaDialog dialog;
    private Random random = new Random();

    private CountDownTimer countDownTimer;
    private NetDialog netDialog;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startChecking() {
        scheduler.scheduleWithFixedDelay(() -> {
            if (AppApplication.setDomainSuccess) {
                scheduler.shutdown();
            } else {
                AppNetManager.INSTANCE.initAppNet();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_land);
        Lztek.create(this).hideNavigationBar();
        initViews();
        setupClickListeners();
        AppNetManager.INSTANCE.initAppNet();
        startChecking();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tv_sn = findViewById(R.id.tv_sn);
        etCaptcha = findViewById(R.id.et_captcha);
        tv_account_login = findViewById(R.id.tv_account_login);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        ll_login = findViewById(R.id.ll_login);
        tvCaptcha = findViewById(R.id.tv_captcha);
        tvCaptcha.setSelected(true);
        tvCaptcha.setEnabled(true);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRemember = findViewById(R.id.cb_remember);
        btnLogin = findViewById(R.id.btn_login);



        tv_sn.setText("新安县奥新餐饮服务有限公司 "  + TimeUtils.getCurrentYearMonthDay() + "  ©SN: " + DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());

         if (!TextUtils.isEmpty(PaymentSettingMMKV.getUsername()) && !TextUtils.isEmpty(PaymentSettingMMKV.getPassword())){
             etUsername.setText(PaymentSettingMMKV.getUsername());
             etPassword.setText(PaymentSettingMMKV.getPassword());
         }

         if (PaymentSettingMMKV.getCheckboxPassword()){
             cbRemember.setChecked(true);
         }else {
             cbRemember.setChecked(false);
         }


        if (!NetworkUtils.isConnected()) {
            netCheck();
            return;
        }




    }
    
    private void setupClickListeners() {
        // Password visibility toggle
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
        
        // Captcha regeneration
        tvCaptcha.setOnClickListener(v -> generateCaptcha());
        
        // Forgot password
        tvForgotPassword.setOnClickListener(v -> {
            ToastUtils.toastMsgWarning( "忘记密码功能待实现");
        });
        
        // Login button
        btnLogin.setOnClickListener(v -> performLogin());


        tv_account_login.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                return false;
            }
        });

        initKeyboardListener();

    }

    /**
     * 监听软键盘状态，控制登录布局的位置
     */
    private void initKeyboardListener() {
        // 获取根视图
        final View rootView = findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            // 获取屏幕高度
            int screenHeight = rootView.getHeight();

            // 计算被键盘遮挡的高度
            int keypadHeight = screenHeight - r.bottom;

            // 如果键盘高度大于屏幕高度的1/3，则认为键盘已弹出
            if (keypadHeight > screenHeight * 0.1) {
                // 键盘显示，将登录布局上移
                moveLoginLayoutUp(keypadHeight);
            } else {
                // 键盘隐藏，恢复登录布局位置
                restoreLoginLayoutPosition();
            }
        });
    }

    /**
     * 将登录布局上移指定高度
     * @param moveDistance 需要上移的距离
     */
    private void moveLoginLayoutUp(int moveDistance) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ll_login.getLayoutParams();
        layoutParams.topMargin = (int)(-moveDistance * 0.1f) ; // 上移键盘高度的一半
        ll_login.setLayoutParams(layoutParams);
    }

    /**
     * 恢复登录布局到原始位置
     */
    private void restoreLoginLayoutPosition() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ll_login.getLayoutParams();
        layoutParams.topMargin = 0; // 恢复原始位置
        ll_login.setLayoutParams(layoutParams);
    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = true;
        }
        // Move cursor to end
        etPassword.setSelection(etPassword.getText().length());
    }
    
    private void generateCaptcha() {

        if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            ToastUtils.toastMsgWarning("用户名不能为空!");
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            ToastUtils.toastMsgWarning("密码不能为空!");
            return;
        }

        if (!NetworkUtils.isConnected()){
            ToastUtils.toastMsgWarning("网络连接已断开!");
            EventBus.getDefault().post(new TTSSpeakEvent("网络连接已断开"));
            return;
        }

        KeyBoardUtils.hideSoftKeyboard(getApplicationContext(), etPassword);
        showLoadingDialog("请求中", "Laoding");
        HashMap<String, String> paramsMap = new HashMap<>();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(LoginService.class)
                .getPicCaptcha(paramsMap)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<GetPicCaptchaInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GetPicCaptchaInfo> userInfoBaseResponse) {

                        if (!userInfoBaseResponse.isSuccess()) {
                            showLoadingDialog("请求失败","FAIL");
                            ToastUtils.toastMsgError(userInfoBaseResponse.getMsg());
                            return;
                        }else {
                            dismissLoadingDialog();

                            if (dialog != null){
                                dialog.dismiss();
                            }

                           dialog = new PicCapchaDialog(LoginLandActivity.this, userInfoBaseResponse.getData().getValidCodeBase64(),userInfoBaseResponse.getData().getValidCodeReqNo(),"图片验证", "取消","确定");

                            dialog.setOnDialogActionListener(new PicCapchaDialog.OnDialogActionListener() {
                                @Override
                                public void onSingleButtonClicked() {

                                }

                                @Override
                                public void onLeftButtonClicked() {

                                }

                                @Override
                                public void onRightButtonClicked(String captcha,String validCodeReqNo) {
                                    if (!TextUtils.isEmpty(captcha)) {
                                        getPhoneValidCode(captcha,validCodeReqNo);
                                    }else {
                                        EventBus.getDefault().post(new TTSSpeakEvent("请输入图片验证码"));
                                        ToastUtils.toastMsgWarning("请输入图片验证码");

                                    }
                                }

                                @Override
                                public void onDialogDismissed() {


                                }
                            });
                            dialog.show();

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        showLoadingDialog("请求失败","FAIL");
                    }
                });




    }


    private void getPhoneValidCode(String validCode,String validCodeReqNo) {

        if (!NetworkUtils.isConnected()) {
            netCheck();
            return;
        }

        if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            ToastUtils.toastMsgWarning("用户名不能为空!");
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            ToastUtils.toastMsgWarning("密码不能为空!");
            return;
        }

        if (!NetworkUtils.isConnected()){
            ToastUtils.toastMsgWarning("网络连接已断开!");
            EventBus.getDefault().post(new TTSSpeakEvent("网络连接已断开"));
            return;
        }

        KeyBoardUtils.hideSoftKeyboard(getApplicationContext(), etPassword);
        showLoadingDialog("请求中", "Laoding");
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("phone", etUsername.getText().toString().trim());
        paramsMap.put("validCode", validCode);
        paramsMap.put("validCodeReqNo", validCodeReqNo);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(LoginService.class)
                .getPhoneValidCode(paramsMap)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<GetPhonevalidcode>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GetPhonevalidcode> userInfoBaseResponse) {

                        if (!userInfoBaseResponse.isSuccess()){
                            dismissLoadingDialog();
                            ToastUtils.toastMsgError(userInfoBaseResponse.getMsg());
                            EventBus.getDefault().post(new TTSSpeakEvent(userInfoBaseResponse.getMsg()));
                            return;
                        }

                        dialog.dismiss();
                        showLoadingDialog("发送成功","SUCCESS");

                        tvCaptcha.setEnabled(false);
                        tvCaptcha.setSelected(false);

                         countDownTimer = new android.os.CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvCaptcha.setText((millisUntilFinished / 1000) + "秒后重试");
                                tvCaptcha.setTextColor(Color.parseColor("#333333"));
                            }

                            @Override
                            public void onFinish() {
                                tvCaptcha.setEnabled(true);
                                tvCaptcha.setSelected(true);
                                tvCaptcha.setText("发送验证码");
                                tvCaptcha.setTextColor(Color.parseColor("#317FFF"));
                            }
                        }.start();



                    }

                    @Override
                    public void onError(Throwable e) {
                        showLoadingDialog("请求失败","FAIL");
                    }
                });




    }

    private void performLogin() {
        if (System.currentTimeMillis() - beforeTime < 1000){
            return;
        }

        if (!NetworkUtils.isConnected()) {
            netCheck();
            return;
        }

        beforeTime = System.currentTimeMillis();
        KeyBoardUtils.hideSoftKeyboard(getApplicationContext(), etPassword);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String captchaInput = etCaptcha.getText().toString().trim();
        
        // Input validation
        if (username.isEmpty()) {
            ToastUtils.toastMsgError("请输入用户名");
            etUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            ToastUtils.toastMsgError( "请输入密码");
            etPassword.requestFocus();
            return;
        }
        
        if (captchaInput.isEmpty()) {
            ToastUtils.toastMsgError( "请输入验证码");
            etCaptcha.requestFocus();
            return;
        }
        
        // Verify captcha
        try {
            loginSystem(username, password);
        } catch (NumberFormatException e) {
            return;
        }



    }

    /**
     * 登录系统
     */
    private void loginSystem(String userName, String password) {

        if (TextUtils.isEmpty(userName)) {
            ToastUtils.toastMsgWarning("用户名不能为空!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastMsgWarning("密码不能为空!");
            return;
        }

        if (TextUtils.isEmpty(etCaptcha.getText().toString().trim())) {
            ToastUtils.toastMsgWarning("验证码不能为空!");
            return;
        }

        if (!NetworkUtils.isConnected()){
            ToastUtils.toastMsgWarning("网络连接已断开!");
            EventBus.getDefault().post(new TTSSpeakEvent("网络连接已断开"));
            return;
        }

        KeyBoardUtils.hideSoftKeyboard(getApplicationContext(), etPassword);
        showLoadingDialog("登录中", "Laoding");
        HashMap<String, String> paramsMap = new HashMap<>();
//        String encryptUserName = DESUtil.encrypt(userName, ParamsUtils.DES_PUBLIC_KEY);
//        String encryptPassword = DESUtil.encrypt(password, ParamsUtils.DES_PUBLIC_KEY);

        paramsMap.put("account", userName);
        paramsMap.put("password", DESUtil.encrypt(password, ParamsUtils.DES_PUBLIC_KEY));
        paramsMap.put("validPhoneCode", etCaptcha.getText().toString().trim());

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(LoginService.class)
                .loginWeb(paramsMap)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseResponse<UserInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<UserInfo> userInfoBaseResponse) {


                        if (!userInfoBaseResponse.isSuccess()) {
                            dismissLoadingDialog();
                            ToastUtils.toastMsgError(userInfoBaseResponse.getMsg());
                            return;
                        }

                        UserInfo userInfo = userInfoBaseResponse.getData();
                        if (userInfoBaseResponse.isSuccess() && userInfo != null && !TextUtils.isEmpty(userInfo.getToken())) {
                            showLoadingDialog("登录成功","SUCCESS");
                            //登录成功
                            LoginHelper.INSTANCE.saveUserInfo(userInfo);
                            LoginHelper.INSTANCE.onLoginSuccess();
                            if (cbRemember.isChecked()) {
                                PaymentSettingMMKV.putUsername(userName);
                                PaymentSettingMMKV.putPassword(password);
                                PaymentSettingMMKV.putCheckboxPassword(true);
                            }else {
                                PaymentSettingMMKV.putUsername("");
                                PaymentSettingMMKV.putPassword("");
                                PaymentSettingMMKV.putCheckboxPassword(false);
                            }
                            startActivity(new Intent(LoginLandActivity.this, MainActivity.class));
                            finish();
                        } else {
                            //登录失败
                            dismissLoadingDialog();
                            LoginHelper.INSTANCE.onLoginError("登录失败: " + userInfoBaseResponse.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        //登录失败
                        LoginHelper.INSTANCE.onLoginError("登录失败:" + e.getMessage());
                    }
                });
    }

    private void showLoadingDialog(String msg, String tag) {
        if (tag == "SUCCESS") {
            if (tipLoadDialog != null) {
                tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_SUCCESS).show();
                tipLoadDialog = null;
            }
        } else if (tag == "FAIL") {
            if (tipLoadDialog == null) {
                tipLoadDialog = new TipLoadDialog(LoginLandActivity.this);
            }
            tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_FAIL).show();
            tipLoadDialog = null;
        } else {
            if (tipLoadDialog == null) {
                tipLoadDialog = new TipLoadDialog(LoginLandActivity.this);
            }
            tipLoadDialog.setMsgAndType(msg, TipLoadDialog.ICON_TYPE_LOADING2).show();
        }
    }



    private void dismissLoadingDialog() {
        if (tipLoadDialog != null) {
            tipLoadDialog.dismiss();
        }
    }

    private void netCheck(){
        if (netDialog == null || !netDialog.isShowing()){
            ToastUtils.toastMsgWarning("网络连接异常,请联网后操作");
            EventBus.getDefault().post(new TTSSpeakEvent("网络连接异常,请联网后操作"));
            if (netDialog !=null){
                netDialog.dismiss();
            }
            netDialog = new NetDialog(this, "设备暂无网络，请检查", "去设置");
            netDialog.setOnDialogActionListener(new NetDialog.OnDialogActionListener() {
                @Override
                public void onSingleButtonClicked() {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }

                @Override
                public void onLeftButtonClicked() {
                }

                @Override
                public void onRightButtonClicked() {
                }

                @Override
                public void onDialogDismissed() {

                }
            });
            if (dialog != null) {
                dialog.show();
            }

        }
    }
}