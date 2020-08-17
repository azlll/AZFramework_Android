package com.azlll.framework.network.download;

/**
 * https://www.cnblogs.com/CVstyle/p/6389966.html
 */
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
