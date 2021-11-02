package com.example.hogwartsbattle.Helpers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.CustomDialog.ShowCardDialog;
import com.example.hogwartsbattle.Game.GameActivity;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ListenerHelpers {
    FirebaseDatabase database;
    Player thisPlayer, opponentPlayer;

    public ListenerHelpers(FirebaseDatabase database, Player thisPlayer, Player opponentPlayer) {
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.database = database;
    }

    public ValueEventListener setListenerForClassroom(ArrayList<Card> generalDeck, IChooseDialog iChooseDialog) {
        ValueEventListener valueEventListenerClassroom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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
                            iChooseDialog.onShowClassroom(classroom);
                        }
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
                if (snapshot.exists()) {
                    if (!snapshot.getValue().toString().equals("0")) {
                        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotDiscarded) {
                                thisPlayer.setDiscarded(snapshotDiscarded.getValue().toString());
                                if (ownDeck.size() < 5 && !thisPlayer.getDiscarded().equals("")) {
                                    ownDeck.addAll(Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()));
                                    Collections.shuffle(ownDeck);
                                    Collections.shuffle(ownDeck);
                                    thisPlayer.setDiscardedToEmpty();
                                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue("");
                                    activity.discardPileShow();
                                }
                                if (snapshot.getValue().toString().equals("1")) {
                                    for (Card card : ownDeck) {
                                        if (!card.getCardType().equals("hex")) {
                                            thisPlayer.setDiscardedString(card.getId());
                                            ownDeck.remove(card);
                                            break;
                                        }
                                    }
                                } else if (snapshot.getValue().toString().equals("2")) {
                                    thisPlayer.setDiscardedString(ownDeck.get(0).getId());
                                    ownDeck.remove(0);
                                }
                                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discardCardSpell").setValue(0);
                                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue(thisPlayer.getDiscarded());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discardCardSpell").addValueEventListener(valueEventListenerForDiscardCard);
        return valueEventListenerForDiscardCard;
    }

    public ValueEventListener setListenerForOpponentHandCards(IChooseDialog iChooseDialog) {
        ValueEventListener valueEventListenerOpponentHandCards = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().equals("")) {
                        ArrayList<Card> opponentsHandCards = new ArrayList<>();
                        String stringCards = snapshot.getValue().toString();
                        String[] cards = stringCards.split(",");
                        for (String stringCardTmp : cards) {
                            opponentsHandCards.add(Common.allCardsMap.get(Integer.valueOf(stringCardTmp)));
                        }
                        if (!opponentsHandCards.isEmpty()) {
                            iChooseDialog.onOpponentShowHand(opponentsHandCards);
                        }
                    } else {
                        iChooseDialog.onShowHandEmpty();
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

                if (snapshot.exists()) {
                    if (snapshot.getValue().equals(thisPlayer.getPlayerName())) {
                        activity.playTurn();
                    }
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
            @SuppressLint("ResourceType")
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

    public ValueEventListener setListenerForOpponentAllys(IChooseDialog iChooseDialog) {
        ValueEventListener valueEventListenerOpponentAllys = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().equals("")) {
                        opponentPlayer.setAlly(snapshot.getValue().toString());
                        iChooseDialog.onOpponentAllysShow(Helpers.getInstance().returnCardsFromString(snapshot.getValue().toString()));
                    } else {
                        opponentPlayer.setAlly("");
                        iChooseDialog.onOpponentAllysShow(new ArrayList<>());
                    }
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
                if (snapshot.exists()) {
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
                if (snapshot.exists()) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/classroomBought").addValueEventListener(valueEventListenerBanishedCard);
        return valueEventListenerBanishedCard;
    }

    public ValueEventListener setListenerForOwnAllys(IChooseDialog iChooseDialog) {
        ValueEventListener valueEventListenerOwnAllys = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().equals("")) {
                        iChooseDialog.removeAlly(Common.allCardsMap.get(Integer.valueOf(snapshot.getValue().toString())));
                        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/idAllyToDiscard").setValue("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/idAllyToDiscard").addValueEventListener(valueEventListenerOwnAllys);
        return valueEventListenerOwnAllys;
    }

    public ValueEventListener setListenerForLibrary(IChooseDialog iChooseDialog) {
        ValueEventListener valueEventListenerLibrary = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().equals("")) {
                        iChooseDialog.onLibraryChange(Integer.parseInt(snapshot.getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/library").addValueEventListener(valueEventListenerLibrary);
        return valueEventListenerLibrary;
    }

    //--------------  Play Turn Get opponent life, opponent discard, set opponent hand --------------//
    //--------------  Get own life, own discard, own hand, set owndeck if it's finish  --------------//

    public void startGettingInfoForTurn(GameActivity activity, Dialog dialog) {
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/lives").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thisPlayer.setLives(Integer.parseInt(snapshot.getValue().toString()));
                getOpponentLives(activity, dialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOpponentLives(GameActivity activity, Dialog dialog) {
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/lives").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opponentPlayer.setLives(Integer.parseInt(snapshot.getValue().toString()));
                getOpponentDiscardPile(activity, dialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOpponentDiscardPile(GameActivity activity, Dialog dialog) {
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discarded").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opponentPlayer.setDiscarded(snapshot.getValue().toString());
                getOwnDiscardPile(activity, dialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOwnDiscardPile(GameActivity activity, Dialog dialog) {
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thisPlayer.setDiscarded(snapshot.getValue().toString());
                startDraw(activity, dialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startDraw(GameActivity activity, Dialog dialog) {
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check for Hexes in hand that opponent might put in players hand
                activity.discardPileShow();

                opponentPlayer.setHand("");

                if (thisPlayer.getLives() < 1) {
                    thisPlayer.setDeaths(thisPlayer.getDeaths() + 1);
                    activity.prepareForNewRound();
                }
                if (dialog.isShowing())
                    dialog.dismiss();

                if (!snapshot.getValue().toString().equals("") && thisPlayer.getDeaths() < 4) {
                    activity.drawCards(snapshot.getValue().toString());
                } else if (thisPlayer.getDeaths() < 4) {
                    activity.drawCards("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
