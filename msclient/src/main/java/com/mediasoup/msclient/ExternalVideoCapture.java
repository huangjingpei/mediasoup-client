package com.mediasoup.msclient;

import android.content.Context;

import org.webrtc.CapturerObserver;
import org.webrtc.JavaI420Buffer;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class ExternalVideoCapture implements VideoCapturer {
    ArrayList<VideoFrame> videoFrameArrayList = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private CapturerObserver capturerObserver;
    private VideoFrame lastVideoFrame;

    private final Timer timer = new Timer();
    private final TimerTask tickTask = new TimerTask() {
        public void run() {
            ExternalVideoCapture.this.tick();
        }
    };
    public void tick() {
        VideoFrame videoFrame = PopVideoFrame();
        this.capturerObserver.onFrameCaptured(videoFrame);
        videoFrame.release();
    }
    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        this.capturerObserver = capturerObserver;

    }

    @Override
    public void startCapture(int width, int height, int frameRate) {
        lastVideoFrame = GenerateBlackFrame(1280, 720);
        this.timer.schedule(this.tickTask, 0L, (long)(1000 / frameRate));

    }

    @Override
    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    @Override
    public void changeCaptureFormat(int i, int i1, int i2) {

    }

    @Override
    public void dispose() {
        lock.lock();
        try {
            videoFrameArrayList.clear();
            if (lastVideoFrame != null) {
                lastVideoFrame.release();
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    public void PushVideoFrame(VideoFrame frame) {
        lock.lock();
        try {
            while (videoFrameArrayList.size() >= 3) {
                videoFrameArrayList.remove(0);
            }
            videoFrameArrayList.add(frame);
        } finally {
            lock.unlock();
        }

    }
    private VideoFrame PopVideoFrame() {
        VideoFrame frame = lastVideoFrame;
        lock.lock();
        try {
            if (videoFrameArrayList.size() == 0) {
                frame = videoFrameArrayList.remove(0);
            }
            lastVideoFrame = frame;
        } finally {
            lock.unlock();
        }
        return frame;
    }


    private VideoFrame GenerateBlackFrame(int frameWidth,int frameHeight)
    {
        JavaI420Buffer newBuffer = JavaI420Buffer.allocate(frameWidth,frameHeight);
        VideoFrame.I420Buffer buf420 = newBuffer.toI420();

        ByteBuffer dataY = newBuffer.getDataY();
        dataY.put(dataY.array(),0x10,frameWidth*frameHeight);

        ByteBuffer dataU = newBuffer.getDataU();
        dataU.put(dataU.array(),0x80,frameWidth*frameHeight/4);

        ByteBuffer dataV = newBuffer.getDataV();
        dataV.put(dataV.array(),0x80,frameWidth*frameHeight/4);

        VideoFrame frameNew= new VideoFrame(newBuffer,90,System.currentTimeMillis());
        buf420.release();
        return  frameNew;
    }

    private static class LazyHolder {
        private static final ExternalVideoCapture INSTANCE = new ExternalVideoCapture();
    }
    public static ExternalVideoCapture getInstance() {
        return LazyHolder.INSTANCE;
    }

}
