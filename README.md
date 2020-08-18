# Android原生App快速开发框架——AZFramework

## 1、框架介绍：

此框架将多种在原生App开发时常用的管理器、UI组件、实用工具集成一身，让开发人员更专注与业务开发，减少低级bug出现的概率。
这里列出本框架已经集成的其他优秀开源框架，并且表示感谢！


### 1-1）框架使用编译的版本
    compileSdkVersion = 29
    buildToolsVersion = "29.0.3"
    supportSdkVersion = "28.0.0" （support包的最后一个版本28.0.0版本进行开发，暂不支持androidX库）
    minSdkVersion = 16
    targetSdkVersion = 28

### 1-2）本框架已集成的其他优秀开源框架
集成本框架后，下列这些开源框架将自动依赖，可直接使用：
```groovy

    // 官方RecyclerView
    api "com.android.support:recyclerview-v7:28.0.0"

    // Android开发人员必用的工具类 https://blankj.com/2016/07/31/android-utils-code/
    //com.blankj:utilcode:1.25.9使用support版本，com.blankj:utilcodex:1.26.0使用AndroidX版本
    api ('com.blankj:utilcode:1.25.9'){
        exclude group: 'com.android.support'
    }

    // android官方示例的权限判断https://github.com/googlesamples/easypermissions
    //2.0.0使用support版本 ，3.0.0使用AndroidX版本
    api ('pub.devrel:easypermissions:2.0.0'){
        exclude group: 'com.android.support'
    }

    // 事件通知https://github.com/greenrobot/EventBus
    api ('org.greenrobot:eventbus:3.2.0'){
        exclude group: 'com.android.support'
    }
    // okhttp  https://github.com/square/okhttp
    api ('com.squareup.okhttp3:okhttp:3.11.0'){
        exclude group: 'com.android.support'
    }

    // 腾讯出品快速轻量级的KeyValue存储组件https://github.com/tencent/mmkv
    //1.0.19使用support版本，1.1.0使用AndroidX版本
    api ('com.tencent:mmkv-static:1.0.19'){
        exclude group: 'com.android.support'
    }

    // 圆形 ImageView
    api ('de.hdodenhof:circleimageview:3.0.1'){
        exclude group: 'com.android.support'
    }
    // Glide加载引擎
    api 'com.github.bumptech.glide:glide:4.9.0'

    // 二维码扫描:https://github.com/bertsir/zBarLibary
    api ('cn.bertsir.zbarLibary:zbarlibary:1.4.2') {
        exclude group: 'com.android.support'
    }
```

## 2、框架功能介绍：

### 2-1）NetworkManager网络管理
- Http的json请求，支持get/post/put/delete四种method的请求，【循环请求】！！【自动缓存】！！等
- Http的post方式的form-data文件上传
- Http的文件下载

### 2-2）JwtToken
- 标准的JwtToken字符串与对象的转换

### 2-3）ActivityManager活动管理（暂未完成）
- App中全局的Activity管理
- App的前后台切换管理

### 2-4）ClockManager时钟管理
- 与北京时间同步
- 可以拿到本机时间
- 可以拿到北京时间，不论本机时间如何变动

### 2-5）AZLog
- 对系统日志框架进行的封装，API与系统一致，可在打包时通体控制日志的输出
- 可将日志指定输出到本地文件（暂未实现）

### 2-6）DeviceManager设备管理
- 获取设备硬件信息：系统版本、系统名称、设备品牌、设备型号等
- 计算本机设备ID，即使App卸载重装也不会改变

### 2-7）CacheManager缓存管理（暂未完成）
- 统一管理本应用的内存数据：单例、临时跨对象变量等
- 统一管理本应用的本地缓存数据：临时图片、临时文件、cookie等
- 清除缓存功能
- 计算所有缓存大小

### 2-8）UI组件（暂未完成）
- 扫码
- 拍照
- 录制
- LoadingDialog
- AlertDialog
- ActionSheetDialog
- BottomDialog
- ProcessingView：圆的、长条的
- DefaultVPageView
- MobileEditText
- PasswordEditText
- SmsCodeEditText
- IdcardEditText
- SafePasswordKeyborad
- SafeNumberKeyborad
- SimpleWebBrowser
- ArrowView


## 3、使用方法

