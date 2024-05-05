package com.hiar.sdk.vslam;

/**
 * Image format
 */
public class HiarSlamImageFormat {

    /** grayscale, 8-bit per pixel */
    public static final int GRAY = 0;

    /** Planar YUV420 with interleaved VU, each channel is 8-bit. */
    public static final int NV21 = 1;

    /** interleaved BGR (in that order in memory), each channel is 8-bit. */
    public static final int BGR =  2;

    /** interleaved BGRA (in that order in memory), each channel is 8-bit. */
    public static final int BGRA = 3;

    /** interleaved RGB (in that order in memory), each channel is 8-bit. */
    public static final int RGB = 4;

    /**interleaved RGBA (in that order in memory), each channel is 8-bit. */
    public static final int RGBA = 5;

    /** Planar YUV420 with interleaved UV, each channel is 8-bit. */
    public static final int NV12 = 10;
}
