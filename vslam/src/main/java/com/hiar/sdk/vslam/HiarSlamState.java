package com.hiar.sdk.vslam;

/**
 * 描述hiar slam当前的跟踪状态
 */
public class HiarSlamState {

    /** sdk未初始化 */
    public static final int WAIT_FOR_INIT = -1;

    /** sdk没有发现识别图 */
    public static final int WAIT_FOR_RECOG_IMAGE = 0;

    /** sdk未开始工作 */
    public static final int START = 1;

    /** sdk定位成功 */
    public static final int SUCCESSFUL = 2;

    /** sdk丢失定位 */
    public static final int LOST = 3;
}

