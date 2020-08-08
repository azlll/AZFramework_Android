package com.azlll.framework;

import android.app.Application;
import android.webkit.CookieManager;

import com.azlll.framework.clock.ClockManager;
import com.azlll.framework.device.DeviceManager;
import com.azlll.framework.log.ZBBLog;
import com.azlll.framework.network.NetworkManager;
import com.tencent.mmkv.MMKV;

public class ZbbFramework {

    public static final String TAG = ZbbFramework.class.getSimpleName();
    public static ZbbFramework _instance;

    public static ZbbFramework init(Application application, boolean forceInit, ZbbFrameworkConfig config) {
        // 初始化单例
        if (forceInit) {
            // 强制初始化，即为如果非null，也强制销毁，重新new
            if (_instance != null) {
                _instance.destory();
                _instance = null;
            }
            _instance = new ZbbFramework(application, config);
        }else{
            // 非强制初始化，如果非null，直接返回instance
            if (_instance == null) {
                _instance = new ZbbFramework(application, config);
            }
        }
        return _instance;
    }

    public static ZbbFramework getInstance() {
        // 获取单例
        if (_instance == null){
            ZBBLog.e(TAG, "getInstance()==> _instance IS NULL!!!");
        }
        return _instance;
    }

    private Application _application;
    private ZbbFrameworkConfig _config;
    private NetworkManager _networkManager;
    private ClockManager _clockManager;
    private DeviceManager _deviceManager;

    public ZbbFramework(Application application, ZbbFrameworkConfig config) {

        this._application = application;
        this._config = config;

        // 1、初始化日志框架
        ZBBLog.init(application);
        ZBBLog.setDebug(config.isDebug());

        // 2、初始化MMKV，使得其他Manager可以正常使用MMKV做数据存取
        String rootDir = MMKV.initialize(application);
        ZBBLog.d(TAG, "MMKV.initialize()==> rootDir=" + rootDir);

        // 3、初始化各种管理器示例-->
        //    网络管理器
        _networkManager = new NetworkManager(application
                , config.getNetworkTimeout()
                , config.getNetworkRawCacheTime()
                , config.getNetworkStroageCacheTime());
        //    时钟管理器，将会自动启动NTP时间同步
        _clockManager = new ClockManager(application);
        // 设备管理器，将会自动创建/获取设备ID
        _deviceManager = new DeviceManager(application);

        //    启动网络监听
        _networkManager.addOnNetworkStatusChangedListener(new NetworkManager.OnNetworkStatusChangedListener() {
            @Override
            public void onConnected() {
                // 同步NTP时间
                _clockManager.syncNtpUtc();
            }

            @Override
            public void onDisconnected() {

            }
        });

        // 4、刷新API缓存
        _networkManager.removeExpiredCache();

        // 刷新DNS缓存
        // 参考https://blog.csdn.net/yonggeit/article/details/88175022?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-9&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-9
        // 防止域名不变但后台已经配置域名指向另一个IP时，目前代码层面无法刷新

        // 5、刷新WebView缓存
        // 把cookie清除了缓存也会清除 参考：https://www.jianshu.com/p/271be3f137dc
        CookieManager.getInstance().removeAllCookies(null);

        // 6、启动任务管理
        // TODO:

        // 7、启动单例管理
        // TODO:

        // 8、启动页面堆栈管理
        // TODO:

    }

    /**
     * 销毁
     * */
    public void destory() {
        _networkManager.cancenAllPostLoop();
    }

    public Application getApplication() {
        return this._application;
    }

    public ZbbFrameworkConfig getConfig() {
        return this._config;
    }

    public NetworkManager getNetworkManager() {
        return this._networkManager;
    }

    public ClockManager getClockManager() {
        return this._clockManager;
    }

    public DeviceManager getDeviceManager() {
        return _deviceManager;
    }
}
