package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

import java.util.ArrayList;

public interface IOwnAllyListener {
    //IN game activity
    void onOwnAllyChange(Card ally);
    void removeAlly(Card ally);
}
