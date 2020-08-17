package com.azlll.framework.network.download;

public enum EnumDownloadStatus {

    /** 初始化完成 */
    INITED,
    /** 等待执行 */
    WAITING,
    /** 正在下载 */
    DOWNLOADING,
    /** 完成-找到本地有相同文件，无需下载 */
    FINISHED_FOUND_SAME_FILE,
    /** 完成-下载完成 */
    FINISHED_DOWNLOAD,
    /** 下载过程中取消 */
    CANCLE,
    /** 下载过程出错 */
    ERROR,
}
