package com.silang.superfileview;

/**
 * Created by 12457 on 2017/8/2.
 */

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

/**
 * Created by ljh
 * on 2016/12/22.
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //增加这句话
//        QbSdk.initX5Environment(this, null);
        this.initWebView();
        ExceptionHandler.getInstance().initConfig(this);
    }

    /**
     * 初始化腾讯 X5 webView
     */
    private void initWebView() {
        //优化异常上报
        com.tencent.smtt.sdk.WebView.getCrashExtraMessage(this);
        //视频为了避免闪屏和透明问题
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //避免输入法界面弹出后遮挡输入光标的问题
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.i("APP", "腾讯 X 5 初始化:" + arg0);
                Log.i("APP", arg0 == true ? "腾讯 X5 初始化成功！" : ("腾讯 X5 初始化失败！" + arg0));
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(), cb);
        QbSdk.initX5Environment(getApplicationContext(), cb);

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                Log.i("APP", "tbs内核下载完成");
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.i("APP", "tbs内核安装完成");
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.i("APP", "tbs内核下载进度监听");
            }
        });

        //tbs内核下载跟踪
        //判断是否要自行下载内核
        boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
        if (needDownload) {
            //isNetworkWifi(this)是我
            //自己写的一个方法，这里我也希望wifi下再下载
            TbsDownloader.startDownload(this);
        }
    }
}
