package com.mediasoup.msclient.vslam;

/**
 * Created by li on 2017/3/13.
 */

public interface SlamProcessListener {
    void onProcess(float[] pose, int state);
    void onPointCloud(float[] points, boolean state);
    void setWidthAndHeight(int width, int height);
    void onDebugInfo(String fps, String processTime, boolean isLost);
}
