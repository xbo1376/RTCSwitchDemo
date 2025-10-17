package com.tencent.trtc.apiexample;

import android.content.Intent;
import android.os.Bundle;

import com.example.basic.TRTCBaseActivity;
import com.tencent.trtc.live.LoginActivity;

public class MainActivity extends TRTCBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

//        findViewById(R.id.ll_live).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onPermissionGranted() {
    }

}
