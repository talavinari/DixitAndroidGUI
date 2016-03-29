package com.example.tal.myfirstapplication;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/30/2016.
 */
public abstract class BaseTask extends AsyncTask<String, String, String> {
    protected Context context;

    public BaseTask(Context context) {
        this.context = context;
    }

    protected JSONObject getBasicInfoJSON() throws JSONException {
        JSONObject subJson = new JSONObject();
        subJson.put(Constants.NAME_FIELD, GameState.getGame().getDevicePlayer().name);
        subJson.put(Constants.ROOM_FIELD, UserData.getInstance().getCurrRoom(context));
        return subJson;
    }
}
