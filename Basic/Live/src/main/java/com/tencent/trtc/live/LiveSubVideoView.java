package com.tencent.trtc.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class LiveSubVideoView extends FrameLayout {

    private SurfaceView         mSurfaceView;
    private Button              mButtonMuteAudio;
    private Button              mButtonMuteVideo;
    private LiveSubViewListener mListener;
    private LinearLayout mVideoMuteTipsView;

    public LiveSubVideoView(Context context) {
        super(context);
    }

    public LiveSubVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.live_sub_view_layout, this);
        mSurfaceView = findViewById(R.id.surface_view);
        mVideoMuteTipsView = findViewById(R.id.ll_mute_tips_video);
        mButtonMuteVideo = findViewById(R.id.btn_mute_remote_video);
        mButtonMuteAudio = findViewById(R.id.btn_mute_remote_audio);

        mButtonMuteAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMuteRemoteAudioClicked(view);
                }
            }
        });
        mButtonMuteVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMuteRemoteVideoClicked(view);
                }
            }
        });
        mButtonMuteVideo.setVisibility(View.GONE);
        mButtonMuteAudio.setVisibility(View.GONE);
    }

    public LiveSubVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SurfaceView getVideoView() {
        mButtonMuteVideo.setVisibility(View.VISIBLE);
        mButtonMuteAudio.setVisibility(View.VISIBLE);
        return mSurfaceView;
    }

    public LinearLayout getMuteVideoTips() {
        return mVideoMuteTipsView;
    }

    public void setLiveSubViewListener(LiveSubViewListener listener) {
        mListener = listener;
    }

    public interface LiveSubViewListener {
        void onMuteRemoteAudioClicked(View view);

        void onMuteRemoteVideoClicked(View view);
    }

}
