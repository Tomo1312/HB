package com.example.hogwartsbattle.Interface;

import android.content.DialogInterface;

import com.example.hogwartsbattle.Model.Card;

import java.util.ArrayList;

public interface IChooseDialog {

    // Initialized in game activity
    void onChooseAlly(DialogInterface dialog, int id);
    void onChooseHouse(DialogInterface dialog, int id);
    void onChooseReturnToLobby(DialogInterface dialog);
    void onPaperRockScissorsChoose(DialogInterface dialog, String hand);

    void onLibraryChange(int libraryFromDatabase);
    void onShowClassroom(ArrayList<Card> classRoom);

    void onOpponentShowHand(ArrayList<Card> cards);
    void onShowHandEmpty();
    void onOpponentAllysShow(ArrayList<Card> cards);

    void onOwnAllyChange(Card ally);
    void removeAlly(Card ally);
    void setAllyAvailable(Card ally);

    void onUpdateAttackGoldHeart();
}
