package com.tencent.trtc.live;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.TRTCBaseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.tencent.trtc.live.net.NetworkManager;
import com.tencent.trtc.live.tools.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends TRTCBaseActivity {

    private RecyclerView recycler_view;
    private RoomAdapter adapter;
    private String TAG = "RoomListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        if (checkPermission()) {
            initView();
            initData();
        }

    }

    private void initData() {
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.getRoomList(new NetworkManager.GetRoomListCallback() {
            @Override
            public void onSuccess(final List<NetworkManager.RoomInfo> roomList) {
                Log.e("xbo", "onSuccess: roomList = " + roomList.toString() );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(roomList);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void initView() {
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        // 添加分割线
        recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // 创建并设置适配器
        adapter = new RoomAdapter(this, new ArrayList<NetworkManager.RoomInfo>());
        adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NetworkManager.RoomInfo roomInfo) {
                // 处理房间点击事件
                Intent intent = new Intent(RoomListActivity.this, LiveAudienceActivity.class);
                intent.putExtra("roomInfo", roomInfo);
                startActivity(intent);
            }
        });
        recycler_view.setAdapter(adapter);

        // 创建房间
        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_create) {
                    createRoom();
                }
            }
        });
    }

    @Override
    protected void onPermissionGranted() {
        initView();
        initData();
    }


    /**
     * 创建房间
     */
    private void createRoom() {

        String userId = SPUtils.getInstance(this).getString("userId", "");

        // 验证userid
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId.length() < 3) {
            Toast.makeText(this, "User ID must be at least 3 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载状态
        Snackbar.make(findViewById(android.R.id.content), "Creating room...", Snackbar.LENGTH_INDEFINITE).show();

        NetworkManager.getInstance().createRoom(userId, "Live", new NetworkManager.CreateRoomCallback() {

            @Override
            public void onSuccess(final NetworkManager.RoomInfo roomInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), "Room created successfully: " + roomInfo.roomId, Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, "Room created successfully: " + roomInfo.toString());

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RoomListActivity.this.startActivity(new Intent(RoomListActivity.this, LiveAnchorActivity.class).putExtra("roomInfo", roomInfo));
                            }
                        } , 1000);

                    }
                });
            }

            @Override
            public void onFailure(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), "Failed to create room: " + errorMessage, Snackbar.LENGTH_LONG).show();
                        Log.e(TAG, "Failed to create room: " + errorMessage);
                    }
                });
            }
        });
    }

}
