package com.example.tal.myfirstapplication;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/29/2016.
 */
public class PickCardTask extends BaseTask {

    public PickCardTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.BASIC_INFO_FIELD, getBasicInfoJSON());
            json.put(Constants.WINNING_CARD , params[0]);
            json.put(Constants.ASSOCIATION, Game.getGame().currentAssociation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Requests.doPostWithResponse(Constants.SEND_PICKED_CARD_API_URL, json);
        return "";
    }
}
