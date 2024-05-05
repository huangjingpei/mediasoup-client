package com.mediasoup.msclient.vslam;

import android.util.Log;

import com.hiar.sdk.vslam.AlgWrapper;
import com.hiar.sdk.vslam.HiarSlamImageFormat;
import com.hiar.sdk.vslam.HiarSlamInitType;
import com.hiar.sdk.vslam.HiarSlamMode;
import com.hiar.sdk.vslam.HiarSlamPose;
import com.hiar.sdk.vslam.HiarSlamResultCode;
import com.hiar.sdk.vslam.HiarSlamState;

import java.io.File;

public class SlamAlgInstance {


    HiarSlamPose pose = new HiarSlamPose();
    AlgWrapper slam;
    SlamProcessListener processListener;
    boolean needProcess = false;
    int nSlamInitType = HiarSlamInitType.INIT_SINGLE; //选择sdk初始化方法
    int nSlamMode = HiarSlamMode.BALANCE; //选择sdk工作模式


    String caoName;
    String dbFileName;
    String datFileName;

    String slamResPath;


    public SlamProcessListener getProcessListener() {
        return processListener;
    }

    public void setProcessListener(SlamProcessListener processListener) {
        this.processListener = processListener;
    }

    public float[] getMatRT() {
        return pose.extrinsics;
    }

    public void setSlamInitType(int nSlamInitType){
        this.nSlamInitType = nSlamInitType;
    }

    public boolean isNeedProcess() {
        return needProcess;
    }

    public void setNeedProcess(boolean needProcess) {
        this.needProcess = needProcess;
    }
    private SlamAlgInstance() {

    }
    public boolean create(int initType_,
                       int mode_,
                       String slamResPath_,
                       String caoName_,
                       String dbFileName_,
                       String datFileName_) {
        int nRtn;

        slamResPath = slamResPath_;
        nSlamInitType = initType_;
        nSlamMode = mode_;
        caoName = caoName_;
        dbFileName = dbFileName_;
        datFileName = datFileName_;

        slam = new AlgWrapper();
        slam.SetVocFilePath(slamResPath_ + File.separator + "HiARVoc.dat");
        String initFilePath = slamResPath + File.separator +
                "HiARRecog.db";
        switch (nSlamInitType) {
            case HiarSlamInitType.INIT_SINGLE:
            case HiarSlamInitType.INIT_DOUBLE:
                initFilePath = null;
                break;
            case HiarSlamInitType.INIT_2DRECOG:
                initFilePath = slamResPath + File.separator + dbFileName;
                break;
            case HiarSlamInitType.INIT_USE_AREA_DESC:
                initFilePath=slamResPath+File.separator+"point_cloud"
                        +File.separator+datFileName;
                break;
            case HiarSlamInitType.INIT_MODEL:
                initFilePath = slamResPath + File.separator +  caoName;
                break;
        }
        nRtn = slam.Create(nSlamInitType, initFilePath, nSlamMode);

        if (nRtn == HiarSlamResultCode.OK) {
            //HiarSdk初始化成功
            //获取OpenGL坐标系下的Projection Matrix
//            slam.ConvertCameraIntrinsicsToGL(myGLSurfaceView.mfRenderingNearPlane, myGLSurfaceView
//                    .mfRenderingFarPlane, myGLSurfaceView.mfRenderingProjectionMatrix);
//            myGLSurfaceView.setProjection();
//            Get3DLine();
            return true;
        }
        return false;
    }

    public void dispose() {

    }

    public void setPreferredSystemCameraIntrinsics(int width, int height) {
        if (slam != null) {

        }
    }

    private String getStatusString(int nRtn, int state) {
        String trackingState;

        if (nRtn < 0)
            trackingState = "Error in calling Process";
        else
        {
            switch (state)
            {
                case HiarSlamState.WAIT_FOR_RECOG_IMAGE:
                    trackingState = "Wait for Recog";
                    break;
                case HiarSlamState.START:
                    trackingState = "Not initialized";
                    break;
                case HiarSlamState.SUCCESSFUL:
                    trackingState = "Tracking";
                    break;
                case HiarSlamState.LOST:
                    trackingState = "Lost";
                    break;
                default:
                    trackingState = "<wrong>";
                    break;
            }
        }
        return trackingState;
    }

    public void onNewFrame(byte[] data, int width, int height) {

        if (needProcess) {
            boolean bRtn;
            int nRtn;

            Long time_begin = System.currentTimeMillis();
            //处理当前帧，获取设备姿态信息
            Log.d("SLAM","hello,"+pose.extrinsics[0]+","+pose.extrinsics[1]+","+pose.extrinsics[2]+","+pose.extrinsics[3]
                    +","+pose.extrinsics[4] +","+pose.extrinsics[5]+","+pose.extrinsics[6]+","+pose.extrinsics[7]
                    +","+pose.extrinsics[8] +","+pose.extrinsics[9]+","+pose.extrinsics[10]+","+pose.extrinsics[11]
                    +","+pose.extrinsics[12] +","+pose.extrinsics[13]+","+pose.extrinsics[14]+","+pose.extrinsics[15]);
            nRtn = slam.ProcessFrame(data, HiarSlamImageFormat.NV12, pose);
            final float processTime = System.currentTimeMillis() - time_begin;
            final int fps = (int)(1000.0f / processTime);
            final String trackingState = getStatusString(nRtn, pose.getTrackingState());

            if(nSlamInitType == HiarSlamInitType.INIT_2DRECOG){
                int[] index = new int[1];
                int[] imageWidth = new int[1];
                int[] imageHeight = new int[1];
                slam.GetInitModelInfo(index, imageWidth, imageHeight);
                processListener.setWidthAndHeight(imageWidth[0], imageHeight[0]);
            }

            bRtn = (nRtn == 0);

            processListener.onDebugInfo("Fps:" + fps, "processTime:" + processTime, trackingState.equals("Lost") == true);

            //将姿态信息转换为OpenGL坐标系下的ModelView Matrix
            nRtn = slam.ConvertCameraExtrinsicsToGL(pose.extrinsics, pose.viewMatrix);
            processListener.onProcess(pose.viewMatrix, pose.getTrackingState());
        }

        float[] points = slam.GetPointCloudPoints();
        if (points != null && pose.getTrackingState() == HiarSlamState.SUCCESSFUL) {
            processListener.onPointCloud(points, true);
        } else {
            processListener.onPointCloud(null, false);
        }

    }
//
//    @Override
//    public void onVideoFrame(VideoFrame frame) {
//        VideoFrame.I420Buffer i420 = frame.getBuffer().toI420();
//        onNewFrame(i420.getDataY().array(), i420.getWidth(), i420.getHeight());
//        i420.release();
//    }


    private static class LazyHolder {
        private static final SlamAlgInstance INSTANCE = new SlamAlgInstance();
    }
    public static SlamAlgInstance getInstance() {
        return SlamAlgInstance.LazyHolder.INSTANCE;
    }



}
