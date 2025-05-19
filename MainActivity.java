package com.masud.voicechat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText email, password, roomName;
    Button loginBtn, createRoomBtn, joinRoomBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        roomName = findViewById(R.id.roomName);
        loginBtn = findViewById(R.id.loginBtn);
        createRoomBtn = findViewById(R.id.createRoomBtn);
        joinRoomBtn = findViewById(R.id.joinRoomBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // শুরুতে রুম বাটনগুলো ডিসেবল করে রাখি
        createRoomBtn.setEnabled(false);
        joinRoomBtn.setEnabled(false);

        loginBtn.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "ইমেইল এবং পাসওয়ার্ড দিন", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "লগইন সফল হয়েছে", Toast.LENGTH_SHORT).show();
                        // লগইন সফল হলে রুম বাটন চালু করো
                        createRoomBtn.setEnabled(true);
                        joinRoomBtn.setEnabled(true);
                    })
                    .addOnFailureListener(e1 -> Toast.makeText(this, "লগইন ব্যর্থ: " + e1.getMessage(), Toast.LENGTH_SHORT).show());
        });

        createRoomBtn.setOnClickListener(v -> {
            String room = roomName.getText().toString().trim();
            if (room.isEmpty()) {
                Toast.makeText(this, "রুমের নাম দিন", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAudioPermissionAndStart(room, true);
        });

        joinRoomBtn.setOnClickListener(v -> {
            String room = roomName.getText().toString().trim();
            if (room.isEmpty()) {
                Toast.makeText(this, "রুমের নাম দিন", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAudioPermissionAndStart(room, false);
        });
    }

    private void checkAudioPermissionAndStart(String room, boolean isCreate) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startVoiceChat(room, isCreate);
        }
    }

    private void startVoiceChat(String room, boolean isCreate) {
        if (isCreate) {
            db.collection("rooms").document(room).set(new HashMap<>());
        }
        Intent intent = new Intent(this, VoiceChatActivity.class);
        intent.putExtra("room", room);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "রেকর্ড অডিও পারমিশন মঞ্জুর হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "রেকর্ড অডিও পারমিশন দরকার", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
