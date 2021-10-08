package com.example.hogwartsbattle.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogwartsbattle.Adapters.ClassroomAdapter;
import com.example.hogwartsbattle.Adapters.OpponentAllyAdapter;
import com.example.hogwartsbattle.Adapters.OpponentHandAdapter;
import com.example.hogwartsbattle.Adapters.OwnAllyAdapter;
import com.example.hogwartsbattle.Adapters.OwnHandAdapter;
import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Common.SpacesItemDecoration;
import com.example.hogwartsbattle.CustomDialog.ChooseAllyDialog;
import com.example.hogwartsbattle.CustomDialog.ChooseHouseDialog;
import com.example.hogwartsbattle.CustomDialog.RockPaperScissorsDialog;
import com.example.hogwartsbattle.Helpers.ListenerHelpers;
import com.example.hogwartsbattle.Interface.IChooseAllyDialog;
import com.example.hogwartsbattle.Interface.IChooseHouseDialog;
import com.example.hogwartsbattle.Interface.IOnClassroomShow;
import com.example.hogwartsbattle.Interface.IOnOpponentHandShow;
import com.example.hogwartsbattle.Interface.IOnOwnHandShow;
import com.example.hogwartsbattle.Interface.IOpponentAllysListener;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IRockPaperScissorsDialog;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class GameActivity extends AppCompatActivity implements IChooseAllyDialog, IChooseHouseDialog, IRockPaperScissorsDialog, IOnOwnHandShow, IOnClassroomShow, IOnOpponentHandShow, IUpdateAttackGoldHeart, IOwnAllyListener, IOpponentAllysListener {

    Player thisPlayer;
    Player opponent;
    AlertDialog loading;

    ArrayList<Card> allCards = new ArrayList<>();
    ArrayList<Card> generalDeck = new ArrayList<>();
    ArrayList<Card> classRoom = new ArrayList<>();
    ArrayList<Card> hexDeck = new ArrayList<>();
    ArrayList<Card> ownDeck = new ArrayList<>();
    ArrayList<Card> hand = new ArrayList<>();
    ArrayList<Card> banishedDeck = new ArrayList<>();
    ArrayList<Card> books = new ArrayList<>();
    Map<Integer, Card> allCardsMap = new HashMap<>();

    IChooseHouseDialog iChooseHouseDialog;
    IChooseAllyDialog iChooseAllyDialog;
    IRockPaperScissorsDialog iRockPaperScissorsDialog;
    IOnOwnHandShow iOnOwnHandShow;
    IOnOpponentHandShow iOnOpponentHandShow;
    IOnClassroomShow iOnClassroomShow;
    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    IOwnAllyListener iOwnAllyListener;
    IOpponentAllysListener iOpponentAllysListener;

    FirebaseDatabase database;

    ValueEventListener valueEventListenerOpponentFirstHand, valueEventListenerPlaying,
            valueEventListenerOpponentHandCards, valueEventListenerClassroom, valueEventListenerOpponentHouse,
            valueEventListenerOpponentAlly, valueEventListenerOwnAlly, valueEventListenerForDiscardCardSpell,
            valueEventListenerForOpponentDiscardCards;

    RecyclerView ownHand, opponentHand, recyclerViewClassroom, recyclerOwnAllys, recyclerOpponentAlly;
    TextView playersGold, playersAttack, playersHeart;

    OwnAllyAdapter ownAllyAdapter;
    ListenerHelpers listenerHelpers;

    Button finishMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getPlayers();
        //Common.Houses.get(id)
        loading = new SpotsDialog.Builder().setCancelable(false).setContext(GameActivity.this).build();
        iChooseAllyDialog = this;
        iChooseHouseDialog = this;
        iRockPaperScissorsDialog = this;
        iOnOwnHandShow = this;
        iOnClassroomShow = this;
        iOnOpponentHandShow = this;
        iUpdateAttackGoldHeart = this;
        iOwnAllyListener = this;
        iOpponentAllysListener = this;
        buildDecks();

        ChooseHouseDialog.getInstance().showChooseHouseDialog(this, iChooseHouseDialog);

    }

    private void buildDecks() {
        //loading.show();
        try {
            allCards = Helpers.getInstance().getDeck(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        for (Card cardTmp : allCards) {
            allCardsMap.put(Integer.valueOf(cardTmp.getId()), cardTmp);
            switch (cardTmp.getCardType()) {
                case "deck":
                    for (int i = 0; i < Integer.valueOf(cardTmp.getCount()); i++)
                        generalDeck.add(cardTmp);
                    break;
                case "hex":
                    hexDeck.add(cardTmp);
                    break;
                case "starter":
                    if (!cardTmp.getType().equals("ally")) {
                        for (int i = 0; i < Integer.valueOf(cardTmp.getCount()); i++) {
                            ownDeck.add(cardTmp);
                        }
                    }
                    break;
                case "book":
                    books.add(cardTmp);
                    break;
            }
        }
        allCards.clear();
        Common.allCardsMap = new HashMap<>(allCardsMap);
        allCardsMap.clear();
        Collections.shuffle(generalDeck);
        Collections.shuffle(ownDeck);

        if (loading.isShowing())
            loading.dismiss();

    }


    @Override
    public void onChooseHouse(DialogInterface dialog, int id) {
        dialog.dismiss();
        thisPlayer.setHouse(Common.Houses.get(id));
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/house").setValue(Common.Houses.get(id));


        ChooseAllyDialog.getInstance().showChooseAllyDialog(this, iChooseAllyDialog);
    }

    @Override
    public void onChooseAlly(DialogInterface dialog, int id) {
        dialog.dismiss();
        Card starterAlly = Common.allCardsMap.get(id);
        ownDeck.add(starterAlly);
        RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iRockPaperScissorsDialog);

    }

    @Override
    public void onPaperRockScissorsChoose(DialogInterface dialog, String hand) {
        dialog.dismiss();
        thisPlayer.setFirstPlayHand(hand);
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/firstPlayHand").setValue(hand);
        if (!loading.isShowing()) {
            loading.show();
        }
        checkForWinner();

    }

    private void checkForWinner() {
        if (opponent.getFirstPlayHand() != "" && thisPlayer.getFirstPlayHand() != "") {
            if (opponent.getFirstPlayHand().equals("rock")) {
                if (thisPlayer.getFirstPlayHand().equals("rock")) {
                    Toast.makeText(GameActivity.this, "Pokazali ste isto!", Toast.LENGTH_LONG).show();
                    thisPlayer.setFirstPlayHand("");
                    opponent.setFirstPlayHand("");
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/firstPlayHand").setValue("");
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iRockPaperScissorsDialog);
                } else if (thisPlayer.getFirstPlayHand().equals("paper")) {
                    startGame(thisPlayer.getPlayerName());
                } else if (thisPlayer.getFirstPlayHand().equals("scissors")) {
                    startGame(opponent.getPlayerName());
                }
            } else if (opponent.getFirstPlayHand().equals("paper")) {
                if (thisPlayer.getFirstPlayHand().equals("rock")) {
                    startGame(opponent.getPlayerName());
                } else if (thisPlayer.getFirstPlayHand().equals("paper")) {
                    Toast.makeText(GameActivity.this, "Pokazali ste isto!", Toast.LENGTH_LONG).show();
                    thisPlayer.setFirstPlayHand("");
                    opponent.setFirstPlayHand("");
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/firstPlayHand").setValue("");
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iRockPaperScissorsDialog);
                } else if (thisPlayer.getFirstPlayHand().equals("scissors")) {
                    startGame(thisPlayer.getPlayerName());
                }
            } else if (opponent.getFirstPlayHand().equals("scissors")) {
                if (thisPlayer.getFirstPlayHand().equals("rock")) {
                    startGame(thisPlayer.getPlayerName());
                } else if (thisPlayer.getFirstPlayHand().equals("paper")) {
                    startGame(opponent.getPlayerName());
                } else if (thisPlayer.getFirstPlayHand().equals("scissors")) {
                    Toast.makeText(GameActivity.this, "Pokazali ste isto!", Toast.LENGTH_LONG).show();
                    thisPlayer.setFirstPlayHand("");
                    opponent.setFirstPlayHand("");
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/firstPlayHand").setValue("");
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iRockPaperScissorsDialog);
                }
            }

            if (loading.isShowing()) {
                loading.dismiss();
            }
        }
    }

    private void startGame(String winner) {
        setUIView();
        if (valueEventListenerOpponentFirstHand != null) {
            database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/firstPlayHand").removeEventListener(valueEventListenerOpponentFirstHand);
            database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/house").removeEventListener(valueEventListenerOpponentHouse);
        }

        if (Common.currentUser.isHost()) {
            database.getReference("rooms/" + Common.currentRoomName + "/playing").setValue(winner);
            setClassroom();
        }
        // Need to be deleted in onDestroy!
        // All listeners for game will be here

        // Function moved to ListenerHelpers
//        valueEventListenerPlaying = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue().equals(thisPlayer.getPlayerName())) {
//                    playTurn();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        database.getReference("rooms/" + Common.currentRoomName + "/playing").addValueEventListener(valueEventListenerPlaying);
        valueEventListenerPlaying = listenerHelpers.startListenerForPlaying(this);
        valueEventListenerOpponentHandCards = listenerHelpers.setListenerForOpponentHandCards(iOnOpponentHandShow);
        valueEventListenerOpponentAlly = listenerHelpers.setListenerForOpponentAllys(iOpponentAllysListener);
        valueEventListenerOwnAlly = listenerHelpers.setListenerForOwnAllys(iOwnAllyListener);
        valueEventListenerClassroom = listenerHelpers.setListenerForClassroom(generalDeck, iOnClassroomShow);
        valueEventListenerForDiscardCardSpell = listenerHelpers.setListenerForDiscardCard(ownDeck,this);
        valueEventListenerForOpponentDiscardCards = listenerHelpers.setListenerForOpponentDiscardCards();
    }

    private void setUIView() {
        ownHand = findViewById(R.id.recycler_own_hand);
        opponentHand = findViewById(R.id.recycler_opponent_hand);
        recyclerViewClassroom = findViewById(R.id.classroom);
        recyclerOwnAllys = findViewById(R.id.recycler_own_allys);
        recyclerOpponentAlly = findViewById(R.id.opponent_allys);

        playersAttack = findViewById(R.id.playersAttack);
        playersGold = findViewById(R.id.playersGold);
        playersHeart = findViewById(R.id.playersHeart);

        iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();

        recyclerViewClassroom.setHasFixedSize(true);
        recyclerViewClassroom.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerViewClassroom.addItemDecoration(new SpacesItemDecoration(8));

        recyclerOwnAllys.setHasFixedSize(true);
        recyclerOwnAllys.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerOwnAllys.addItemDecoration(new SpacesItemDecoration(8));
        ownAllyAdapter = new OwnAllyAdapter(this, thisPlayer, opponent, ownDeck, hexDeck, database, iUpdateAttackGoldHeart);
        recyclerOwnAllys.setAdapter(ownAllyAdapter);

        LinearLayoutManager horizontalLayoutManagerOwnHand = new LinearLayoutManager(GameActivity.this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManagerOpponentHand = new LinearLayoutManager(GameActivity.this, LinearLayoutManager.HORIZONTAL, false);
        ownHand.setLayoutManager(horizontalLayoutManagerOwnHand);
        ownHand.setHasFixedSize(true);
        ownHand.addItemDecoration(new SpacesItemDecoration(8));

        opponentHand.setLayoutManager(horizontalLayoutManagerOpponentHand);
        opponentHand.setHasFixedSize(true);
        opponentHand.addItemDecoration(new SpacesItemDecoration(8));

        recyclerOpponentAlly.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerOpponentAlly.setHasFixedSize(true);
        recyclerOpponentAlly.addItemDecoration(new SpacesItemDecoration(8));

        finishMove = findViewById(R.id.finishMove);
        finishMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thisPlayer.getHeart() > 0) {
                    if (thisPlayer.getLives() < 7)
                        if (thisPlayer.getLives() + thisPlayer.getHeart() > 7) {
                            thisPlayer.setLives(7);
                        } else {
                            thisPlayer.setLives(thisPlayer.getLives() + thisPlayer.getHeart());
                        }
                    thisPlayer.setLives(thisPlayer.getLives() + thisPlayer.getHeart());
                }
                thisPlayer.setDiscarded(thisPlayer.getDiscarded() + "," + thisPlayer.getPlayedCards());
                thisPlayer.setPlayedCards("");
                opponent.setLives(opponent.getLives() - thisPlayer.getAttacks());
                if (opponent.getLives() < 1) {

                }
                thisPlayer.setCoins(0);
                thisPlayer.setAttacks(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/lives").setValue(thisPlayer.getLives());
                database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/lives").setValue(opponent.getLives());
                database.getReference("rooms/" + Common.currentRoomName + "/playing").setValue(opponent.getPlayerName());
                finishMove.setEnabled(false);
            }
        });
        finishMove.setEnabled(false);
    }

    private void setClassroom() {
        String stringClassroom = "";
        for (int i = 0; i < 4; i++) {
            stringClassroom = stringClassroom + generalDeck.get(i).getId();
            if (!(i == 3))
                stringClassroom = stringClassroom + ",";
            classRoom.add(generalDeck.get(i));
            generalDeck.remove(i);
        }
        database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(stringClassroom);
    }

    public void playTurn() {
        finishMove.setEnabled(true);

        // Check for Hexes in hand that opponent might put in players hand
//        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshotTmp : snapshot.getChildren()) {
//                    if (!(Objects.requireNonNull(snapshotTmp.getValue()).toString().equals(""))) {
//                        drawCards(snapshotTmp.getValue().toString());
//                    } else {
//                        drawCards("");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        drawCards("");
    }

    private void drawCards(String stringHand) {
        // It will come 1,2,3 -> which may be hexes
        if (!stringHand.equals("")) {
            stringHand += ",";
        }
        int i;
        StringBuilder stringHandBuilder = new StringBuilder(stringHand);
        StringBuilder hexes = new StringBuilder(stringHand);
        for (i = 0; i < 5; i++) {
            hand.add(ownDeck.get(0));
            if (ownDeck.get(0).getType().equals("hex")) {
                if (hexes.equals(""))
                    hexes.append(ownHand.getId());
                else
                    hexes.append(",").append(ownHand.getId());

            }
            stringHandBuilder.append(ownDeck.get(0).getId());
            if (!(i == 4))
                stringHandBuilder.append(",");
            ownDeck.remove(0);
        }
        if (!thisPlayer.getAlly().equals("")) {
            ArrayList<Card> Allies = Helpers.getInstance().returnCardsFromString(thisPlayer.getAlly());
            ownAllyAdapter.updateAllies();
        }

        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(stringHandBuilder.toString());

        thisPlayer.setHexes(hexes.toString());

        // testing purpose
        hand.add(Common.allCardsMap.get(10));
        iOnOwnHandShow.onShowHand(hand);
    }

    @Override
    public void onShowClassroom(ArrayList<Card> classRoomTmp) {
        classRoom = classRoomTmp;
        ClassroomAdapter imageAdapter = new ClassroomAdapter(this, classRoom, database, thisPlayer, iUpdateAttackGoldHeart);
        recyclerViewClassroom.setAdapter(imageAdapter);
    }

    @Override
    public void onShowHand(ArrayList<Card> cards) {
        OwnHandAdapter ownHandAdapter = new OwnHandAdapter(this, cards, thisPlayer, opponent, ownDeck, hexDeck, database, iUpdateAttackGoldHeart, iOwnAllyListener, classRoom);
        ownHand.setAdapter(ownHandAdapter);
    }

    @Override
    public void onOpponentShowHand(ArrayList<Card> opponentsHand) {
        OpponentHandAdapter opponentHandAdapter = new OpponentHandAdapter(this, opponentsHand);
        opponentHand.setAdapter(opponentHandAdapter);

    }

    @Override
    public void onOpponentAllysShow(ArrayList<Card> allys) {
        OpponentAllyAdapter opponentAllyAdapter = new OpponentAllyAdapter(this, allys);
        recyclerOpponentAlly.setAdapter(opponentAllyAdapter);
    }

    @Override
    public void onOwnAllyChange(Card ally) {
        ownAllyAdapter.addAlly(ally);
    }

    @Override
    public void setNewAllys(ArrayList<Card> allys) {
        ownAllyAdapter = new OwnAllyAdapter(this, allys, thisPlayer, opponent, ownDeck, hexDeck, database, iUpdateAttackGoldHeart);
        recyclerOwnAllys.setAdapter(ownAllyAdapter);
    }

    @Override
    public void onShowHandEmpty() {
        ArrayList<Card> opponentsHand = new ArrayList<>();
        OpponentHandAdapter opponentHandAdapter = new OpponentHandAdapter(this, opponentsHand);
        opponentHand.setAdapter(opponentHandAdapter);
    }

    private void setListenerForFirstOpponentHand() {
        valueEventListenerOpponentFirstHand = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    opponent.setFirstPlayHand(snapshot.getValue().toString());
                    checkForWinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/firstPlayHand").addValueEventListener(valueEventListenerOpponentFirstHand);
    }

    private void getPlayers() {
        database = FirebaseDatabase.getInstance();
        database.getReference("rooms/" + Common.currentRoomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotTmp : snapshot.getChildren()) {
                    if (!(snapshotTmp.getKey().equals("startGame")) && !(snapshotTmp.getKey().equals("banished")) && !(snapshotTmp.getKey().equals("classroom"))
                            && !(snapshotTmp.getKey().equals("playing"))) {
                        Player playerTmp = snapshotTmp.getValue(Player.class);
                        if (playerTmp.getPlayerName().equals(Common.currentUser.getUserName())) {
                            thisPlayer = new Player(playerTmp.getPlayerName());
                        } else {
                            opponent = new Player(playerTmp.getPlayerName());
                        }
                    }
                }
                listenerHelpers = new ListenerHelpers(database, thisPlayer, opponent);
                valueEventListenerOpponentHouse = listenerHelpers.getOpponentHouse();
                setListenerForFirstOpponentHand();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deckNeedShuffle() {
        if (ownDeck.size() < 6 && !thisPlayer.getDiscarded().equals("")) {
            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndHand(thisPlayer, ownDeck);
            Collections.shuffle(ownDeck);
        }
    }

    @Override
    public void onUpdateAttackGoldHeart() {
        playersHeart.setText(String.valueOf(thisPlayer.getHeart()));
        playersAttack.setText(String.valueOf(thisPlayer.getAttacks()));
        playersGold.setText(String.valueOf(thisPlayer.getCoins()));
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/coins").setValue(thisPlayer.getCoins());
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/heart").setValue(thisPlayer.getHeart());
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/attacks").setValue(thisPlayer.getAttacks());
    }
}