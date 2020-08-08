package com.azlll.framework.network;

public class LoopSettings {

    public enum EnumLoopMode{
        /** 只循环1次 */
        ONCE,
        /** 循环指定次数，次数必须>=0（默认） */
        SPECIFIED,
        /** 无限循环，使用无限循环则需要在必要的生命周期停止循环，否则刷流量、耗电 */
        INFINITY
    }

    /**
     * 循环模式，默认指定循环次数
     */
    private EnumLoopMode loopMode = EnumLoopMode.SPECIFIED;
    /**
     * 每次循环的间隔（单位：毫秒），默认10000毫秒（10秒）
     */
    private int loopInterval = 10 * 1000;
    /**
     * 总循环次数，当loopMode=EnumLoopMode.INFINITY时设置生效，若<1则视为1
     * 默认循环10次，防止Activity全部关闭后，Application仍存活的情况，不要在后台刷流量
     */
    private int loopCount = 10;

    public void setLoopMode(EnumLoopMode loopMode) {
        this.loopMode = loopMode;
    }

    public EnumLoopMode getLoopMode() {
        return loopMode;
    }

    public void setLoopInterval(int loopInterval) {
        this.loopInterval = loopInterval;
    }

    public int getLoopInterval() {
        return loopInterval;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getLoopCount() {
        return loopCount;
    }
}
