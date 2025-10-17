package com.tencent.trtc.live.rtc;

import android.content.Context;
import android.view.SurfaceView;

import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

public class TRTCEngineImpl extends BaseRTCEngine {

    private static final String TAG = "TRTCEngineImpl";

    private TRTCCloud mTRTCCloud;
    private RoomParams.Quality quality;

    public TRTCEngineImpl(Context context) {
        mTRTCCloud = TRTCCloud.sharedInstance(context.getApplicationContext());
    }

    @Override
    public RTCEngine create() {
        mTRTCCloud.setListener(new TRTCCloudImplListener());
        return null;
    }

    @Override
    public void destroy() {
        if (mTRTCCloud == null) return;

        mTRTCCloud.setListener(null);
        mTRTCCloud = null;

        TRTCCloud.destroySharedInstance();
    }

    @Override
    public void enterRoom(RoomParams roomParams , RoomParams.RoomScene scene) {

        if (mTRTCCloud == null) return;

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.strRoomId = roomParams.roomId;
        trtcParams.role = roomParams.role == RoomParams.Role.Anchor ? TRTCCloudDef.TRTCRoleAnchor : TRTCCloudDef.TRTCRoleAudience;
        trtcParams.userId = roomParams.userId;
        trtcParams.sdkAppId = Integer.parseInt(roomParams.appId);
        trtcParams.userSig = roomParams.token;
        int structScene = scene == RoomParams.RoomScene.Live ? TRTCCloudDef.TRTC_APP_SCENE_LIVE : TRTCCloudDef.TRTC_APP_SCENE_VOICE_CHATROOM;
        mTRTCCloud.enterRoom(trtcParams ,structScene);
    }

    @Override
    public void exitRoom() {
        if (mTRTCCloud == null) return;

        mTRTCCloud.stopLocalPreview();
        mTRTCCloud.stopLocalAudio();
        mTRTCCloud.exitRoom();
    }

    @Override
    public void startLocalVideo(boolean frontCamera, SurfaceView surfaceView) {
        if (mTRTCCloud == null) return;

        TXCloudVideoView txCloudVideoView = new TXCloudVideoView(surfaceView);
        mTRTCCloud.startLocalPreview(frontCamera ,txCloudVideoView);
    }

    @Override
    public void stopLocalVideo() {
        if (mTRTCCloud == null) return;

        mTRTCCloud.stopLocalPreview();
    }


    @Override
    public void setAudioQuality(RoomParams.Quality quality) {
        if (mTRTCCloud == null) return;

        this.quality = quality;
    }

    @Override
    public void setVideoEncoderParam(VideoEncoderParam param) {
        if (mTRTCCloud == null) return;
        if (param == null) return;

        TRTCCloudDef.TRTCVideoEncParam trtcVideoEncParam = new TRTCCloudDef.TRTCVideoEncParam();
        trtcVideoEncParam.videoBitrate = param.videoBitrate;
        trtcVideoEncParam.videoFps = param.videoBitrate;

        int videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720;

        switch (param.videoResolution){
            case VideoEncoderParam.VIDEO_RESOLUTION_640_360:
                videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
                break;
            case VideoEncoderParam.VIDEO_RESOLUTION_1280_720:
                videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720;
                break;
            case VideoEncoderParam.VIDEO_RESOLUTION_1920_1080:
                videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080;
                break;
        }
        trtcVideoEncParam.videoResolution = videoResolution;

        int videoResolutionMode = param.videoResolutionMode == VideoEncoderParam.VIDEO_RESOLUTION_MODE_LANDSCAPE ? TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE : TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;

        trtcVideoEncParam.videoResolutionMode = videoResolutionMode;

        mTRTCCloud.setVideoEncoderParam(trtcVideoEncParam);
    }

    @Override
    public void startLocalAudio() {
        if (mTRTCCloud == null) return;

        int trtc_quality = TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT;

        switch (quality){
            case Low:
                trtc_quality = TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH;
                break;
            case Medium:
                trtc_quality = TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT;
                break;
            case High:
                trtc_quality = TRTCCloudDef.TRTC_AUDIO_QUALITY_MUSIC;
                break;
        }
        mTRTCCloud.startLocalAudio(trtc_quality);
    }

    @Override
    public void stopLocalAudio() {
        if (mTRTCCloud == null) return;

        mTRTCCloud.stopLocalAudio();
    }

    @Override
    public void startRemoteVideo(String userId, SurfaceView surfaceView) {
        if (mTRTCCloud == null) return;

        TXCloudVideoView txCloudVideoView = new TXCloudVideoView(surfaceView);
        mTRTCCloud.startRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG,txCloudVideoView);
    }

    @Override
    public void stopRemoteVideo(String userId) {
        if (mTRTCCloud == null) return;

        mTRTCCloud.stopRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
    }

    @Override
    public void muteRemoteAudio(String userId, boolean mute) {
        if (mTRTCCloud == null) return;

        mTRTCCloud.muteRemoteAudio(userId, mute);
    }

    @Override
    public void muteRemoteVideo(String userId, boolean mute) {
        if (mTRTCCloud == null) return;

        mTRTCCloud.muteRemoteVideoStream(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, mute);
    }

    @Override
    public void switchCamera(boolean isFrontCamera) {
        if (mTRTCCloud == null) return;
        mTRTCCloud.getDeviceManager().switchCamera(isFrontCamera);
    }


    class TRTCCloudImplListener extends TRTCCloudListener {

        @Override
        public void onEnterRoom(long result) {
            super.onEnterRoom(result);

           if (rtcListener != null) {
                rtcListener.onEnterRoom(result);
            }
        }

        @Override
        public void onExitRoom(int reason) {
            super.onExitRoom(reason);

            if (rtcListener != null) {
                rtcListener.onExitRoom(reason);
            }
        }

        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            super.onUserVideoAvailable(userId, available);

            if (rtcListener != null) {
                rtcListener.onUserVideoAvailable(userId, available);
            }
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean available) {
            super.onUserAudioAvailable(userId, available);

            if (rtcListener != null) {
                rtcListener.onUserAudioAvailable(userId, available);
            }
        }


        @Override
        public void onRemoteUserEnterRoom(String userId) {
            super.onRemoteUserEnterRoom(userId);
            if (rtcListener != null) {
                rtcListener.onRemoteUserEnterRoom(userId);
            }
        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            super.onRemoteUserLeaveRoom(userId, reason);

            if (rtcListener != null) {
                rtcListener.onRemoteUserLeaveRoom(userId, reason);
            }
        }

    }


}
