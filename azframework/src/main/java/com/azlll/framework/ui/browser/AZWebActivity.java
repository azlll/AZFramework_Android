package com.azlll.framework.ui.browser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.GsonUtils;
import com.bumptech.glide.Glide;
import com.azlll.framework.R;
import com.azlll.framework.log.AZLog;
import com.azlll.framework.ui.browser.utils.SimpleToolbar;

//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.entity.LocalMedia;
//import com.azlll.framework.ui.cameraphoto.widget.GlideEngine;


public class AZWebActivity extends AppCompatActivity {

    private static final String TAG = AZWebActivity.class.getSimpleName();
    private WebView mWebView;
    private ProgressBar progressBar;
    private AlertDialog mAlertDialog;//点击左边退出按钮提示
    private SimpleToolbar simpleToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 接收从上一个页面传过的数据
     */
    private String url = "";
    private Bitmap bitmapIvBack = null;
    private Bitmap bitmapIvClose = null;
    private Bitmap bitmapIvFunction = null;
    /** 配置WebActivity -setResultCode-退回上一个Activity */
    private static final int REQUEST_CODE_BY_ADVERTISING_REQUEST_ACTIVITY = 3;
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

//    private List<LocalMedia> selectList = new ArrayList<>();
//    private LocalMedia localMedia;
//    private int position = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_webview_activity);

        initView();
        initDate();
        initOnClickListener();
    }
    /**
     * 初始化视图
     */
    public void initView(){
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mWebView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        simpleToolbar = (SimpleToolbar) findViewById(R.id.simple_toolbar);
    }

    /**
     * 初始化数据
     */
    public void initDate(){
        Intent intent = getIntent();
        if (intent != null){
            url = intent.getStringExtra("url");
            if (null != intent.getByteArrayExtra("ivBack")){
                byte[] byteIvBack = intent.getByteArrayExtra("ivBack");
                if (null != byteIvBack){
                    bitmapIvBack = BitmapFactory.decodeByteArray(byteIvBack,0,byteIvBack.length);
                }
            }

            if (null != intent.getByteArrayExtra("ivClose")){
                byte[] byteIvClose = intent.getByteArrayExtra("ivClose");
                if (null != byteIvClose){
                    bitmapIvClose = BitmapFactory.decodeByteArray(byteIvClose,0,byteIvClose.length);
                }
            }

            if (null != intent.getByteArrayExtra("ivFunction")){
                byte[] byteIvFunction = intent.getByteArrayExtra("ivFunction");
                if (null != byteIvFunction){
                    bitmapIvFunction = BitmapFactory.decodeByteArray(byteIvFunction,0,byteIvFunction.length);
                    simpleToolbar.getIvFunction().setVisibility(View.VISIBLE);
                }
            }else {
                simpleToolbar.getIvFunction().setVisibility(View.GONE);
            }

        }

        /**
         * simpleToolbar的图片显示
         */
        if (bitmapIvBack != null){
            simpleToolbar.setLeftIvBackBitmap(bitmapIvBack);
        }
        if (bitmapIvClose != null){
            simpleToolbar.setLeftIvCloseBitmap(bitmapIvClose);
        }
        if (bitmapIvFunction != null){
            simpleToolbar.setLeftFunctionBitmap(bitmapIvFunction);
        }

        mWebView.loadUrl(url);
        mWebView.requestFocus();//触摸焦点起作用
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);//使能JavaScipt调用功能
        webSettings.setAppCacheEnabled(true);//设置允许缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        webSettings.setDatabaseEnabled(true);//设置数据库存储API可用
        webSettings.setDomStorageEnabled(true);//设置DOM存储API可用
        webSettings.setSupportZoom(true);//设置WebView支持使用屏幕上的缩放控件和手势进行缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);//设置webview推荐使用的窗口，设置为true。
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式，也设置为true
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置允许js弹出alert对话框
        MyWebViewClient myWebClient = new MyWebViewClient();
        MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
        mWebView.setWebViewClient(myWebClient);
        mWebView.setWebChromeClient(myWebChromeClient);
        /*** webview绑定js事件 ****/
