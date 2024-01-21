package com.rmit.bookflowapp.util;

import android.util.Log;

import com.rmit.bookflowapp.activity.CallActivity;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;

public class LeaveCall implements ZegoUIKitPrebuiltCallFragment.LeaveCallListener {
    private CallActivity activity;

    public LeaveCall(CallActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onLeaveCall() {
        activity.finish();
    }

}
