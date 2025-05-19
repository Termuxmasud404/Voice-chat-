package com.masud.voicechat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class VoiceChatActivity extends AppCompatActivity {

    private RtcEngine mRtcEngine;
    private final String APP_ID = "d2338c06cd7c4f4caf50d710a3fb6ed1"; // তোমার Agora App ID
    private String roomName;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            runOnUiThread(() -> Toast.makeText(VoiceChatActivity.this, "চ্যানেল জয়েন হয়েছে: " + channel, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> Toast.makeText(VoiceChatActivity.this, "কেউ চলে গেছে", Toast.LENGTH_SHORT).show());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roomName = getIntent().getStringExtra("room");

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), APP_ID, mRtcEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mRtcEngine != null) {
            mRtcEngine.enableAudio();
            mRtcEngine.joinChannel(null, roomName, "Extra Optional Data", 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            RtcEngine.destroy();
            mRtcEngine = null;
        }
    }
}
