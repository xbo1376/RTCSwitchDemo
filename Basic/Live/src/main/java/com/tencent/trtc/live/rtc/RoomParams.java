package com.tencent.trtc.live.rtc;

public class RoomParams {

    public String appId;
    public String userId;
    public String roomId;
    public Role role;
    public String token;




    public enum Role {
        Anchor(1),
        Audience(2);

        Role(int i) {
            this.value = i;
        }
        private final int value;

        public int getValue() {
            return value;
        }
    }

    public enum RoomScene {
        Live(1),
        Audio(2);

        RoomScene(int i) {
            this.value = i;
        }
        private final int value;

        public int getValue() {
            return value;
        }
    }

    public enum Quality {
        High(1),
        Medium(2),
        Low(3);

        Quality(int i) {
            this.value = i;
        }
        private final int value;

        public int getValue() {
            return value;
        }
    }

    public enum EngineType {
        TRTC("TRTC"),
        Agora("Agora");

        EngineType(String name) {
            this.value = name;
        }
        private final String value;

        public String getValue() {
            return value;
        }
    }

}
