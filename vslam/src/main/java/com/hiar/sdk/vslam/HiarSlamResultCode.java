package com.hiar.sdk.vslam;

/**
 * Return code indicating success or failure of a function
 */
public class HiarSlamResultCode {

    /** no error, success */
    public static final int OK = 0;

    /** one or more pointer parameters are empty */
    public static final int NULL_PTR = -1;

    /** one or more parameters are invalid */
    public static final int INVALID_PARAM = -2;

    /** out of bounds access */
    public static final int OUT_OF_BOUNDARY = -3;

    /** insufficient memory */
    public static final int OUT_OF_MEMORY = -4;

    /** current state is not supported yet */
    public static final int UNSUPPORTED = -5;

    /** hiar slam inits failed */
    public static final int INIT_FAILURE = -6;

    /** hiar slam sets mode failed */
    public static final int SET_MODE_FAILURE = -7;

    /** all other unlised errors */
    public static final int UNKNOWN = -8;

    /**  hiar slam api function failed */
    public static final int FAILURE = -9;
}
