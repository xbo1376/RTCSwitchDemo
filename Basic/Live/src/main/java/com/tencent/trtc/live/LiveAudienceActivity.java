package com.tencent.trtc.live;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basic.TRTCBaseActivity;
import com.tencent.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.live.net.NetworkManager;
import com.tencent.trtc.live.rtc.RTCEngineFactory;
import com.tencent.trtc.live.rtc.RoomParams;
import com.tencent.trtc.live.rtc.RoomParams.EngineType;
import com.tencent.trtc.live.rtc.RTCEngine;
import com.tencent.trtc.live.tools.SPUtils;

public class LiveAudienceActivity extends TRTCBaseActivity implements View.OnClickListener {
    private static final String TAG = "LiveAudienceActivity";

    private SurfaceView surfase_view;
    private Button btn_mute_remote_audio;
    private NetworkManager.RoomInfo mRoomInfo;
    private RTCEngine engine;

    private String  mRemoteUserId;
    private boolean mMuteAudioFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_audience);
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

            EngineType engineType = EngineType.valueOf(mRoomInfo.rtcType);
            engine = RTCEngineFactory.getEngine(engineType, this);
        }
    }

    private void initView() {
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tv_roomid = findViewById(R.id.tv_roomid);
        btn_mute_remote_audio = findViewById(R.id.btn_mute_remote_audio);
        surfase_view = findViewById(R.id.surfase_view);

        String roomId = mRoomInfo.roomId;
        tv_roomid.setText("RoomId: " + roomId);
        iv_back.setOnClickListener(this);
        btn_mute_remote_audio.setOnClickListener(this);


        engine.setRtcListener(new RTCEngine.RTCListener(){

            @Override
            public void onEnterRoom(long result) {

            }

            @Override
            public void onExitRoom(int reason) {

            }

            @Override
            public void onRemoteUserEnterRoom(String userId) {

            }

            @Override
            public void onRemoteUserLeaveRoom(String userId, int reason) {

            }

            @Override
            public void onUserVideoAvailable(String userId, boolean available) {
                Log.d(TAG, "onUserVideoAvailable  available " + available + " userId " + userId);
                if (available) {
                    mRemoteUserId = userId;
                    engine.startRemoteVideo(mRemoteUserId, surfase_view);
                } else {
                    mRemoteUserId = "";
                    engine.stopRemoteVideo(mRemoteUserId);
                }
            }

            @Override
            public void onUserAudioAvailable(String userId, boolean available) {

            }
        });

    }

    private void enterRoom() {
        if (engine == null)  return;

        String userId = SPUtils.getInstance(this).getString("userId", "");

        RoomParams roomParams = new RoomParams();
        roomParams.appId = GenerateTestUserSig.SDKAPPID + "";
        roomParams.roomId = mRoomInfo.roomId;
        roomParams.userId = userId;
        roomParams.role = RoomParams.Role.Audience;
        roomParams.token = GenerateTestUserSig.genTestUserSig(userId);

        Log.e(TAG, "enterRoom: roomParams " + roomParams.toString());

        engine.enterRoom(roomParams , RoomParams.RoomScene.Live);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_mute_remote_audio) {
            muteAudio();
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

    private void muteAudio() {
        mMuteAudioFlag = !mMuteAudioFlag;
        if (mMuteAudioFlag) {
            if (!TextUtils.isEmpty(mRemoteUserId)) {
                engine.muteRemoteAudio(mRemoteUserId, true);
            }
            btn_mute_remote_audio.setText(getString(R.string.live_unmute));
        } else {
            if (!TextUtils.isEmpty(mRemoteUserId)) {
                engine.muteRemoteAudio(mRemoteUserId, false);
            }
            btn_mute_remote_audio.setText(getString(R.string.live_mute));
        }
    }

    @Override
    protected void onPermissionGranted() {
        initView();
        enterRoom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        engine.exitRoom();
    }

}
