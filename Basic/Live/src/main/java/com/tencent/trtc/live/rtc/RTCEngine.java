package com.tencent.trtc.live.rtc;

import android.view.SurfaceView;

public interface RTCEngine {
    abstract void init();
    abstract void destroy();
    abstract void setRtcListener(RTCListener rtcListener);
    abstract void enterRoom(RoomParams roomParams , RoomParams.RoomScene scene);
    abstract void exitRoom();
    abstract void setAudioQuality(RoomParams.Quality quality);
    abstract void setVideoEncoderParam(VideoEncoderParam param);
    abstract void startLocalVideo(boolean frontCamera, SurfaceView surfaceView);
    abstract void stopLocalVideo();
    abstract void startLocalAudio();
    abstract void stopLocalAudio();
    abstract void startRemoteVideo(String userId, SurfaceView surfaceView);
    abstract void stopRemoteVideo(String userId);
    abstract void muteRemoteAudio(String userId, boolean mute);
    abstract void muteRemoteVideo(String userId, boolean mute);
    abstract void switchCamera(boolean isFrontCamera);

    public static interface RTCListener {
        void onEnterRoom(long result);
        void onExitRoom(int reason);
        void onRemoteUserEnterRoom(String userId);
        void onRemoteUserLeaveRoom(String userId, int reason);
        void onUserVideoAvailable(String userId, boolean available);
        void onUserAudioAvailable(String userId, boolean available);
    }

}
