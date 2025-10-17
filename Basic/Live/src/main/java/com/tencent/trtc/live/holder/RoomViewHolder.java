package com.tencent.trtc.live.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tencent.trtc.live.R;

public class RoomViewHolder extends RecyclerView.ViewHolder {
    public TextView roomIdText;
    public TextView createTimeText;
    public TextView roomTypeText;
    public TextView rtcTypeText;

    public RoomViewHolder(View itemView) {
        super(itemView);
        roomIdText = itemView.findViewById(R.id.tv_room_id);
        createTimeText = itemView.findViewById(R.id.tv_create_time);
        roomTypeText = itemView.findViewById(R.id.tv_room_type);
        rtcTypeText = itemView.findViewById(R.id.tv_rtc_type);
    }
}