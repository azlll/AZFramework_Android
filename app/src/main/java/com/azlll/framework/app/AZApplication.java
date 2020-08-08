package com.azlll.framework.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;

import com.azlll.framework.ZbbFramework;
import com.azlll.framework.ZbbFrameworkConfig;
import com.azlll.framework.log.ZBBLog;

public class AZApplication extends MultiDexApplication {

    private static final String TAG = AZApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化AZFramework
        initAZFramework();
    }
    /**
     * 初始化AZFramework
     */
    public void initAZFramework(){
        ZbbFrameworkConfig zbbFrameworkConfig = new ZbbFrameworkConfig(BuildConfig.DEBUG);
        ZbbFramework.init(this, true, zbbFrameworkConfig);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            private int activityInstanceCount = 0;
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activityInstanceCount++;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activityInstanceCount--;
                if (activityInstanceCount == 0){
                    ZBBLog.d(TAG, "onActivityDestroyed()==> ALL Activity is Destroyed!!!");
                    // 所有页面都退出了，做一些内存释放动作
                    ZbbFramework.getInstance().getNetworkManager().cancenAllPostLoop();
                }
            }
        });
    }
}
