package com.tencent.trtc.live.rtc;

public abstract class BaseRTCEngine implements RTCEngine {

    protected RTCListener rtcListener;

    @Override
    public void setRtcListener(RTCListener rtcListener) {
        this.rtcListener = rtcListener;
    }

}
