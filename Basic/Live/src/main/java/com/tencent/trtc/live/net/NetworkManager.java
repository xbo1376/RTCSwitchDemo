
package com.tencent.trtc.live.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Network request manager class
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private static NetworkManager instance;
    private OkHttpClient client;
    
    // Server address - please modify according to actual situation
    private static final String BASE_URL = "http://175.27.215.206:8376";
    
    public NetworkManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public static NetworkManager getInstance() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 获取房间列表回调接口
     */
    public interface GetRoomListCallback {
        void onSuccess(List<RoomInfo> roomList);
        void onFailure(String errorMessage);
    }
    
    /**
     * 获取房间列表
     */
    public void getRoomList(final GetRoomListCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/room/list")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Get room list request failed: " + e.getMessage());
                if (callback != null) {
                    callback.onFailure("Get room list request failed: " + e.getMessage());
                }
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorMsg = "request failed code: " + response.code();
                    Log.e(TAG, errorMsg);
                    if (callback != null) {
                        callback.onFailure(errorMsg);
                    }
                    return;
                }
                
                String responseData = response.body().string();
                Log.d(TAG, "Get room list response: " + responseData);
                
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    List<RoomInfo> roomList = new ArrayList<>();
                    
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject roomJson = jsonArray.getJSONObject(i);
                        RoomInfo roomInfo = new RoomInfo();
                        roomInfo.roomId = roomJson.optString("room_id");
                        roomInfo.ownerUserId = roomJson.optString("owner_userid");
                        roomInfo.createTime = roomJson.optString("create_time");
                        roomInfo.roomType = roomJson.optString("room_type");
                        roomInfo.rtcType = roomJson.optString("rtc_type");
                        roomList.add(roomInfo);
                    }
                    
                    if (callback != null) {
                        callback.onSuccess(roomList);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    if (callback != null) {
                        callback.onFailure("JSON parsing error");
                    }
                }
            }
        });
    }
    
    /**
     * 创建房间回调接口
     */
    public interface CreateRoomCallback {
        void onSuccess(RoomInfo roomInfo);
        void onFailure(String errorMessage);
    }
    
    public void createRoom(String userid, String roomType, final CreateRoomCallback callback) {
        JSONObject json = new JSONObject();
         try {
             json.put("userid", userid);
             json.put("room_type", roomType);
         } catch (JSONException e) {
             e.printStackTrace();
         }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/room/create")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure("Create room request failed: " + e.getMessage());
                }
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorMsg = "request failed code: " + response.code();
                    Log.e(TAG, errorMsg);
                    if (callback != null) {
                        callback.onFailure(errorMsg);
                    }
                    return;
                }
                
                String responseData = response.body().string();
                Log.d(TAG, "Create room response: " + responseData);
                
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    RoomInfo roomInfo = new RoomInfo();
                    roomInfo.roomId = jsonResponse.optString("room_id");
                    roomInfo.ownerUserId = jsonResponse.optString("owner_userid");
                    roomInfo.createTime = jsonResponse.optString("create_time");
                    roomInfo.roomType = jsonResponse.optString("room_type");
                    roomInfo.rtcType = jsonResponse.optString("rtc_type");
                    
                    if (callback != null) {
                        callback.onSuccess(roomInfo);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    if (callback != null) {
                        callback.onFailure("JSON parsing error");
                    }
                }
            }
        });
    }
    
    /**
     * 房间信息类
     */
    public static class RoomInfo implements Serializable {
        public String roomId;
        public String ownerUserId;
        public String createTime;
        public String roomType;
        public String rtcType;

        @Override
        public String toString() {
            return "RoomInfo{" +
                    "roomId='" + roomId + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", roomType='" + roomType + '\'' +
                    ", rtcType='" + rtcType + '\'' +
                    ", ownerUserId='" + ownerUserId + '\'' +
                    '}';
        }
    }
}