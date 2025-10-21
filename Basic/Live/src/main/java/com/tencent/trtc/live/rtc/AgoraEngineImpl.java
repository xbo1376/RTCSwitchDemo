package com.tencent.trtc.live.rtc;

import static io.agora.rtc2.Constants.AUDIO_SCENARIO_DEFAULT;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.tencent.trtc.live.Config;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rte.VideoMirrorMode;
import io.agora.rte.VideoRenderMode;


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
            // 打开日志调试
            mRtcEngine.setParameters("{\"parameters\":{\"rtc.debug.enable\": true}}");
            // 设置日志级别
            mRtcEngine.setLogFilter(Constants.LOG_FILTER_DEBUG);
            // 获取应用内部缓存目录 (Context.getCacheDir())
            String internalCacheDir = context.getCacheDir().getAbsolutePath();
            mRtcEngine.setLogFile(internalCacheDir + "/agora_log.txt"); // 设置日志文件路径
            mRtcEngine.setLogFileSize(2048); // 设置日志文件大小(KB)
        } catch (Exception e) {
            Log.e(TAG, "AgoraEngineImpl: failed to create RtcEngine", e);
            e.printStackTrace();
        }
        init();
    }

    @Override
    public void init() {
        if (mRtcEngine == null) return;

        // 启用视频模块（总开关）
        mRtcEngine.enableVideo();
        // 启用音频模块（总开关）
        mRtcEngine.enableAudio();
    }

    @Override
    public void destroy() {
        if (mRtcEngine == null) return;

        exitRoom();
        mRtcEngine.removeHandler(mRtcEventHandler);
        mRtcEngine = null;
        // 销毁引擎
        RtcEngine.destroy();
    }

    @Override
    public void enterRoom(RoomParams roomParams, RoomParams.RoomScene scene) {
        if (mRtcEngine == null) return;

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
        mRtcEngine.setAudioProfile(quality, AUDIO_SCENARIO_DEFAULT);
        // 设置频道场景为 BROADCASTING (直播场景)
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        // 设置用户角色为 BROADCASTER (主播) 或 AUDIENCE (观众)
        mRtcEngine.setClientRole(roomParams.role == RoomParams.Role.Anchor ? Constants.CLIENT_ROLE_BROADCASTER : Constants.CLIENT_ROLE_AUDIENCE);
        // 使用临时 Token 和频道名加入频道，uid 为 0 表示引擎内部随机生成用户名
        // 成功后会触发 onJoinChannelSuccess 回调
        mRtcEngine.joinChannel(roomParams.token, roomParams.roomId, null, Integer.parseInt(roomParams.userId));
    }

    @Override
    public void exitRoom() {
        if (mRtcEngine == null) return;

        // 关闭音视频采集
        mRtcEngine.disableAudio();
        mRtcEngine.disableVideo();

        // 停止本地视频预览
        mRtcEngine.stopPreview();
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
                dimensions = new VideoEncoderConfiguration.VideoDimensions(1920, 1080);
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
        if (mRtcEngine == null) return;

        // 设置视频采集状态
        mRtcEngine.enableLocalVideo(true);

        // 设置本地渲染视图
        VideoCanvas videoCanvas = new VideoCanvas(surfaceView);

        mRtcEngine.setLocalRenderMode(
                VideoRenderMode.VIDEO_RENDER_MODE_FIT,  // 渲染模式
                VideoMirrorMode.VIDEO_MIRROR_MODE_AUTO  // 镜像模式
        );

        // 开启本地预览
        mRtcEngine.setupLocalVideo(videoCanvas);
        mRtcEngine.startPreview();
    }

    @Override
    public void stopLocalVideo() {
        if (mRtcEngine == null) return;

        // 设置视频采集状态
        mRtcEngine.enableLocalVideo(false);

        // 停止本地视频预览
        mRtcEngine.stopPreview();
        mRtcEngine.disableVideo();
    }

    @Override
    public void startLocalAudio() {
        if (mRtcEngine == null) return;

        // 发布本地音频
        mRtcEngine.enableLocalAudio(true);
    }

    @Override
    public void stopLocalAudio() {
        if (mRtcEngine == null) return;

        // 停止发布本地音频
        mRtcEngine.enableLocalAudio(false);
    }

    @Override
    public void startRemoteVideo(String userId, SurfaceView surfaceView) {
        if (mRtcEngine == null) return;

        if (surfaceView != null) {
            surfaceView.setZOrderMediaOverlay(true);
        }

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

    @Override
    public String getRTCType() {
        return RoomParams.EngineType.Agora.getValue();
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
