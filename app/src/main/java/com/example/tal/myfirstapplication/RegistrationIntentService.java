package com.example.tal.myfirstapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            String operation = intent.getStringExtra(Constants.OPERATION_TYPE);
            String topicRoomName = intent.getStringExtra(Constants.TOPIC_ROOM_NAME);

            if (operation.equals(Constants.REGISTER_OPERATION)) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken("102043151700",
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                UserData.getInstance().setToken(token);
                Log.i(TAG, "GCM Registration Token: " + token);

                // Subscribe to topic channels
                subscribeTopics(token, topicRoomName);
            }
            else if(operation.equals(Constants.UNREGISTER_OPERATION)){
                unsubscribeTopic(topicRoomName);
            }

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void subscribeTopics(String token, String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.subscribe(token, "/topics/" + topic, null);
    }

    private void unsubscribeTopic(String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.unsubscribe(UserData.getInstance().getToken(), "/topics/" + topic);
    }

}
