package com.stkj.aoxin.weight.base.utils;

import android.content.Context;

import com.stkj.aoxin.weight.base.device.DeviceManager;
import com.stkj.aoxin.weight.base.net.AppNetManager;
import com.stkj.aoxin.weight.base.ui.dialog.BindingPwdAlertDialogFragment;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.ui.dialog.CommonBindSignleAlertDialogFragment;
import com.stkj.aoxin.weight.login.helper.LoginHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.utils.AndroidUtils;

public class CommonDialogUtils {

    public static void showTipsDialog(Context context, String msg) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .show(context);
    }

    public static void showTipsDialog(Context context, String msg, String confirmText, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setLeftNavTxt(confirmText)
                .setLeftNavClickListener(confirmClickListener)
                .show(context);
    }

    public static void showAppResetDialog(Context context, String msg, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setNeedHandleDismiss(true)
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.setAlertContentTxt("数据清理中,请稍等...")
                                .setLeftNavTxt("清理中")
                                .setRightNavTxt("")
                                .setLeftNavClickListener(null);
                        //确认回调
                        if (confirmClickListener != null) {
                            confirmClickListener.onClick(alertDialogFragment);
                        }
                        //释放设备接口
                        DeviceManager.INSTANCE.getDeviceInterface().release();
                        //清理网络缓存
                        AppNetManager.INSTANCE.clearAppNetCache();
                        //清理用户登录信息
                        LoginHelper.INSTANCE.clearUserInfo();

                    }
                })
                .setRightNavTxt("取消")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.dismiss();
                    }
                });
        commonAlertDialogFragment.show(context);
    }

}
