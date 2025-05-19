// MainActivity.java package com.masud.voicechat;

import android.content.Intent; import android.os.Bundle; import android.view.View; import android.widget.Button; import android.widget.EditText; import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth; import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity { EditText email, password, roomName; Button loginBtn, createRoomBtn, joinRoomBtn; FirebaseAuth auth; FirebaseFirestore db;

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

    loginBtn.setOnClickListener(v -> {
        String e = email.getText().toString();
        String p = password.getText().toString();
        auth.signInWithEmailAndPassword(e, p)
            .addOnSuccessListener(a -> Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e1 -> Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show());
    });

    createRoomBtn.setOnClickListener(v -> {
        String room = roomName.getText().toString();
        if (!room.isEmpty()) {
            db.collection("rooms").document(room).set(new HashMap<>());
            startActivity(new Intent(this, VoiceChatActivity.class).putExtra("room", room));
        }
    });

    joinRoomBtn.setOnClickListener(v -> {
        String room = roomName.getText().toString();
        if (!room.isEmpty()) {
            startActivity(new Intent(this, VoiceChatActivity.class).putExtra("room", room));
        }
    });
}

}

// VoiceChatActivity.java package com.masud.voicechat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc.Constants; import io.agora.rtc.RtcEngine; import io.agora.rtc.IRtcEngineEventHandler;

public class VoiceChatActivity extends AppCompatActivity { private RtcEngine mRtcEngine; private final String APP_ID = "d2338c06cd7c4f4caf50d710a3fb6ed1"; // Your Agora App ID private String roomName;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    roomName = getIntent().getStringExtra("room");

    try {
        mRtcEngine = RtcEngine.create(getBaseContext(), APP_ID, new IRtcEngineEventHandler() {
        });
    } catch (Exception e) {
        e.printStackTrace();
    }

    mRtcEngine.enableAudio();
    mRtcEngine.joinChannel(null, roomName, "Extra Optional Data", 0);
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

// activity_main.xml

<?xml version="1.0" encoding="utf-8"?><LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:orientation="vertical"
android:padding="16dp">

<EditText
    android:id="@+id/email"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Email" />

<EditText
    android:id="@+id/password"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Password"
    android:inputType="textPassword" />

<EditText
    android:id="@+id/roomName"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Room Name" />

<Button
    android:id="@+id/loginBtn"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Login" />

<Button
    android:id="@+id/createRoomBtn"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Create Room" />

<Button
    android:id="@+id/joinRoomBtn"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Join Room" />

</LinearLayout>// AndroidManifest.xml <manifest xmlns:android="http://schemas.android.com/apk/res/android"
package="com.masud.voicechat">

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar">
    <activity android:name=".VoiceChatActivity" />
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>

</manifest>// build.gradle (Module: app) plugins { id 'com.android.application' id 'com.google.gms.google-services' }

android { namespace 'com.masud.voicechat' compileSdk 34

defaultConfig {
    applicationId "com.masud.voicechat"
    minSdk 21
    targetSdk 34
    versionCode 1
    versionName "1.0"

    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
}

buildTypes {
    release {
        minifyEnabled false
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}

}

dependencies { implementation 'androidx.appcompat:appcompat:1.6.1' implementation 'com.google.firebase:firebase-auth:22.3.1' implementation 'com.google.firebase:firebase-firestore:24.10.0' implementation 'io.agora.rtc:full-sdk:3.7.0.2' }

// Firebase Firestore Rules (for testing only) rules_version = '2'; service cloud.firestore { match /databases/{database}/documents { match /{document=**} { allow read, write: if true; } } }

