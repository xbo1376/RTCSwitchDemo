package com.tencent.trtc.live;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.tencent.trtc.live.holder.RoomViewHolder;
import com.tencent.trtc.live.net.NetworkManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {
    private List<NetworkManager.RoomInfo> roomList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public RoomAdapter(Context context, List<NetworkManager.RoomInfo> roomList) {
        this.context = context;
        this.roomList = roomList;

        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.dateFormat.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_view, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        final NetworkManager.RoomInfo room = roomList.get(position);
        
        holder.roomIdText.setText(context.getString(R.string.roomid) + room.roomId);

        try {
            long timestamp = Long.parseLong(room.createTime);
            String chinaTime = formatTimestampToTime(timestamp);
            holder.createTimeText.setText(context.getString(R.string.create_time) + chinaTime);
        } catch (NumberFormatException e) {
            Log.e("xbo", "onBindViewHolder: ", e);
        }
        holder.roomTypeText.setText(context.getString(R.string.room_type) + room.roomType);
        holder.rtcTypeText.setText(context.getString(R.string.rtc_type) + room.rtcType);

        // 设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               if (listener != null) {
                   listener.onItemClick(room);
               }
            }
        });
    }

        private String formatTimestampToTime(long timestamp) {
        try {
            Date date = new Date(timestamp * 1000);
            return dateFormat.format(date);
        } catch (Exception e) {
            return context.getString(R.string.time_format_fail);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
    
    // 更新数据方法
    public void updateData(List<NetworkManager.RoomInfo> newRoomList) {

        this.roomList.clear();
        this.roomList.addAll(newRoomList);
        notifyDataSetChanged();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(NetworkManager.RoomInfo room);
    }
}