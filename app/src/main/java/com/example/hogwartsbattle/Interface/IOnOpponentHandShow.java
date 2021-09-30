package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

import java.util.ArrayList;

public interface IOnOpponentHandShow {
     void onOpponentShowHand(ArrayList<Card> cards);
     void onShowHandEmpty();
}
