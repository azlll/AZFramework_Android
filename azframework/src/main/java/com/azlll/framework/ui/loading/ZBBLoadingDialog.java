package com.azlll.framework.ui.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;

import com.azlll.framework.R;

public class ZBBLoadingDialog extends Dialog {
    public ZBBLoadingDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZBBLoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected ZBBLoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }


    private ContentLoadingProgressBar pbLoading;

    private void initView() {

//        设置透明度
        getWindow().setDimAmount(0);

        setContentView(R.layout.common_loading_dialog);

        pbLoading = findViewById(R.id.pbLoading);
        setLoadingColor(Color.WHITE);
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

    }

}
