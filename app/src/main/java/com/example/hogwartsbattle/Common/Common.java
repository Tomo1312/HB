package com.example.hogwartsbattle.Common;

import androidx.collection.CircularArray;

import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.Model.User;

import java.util.HashMap;
import java.util.Map;

public class Common {
    public static final String KEY_LOGGED = "logged";
    public static final String KEY_USER_ID = "userid";
    public static User currentUser;
    public static String currentRoomName;
    public static Map<Integer, Card> allCardsMap;
    public static Map<Integer, String> Houses = new HashMap<Integer, String>() {{
        put(0, "Gryffindor");
        put(1, "Ravenclaw");
        put(2, "Hufflepuff");
        put(3, "Slytherin");
    }};
}
