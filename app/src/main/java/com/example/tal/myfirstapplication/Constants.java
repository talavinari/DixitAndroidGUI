package com.example.tal.myfirstapplication;

public class Constants {

    public static final String ID_EXTRA = "extra";
//    public static final String REST_API_PREFIX = "http://192.168.14.103:8080/DixitRESTfulAPI/rest/service/";
    public static final String REST_API_PREFIX = "http://10.0.0.6:8080/DixitRESTfulAPI/rest/service/";
//    public static final String REST_API_PREFIX = "http://149.78.156.195:8080/DixitRESTfulAPI/rest/service/";
    public static final String ADD_ROOM_API_URL = REST_API_PREFIX + "addRoom";
    public static final String ADD_PLAYER_TO_ROOM_API_URL = REST_API_PREFIX + "join";
    public static final String VOTE_FOR_CARD_API_URL = REST_API_PREFIX + "vote";
    public static final String SEND_PICKED_CARD_API_URL = REST_API_PREFIX + "sendPickedCard";
    public static final String SEND_ASSOCIATION_API_URL = REST_API_PREFIX + "sendAssociation";
    public static final String GET_ROOMS_API_URL = REST_API_PREFIX + "rooms";
    public static final String REMOVE_PLAYER = REST_API_PREFIX + "removePlayer";
    public static final String GET_ROOM_DETAILS_API_URL = REST_API_PREFIX + "players"; // /players/{roomName}
    public static final String ROOM_FIELD = "roomName";
    public static final String NAME_FIELD = "nickName";
    public static final String BASIC_INFO_FIELD = "basicInfo";
    public static final String NEW_CARD = "newCard";
    public static final String IMG_PREFIX = "img";

    public static final String TOPIC_ROOM_NAME = "roomName";

    public static final String PLAYER_NAME = "playerName";
    public static final String WINNING_CARD = "winningCard";
    public static final String VOTED_CARD = "card";
    public static final String ASSOCIATION = "association";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String INDEX = "index";
    public static final String ERROR = "error";
    public static final int NUMBER_OF_CARDS_IN_HAND = 6;
    public static final int NUMBER_OF_PLAYERS_IN_DIXIT = 4;
    public static int WINNING_SCORE_THRESHOLD = 10;

    public static final String DELIMITER = ",";

}
