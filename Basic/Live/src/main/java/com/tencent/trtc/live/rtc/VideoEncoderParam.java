package com.tencent.trtc.live.rtc;

public class VideoEncoderParam {

    // 视频分辨率常量
    public static final int VIDEO_RESOLUTION_640_360 = 0;
    public static final int VIDEO_RESOLUTION_1280_720 = 1;
    public static final int VIDEO_RESOLUTION_1920_1080 = 2;

    // 视频分辨率模式常量
    public static final int VIDEO_RESOLUTION_MODE_PORTRAIT = 0;
    public static final int VIDEO_RESOLUTION_MODE_LANDSCAPE = 1;

    public int videoBitrate = 1200;
    public int videoFps = 15;
    public int videoResolution = VIDEO_RESOLUTION_1280_720;
    public int videoResolutionMode = VIDEO_RESOLUTION_MODE_PORTRAIT;

}
