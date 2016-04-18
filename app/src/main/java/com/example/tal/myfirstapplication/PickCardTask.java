package com.example.tal.myfirstapplication;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class PickCardTask extends BaseTask {

    public PickCardTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.BASIC_INFO_FIELD, getBasicInfoJSON());
            String pickedCard = params[0];
            json.put(Constants.WINNING_CARD, pickedCard);
            json.put(Constants.ASSOCIATION, Game.getGame().currentAssociation);
            UserData.getInstance().removeCard(pickedCard);
            String responseJSON = Requests.doPostWithResponse(Constants.SEND_PICKED_CARD_API_URL, json);
            JSONObject response = new JSONObject(responseJSON);
            String card = (String) response.get(Constants.NEW_CARD);
            UserData.getInstance().addCard(card);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
