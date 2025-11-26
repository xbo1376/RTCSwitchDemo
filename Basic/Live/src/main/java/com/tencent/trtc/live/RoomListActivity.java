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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.basic.TRTCBaseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.tencent.trtc.live.net.NetworkManager;
import com.tencent.trtc.live.tools.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends TRTCBaseActivity {

    private RecyclerView recycler_view;
    private SwipeRefreshLayout swipe_refresh_layout;
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
        loadRoomList();
    }

    /**
     * 加载房间列表数据
     */
    private void loadRoomList() {
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.getRoomList(new NetworkManager.GetRoomListCallback() {
            @Override
            public void onSuccess(final List<NetworkManager.RoomInfo> roomList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(roomList);
                        // 停止刷新动画
                        if (swipe_refresh_layout != null) {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新动画
                        if (swipe_refresh_layout != null) {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                        Toast.makeText(RoomListActivity.this, "Failed to load room list: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to load room list: " + errorMessage);
                    }
                });
            }
        });
    }

    private void initView() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
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

        // 设置下拉刷新监听器
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新时重新加载数据
                loadRoomList();
            }
        });

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
        loadRoomList();
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

        // 请求创建房间，从服务端获取房间信息
        NetworkManager.getInstance().createRoom(userId, "Live", new NetworkManager.CreateRoomCallback() {

            @Override
            public void onSuccess(final NetworkManager.RoomInfo roomInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), "Room created successfully: " + roomInfo.roomId, Snackbar.LENGTH_LONG).show();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(RoomListActivity.this, LiveAnchorActivity.class);
                                intent.putExtra("roomInfo", roomInfo);
                                RoomListActivity.this.startActivity(intent);
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
