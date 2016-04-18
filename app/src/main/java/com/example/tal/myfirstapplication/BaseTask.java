package com.example.tal.myfirstapplication;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseTask extends AsyncTask<String, String, String> {
    protected Context context;
    protected int doInBackgroundExitCode;


    public BaseTask(Context context) {
        this.context = context;
    }

    protected JSONObject getBasicInfoJSON() throws JSONException {
        JSONObject subJson = new JSONObject();
        subJson.put(Constants.NAME_FIELD, UserData.getInstance().getNickName(context));
        subJson.put(Constants.ROOM_FIELD, UserData.getInstance().getCurrRoom(context));
        return subJson;
    }
}
