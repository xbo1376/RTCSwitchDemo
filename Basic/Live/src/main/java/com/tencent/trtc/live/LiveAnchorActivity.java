package com.tencent.trtc.live;

import static com.tencent.trtc.live.tools.SPUtils.getInstance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basic.TRTCBaseActivity;
import com.tencent.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.live.net.NetworkManager;
import com.tencent.trtc.live.rtc.RTCEngineFactory;
import com.tencent.trtc.live.rtc.RoomParams;
import com.tencent.trtc.live.rtc.RTCEngine;
import com.tencent.trtc.live.rtc.VideoEncoderParam;
import com.tencent.trtc.live.tools.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class LiveAnchorActivity extends TRTCBaseActivity implements View.OnClickListener {
    private static final String TAG = "LiveAnchorActivity";
    private SurfaceView surfase_view;
    private Button btn_switch_camera;
    private Button btn_mute_video;
    private Button btn_mute_audio;
    private ImageView iv_back;
    private TextView tv_roomid;
    private boolean mIsFrontCamera = true;
    private List<String> mRemoteUidList;
    private List<LiveSubVideoView> mRemoteViewList;
    private boolean mMuteVideoFlag = true;
    private boolean mMuteAudioFlag = true;

    private NetworkManager.RoomInfo mRoomInfo;
    private RTCEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_anchor);
        getSupportActionBar().hide();

        initData();

        if (checkPermission()) {
            initView();
            enterRoom();
        }
    }

    private void initData() {
        Intent intent = getIntent();

        if (null == intent) return;

        if (intent.hasExtra("roomInfo")) {
            mRoomInfo = (NetworkManager.RoomInfo) intent.getSerializableExtra("roomInfo");
            Log.d(TAG, "roomInfo: " + mRoomInfo.toString());

            // 根据 roomInfo 中的 rtcType 来选择 RTC 引擎线路
            RoomParams.EngineType engineType = RoomParams.EngineType.valueOf(mRoomInfo.rtcType);
            engine = RTCEngineFactory.getEngine(engineType, this);
            engine.setRtcListener(new RTCListenerImpl());

            Log.d(TAG, "initData: engineType=" + engine.getRTCType());
            Toast.makeText(this, "rtc_type:" + engine.getRTCType(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void initView() {
        surfase_view = findViewById(R.id.surfase_view);
        tv_roomid = findViewById(R.id.tv_roomid);
        iv_back = findViewById(R.id.iv_back);
        btn_mute_video = findViewById(R.id.btn_mute_video);
        btn_mute_audio = findViewById(R.id.btn_mute_audio);
        btn_switch_camera = findViewById(R.id.btn_switch_camera);

        String roomId = mRoomInfo.roomId;
        tv_roomid.setText("RoomId: " + roomId + " " + engine.getRTCType());

        mRemoteUidList = new ArrayList<>();
        mRemoteViewList = new ArrayList<>();

        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_1));
        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_2));
        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_3));
        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_4));
        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_5));
        mRemoteViewList.add((LiveSubVideoView) findViewById(R.id.live_cloud_view_6));

        for (int index = 0; index < mRemoteViewList.size(); index++) {
            mRemoteViewList.get(index).setLiveSubViewListener(new LiveSubViewListenerImpl(index));
        }

        iv_back.setOnClickListener(this);
        btn_mute_video.setOnClickListener(this);
        btn_mute_audio.setOnClickListener(this);
        btn_switch_camera.setOnClickListener(this);
    }

    public void enterRoom() {
        if (engine == null)  return;

        VideoEncoderParam videoEncoderParam = new VideoEncoderParam();
        videoEncoderParam.videoBitrate = 1200;
        videoEncoderParam.videoFps = 15;
        videoEncoderParam.videoResolution = VideoEncoderParam.VIDEO_RESOLUTION_1280_720;
        videoEncoderParam.videoResolutionMode = VideoEncoderParam.VIDEO_RESOLUTION_MODE_PORTRAIT;

        engine.setVideoEncoderParam(videoEncoderParam);
        engine.startLocalVideo(mIsFrontCamera, surfase_view);
        engine.setAudioQuality(RoomParams.Quality.Medium);
        engine.startLocalAudio();

        String userId = SPUtils.getInstance(this).getString("userId", "");
        Log.e(TAG, "enterRoom: userId " + userId);

        RoomParams roomParams = new RoomParams();
        roomParams.roomId = mRoomInfo.roomId;
        roomParams.userId = userId;
        roomParams.role = RoomParams.Role.Anchor;

        // 根据引擎设置不同引擎的 token，这里只是测试使用，正式上线需要服务端下发对应引擎的 appid 和 token
        if (RoomParams.EngineType.Agora.getValue().equals(mRoomInfo.rtcType)) {
            roomParams.appId = Config.AGORA_APPID;
            roomParams.roomId = Config.CHANNEL_ID;
            roomParams.token = Config.AGORA_TOKEN;
        } else if (RoomParams.EngineType.TRTC.getValue().equals(mRoomInfo.rtcType)) {
            roomParams.appId = Config.TENCENT_SDKAPPID;
            roomParams.token = GenerateTestUserSig.genTestUserSig(Integer.parseInt(Config.TENCENT_SDKAPPID), Config.TENCENT_SECRETKEY, userId);
        }

        Log.e(TAG, "enterRoom: roomParams " + roomParams.toString());
        engine.enterRoom(roomParams , RoomParams.RoomScene.Live);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_mute_video) {
            muteVideo();
        } else if (id == R.id.btn_mute_audio) {
            muteAudio();
        } else if (id == R.id.btn_switch_camera) {
            switchCamera();
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

    protected void switchCamera() {
        if (mIsFrontCamera) {
            mIsFrontCamera = false;
            btn_switch_camera.setText(getString(R.string.live_user_front_camera));
        } else {
            mIsFrontCamera = true;
            btn_switch_camera.setText(getString(R.string.live_user_back_camera));
        }
        engine.switchCamera(mIsFrontCamera);
    }

    private void muteVideo() {
        if (mMuteVideoFlag) {
            mMuteVideoFlag = false;
            engine.stopLocalVideo();
            btn_mute_video.setText(getString(R.string.live_open_camera));
        } else {
            mMuteVideoFlag = true;
            engine.startLocalVideo(true,surfase_view);
            btn_mute_video.setText(getString(R.string.live_close_camera));
        }
    }

    private void muteAudio() {
        if (mMuteAudioFlag) {
            mMuteAudioFlag = false;
            engine.stopLocalAudio();
            btn_mute_audio.setText(getString(R.string.live_open_mic));
        } else {
            mMuteAudioFlag = true;
            engine.startLocalAudio();
            btn_mute_audio.setText(getString(R.string.live_close_mic));
        }
    }


    class  RTCListenerImpl implements RTCEngine.RTCListener {

        @Override
        public void onEnterRoom(long result) {
            Log.e(TAG, "onEnterRoom: result：" + result);
        }

        @Override
        public void onExitRoom(int reason) {
            Log.e(TAG, "onExitRoom: reason：" + reason);
        }

        @Override
        public void onRemoteUserEnterRoom(String userId) {
            Log.e(TAG, "onRemoteUserEnterRoom: userId：" + userId);
        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            Log.e(TAG, "onRemoteUserLeaveRoom: userId：" + userId + " reason：" + reason);
        }

        @Override
        public void onUserVideoAvailable(String remoteUserid, boolean available) {
            Log.e(TAG, "onUserVideoAvailable: remoteUserid：" + remoteUserid + " available：" + available);

            int index = mRemoteUidList.indexOf(remoteUserid);

            String userId = SPUtils.getInstance(LiveAnchorActivity.this).getString("userId", "");

            if (available) {
                if (index == -1 && !remoteUserid.equals(userId)) {
                    mRemoteUidList.add(remoteUserid);
                    refreshRemoteVideoViews();
                }
            } else {
                if (index != -1 && !remoteUserid.equals(userId)) {
                    engine.stopRemoteVideo(remoteUserid);
                    mRemoteUidList.remove(index);
                    refreshRemoteVideoViews();
                }
            }
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean available) {
            Log.e(TAG, "onUserAudioAvailable: userId：" + userId + " available：" + available);
        }

        private void refreshRemoteVideoViews() {
            for (int i = 0; i < mRemoteViewList.size(); i++) {
                LiveSubVideoView liveSubVideoView = mRemoteViewList.get(i);
                if (i < mRemoteUidList.size()) {
                    String remoteUid = mRemoteUidList.get(i);
                    liveSubVideoView.setVisibility(View.VISIBLE);
                    // 开始显示用户userId的视频画面
                    engine.startRemoteVideo(remoteUid, liveSubVideoView.getVideoView());
                } else {
                    liveSubVideoView.setVisibility(View.GONE);
                }
            }
        }
    }

    private class LiveSubViewListenerImpl implements LiveSubVideoView.LiveSubViewListener {

        private final int mIndex;

        public LiveSubViewListenerImpl(int index) {
            mIndex = index;
        }

        @Override
        public void onMuteRemoteAudioClicked(View view) {
            boolean isSelected = view.isSelected();
            if (!isSelected) {
                engine.muteRemoteAudio(mRemoteUidList.get(mIndex), true);
                view.setBackground(getResources().getDrawable(R.mipmap.live_subview_sound_mute));
            } else {
                engine.muteRemoteAudio(mRemoteUidList.get(mIndex), false);
                view.setBackground(getResources().getDrawable(R.mipmap.live_subview_sound_unmute));
            }
            view.setSelected(!isSelected);
        }

        @Override
        public void onMuteRemoteVideoClicked(View view) {
            boolean isSelected = view.isSelected();

            LiveSubVideoView liveSubVideoView = mRemoteViewList.get(mIndex);

            if (!isSelected) {
                engine.stopRemoteVideo(mRemoteUidList.get(mIndex));
                liveSubVideoView.getMuteVideoTips().setVisibility(View.VISIBLE);
                view.setBackground(getResources().getDrawable(R.mipmap.live_subview_video_mute));
            } else {
                engine.startRemoteVideo(mRemoteUidList.get(mIndex), liveSubVideoView.getVideoView());

                view.setBackground(getResources().getDrawable(R.mipmap.live_subview_video_unmute));
                liveSubVideoView.getMuteVideoTips().setVisibility(View.GONE);
            }
            view.setSelected(!isSelected);
        }
    }

    @Override
    protected void onDestroy() {
        engine.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPermissionGranted() {
        initView();
        enterRoom();
    }
}
