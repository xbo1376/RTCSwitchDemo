package com.tencent.trtc.live;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.trtc.live.tools.SPUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText et_user_id;
    private Button   btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        initView();
    }

    private void initView() {
        et_user_id = findViewById(R.id.et_user_id);

        String userId = SPUtils.getInstance(this).getString("userId", "");
        if (TextUtils.isEmpty(userId)) {
            userId = System.currentTimeMillis() % 10000000 + "";
        }
        et_user_id.setText(userId);

        findViewById(R.id.root_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = et_user_id.getText().toString().trim();
                // 保存用户id 全局使用
                SPUtils.getInstance(LoginActivity.this).putString("userId", userId);
                startActivity(new Intent(LoginActivity.this, RoomListActivity.class));
            }
        });

    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getWindow().peekDecorView();
        if (null != view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
