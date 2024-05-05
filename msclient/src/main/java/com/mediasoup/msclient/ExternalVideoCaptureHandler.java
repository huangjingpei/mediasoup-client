package com.mediasoup.msclient;

import org.webrtc.VideoFrame;

public interface ExternalVideoCaptureHandler {
    void onVideoFrame(VideoFrame frame);
}