//        mWebView.addJavascriptInterface(new MyJavaScripteInterface(AZWebActivity.this), "imagelistener");
        mWebView.addJavascriptInterface(new MyJavaScripteInterface() {
            @JavascriptInterface
            @Override
            public void openImage(String img, String[] imageUrls) {
                AZLog.i("网页图片url=", GsonUtils.toJson(imageUrls));
//                ToastUtils.showShort(img);
//                selectList.clear();
//                for (int i = 0; i < imageUrls.length; i++) {
//                    localMedia = new LocalMedia();
//                    localMedia.setPath(imageUrls[i]);
//                    selectList.add(localMedia);
//                }
//                    /**
//                     * 调用PictureSelector查看网页图片
//                     * 预览图片
//                     */
//                    PictureSelector.create(AZWebActivity.this)
//                            .themeStyle(R.style.picture_default_style)
//                            .isNotPreviewDownload(true)
//                            .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
//                            .isGif(true)
//                            .openExternalPreview(position, selectList);
            }

//            @Override
//            public void openImage(String img) {
//                ToastUtils.showShort(img);
//                selectList.clear();
//                localMedia = new LocalMedia();
//                localMedia.setPath(img);
//                selectList.add(localMedia);
//                    /**
//                     * 调用PictureSelector查看网页图片
//                     * 预览图片
//                     */
//                    PictureSelector.create(AZWebActivity.this)
//                            .themeStyle(R.style.picture_default_style)
//                            .isNotPreviewDownload(true)
//                            .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
//                            .isGif(true)
//                            .openExternalPreview(position, selectList);
//                }
        }, "imagelistener");

        /**
         * wevView的下拉刷新监听
         */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mWebView.getUrl());
            }
        });
        /**
         * WebView下拉刷新与SwipeRefreshLayout事件冲突解决
         */
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout swipeRefreshLayout, @Nullable View view) {
                return mWebView.getScrollY() > 0;  // 原生安卓使用getScrollY()来判断在Y轴的距离
            }
        });
    }
    private interface MyJavaScripteInterface{
        void openImage(String img, String[] imageUrls);
//        void openImage(String img);
    }
    /**
     * 控件点击事件操作
     */
    public void initOnClickListener(){

        /**
         * 左边关闭按钮
         */
        simpleToolbar.setLeftIvCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        /**
         * 左边返回上一页的按钮
         */
        simpleToolbar.setLeftIvBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();//返回上个页面
                    return;
                } else {
                    AZWebActivity.this.finish();
                }
            }
        });
    }
    /**
     * WebViewClient
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            if (request.getUrl().toString().startsWith("http:") || request.getUrl().toString().startsWith("https:")) {
//                return false;
//            }
            return super.shouldOverrideUrlLoading(view, request);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            AZLog.i("url=",url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //webView默认是不处理https请求的，页面显示空白，需要进行如下设置：
            handler.proceed();//如果只是简单的接受所有证书的话，就直接调process()方法就行了
        }
    };
    /**
     * WebChromeClient
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            simpleToolbar.setMainTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);//设置进度值
            progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                swipeRefreshLayout.setRefreshing(false);//设置是否应该显示刷新进度。true是，false否
            }
            super.onProgressChanged(view, newProgress);
        }
    };

    /**
     * 关闭弹窗
     */
    private void showDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("您确定要关闭该页面吗?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                        }
                    })//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                            setResultCode();
                        }
                    }).create();
        }
        mAlertDialog.show();

    }

    /**
     * 退出当前WebActivity设置返回结果嘛
     */
    private void setResultCode(){
        setResult(REQUEST_CODE_BY_ADVERTISING_REQUEST_ACTIVITY);
        AZWebActivity.this.finish();
    }

    /**
     * 待网页加载完全后设置图片点击的监听方法
     * @param webView
     */
    private void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                " var array=new Array(); " +
                " for(var j=0;j<objs.length;j++){ array[j]=objs[j].src; }"+//这个循环将所图片放入数组，当js调用本地方法时传入。当然如果采用方式一获取图片的话，本地方法可以不需要传入这个数组
                "for(var i=0;i<objs.length;i++)" +
                "{"
                + "objs[i].onclick=function()" +
                "{"
                + "window.imagelistener.openImage(this.src,array);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                "}  " +
                "}" +
                "})()");
//        webView.loadUrl("javascript:(function(){" +
//                "var objs = document.getElementsByTagName(\"img\"); " +
//                "for(var i=0;i<objs.length;i++)" +
//                "{"
//                + "objs[i].onclick=function()" +
//                "{"
//                + "window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
//                "}  " +
//                "}" +
//                "})()");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一层页面
                return true;
            } else {
                setResultCode();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if(mWebView!=null) {
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(AZWebActivity.this).clearDiskCache();//清理磁盘缓存需要在子线程中执行
            }
        }).start();
        Glide.get(this).clearMemory();//清理内存缓存可以在UI主线程中进行
        super.onDestroy();
    }
}
