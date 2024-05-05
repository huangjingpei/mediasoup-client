package com.hiar.sdk.vslam;

/**
 * 描述hiar slam初始化方式
 */
public class HiarSlamInitType {

    /** 单帧初始化 */
    public static final int INIT_SINGLE = 0;

    /** 2D识别初始化 */
    public static final int INIT_2DRECOG = 1;

    /** 区域描述初始化 */
    public static final int INIT_USE_AREA_DESC = 2;

    /** 双帧初始化 */
    public static final int INIT_DOUBLE = 3;

    /** 地图集初始化 */
    public static final int INIT_ATLAS = 4;

    /** 模型边缘对齐初始化 */
    public static final int INIT_MODEL = 5;

    /** 3D识别初始化 */
    public static final int INIT_MODELREC = 6;
}
