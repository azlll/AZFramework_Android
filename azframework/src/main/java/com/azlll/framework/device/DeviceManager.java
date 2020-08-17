package com.azlll.framework.device;

import android.Manifest;
import android.app.Application;
import android.os.Build;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.azlll.framework.constant.AZCacheConstant;
import com.azlll.framework.constant.AZDeviceConstant;
import com.azlll.framework.log.AZLog;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 设备管理
 * 各种系统参数获取：https://blog.csdn.net/jin_pan/article/details/81197871
 */
public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();
    private static final String[] MUST_PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Application application;
    private String deviceId;

    public DeviceManager(Application application) {
        this.application = application;

        // 初始化设备Id
        initDeviceId();
    }

    /**
     * 初始化设备Id
     * 请在用户允许了以下两个权限后再调用
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * @return 是否初始化DeviceId成功
     */
    private boolean initDeviceId() {


        if (EasyPermissions.hasPermissions(this.application, MUST_PERMISSIONS) == false){
            // 没有SD卡读写权限
            AZLog.e(TAG, "initDeviceId()==> Please get user's permission: Manifest.permission.READ_EXTERNAL_STORAGE and Manifest.permission.WRITE_EXTERNAL_STORAGE !!!");
            return false;
        }


        // 如果没有设备缓存路径，则创建路径
        File fileDeviceCacheDir = new File(AZCacheConstant.DeviceCache.DEVICE_CACHE_DIR);
        if (!FileUtils.isFileExists(fileDeviceCacheDir)){
            // 如果缓存路径不存在，则创建路径
            fileDeviceCacheDir.mkdirs();
        }

        // 如果没有设备ID文件，则创建一个
        File fileDeviceId = new File(AZCacheConstant.DeviceCache.DEVICE_ID_FILE_NAME);
        if (!FileUtils.isFileExists(fileDeviceId)){
            // 如果DeviceId不存在，则创建

            UUID uuid = UUID.randomUUID();
            int randomCode = new Random().nextInt(999999);
            this.deviceId = EncryptUtils.encryptMD5ToString(uuid.toString() + String.valueOf(randomCode));
            this.deviceId = this.deviceId.toLowerCase();// 全小写

            FileIOUtils.writeFileFromString(fileDeviceId, this.deviceId, false);
        }else{
            this.deviceId = FileIOUtils.readFile2String(fileDeviceId);
        }
        return true;
    }

    public void reloadDeviceId(){
        AZLog.d(TAG, "reloadDeviceId()==>");
        // 当外部获取到用户权限后，外部主动调用，否则deviceId一直为null
        boolean isInitSuccess = initDeviceId();
        AZLog.d(TAG, "reloadDeviceId()==>isInitSuccess=" + String.valueOf(isInitSuccess));
    }

    public String getDeviceId() {
        if (this.deviceId == null){
            // 防止业务使用时，因为没有SD卡读写权限造成的deviceId=null的问题
            initDeviceId();
        }
        return this.deviceId;
    }

    public String getDeviceType() {
        return AZDeviceConstant.DEVICE_TYPE_ANDROID;
    }

    public String getDeviceOsVer() {
        return Build.VERSION.RELEASE;
    }

    public String getDeviceName() {
        return Build.DEVICE;
    }

    public String getDeviceBrand() {
        return Build.BRAND;
    }

    public String getDeviceModel() {
        return Build.MODEL;
    }
}
