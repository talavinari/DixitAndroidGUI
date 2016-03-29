package com.example.tal.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/30/2016.
 */
public class SendAssociationTask extends BaseTask {

    public SendAssociationTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.BASIC_INFO_FIELD, getBasicInfoJSON());
            json.put(Constants.WINNING_CARD, params[0]);
            json.put(Constants.ASSOCIATION, params[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String responseJSON = Requests.doPostWithResponse(Constants.SEND_ASSOCIATION_API_URL, json);
        try {
            JSONObject response = new JSONObject(responseJSON);
            String card = (String) response.get(Constants.NEW_CARD);
            UserData.getInstance().removeCard(card);
            UserData.getInstance().addCard(card);
            Intent intent = new Intent(QuickstartPreferences.IN_APP_MESSAGE);
            intent.putExtra(Constants.IN_APP_MESSAGE_TYPE, Constants.CARD_RECEIVED_EVENT);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
