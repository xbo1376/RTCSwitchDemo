package com.tencent.trtc.live.rtc;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.tencent.trtc.live.Config;


import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class AgoraEngineImpl extends BaseRTCEngine {
    private static final String TAG = "AgoraEngineImpl";


    private RtcEngine mRtcEngine;
    private RoomParams.Quality mQuality = RoomParams.Quality.Medium;

    public AgoraEngineImpl(Context context) {
        // 创建 RtcEngineConfig 对象，并进行配置
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = context.getApplicationContext();
        config.mAppId = Config.AGORA_APPID;
        config.mEventHandler = mRtcEventHandler;
        config.mLogConfig.level = Constants.LOG_FILTER_DEBUG;

        try {
            // 创建并初始化 RtcEngine
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            Log.e(TAG, "AgoraEngineImpl: failed to create RtcEngine", e);
            e.printStackTrace();
        }
        init();
    }

    @Override
    public void init() {


    }

    @Override
    public void destroy() {
        if (mRtcEngine == null) return;

        // 停止本地视频预览
        mRtcEngine.stopPreview();
        // 离开频道
        mRtcEngine.leaveChannel();
        mRtcEngine = null;
        // 销毁引擎
        RtcEngine.destroy();

    }

    @Override
    public void enterRoom(RoomParams roomParams, RoomParams.RoomScene scene) {

        // 创建 ChannelMediaOptions 对象，并进行配置
        ChannelMediaOptions options = new ChannelMediaOptions();
        // 设置用户角色为 BROADCASTER (主播) 或 AUDIENCE (观众)
        options.clientRoleType = roomParams.role == RoomParams.Role.Anchor ? Constants.CLIENT_ROLE_BROADCASTER : Constants.CLIENT_ROLE_AUDIENCE;
        // 设置频道场景为 BROADCASTING (直播场景)
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        // 发布麦克风采集的音频
        options.publishMicrophoneTrack = true;
        // 发布摄像头采集的视频
        options.publishCameraTrack = true;
        // 自动订阅所有音频流
        options.autoSubscribeAudio = true;
        // 自动订阅所有视频流
        options.autoSubscribeVideo = true;

        int quality = Constants.AudioProfile.getValue(Constants.AudioProfile.MUSIC_STANDARD);

        switch (mQuality) {
            case Low:
                quality = Constants.AudioProfile.getValue(Constants.AudioProfile.SPEECH_STANDARD);
                break;
            case Medium:
                quality = Constants.AudioProfile.getValue(Constants.AudioProfile.MUSIC_STANDARD);
                break;
            case High:
                quality = Constants.AudioProfile.getValue(Constants.AudioProfile.MUSIC_HIGH_QUALITY_STEREO);
                break;
        }
        // 设置音频质量
        Log.e(TAG, "enterRoom: mRtcEngine = " + mRtcEngine);
        mRtcEngine.setAudioProfile(quality);

        // 使用临时 Token 和频道名加入频道，uid 为 0 表示引擎内部随机生成用户名
        // 成功后会触发 onJoinChannelSuccess 回调
        mRtcEngine.joinChannel(roomParams.token, roomParams.roomId, Integer.parseInt(roomParams.userId), options);
    }

    @Override
    public void exitRoom() {

        if (mRtcEngine == null) return;

        mRtcEngine.disableAudio();
        // 停止本地视频预览
        mRtcEngine.stopPreview();
        mRtcEngine.disableVideo();
        // 离开频道
        mRtcEngine.leaveChannel();

    }

    @Override
    public void setAudioQuality(RoomParams.Quality quality) {
        this.mQuality = quality;
    }

    @Override
    public void setVideoEncoderParam(VideoEncoderParam param) {
        if (mRtcEngine == null || param == null) return;
        // 设置视频编码参数

        VideoEncoderConfiguration videoEncoderConfiguration = new VideoEncoderConfiguration();

        // 根据参数设置视频分辨率
        VideoEncoderConfiguration.VideoDimensions dimensions = VideoEncoderConfiguration.VD_1280x720;
        switch (param.videoResolution) {
            case VideoEncoderParam.VIDEO_RESOLUTION_640_360:
                dimensions = VideoEncoderConfiguration.VD_640x360;
                break;
            case VideoEncoderParam.VIDEO_RESOLUTION_1280_720:
                dimensions = VideoEncoderConfiguration.VD_1280x720;
                break;
            case VideoEncoderParam.VIDEO_RESOLUTION_1920_1080:
                dimensions = VideoEncoderConfiguration.VD_1920x1080;
                break;
        }

        videoEncoderConfiguration.dimensions = dimensions;
        videoEncoderConfiguration.bitrate = param.videoBitrate;
        videoEncoderConfiguration.frameRate = param.videoFps;
        videoEncoderConfiguration.orientationMode = param.videoResolutionMode == VideoEncoderParam.VIDEO_RESOLUTION_MODE_PORTRAIT ? VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT : VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE;

        mRtcEngine.setVideoEncoderConfiguration(videoEncoderConfiguration);
    }

    @Override
    public void startLocalVideo(boolean frontCamera, SurfaceView surfaceView) {
        // 启用视频模块
        mRtcEngine.enableVideo();
        // 将 SurfaceView 对象传入声网实时互动 SDK，设置本地视图
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        // 开启本地预览
        mRtcEngine.startPreview();
    }

    @Override
    public void stopLocalVideo() {
        if (mRtcEngine == null) return;

        // 停止本地视频预览
        mRtcEngine.stopPreview();
    }

    @Override
    public void startLocalAudio() {
        if (mRtcEngine == null) return;

        // 发布本地音频
        mRtcEngine.enableAudio();
    }

    @Override
    public void stopLocalAudio() {
        if (mRtcEngine == null) return;

        // 停止发布本地音频
        mRtcEngine.disableAudio();
    }

    @Override
    public void startRemoteVideo(String userId, SurfaceView surfaceView) {
        surfaceView.setZOrderMediaOverlay(true);
        // 将 SurfaceView 对象传入声网实时互动 SDK，设置远端视图
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, Integer.parseInt(userId)));
    }

    @Override
    public void stopRemoteVideo(String userId) {
        if (mRtcEngine == null) return;

        // 停止订阅远端视频流
        mRtcEngine.muteRemoteVideoStream(Integer.parseInt(userId), true);
    }

    @Override
    public void muteRemoteAudio(String userId, boolean mute) {
        if (mRtcEngine == null) return;

        mRtcEngine.muteRemoteAudioStream(Integer.parseInt(userId), mute);
    }

    @Override
    public void muteRemoteVideo(String userId, boolean mute) {
        if (mRtcEngine == null) return;

        mRtcEngine.muteRemoteVideoStream(Integer.parseInt(userId), mute);
    }

    private boolean isFrontCamera = true;

    @Override
    public void switchCamera(boolean isFrontCamera) {

        if (mRtcEngine == null) return;

        if (isFrontCamera != this.isFrontCamera) {
            mRtcEngine.switchCamera();
            this.isFrontCamera = !isFrontCamera;
        }

    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);

            if (rtcListener != null) {
                rtcListener.onEnterRoom(elapsed);
            }
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);

            if (rtcListener != null) {
                rtcListener.onExitRoom(0);
            }
        }

        @Override
        public void onUserMuteAudio(int uid, boolean mute) {
            super.onUserMuteAudio(uid, mute);

            if (rtcListener != null) {
                rtcListener.onUserAudioAvailable(uid + "", !mute);
            }
        }

        @Override
        public void onUserMuteVideo(int uid, boolean mute) {
            super.onUserMuteVideo(uid, mute);

            if (rtcListener != null) {
                rtcListener.onUserVideoAvailable(uid + "", !mute);
            }
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);

            if (rtcListener != null) {
                rtcListener.onRemoteUserEnterRoom(uid + "");
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);

            if (rtcListener != null) {
                rtcListener.onRemoteUserLeaveRoom(uid + "", reason);
            }

        }
    };

}
