package com.azlll.framework.ui.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.TextView;

import com.azlll.framework.R;

public class ZBBUploadProgressDialog extends Dialog {
    public ZBBUploadProgressDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZBBUploadProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected ZBBUploadProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }
    private ContentLoadingProgressBar pbLoading;
    private TextView tvTitle;
    private TextView tvProgress;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private void initView() {
        // 设置透明度
        getWindow().setDimAmount(0);
        setContentView(R.layout.common_upload_progress_dialog);

        pbLoading = findViewById(R.id.pbLoading);
        setLoadingColor(Color.WHITE);
        pbLoading.setProgress(0);
        tvTitle = findViewById(R.id.tvTitle);
        tvProgress = findViewById(R.id.tvProgress);
    }

    public void setLoadingColor(int color) {
        ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        pbLoading.getIndeterminateDrawable().setColorFilter(colorFilter);
    }

    @Override
    public void show() {
        super.show();
//        https://blog.csdn.net/zfr930102/article/details/95653354
//        Android Dialog 设置窗体背景颜色以及宽高

        pbLoading.setProgress(0);
        tvProgress.setText("0%");
    }

    public void setProgress(final int progress) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvProgress.setText(String.valueOf(progress) + "%");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbLoading.setProgress(progress, true);
                }else{
                    pbLoading.setProgress(progress);
                }
            }
        });
    }
}
