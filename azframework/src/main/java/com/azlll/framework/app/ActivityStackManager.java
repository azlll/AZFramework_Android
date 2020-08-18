package com.azlll.framework.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Activity堆栈管理类
 * 参考：https://www.jianshu.com/p/ed897d567b02
 */
public class ActivityStackManager {

    private Application application;
    private Stack<Activity> activityStack;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private int resumeActivityCount = 0;

    public ActivityStackManager(Application application) {
        this.application = application;
        this.activityStack = new Stack<>();

        this.activityLifecycleCallbacks = initActivityLifecycleCallbacks();
        application.registerActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks initActivityLifecycleCallbacks() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activityStack.push(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                resumeActivityCount++;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                resumeActivityCount--;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activityStack.remove(activity);
            }
        };
    }

    /**
     * 当前App是否在前台
     * @return
     */
    public boolean isAppForeground() {
        return resumeActivityCount > 0;
    }

    /**
     * 获取栈顶的Activity
     * @return
     */
    public Activity getTopActivity() {
        if (activityStack.size() > 0) {
            return activityStack.peek();
        }else{
            return null;
        }
    }

    /**
     * 彻底退出
     */
    public void finishAllActivity() {
        Activity activity;
        while (!activityStack.empty()) {
            activity = activityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
        activityStack.clear();
    }

    /**
     * 查找栈中是否存在指定的activity
     *
     * @param cls
     * @return
     */
    public boolean checkActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * finish指定的activity之上所有的activity
     *
     * @param actCls
     * @param isIncludeSelf
     * @return
     */
    public boolean finishToActivity(Class<? extends Activity> actCls, boolean isIncludeSelf) {
        List<Activity> buf = new ArrayList<Activity>();
        int size = activityStack.size();
        Activity activity = null;
        for (int i = size - 1; i >= 0; i--) {
            activity = activityStack.get(i);
            if (activity.getClass().isAssignableFrom(actCls)) {
                for (Activity a : buf) {
                    a.finish();
                }
                return true;
            } else if (i == size - 1 && isIncludeSelf) {
                buf.add(activity);
            } else if (i != size - 1) {
                buf.add(activity);
            }
        }
        return false;
    }
}
