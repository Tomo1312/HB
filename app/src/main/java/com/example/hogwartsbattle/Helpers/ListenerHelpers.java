package com.example.hogwartsbattle.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.CustomDialog.ShowCardDialog;
import com.example.hogwartsbattle.Game.GameActivity;
import com.example.hogwartsbattle.Interface.ILibraryListener;
import com.example.hogwartsbattle.Interface.IOnClassroomShow;
import com.example.hogwartsbattle.Interface.IOnOpponentHandShow;
import com.example.hogwartsbattle.Interface.IOpponentAllysListener;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ListenerHelpers {
    FirebaseDatabase database;
    Player thisPlayer, opponentPlayer;

    public ListenerHelpers(FirebaseDatabase database, Player thisPlayer, Player opponentPlayer) {
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.database = database;
    }

    public ValueEventListener setListenerForClassroom(ArrayList<Card> generalDeck, IOnClassroomShow iOnClassroomShow) {
        ValueEventListener valueEventListenerClassroom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    String stringCards = snapshot.getValue().toString();
                    ArrayList<Card> classroom = new ArrayList<>(Helpers.getInstance().returnCardsFromString(stringCards));
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

    public ValueEventListener setListenerForDiscardCard(ArrayList<Card> ownDeck, GameActivity activity) {

        ValueEventListener valueEventListenerForDiscardCard = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().toString().equals("0")) {
                    activity.deckNeedShuffle();
                    if (snapshot.getValue().toString().equals("1")) {
                        for (Card card : ownDeck) {
                            if (!card.getCardType().equals("hex")) {
                                if (thisPlayer.getDiscarded().equals("")) {
                                    thisPlayer.setDiscarded(card.getId());
                                } else {
                                    thisPlayer.setDiscarded(thisPlayer.getDiscarded() + "," + card.getId());
                                }
                                ownDeck.remove(card);
                                break;
                            }
                        }
                    } else if (snapshot.getValue().toString().equals("2")) {
                        if (!thisPlayer.getDiscarded().equals("")) {
                            thisPlayer.setDiscarded(thisPlayer.getDiscarded() + ",");
                        }
                        thisPlayer.setDiscarded(thisPlayer.getDiscarded() + ownDeck.get(0).getId());
                        ownDeck.remove(0);
                    }
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discardCardSpell").setValue(0);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue(thisPlayer.getDiscarded());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discardCardSpell").addValueEventListener(valueEventListenerForDiscardCard);
        return valueEventListenerForDiscardCard;
    }

    public ValueEventListener setListenerForOpponentDiscardCards() {

        ValueEventListener valueEventListenerForDiscardCard = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opponentPlayer.setDiscarded(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discarded").addValueEventListener(valueEventListenerForDiscardCard);
        return valueEventListenerForDiscardCard;
    }

    public ValueEventListener setListenerForThisPlayerDiscardCards() {

        ValueEventListener valueEventListenerForDiscardCard = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!thisPlayer.getDiscarded().equals(snapshot.getValue().toString()))
                    thisPlayer.setDiscarded(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").addValueEventListener(valueEventListenerForDiscardCard);
        return valueEventListenerForDiscardCard;
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
                    }
                } else {
                    iOnOpponentHandShow.onShowHandEmpty();
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

    public ValueEventListener getOpponentHouse(Context context, ImageView opponent_house_image) {
        ValueEventListener valueEventListenerPlaying = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.getValue().equals("") && snapshot.exists()) {
                    opponentPlayer.setHouse(snapshot.getValue().toString());
                    int id = context.getResources().getIdentifier("drawable/" + opponentPlayer.getHouse(), null, context.getPackageName());
                    opponent_house_image.setImageResource(id);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/house").removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/house").addValueEventListener(valueEventListenerPlaying);
        return valueEventListenerPlaying;
    }

    public ValueEventListener setListenerForOpponentAllys(IOpponentAllysListener iOpponentAllysListener) {
        ValueEventListener valueEventListenerOpponentAllys = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    opponentPlayer.setAlly(snapshot.getValue().toString());
                    iOpponentAllysListener.onOpponentAllysShow(Helpers.getInstance().returnCardsFromString(snapshot.getValue().toString()));
                } else {
                    opponentPlayer.setAlly("");
                    iOpponentAllysListener.onOpponentAllysShow(new ArrayList<>());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/ally").addValueEventListener(valueEventListenerOpponentAllys);
        return valueEventListenerOpponentAllys;
    }

    public ValueEventListener setListenerForBanishedCard(Context context) {
        ValueEventListener valueEventListenerBanishedCard = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    Card cardToShow = Common.allCardsMap.get(Integer.parseInt(snapshot.getValue().toString()));
                    String title;
                    if (thisPlayer.isPlaying())
                        title = thisPlayer.getPlayerName() + " have banished:";
                    else
                        title = opponentPlayer.getPlayerName() + " have banished:";
                    ShowCardDialog.getInstance().showCardDialog(context, cardToShow, title);
                    database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/banished").addValueEventListener(valueEventListenerBanishedCard);
        return valueEventListenerBanishedCard;
    }

    public ValueEventListener setListenerForBoughtClassroomCard(Context context) {
        ValueEventListener valueEventListenerBanishedCard = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    Card cardToShow = Common.allCardsMap.get(Integer.parseInt(snapshot.getValue().toString()));
                    String title;
                    if (thisPlayer.isPlaying())
                        title = thisPlayer.getPlayerName() + " have bought:";
                    else
                        title = opponentPlayer.getPlayerName() + " have bought:";
                    ShowCardDialog.getInstance().showCardDialog(context, cardToShow, title);
                    database.getReference("rooms/" + Common.currentRoomName + "/classroomBought").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/classroomBought").addValueEventListener(valueEventListenerBanishedCard);
        return valueEventListenerBanishedCard;
    }

    public ValueEventListener setListenerForOwnAllys(IOwnAllyListener iOwnAllyListener) {
        ValueEventListener valueEventListenerOwnAllys = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    iOwnAllyListener.removeAlly(Common.allCardsMap.get(Integer.valueOf(snapshot.getValue().toString())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/idAllyToDiscard").addValueEventListener(valueEventListenerOwnAllys);
        return valueEventListenerOwnAllys;
    }

    public ValueEventListener setListenerForLibrary(ILibraryListener iLibraryListener) {
        ValueEventListener valueEventListenerLibrary = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    iLibraryListener.onLibraryChange(Integer.parseInt(snapshot.getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/library").addValueEventListener(valueEventListenerLibrary);
        return valueEventListenerLibrary;
    }
}
