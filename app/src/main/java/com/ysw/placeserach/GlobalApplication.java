package com.ysw.placeserach;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "8b88821a2b30492594c12af576f17ba9");
    }
}
