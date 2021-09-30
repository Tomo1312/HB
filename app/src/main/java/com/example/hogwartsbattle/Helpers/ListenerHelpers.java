package com.example.hogwartsbattle.Helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.Helpers;
import com.example.hogwartsbattle.Game.GameActivity;
import com.example.hogwartsbattle.Interface.IOnClassroomShow;
import com.example.hogwartsbattle.Interface.IOnOpponentHandShow;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListenerHelpers {
    FirebaseDatabase database;
    Player thisPlayer, opponentPlayer;
    Helpers deckHelper;

    public ListenerHelpers(FirebaseDatabase database, Player thisPlayer, Player opponentPlayer) {
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.database = database;
        deckHelper = new Helpers();
    }

    public ValueEventListener setListenerForClassroom(ArrayList<Card> generalDeck, IOnClassroomShow iOnClassroomShow) {
        ValueEventListener valueEventListenerClassroom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    String stringCards = snapshot.getValue().toString();
                    ArrayList<Card> classroom = new ArrayList<>(deckHelper.returnCardsFromString(stringCards));
                    if (classroom.size() == 3 && Common.currentUser.isHost()) {
                        Card cardTmp = generalDeck.get(0);
                        generalDeck.remove(0);
                        classroom.add(Common.allCardsMap.get(Integer.valueOf(cardTmp.getId())));
                        stringCards += "," + cardTmp.getId();
                        database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(stringCards);
                    } else if (!classroom.isEmpty()) {
                        iOnClassroomShow.onShowClassroom(classroom);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/classroom").addValueEventListener(valueEventListenerClassroom);
        return valueEventListenerClassroom;
    }

    public ValueEventListener setListenerForOpponentHandCards(IOnOpponentHandShow iOnOpponentHandShow) {
        ValueEventListener valueEventListenerOpponentHandCards = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    ArrayList<Card> opponentsHandCards = new ArrayList<>();
                    String stringCards = snapshot.getValue().toString();
                    String[] cards = stringCards.split(",");
                    for (String stringCardTmp : cards) {
                        opponentsHandCards.add(Common.allCardsMap.get(Integer.valueOf(stringCardTmp)));
                    }
                    if (!opponentsHandCards.isEmpty()) {
                        iOnOpponentHandShow.onOpponentShowHand(opponentsHandCards);
                    } else {
                        iOnOpponentHandShow.onShowHandEmpty();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/hand").addValueEventListener(valueEventListenerOpponentHandCards);
        return valueEventListenerOpponentHandCards;
    }

    public ValueEventListener startListenerForPlaying(GameActivity activity) {
        ValueEventListener valueEventListenerPlaying = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().equals(thisPlayer.getPlayerName())) {
                    activity.playTurn();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/playing").addValueEventListener(valueEventListenerPlaying);
        return valueEventListenerPlaying;
    }

    public ValueEventListener getOpponentHouse() {
        ValueEventListener valueEventListenerPlaying = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    opponentPlayer.setHouse(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + opponentPlayer.getPlayerName() + "/house").addValueEventListener(valueEventListenerPlaying);
        return valueEventListenerPlaying;
    }
}
