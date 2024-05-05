package com.hiar.sdk.vslam;

public class HiarSlamPose {

    /**
     * camera pose, row major
     */
    public float[] extrinsics = new float[16];

    /**
     * camera view matrix, col major
     */
    public float[] viewMatrix = new float[16];

    /**
     * tracking state
     */
    public int[] trackingState = new int[1];

    /**
     * @return Returns the current tracking state of sdk, 参考{@link HiarSlamState}
     */
    public int getTrackingState() {
        return trackingState[0];
    }
}
