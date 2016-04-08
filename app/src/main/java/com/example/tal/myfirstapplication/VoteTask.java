package com.example.tal.myfirstapplication;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/29/2016.
 */
public class VoteTask extends BaseTask {

    public VoteTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject json = new JSONObject();
        try {
            String votedCard = params[0];
            json.put(Constants.BASIC_INFO_FIELD, getBasicInfoJSON());
            json.put(Constants.VOTED_CARD, votedCard);

            String responseJSON = Requests.doPostWithResponse(Constants.VOTE_FOR_CARD_API_URL, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
