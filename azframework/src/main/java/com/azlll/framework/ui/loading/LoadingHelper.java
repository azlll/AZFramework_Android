package com.azlll.framework.ui.loading;

import android.app.Activity;

public class LoadingHelper {

    public static AZLoadingDialog showLoading(Activity activity) {


        AZLoadingDialog dialog = new AZLoadingDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static void hideLoading(AZLoadingDialog dialog) {
        if (dialog == null) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
