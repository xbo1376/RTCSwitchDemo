package com.tencent.trtc.live.rtc;

import android.content.Context;
import android.view.SurfaceView;

public class AgoraEngineImpl extends BaseRTCEngine {
    private static final String TAG = "AgoraEngineImpl";

    public AgoraEngineImpl(Context context) {

    }

    @Override
    public RTCEngine create() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void enterRoom(RoomParams roomParams, RoomParams.RoomScene scene) {

    }

    @Override
    public void exitRoom() {

    }

    @Override
    public void setAudioQuality(RoomParams.Quality quality) {

    }

    @Override
    public void setVideoEncoderParam(VideoEncoderParam param) {

    }

    @Override
    public void startLocalVideo(boolean frontCamera, SurfaceView surfaceView) {

    }

    @Override
    public void stopLocalVideo() {

    }

    @Override
    public void startLocalAudio() {

    }

    @Override
    public void stopLocalAudio() {

    }

    @Override
    public void startRemoteVideo(String userId, SurfaceView surfaceView) {

    }

    @Override
    public void stopRemoteVideo(String userId) {

    }

    @Override
    public void muteRemoteAudio(String userId, boolean mute) {

    }

    @Override
    public void muteRemoteVideo(String userId, boolean mute) {

    }

    @Override
    public void switchCamera(boolean isFrontCamera) {

    }
}
