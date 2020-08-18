package com.azlll.framework.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;

import com.azlll.framework.AZFramework;
import com.azlll.framework.AZFrameworkConfig;
import com.azlll.framework.log.AZLog;

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
        AZFrameworkConfig azFrameworkConfig = new AZFrameworkConfig(BuildConfig.DEBUG);
        AZFramework.init(this, true, azFrameworkConfig);

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
                    AZLog.d(TAG, "onActivityDestroyed()==> ALL Activity is Destroyed!!!");
                    // 所有页面都退出了，做一些内存释放动作
                    AZFramework.getInstance().destory();
                }
            }
        });
    }
}
