package com.stkj.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import com.stkj.common.core.AppManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.storage.StorageHelper;
import top.zibin.luban.Luban;

public class BitmapUtils {

    public static void getViewCacheBitmap(View view, ViewCacheBitmapCallback callback) {
        getViewCacheBitmap(view, true, 250, callback);
    }

    public static void getViewCacheBitmap(View view, boolean needLuBanCompress, int ignoreSize, ViewCacheBitmapCallback callback) {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        emitter.onNext(syncGetViewDrawCache(view, needLuBanCompress, ignoreSize));
                        emitter.onComplete();
                    }
                }).compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<String>() {
                    @Override
                    protected void onSuccess(@NonNull String uri) {
                        if (callback != null) {
                            if (!TextUtils.isEmpty(uri)) {
                                callback.onComplete(uri);
                            } else {
                                callback.onFail();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (callback != null) {
                            callback.onFail();
                        }
                    }
                });
    }

    public static String syncGetViewDrawCache(View view, boolean needLuBanCompress, int ignoreSize) {
        String drawCachePath = "";
        try {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            String fileName = "view_cache_" + System.currentTimeMillis() + ".jpg";
            File shareFile = StorageHelper.createShareFile(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(shareFile));
            view.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();
            if (needLuBanCompress) {
                List<File> fileList = Luban.with(AppManager.INSTANCE.getApplication())
                        .load(shareFile)
                        .setTargetDir(StorageHelper.getExternalShareDirPath())
                        .ignoreBy(ignoreSize > 0 ? ignoreSize : 250)
                        .get();
                if (fileList != null && !fileList.isEmpty()) {
                    drawCachePath = fileList.get(0).getAbsolutePath();
                } else {
                    drawCachePath = shareFile.getAbsolutePath();
                }
            } else {
                drawCachePath = shareFile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawCachePath;
    }

    public interface ViewCacheBitmapCallback {
        void onComplete(String path);

        default void onFail() {
        }
    }

    /**
     * 将base64格式字符串转换成Bitmap
     *
     * @param base64String base64编码的图片字符串
     * @return 转换后的Bitmap对象，如果转换失败返回null
     */
    public static Bitmap base64ToBitmap(String base64String) {
        if (TextUtils.isEmpty(base64String)) {
            return null;
        }

        try {
            // 去除base64数据头部信息（如"data:image/jpeg;base64,"）
            String pureBase64 = base64String;
            if (base64String.contains(",")) {
                pureBase64 = base64String.substring(base64String.indexOf(",") + 1);
            }

            // 解码base64字符串
            byte[] decodedBytes = android.util.Base64.decode(pureBase64, android.util.Base64.DEFAULT);

            // 将字节数组转换为Bitmap
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