### 使用前注意事项！！！
请先使用EasyPermission对权限进行“允许”
```java
Manifest.permission.READ_EXTERNAL_STORAGE,
Manifest.permission.WRITE_EXTERNAL_STORAGE,
Manifest.permission.INTERNET
```

### 3-0 初始化Framework
在工程中自定义Application，在Application中加入以下代码
```java
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        AZFrameworkConfig azFrameworkConfig = new AZFrameworkConfig(BuildConfig.DEBUG);
        AZFramework.init(this, true, azFrameworkConfig);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 系统停止时需调用销毁接口
        AZFramework.getInstance().destory();
    }
```

### 3-1 NetworkManager使用
- 首先在主工程中建立Api.java文件（此文件名可随意），在此文件中添加请求Api的实体
```java

public class Api {
    // http api的配置
    private static final String BASE_URL = "当前App的后台服务域名，例：http://119.23.49.169:13012";

    // 创建请求对应接口的实类，指定请求方式(GET\POST\PUT\DELETE)、BaseUrl、subPath
    public static final ApiInfo QUERY_USER_INFO0 = new ApiInfo(NetworkManager.EnumMethod.GET
                                                            , BASE_URL
                                                            ,"/api/userinfoExt/getUserInfo");
    public static final ApiInfo QUERY_USER_INFO1 = new ApiInfo(NetworkManager.EnumMethod.POST
                                                            , BASE_URL
                                                            ,"/api/userinfoExt/getUserInfo1");
    public static final ApiInfo QUERY_USER_INFO2 = new ApiInfo(NetworkManager.EnumMethod.PUT
                                                            , BASE_URL
                                                            ,"/api/userinfoExt/getUserInfo2");
    public static final ApiInfo QUERY_USER_INFO3 = new ApiInfo(NetworkManager.EnumMethod.DELETE
                                                            , BASE_URL
                                                            ,"/api/userinfoExt/getUserInfo2");
    // 有多个接口则往下顺序添加...
}
```
###### 在需要做网络请求的位置添加如下代码：
- 普通请求
```java

        RequestEntity requestEntity = new RequestEntity(Api.SYSTEM_GET_CODE);

        requestEntity.addAuthorization("jwtToken");// 可选，向Header添加key=Authorization的jwt标准的token
        requestEntity.addHeader("key1", "value");  // 可选，向Header添加自定义key/value
        requestEntity.addHeader("key2", "value");  // 可选，向Header添加自定义key/value

        requestEntity.addParamToUrl("urlParams1", 123);  // 可选，指定url添加自定义key/value，?urlParams1=123
        requestEntity.addParamToUrl("urlParams2", "string"); // 可选，指定url添加自定义key/value，&urlParams2=string

        requestEntity.addParamToBody("params1", 123); // 可选，在请求body添加自定义key/value
        requestEntity.addParamToBody("params2", "string"); // 可选，指在请求body添加自定义key/value

        ZbbFramework.getInstance().getNetworkManager().request(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                // do nothings...
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                // do nothings...
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                // do nothings...
            }
        });
```
- 循环请求
（一般可用在定时请求或者增加某些数据获取成功的概率、实时性时使用，例如心跳，获取新消息，等）
```java
        RequestEntity requestEntity = new RequestEntity(Api.QUERY_USER_INFO0);

        // 循环设定
        LoopSettings loopSettings = new LoopSettings();
        loopSettings.setLoopMode(LoopSettings.EnumLoopMode.SPECIFIED); // 循环模式：ONCE仅一次，SPECIFIED指定次数（默认）， INFINITY无限循环
        loopSettings.setLoopCount(10); // 本请求循环次数，仅在LoopMode=SPECIFIED时生效，默认10次
        loopSettings.setLoopInterval(1 * 1000); // 每次循环请求的间隔，单位毫秒，默认10秒

        NetworkManager networkManager = ZbbFramework.getInstance().getNetworkManager();

        UUID uuid = networkManager.requestWithLoop(requestEntity, loopSettings, new RequestEntity.OnLoopRequestFinishListener() {
            @Override
            public void onResponse(PostLooper postLooper, String responseJson, long consumeTime) {
                // do nothings...

                // 根据业务逻辑，你可以随时停止此次循环，也可不停
                postLooper.cancel();
            }

            @Override
            public void onServerError(PostLooper postLooper, ServerErrorException e, long consumeTime) {
                // do nothings...

                // 根据业务逻辑，你可以随时停止此次循环，也可不停
                postLooper.cancel();
            }

            @Override
            public void onNetworkError(PostLooper postLooper, IOException e, long consumeTime) {
                // do nothings...

                // 根据业务逻辑，你可以随时停止此次循环，也可不停
                postLooper.cancel();
            }
        });


        // 在需要停止循环的地方停止，一般为onDestory或onPause
        networkManager.cancelPostLooperByUuid(uuid);
```
- 带缓存的请求
```java
        RequestEntity requestEntity = new RequestEntity(Api.LIVE_QUERY_ONLINE_LIST_BY_PAGE);
        // 设置请求参数
        requestEntity.addParamToUrl("liveUserId", Integer.parseInt(pusherId));
        requestEntity.addParamToUrl("page", 1);
        requestEntity.addParamToUrl("pageSize", 100);
        // 设置缓存参数
        requestEntity.setUid(uid);// 此处uid建议在App登录情况下设为用户ID，未登录下设为设备ID，内部使用此uid区分缓存数据的所属。即：即使同一个请求如果uid不一致也不会有缓存
        requestEntity.setCacheType(RequestEntity.EnumCacheType.RAW_NOT_REQUEST);// 缓存类型相见下文EnumCacheType枚举
        requestEntity.setRawCacheTime(2 * 1000); // 内存缓存时间，默认10秒，单位毫秒
        requestEntity.setStorageCacheTime(10 * 24 * 3600 * 1000); // 本地文件缓存时间，默认10天，单位毫秒

        NetworkManager networkManager = ZbbFramework.getInstance().getNetworkManager();
        networkManager.requestWithCache(requestEntity, new RequestEntity.OnRequestAndCacheFinishListener() {

            @Override
            public void onResponseFromCache(boolean isFoundCache, String responseJson) {
                if (isFoundCache) {
                    // 找到了缓存，即之前请求成功过
                }else{
                    // 没找到缓存，前一次请求失败或缓存过期
                }
            }

            @Override
            public boolean onResponseFromNetwork(String responseJson, long consumeTime) {
                // 从网络回来的数据
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                // do somethings..
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                // do somethings..
            }
        });

```
缓存类型
```java
    public enum EnumCacheType {
        // 不缓存，仅请求网络
        NONE,
        // 查询RAW缓存，并请求网络
        RAW_ONLY,
        // 查询本地STORAGE，并请求网络
        STORAGE_ONLY,
        // 有RAW返回RAW，无RAW返回STORAGE，然后请求网络
        ALL,

        // 如果有RAW则直接返回，不请求网络
        RAW_NOT_REQUEST,
        // 如果有STORAGE则直接返回，不请求网络
        STORAGE_NOT_REQUEST,
        // 有RAW返回RAW，无RAW返回STORAGE，如果无STORAGE则返回错误，不请求网络
        ALL_NOT_REQUEST
    }
```

