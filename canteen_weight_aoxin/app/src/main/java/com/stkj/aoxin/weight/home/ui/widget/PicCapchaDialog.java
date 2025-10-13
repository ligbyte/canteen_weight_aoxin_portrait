package com.stkj.aoxin.weight.home.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.common.util.Base64Utils;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.home.ui.activity.LoginLandActivity;
import com.stkj.aoxin.weight.login.model.GetPicCaptchaInfo;
import com.stkj.aoxin.weight.login.service.LoginService;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.model.TTSSpeakEvent;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.BitmapUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.common.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class PicCapchaDialog extends Dialog {
    private TextView tvTitle;

    private ImageView iv_close;

    private String validCodeReqNo;

    private EditText et_captcha;

    private ImageView iv_captcha;

    private LinearLayout ll_dialog_login;
    private AppCompatButton btnSingle;
    private LinearLayout layoutDoubleButtons;
    private AppCompatButton btnLeft;
    private AppCompatButton btnRight;
    private OnDialogActionListener listener;

    public interface OnDialogActionListener {
        void onSingleButtonClicked();
        void onLeftButtonClicked();
        void onRightButtonClicked(String captcha,String validCodeReqNo);
        void onDialogDismissed();
    }

    // Single button constructor without title
    public PicCapchaDialog(@NonNull Context context, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(null, buttonText, null);
    }

    // Single button constructor with title
    public PicCapchaDialog(@NonNull Context context, String title, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, buttonText, null);
    }

    // Double button constructor with title
    public PicCapchaDialog(@NonNull Context context,String validCodeBase64, String validCodeReqNo, String title, String leftButtonText, String rightButtonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, leftButtonText, rightButtonText);
        iv_captcha.setImageBitmap(BitmapUtils.base64ToBitmap(validCodeBase64));
        this.validCodeReqNo = validCodeReqNo;

    }
    
    // Static factory method for double button without title
    public static PicCapchaDialog createWithTwoButtons(@NonNull Context context, String leftButtonText, String rightButtonText) {
        PicCapchaDialog dialog = new PicCapchaDialog(context, "");
        dialog.initDialog(null, leftButtonText, rightButtonText);
        return dialog;
    }

    private void initDialog(String title, String buttonText, String rightButtonText) {
        // Set dialog properties
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        // Inflate layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pic_captcha, null);
        setContentView(dialogView);
        
        // Make dialog fullscreen
        if (getWindow() != null) {
            getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        }
        
        // Initialize views
        tvTitle = dialogView.findViewById(R.id.tv_title);
        btnSingle = dialogView.findViewById(R.id.btn_single);
        layoutDoubleButtons = dialogView.findViewById(R.id.layout_double_buttons);
        btnLeft = dialogView.findViewById(R.id.btn_left);
        btnRight = dialogView.findViewById(R.id.btn_right);
        iv_close = dialogView.findViewById(R.id.iv_close);
        iv_captcha = dialogView.findViewById(R.id.iv_captcha);
        et_captcha = dialogView.findViewById(R.id.et_captcha);
        ll_dialog_login = dialogView.findViewById(R.id.ll_dialog_login);
        iv_close.setOnClickListener(v -> {

            if (listener != null) {
                listener.onRightButtonClicked(et_captcha.getText().toString().trim(), validCodeReqNo);
            }
            dismiss();
        });


        iv_captcha.setOnClickListener(v -> {
            generateCaptcha();
        });

        // Set initial countdown text

        // Configure title
        if (title != null && !title.trim().isEmpty()) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        
        // Configure buttons based on parameters
        if (rightButtonText == null) {
            // Single button mode
            btnSingle.setText(buttonText);
            btnSingle.setVisibility(View.VISIBLE);
            layoutDoubleButtons.setVisibility(View.GONE);
            
            btnSingle.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSingleButtonClicked();
                }
                dismiss();
            });
        } else {
            // Double button mode
            btnLeft.setText(buttonText);
            btnRight.setText(rightButtonText);
            btnSingle.setVisibility(View.GONE);
            layoutDoubleButtons.setVisibility(View.VISIBLE);
            
            btnLeft.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onLeftButtonClicked();
                }
                dismiss();
            });
            
            btnRight.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onRightButtonClicked(et_captcha.getText().toString().trim(), validCodeReqNo);
                }
//                dismiss();
            });
        }
        
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
            if (keypadHeight > screenHeight * 0.3) {
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
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ll_dialog_login.getLayoutParams();
        layoutParams.topMargin = (int)(-moveDistance * 0.3f) ; // 上移键盘高度的一半
        ll_dialog_login.setLayoutParams(layoutParams);
    }

    /**
     * 恢复登录布局到原始位置
     */
    private void restoreLoginLayoutPosition() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ll_dialog_login.getLayoutParams();
        layoutParams.topMargin = 0; // 恢复原始位置
        ll_dialog_login.setLayoutParams(layoutParams);
    }

    
    public void setOnDialogActionListener(OnDialogActionListener listener) {
        this.listener = listener;
    }
    
    public void setTitle(String title) {
        if (tvTitle != null) {
            if (title != null && !title.trim().isEmpty()) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    public void dismiss() {

        super.dismiss();
    }
    
    @Override
    protected void onStop() {
        super.onStop();

    }

    private void generateCaptcha() {


        if (!NetworkUtils.isConnected()){
            AppToast.toastMsg("网络连接已断开!");
            EventBus.getDefault().post(new TTSSpeakEvent("网络连接已断开"));
            return;
        }

        HashMap<String, String> paramsMap = new HashMap<>();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(LoginService.class)
                .getPicCaptcha(paramsMap)
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseResponse<GetPicCaptchaInfo>>() {
                    @Override
                    protected void onSuccess(BaseResponse<GetPicCaptchaInfo> userInfoBaseResponse) {

                        if (!userInfoBaseResponse.isSuccess()) {
                            ToastUtils.toastMsgError(userInfoBaseResponse.getMsg());
                            EventBus.getDefault().post(new TTSSpeakEvent(userInfoBaseResponse.getMsg()));
                            return;
                        }else {
                            iv_captcha.setImageBitmap(BitmapUtils.base64ToBitmap(userInfoBaseResponse.getData().getValidCodeBase64()));
                            validCodeReqNo = userInfoBaseResponse.getData().getValidCodeReqNo();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });




    }



}