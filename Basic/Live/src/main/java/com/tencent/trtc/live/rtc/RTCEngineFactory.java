package com.tencent.trtc.live.rtc;

import android.content.Context;

public class RTCEngineFactory {

    // Get the RTC engine and get different engines according to EngineType
    public static RTCEngine getEngine(RoomParams.EngineType type , Context context) {
        return createEngine(type , context);
    }

    private static RTCEngine createEngine(RoomParams.EngineType type, Context context) {
        switch (type) {
            case TRTC:
                return new TRTCEngineImpl(context);
            case Agora:
                return new AgoraEngineImpl(context);
        }
        return new TRTCEngineImpl(context);
    }
}