- 带缓存的循环请求
```java

        RequestEntity requestEntity = new RequestEntity(Api.LIVE_QUERY_ONLINE_LIST_BY_PAGE);
        requestEntity.addParamToUrl("liveUserId", Integer.parseInt(pusherId));
        requestEntity.addParamToUrl("page", 1);
        requestEntity.addParamToUrl("pageSize", 100);

        requestEntity.setUid(mUserId);
        requestEntity.setCacheType(RequestEntity.EnumCacheType.RAW_ONLY);
        requestEntity.setRawCacheTime(1 * 1000);

        LoopSettings loopSettings = new LoopSettings();
        loopSettings.setLoopMode(LoopSettings.EnumLoopMode.INFINITY);
        loopSettings.setLoopInterval(5 * 1000);

        NetworkManager networkManager = ZbbFramework.getInstance().getNetworkManager();
        UUID uuidOnlineListLoopRequest = networkManager.requestWithLoopAndCache(requestEntity, loopSettings, new RequestEntity.OnLoopRequestAndCacheFinishListener() {

            @Override
            public void onResponseFromCache(PostLooper postLooper, boolean isFoundCache, String responseJson) {
                // do somethings..
            }

            @Override
            public boolean onResponseFromNetwork(PostLooper postLooper, String responseJson, long consumeTime) {
                // do somethings..
            }

            @Override
            public void onServerError(PostLooper postLooper, ServerErrorException e, long consumeTime) {
                // do somethings..
            }

            @Override
            public void onNetworkError(PostLooper postLooper, IOException e, long consumeTime) {
                // do somethings..
            }

        });
```
- 上传文件（以POST请求的form-data方式上传）
```java
        // 初始化一个下载任务
        DownloadTask task = new DownloadTask();
        task.setRequestUrl("http的文件下载地址");
        task.setDestType(EnumDownloadDestinationType.SYSTEM_DOWNLOADS);// 下载路径的根目录类型，有如下几种类型
        task.setDestSubPath("packageName", "tmp", "image");// 下载路径的子目录，此处传入数组，内部会自动拼接文件分隔符：例如：packageName/tmp/image
        task.setDestFilename("hello.jpeg");// 保存到本地的文件名，注意：不含路径
```
```java
        // 下载
        AZFramework.getInstance().getNetworkManager().download(task, new DownloadTask.OnDownloadListener() {
            @Override
            public void onProgress(DownloadTask task, double downloadedBytesSize, double totalBytesSize, int currentPercent, long consumeTime) {
                // 下载进度回调，内部已经切换到UIThread，请放心使用

                // downloadedBytesSize 当前已下载到字节数
                // totalBytesSize      此文件总共字节数
                // currentPercent      当前下载到百分比，已经换算成0-100的整数
                // consumeTime         从下载开始到现在已经过去了多少时间（单位：毫秒）
            }

            @Override
            public void onFinish(DownloadTask task, double totalBytesSize, String fileMd5, long consumeTime) {
                // 下载完成的回调，内部已经切换到UIThread，请放心使用

                // totalBytesSize      此文件总共字节数
                // fileMd5             自动计算出的此文件的md5，可自行做文件有效性校验
                // consumeTime         从下载开始到现在已经过去了多少时间（单位：毫秒）
            }

            @Override
            public void onCancel(DownloadTask task, long consumeTime) {
                // 当因外部原因被取消下载时会回调，如：App进程杀死、全部页面被关闭 等，内部已经切换到UIThread，请放心使用
            }

            @Override
            public void onError(DownloadTask task, Exception ex, long consumeTime) {
                // 下载过程中出现任何错误时回调，内部已经切换到UIThread，请放心使用
            }
        });
```
```java
// 下载路径的根目录类型
public enum EnumDownloadDestinationType {

    /**
     * Environment.DIRECTORY_DOWNLOADS
     * 后【选填】拼接路径，必须拼接文件名
     */
    SYSTEM_DOWNLOADS,
    /**
     * Environment.DIRECTORY_PICTURES
     * 后【无需】拼接路径，仅需拼接文件名
     */
    SYSTEM_PICTURES,
    /**
     * Environment.DIRECTORY_DCIM
     * 后【无需】拼接路径，仅需拼接文件名
     */
    SYSTEM_DCIM,
    /**
     * Environment.DIRECTORY_MUSIC
     * 后【无需】拼接路径，仅需拼接文件名
     */
    SYSTEM_MUSIC,
    /**
     * Environment.DIRECTORY_MOVIES
     * 后【无需】拼接路径，仅需拼接文件名
     */
    SYSTEM_MOVIES,
//    /**
//     * Environment.DIRECTORY_DOCUMENTS
//     * 后【无需】拼接路径，仅需拼接文件名
//     */
//    SYSTEM_DOCUMENTS,


    /**
     * 应用外数据，SD卡根目录
     * 后【选填】拼接路径，必须拼接文件名
     */
    EXTERNAL_STORAGE_DIRECTORY,

    /**
     * /data/data/此应用包名/files
     * 后【选填】拼接路径，必须拼接文件名
     */
    INTERNAL_FILES,
    /**
     * /data/data/此应用包名/cache
     * 后【选填】拼接路径，必须拼接文件名
     */
    INTERNAL_CACHE,
}
```
- 文件上传
```java

        RequestEntity requestEntity = new RequestEntity(Api.UPLOAD_AVATAR);
        requestEntity.addFormDataFile(要上传的file对象, 接口定义的formData-key, 文件类型如：image/jpeg);

        ZbbFramework.getInstance().getNetworkManager().uploadFile(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {

            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {

            }
        });
```
