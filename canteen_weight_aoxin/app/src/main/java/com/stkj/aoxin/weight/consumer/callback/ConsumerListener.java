package com.stkj.aoxin.weight.consumer.callback;

import android.view.SurfaceView;

import com.stkj.aoxin.weight.home.ui.widget.HomeTitleLayout;

public interface ConsumerListener {

    default void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreviewView) {

    }

    default void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {

    }

    default void onConsumerDismiss() {

    }

    default void onConsumerChanged() {

    }
}
