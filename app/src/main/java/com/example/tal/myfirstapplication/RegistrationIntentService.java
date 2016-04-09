/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token, String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.subscribe(token, "/topics/" + topic, null);
    }

    private void unsubscribeTopic(String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.unsubscribe(UserData.getInstance().getToken(), "/topics/" + topic);
    }

}
