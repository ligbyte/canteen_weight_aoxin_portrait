package com.stkj.aoxin.weight.home.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.stkj.aoxin.weight.R;

public class NetDialog extends Dialog {
    private TextView tvTitle;
    private AppCompatButton btnSingle;
    private LinearLayout layoutDoubleButtons;
    private AppCompatButton btnLeft;
    private AppCompatButton btnRight;
    private OnDialogActionListener listener;

    public interface OnDialogActionListener {
        void onSingleButtonClicked();
        void onLeftButtonClicked();
        void onRightButtonClicked();
        void onDialogDismissed();
    }

    // Single button constructor without title
    public NetDialog(@NonNull Context context, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(null, buttonText, null);
    }

    // Single button constructor with title
    public NetDialog(@NonNull Context context, String title, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, buttonText, null);
    }

    // Double button constructor with title
    public NetDialog(@NonNull Context context, String title, String leftButtonText, String rightButtonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, leftButtonText, rightButtonText);
    }
    
    // Static factory method for double button without title
    public static NetDialog createWithTwoButtons(@NonNull Context context, String leftButtonText, String rightButtonText) {
        NetDialog dialog = new NetDialog(context, "");
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
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        // Inflate layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_net, null);
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
                    listener.onRightButtonClicked();
                }
                dismiss();
            });
        }
        
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
}