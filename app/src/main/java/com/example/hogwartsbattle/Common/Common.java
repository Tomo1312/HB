package com.example.hogwartsbattle.Common;

import android.media.MediaPlayer;

import androidx.collection.CircularArray;

import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class Common {


    public static final String KEY_LOGGED = "logged";
    public static final String KEY_USER_ID = "userid";
    public static User currentUser;
    public static String currentRoomName;
    public static Map<Integer, Card> allCardsMap;
    public static Map<Integer, String> Houses = new HashMap<Integer, String>() {{
        put(0, "gryffindor");
        put(1, "ravenclaw");
        put(2, "hufflepuff");
        put(3, "slytherin");
    }};

    public static final String ATTACK = "Attack";
    public static final String GOLD = "Gold";
    public static final String HEART = "Heart";
    public static final String REVEAL_TOP_CARD = "Reveal Top Card";
    public static final String DISCARD_OPPONENTS_ALLY = "Discard Opponents Ally";
    public static final String BANISH_FROM_CLASSROOM = "Banish from Classroom";
    public static final String DRAW_ITEM_FROM_DISCARD_PILE = "Draw Item from discard pile";
    public static final String BANISH_HEX_FROM_DISCARD_PILE = "Banish Hex From Discard Pile";
    public static final String DRAW_CARD = "Draw Card";
    public static final String BANISH_FROM_DISCARD_PILE = "Banish from discard pile";
    public static final String BANISH_FROM_HAND = "Banish from hand";
    public static final String COPY_SPELL = "Copy spell";
    public static final String OPPONENT_PUT_HEX_TO_DISCARD_PILE = "Opponent put Hex To Discard Pile";
    public static final String OPPONENT_PUT_HEX_TO_HAND = "Opponent put Hex in hand";
    public static final String OPPONENT_DISCARD_NON_HEX_CARD = "Opponent discard non-hex card";
    public static final String OPPONENT_DISCARD_RANDOM_CARD = "Opponent discard random card";
    public static final String RETURN_TO_LIBRARY = "Return to library";
    public static final String GOLD_PER_ALLY = "Gain gold for each Ally";
    public static final String ATTACK_PER_ALLY = "Gain attack for each Ally";
    public static final String ATTACK_PER_OPPONENT_ALLY = "Gain attack for opponents Ally";
    public static final String HEART_PER_ALLY = "Gain heart for each Ally";
    public static final String DRAW_CARD_THEN_DISCARD_ANY = "Draw card and discard any";
    public static final String PUT_CARD_FROM_YOUR_TO_OPPONENT_DISCARD = "Put card from your discard to opponents";
    public static final String DRAW_TWO_CARD_THEN_DISCARD_ANY = "Draw two cards. Then discard any";
    public static final String DISCARD_CARD = "Discard card";
    public static final String DISCARD_SPELL = "Discard spell from hand";
    public static final String COPY_ALLY_SPELL = "Copy ability of your Ally (activate again)";
    public static final String BANISH_PLAYED_HEX = "Banish played Hex";
    public static final String SAVE_GOLD = "Save 1 gold";
    public static final String COLLECT_ALL_GOLD = "Collect all golds";
}
