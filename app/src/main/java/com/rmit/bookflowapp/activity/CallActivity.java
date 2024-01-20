package com.rmit.bookflowapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.rmit.bookflowapp.R;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        addCallFragment();
    }

    public void sendInvitation() {
        String targetUserID = getIntent().getStringExtra("targetID");
        String targetUserName = getIntent().getStringExtra("targetName");
        Context context = getApplicationContext();

        ZegoSendCallInvitationButton button = new ZegoSendCallInvitationButton(context);
        button.setIsVideoCall(true);
        button.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserName)));
    }

    public void addCallFragment() {
        long appID = 832849511;
        String appSign = "85d7f6dbb1ca537b623ae1c4014e46c692862eb528408647054a6d98e09e42e2";
        Intent intent = getIntent();
        String callID = intent.getStringExtra("callID");
        String userID = intent.getStringExtra("userID");
//        String userName = intent.getStringExtra("username");
//        String callID = "123";
//        String userID = "123";
        String userName = "USER";
        Log.d("INFO", callID);
        Log.d("INFO", userID);
        Log.d("INFO", userName);
        // You can also use GroupVideo/GroupVoice/OneOnOneVoice to make more types of calls.
        ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();

        ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
                appID, appSign, callID, userID, userName, config);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow();
    }
}