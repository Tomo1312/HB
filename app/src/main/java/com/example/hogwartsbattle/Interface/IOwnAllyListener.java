package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

import java.util.ArrayList;

public interface IOwnAllyListener {
    void onOwnAllyChange(Card ally);
    void setNewAllys(ArrayList<Card> allys);
}
