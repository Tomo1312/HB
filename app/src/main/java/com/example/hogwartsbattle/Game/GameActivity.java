package com.example.hogwartsbattle.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogwartsbattle.Adapters.ClassroomAdapter;
import com.example.hogwartsbattle.Adapters.OpponentAllyAdapter;
import com.example.hogwartsbattle.Adapters.OpponentHandAdapter;
import com.example.hogwartsbattle.Adapters.OwnAllyAdapter;
import com.example.hogwartsbattle.Adapters.OwnHandAdapter;
import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.HarryMediaPlayer;
import com.example.hogwartsbattle.CustomDialog.CardBuyDialog;
import com.example.hogwartsbattle.CustomDialog.DiscardCard;
import com.example.hogwartsbattle.CustomDialog.ShowCardDialog;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Common.SpacesItemDecoration;
import com.example.hogwartsbattle.CustomDialog.ChooseAllyDialog;
import com.example.hogwartsbattle.CustomDialog.ChooseHouseDialog;
import com.example.hogwartsbattle.CustomDialog.RockPaperScissorsDialog;
import com.example.hogwartsbattle.Helpers.ListenerHelpers;
import com.example.hogwartsbattle.Interface.IChooseDialog;
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
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;


public class GameActivity extends AppCompatActivity implements IChooseDialog {

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
    HarryMediaPlayer mediaPlayer;

    IChooseDialog iChooseDialog;

    FirebaseDatabase database;

    ValueEventListener valueEventListenerOpponentFirstHand, valueEventListenerPlaying,
            valueEventListenerOpponentHandCards, valueEventListenerClassroom, valueEventListenerOpponentHouse,
            valueEventListenerOpponentAlly, valueEventListenerOwnAlly, valueEventListenerForDiscardCardSpell,
            valueEventListenerBoughtClassroomCard, valueEventListenerBanishedCard, valueEventListenerLibrary;

    RecyclerView ownHand, opponentHand, recyclerViewClassroom, recyclerOwnAllys, recyclerOpponentAlly;
    TextView playersGold, playersAttack, playersHeart;

    OwnAllyAdapter ownAllyAdapter;
    OwnHandAdapter ownHandAdapter;
    OpponentHandAdapter opponentHandAdapter;
    ListenerHelpers listenerHelpers;
    Button finishMove;

    ImageView opponent_house_image, own_house_image;
    ImageView opponent_life_start, opponent_life_1, opponent_life_2, opponent_life_3, opponent_life_4, opponent_life_5, opponent_life_6;
    ImageView own_life_start, own_life_1, own_life_2, own_life_3, own_life_4, own_life_5, own_life_6;
    ImageView opponent_death_1, opponent_death_2, opponent_death_3;
    ImageView own_death_1, own_death_2, own_death_3;

    ImageView own_discard_pile, own_deck_pile;

    ImageView opponent_visible_helper, own_visible_helper;

    ImageView libraryImageView;
    int library = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mediaPlayer = new HarryMediaPlayer(this);
        mediaPlayer.startPlaying();
        loading = new SpotsDialog.Builder().setCancelable(false).setContext(GameActivity.this).build();
        loading.show();

        getPlayers();
        opponent_house_image = findViewById(R.id.opponent_house_image);
        own_house_image = findViewById(R.id.own_house_image);
        setUiForPlayerImage();

        iChooseDialog = this;
        buildDecks();
    }

    private void setUiForPlayerImage() {
        opponent_life_start = findViewById(R.id.opponent_life_start);
        opponent_life_1 = findViewById(R.id.opponent_life_1);
        opponent_life_2 = findViewById(R.id.opponent_life_2);
        opponent_life_3 = findViewById(R.id.opponent_life_3);
        opponent_life_4 = findViewById(R.id.opponent_life_4);
        opponent_life_5 = findViewById(R.id.opponent_life_5);
        opponent_life_6 = findViewById(R.id.opponent_life_6);

        own_life_start = findViewById(R.id.own_life_start);
        own_life_1 = findViewById(R.id.own_life_1);
        own_life_2 = findViewById(R.id.own_life_2);
        own_life_3 = findViewById(R.id.own_life_3);
        own_life_4 = findViewById(R.id.own_life_4);
        own_life_5 = findViewById(R.id.own_life_5);
        own_life_6 = findViewById(R.id.own_life_6);

        opponent_death_1 = findViewById(R.id.opponent_death_1);
        opponent_death_2 = findViewById(R.id.opponent_death_2);
        opponent_death_3 = findViewById(R.id.opponent_death_3);
        own_death_1 = findViewById(R.id.own_death_1);
        own_death_2 = findViewById(R.id.own_death_2);
        own_death_3 = findViewById(R.id.own_death_3);
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
        Collections.shuffle(generalDeck);
        Collections.shuffle(generalDeck);
        Collections.shuffle(generalDeck);
        Collections.shuffle(ownDeck);

        if (loading.isShowing()) {
            loading.dismiss();
            ChooseHouseDialog.getInstance().showChooseHouseDialog(this, iChooseDialog);
        }

    }


    @Override
    public void onChooseHouse(DialogInterface dialog, int id) {
        dialog.dismiss();
        thisPlayer.setHouse(Common.Houses.get(id));
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/house").setValue(Common.Houses.get(id));


        int house = getResources().getIdentifier("drawable/" + thisPlayer.getHouse(), null, getPackageName());
        int house1 = getResources().getIdentifier("drawable/" + thisPlayer.getHouse() + "1", null, getPackageName());
        own_house_image.setImageResource(house);

        own_life_start.setImageResource(house1);
        own_life_1.setImageResource(house1);
        own_life_2.setImageResource(house1);
        own_life_3.setImageResource(house1);
        own_life_4.setImageResource(house1);
        own_life_5.setImageResource(house1);
        own_life_6.setImageResource(house1);

        ChooseAllyDialog.getInstance().showChooseAllyDialog(this, iChooseDialog);
    }

    @Override
    public void onChooseAlly(DialogInterface dialog, int id) {
        dialog.dismiss();
        Card starterAlly = Common.allCardsMap.get(id);
        ownDeck.add(starterAlly);
        Collections.shuffle(ownDeck);
        RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iChooseDialog);
    }

    @Override
    public void onChooseReturnToLobby(DialogInterface dialog) {

    }

    @Override
    public void onPaperRockScissorsChoose(DialogInterface dialog, String hand) {
        if (!loading.isShowing())
            loading.show();
        dialog.dismiss();
        thisPlayer.setFirstPlayHand(hand);
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/firstPlayHand").setValue(hand);
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
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iChooseDialog);
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
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iChooseDialog);
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
                    RockPaperScissorsDialog.getInstance().showRockPaperScissorsDialog(this, iChooseDialog);
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

        valueEventListenerPlaying = listenerHelpers.startListenerForPlaying(GameActivity.this);
        valueEventListenerOpponentHandCards = listenerHelpers.setListenerForOpponentHandCards(iChooseDialog);
        valueEventListenerOpponentAlly = listenerHelpers.setListenerForOpponentAllys(iChooseDialog);
        valueEventListenerOwnAlly = listenerHelpers.setListenerForOwnAllys(iChooseDialog);
        valueEventListenerClassroom = listenerHelpers.setListenerForClassroom(generalDeck, iChooseDialog);
        valueEventListenerForDiscardCardSpell = listenerHelpers.setListenerForDiscardCard(ownDeck, GameActivity.this);
        valueEventListenerLibrary = listenerHelpers.setListenerForLibrary(iChooseDialog);
        valueEventListenerBoughtClassroomCard = listenerHelpers.setListenerForBoughtClassroomCard(GameActivity.this);
        valueEventListenerBanishedCard = listenerHelpers.setListenerForBanishedCard(GameActivity.this);

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

        libraryImageView = findViewById(R.id.books);

        iChooseDialog.onUpdateAttackGoldHeart();

        recyclerViewClassroom.setHasFixedSize(true);
        recyclerViewClassroom.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerViewClassroom.addItemDecoration(new SpacesItemDecoration(8));

        recyclerOwnAllys.setHasFixedSize(true);
        recyclerOwnAllys.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerOwnAllys.addItemDecoration(new SpacesItemDecoration(8));
        ownAllyAdapter = new OwnAllyAdapter(this, thisPlayer, opponent, ownDeck, hexDeck, classRoom, database, iChooseDialog);
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
                if (!thisPlayer.getPlayedCards().equals(""))
                    thisPlayer.setDiscarded(thisPlayer.getPlayedCards());

                thisPlayer.setPlayedCards("");

                if (!thisPlayer.getHand().equals(""))
                    ownHandAdapter.cleanCardsInHand();
                opponent.setLives(opponent.getLives() - thisPlayer.getAttacks());

                // Player got death!
                // Let's start again!
                if (opponent.getLives() < 1) {
                    Toast.makeText(GameActivity.this, "You have stunned " + opponent.getPlayerName(), Toast.LENGTH_LONG).show();
                    database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/lives").setValue(opponent.getLives());
                    thisPlayer.setLives(7);
                    opponent.setLives(7);
                    opponent.setDeaths(opponent.getDeaths() + 1);

                    if (!thisPlayer.getAlly().equals(""))
                        thisPlayer.setDiscarded(thisPlayer.getAlly());
                    if (!thisPlayer.getDiscarded().equals("")) {
                        ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck);
                    }
                    thisPlayer.setDiscardedToEmpty();
                    thisPlayer.setAlly("");

                    ownAllyAdapter = new OwnAllyAdapter(GameActivity.this, thisPlayer, opponent, ownDeck, hexDeck, classRoom, database, iChooseDialog);
                    recyclerOwnAllys.setAdapter(ownAllyAdapter);

                    setDeathImage();

                } else {
                    database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/lives").setValue(opponent.getLives());
                }

                if (thisPlayer.getHeart() > 0 && thisPlayer.getLives() < 7) {
                    if (thisPlayer.getLives() + thisPlayer.getHeart() >= 7) {
                        thisPlayer.setLives(7);
                    } else {
                        thisPlayer.setLives(thisPlayer.getLives() + thisPlayer.getHeart());
                    }
                }
                thisPlayer.setCoins(0);
                thisPlayer.setAttacks(0);
                thisPlayer.setHeart(0);
                thisPlayer.setPlaying(false);
                thisPlayer.setHexes("");
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue(thisPlayer.getDiscarded());
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/lives").setValue(thisPlayer.getLives());
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue("");

                database.getReference("rooms/" + Common.currentRoomName + "/playing").setValue(opponent.getPlayerName());
                finishMove.setEnabled(false);
                onUpdateAttackGoldHeart();
                updatePlayerImage();
                discardPileShow();

            }
        });
        finishMove.setEnabled(false);
        libraryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thisPlayer.isPlaying()) {
                    CardBuyDialog cardBuyDialog = new CardBuyDialog(GameActivity.this, classRoom, library, Common.allCardsMap.get(0), thisPlayer, database, iChooseDialog, ownDeck);
                    cardBuyDialog.showDialog();
                } else
                    Toast.makeText(getApplicationContext(), "In library is " + String.valueOf(library) + " books", Toast.LENGTH_LONG).show();
            }
        });

        // Set opponent player image
        int opponentHouse = getResources().getIdentifier("drawable/" + opponent.getHouse() + "1", null, getPackageName());
        opponent_life_start.setImageResource(opponentHouse);
        opponent_life_1.setImageResource(opponentHouse);
        opponent_life_2.setImageResource(opponentHouse);
        opponent_life_3.setImageResource(opponentHouse);
        opponent_life_4.setImageResource(opponentHouse);
        opponent_life_5.setImageResource(opponentHouse);
        opponent_life_6.setImageResource(opponentHouse);

        opponent_visible_helper = opponent_life_start;
        own_visible_helper = own_life_start;
        own_life_start.setVisibility(View.VISIBLE);
        opponent_life_start.setVisibility(View.VISIBLE);

        own_discard_pile = findViewById(R.id.own_discard_pile);
        own_deck_pile = findViewById(R.id.own_deck_pile);

        own_discard_pile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thisPlayer.getDiscarded().equals("")) {
                    DiscardCard discardCard = new DiscardCard(GameActivity.this, database, thisPlayer.getDiscarded(), 12, opponent, thisPlayer);
                    discardCard.showDialog();
                }
            }
        });
        own_deck_pile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You have " + ownDeck.size() + " in deck!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDeathImage() {
        switch (thisPlayer.getDeaths()) {
            case 1:
                own_death_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                own_death_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                own_death_3.setVisibility(View.VISIBLE);
                finishGame(opponent);
                break;
        }
        switch (opponent.getDeaths()) {
            case 1:
                opponent_death_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                opponent_death_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                opponent_death_3.setVisibility(View.VISIBLE);
                finishGame(thisPlayer);
                break;
        }
    }

    private void finishGame(Player winner) {
        killAllListeners();
    }

    private void killAllListeners() {
        if (valueEventListenerPlaying != null)
            database.getReference("rooms/" + Common.currentRoomName + "/playing").removeEventListener(valueEventListenerPlaying);
        if (valueEventListenerOpponentHandCards != null)
            database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/hand").removeEventListener(valueEventListenerOpponentHandCards);
        if (valueEventListenerOpponentAlly != null)
            database.getReference("rooms/" + Common.currentRoomName + "/" + opponent.getPlayerName() + "/ally").addValueEventListener(valueEventListenerOpponentAlly);
        if (valueEventListenerOwnAlly != null)
            database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/idAllyToDiscard").addValueEventListener(valueEventListenerOwnAlly);
        if (valueEventListenerClassroom != null)
            database.getReference("rooms/" + Common.currentRoomName + "/classroom").addValueEventListener(valueEventListenerClassroom);
        if (valueEventListenerForDiscardCardSpell != null)
            database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discardCardSpell").addValueEventListener(valueEventListenerForDiscardCardSpell);
        if (valueEventListenerLibrary != null)
            database.getReference("rooms/" + Common.currentRoomName + "/library").addValueEventListener(valueEventListenerLibrary);
        if (valueEventListenerBoughtClassroomCard != null)
            database.getReference("rooms/" + Common.currentRoomName + "/classroomBought").addValueEventListener(valueEventListenerBoughtClassroomCard);
        if (valueEventListenerBanishedCard != null)
            database.getReference("rooms/" + Common.currentRoomName + "/banished").addValueEventListener(valueEventListenerBanishedCard);
    }


    public void updatePlayerImage() {
        opponent_visible_helper.setVisibility(View.INVISIBLE);
        own_visible_helper.setVisibility(View.INVISIBLE);

        switch (opponent.getLives()) {
            case 6:
                opponent_visible_helper = opponent_life_1;
                break;
            case 5:
                opponent_visible_helper = opponent_life_2;
                break;
            case 4:
                opponent_visible_helper = opponent_life_3;
                break;
            case 3:
                opponent_visible_helper = opponent_life_4;
                break;
            case 2:
                opponent_visible_helper = opponent_life_5;
                break;
            case 1:
                opponent_visible_helper = opponent_life_6;
                break;
            default:
                opponent_visible_helper = opponent_life_start;
                break;
        }

        switch (thisPlayer.getLives()) {
            case 6:
                own_visible_helper = own_life_1;
                break;
            case 5:
                own_visible_helper = own_life_2;
                break;
            case 4:
                own_visible_helper = own_life_3;
                break;
            case 3:
                own_visible_helper = own_life_4;
                break;
            case 2:
                own_visible_helper = own_life_5;
                break;
            case 1:
                own_visible_helper = own_life_6;
                break;
            default:
                own_visible_helper = own_life_start;
                break;
        }
        opponent_visible_helper.setVisibility(View.VISIBLE);
        own_visible_helper.setVisibility(View.VISIBLE);
    }

    private void setClassroom() {
        String stringClassroom = "";
        for (int i = 0; i < 4; i++) {
            stringClassroom = stringClassroom + generalDeck.get(0).getId();
            if (!(i == 3))
                stringClassroom = stringClassroom + ",";
            classRoom.add(generalDeck.get(0));
            generalDeck.remove(0);
        }
        database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(stringClassroom);
    }

    public void discardPileShow() {
        if (thisPlayer.getDiscarded().equals(""))
            own_discard_pile.setVisibility(View.INVISIBLE);
        else
            own_discard_pile.setVisibility(View.VISIBLE);
    }

    public void prepareForNewRound() {
        setDeathImage();
        Toast.makeText(GameActivity.this, opponent.getPlayerName() + " stunned you!", Toast.LENGTH_LONG).show();
        thisPlayer.setLives(7);
        opponent.setLives(7);
        thisPlayer.setDeaths(thisPlayer.getDeaths() + 1);

        if (!thisPlayer.getAlly().equals(""))
            thisPlayer.setDiscarded(thisPlayer.getAlly());

        if (!thisPlayer.getDiscarded().equals(""))
            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck);


        thisPlayer.setDiscardedToEmpty();
        thisPlayer.setAlly("");
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/ally").setValue("");
        ownAllyAdapter = new OwnAllyAdapter(GameActivity.this, thisPlayer, opponent, ownDeck, hexDeck, classRoom, database, iChooseDialog);
        recyclerOwnAllys.setAdapter(ownAllyAdapter);

    }

    public void playTurn() {
        loading.show();
        finishMove.setEnabled(true);
        thisPlayer.setPlaying(true);

        listenerHelpers.startGettingInfoForTurn(GameActivity.this, loading);
    }

    public void drawCards(String stringHand) {
        updatePlayerImage();
        // It will come 1,2,3 -> which may be hexes
        // Hexes that are active are going to player.getHexes, but also need to be added to playedCards
        StringBuilder hexesString = new StringBuilder(stringHand);
        if (!stringHand.equals("")) {
            hand.addAll(Helpers.getInstance().returnCardsFromString(stringHand));
            stringHand += ",";
        }
        int i;
        StringBuilder stringHandBuilder = new StringBuilder(stringHand);

        deckNeedShuffle(5);
        for (i = 0; i < 5; i++) {
            if (ownDeck.get(0).getCardType().equals("hex")) {
                if (hexesString.toString().equals(""))
                    hexesString.append(ownHand.getId());
                else
                    hexesString.append(",").append(ownHand.getId());
            }
            stringHandBuilder.append(ownDeck.get(0).getId());
            if (!(i == 4))
                stringHandBuilder.append(",");
            hand.add(ownDeck.get(0));

            ownDeck.remove(0);
        }


        for (Card cardTmp : hand) {
            if (cardTmp.getId().equals("80")) {
                hand.remove(cardTmp);
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(cardTmp.getId());
            } else if (cardTmp.getId().equals("81")) {
                if (!thisPlayer.getAlly().equals("")) {
                    DiscardCard discardOwnAlly = new DiscardCard(GameActivity.this, database, thisPlayer.getAlly(), 12, null, thisPlayer);
                    discardOwnAlly.setIChooseDialog(iChooseDialog);
                    discardOwnAlly.showDialog();
                } else {
                    Toast.makeText(GameActivity.this, "You lucky wizard, you don't have any ally currently active!", Toast.LENGTH_LONG).show();
                }
            } else if (cardTmp.getId().equals("84")) {
                Toast.makeText(GameActivity.this, "You need to banish top card of your deck because of Jelly-brain jinx!", Toast.LENGTH_LONG).show();
                deckNeedShuffle(1);
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(ownDeck.get(0));
                ownDeck.remove(0);
                hand.remove(cardTmp);
            } else if (cardTmp.getId().equals("87")) {
                Toast.makeText(GameActivity.this, "You got 2 hexes in discard pile and banished Geminio!", Toast.LENGTH_LONG).show();
                deckNeedShuffle(1);
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(cardTmp.getId());
                hand.remove(cardTmp);
                StringBuilder newTwoHexes = new StringBuilder();
                newTwoHexes.append(hexDeck.get(0).getId()).append(",").append(hexDeck.get(1).getId());
                hexDeck.remove(0);
                hexDeck.remove(0);
                thisPlayer.setDiscarded(newTwoHexes.toString());
            }
        }

        if (!thisPlayer.getAlly().equals("") && thisPlayer.getHexes().contains("89")) {
            ownAllyAdapter.updateAllies();
        }

        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(stringHandBuilder.toString());
        thisPlayer.setHand(stringHandBuilder.toString());
        thisPlayer.setHexes(hexesString.toString());
        thisPlayer.setPlayedCards(hexesString.toString());

//        Log.e("GameActivity", "Player hexes:" + thisPlayer.getHexes());

        // testing purpose
        // hand.add(Common.allCardsMap.get(7));

        ownHandAdapter = new OwnHandAdapter(this, hand, thisPlayer, opponent, ownDeck, hexDeck,
                database, iChooseDialog, classRoom);
        ownHandAdapter.setLibrary(library);

        // so we can draw card from ally!
        if (!ownAllyAdapter.checkIfHandAdapterIsSet())
            ownAllyAdapter.setOwnHandAdapter(ownHandAdapter);
        ownHand.setAdapter(ownHandAdapter);
    }

    @Override
    public void onShowClassroom(ArrayList<Card> classRoomTmp) {
        classRoom = classRoomTmp;
        ClassroomAdapter imageAdapter = new ClassroomAdapter(this, classRoom, database, thisPlayer, iChooseDialog);
        imageAdapter.setOwnDeck(ownDeck);
        recyclerViewClassroom.setAdapter(imageAdapter);
    }

    @Override
    public void onOpponentShowHand(ArrayList<Card> opponentsHand) {
        opponentHandAdapter = new OpponentHandAdapter(this, opponentsHand);
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
    public void removeAlly(Card removedAlly) {
        ownAllyAdapter.removeAlly(removedAlly);
    }

    @Override
    public void setAllyAvailable(Card ally) {
        ownAllyAdapter.setAllyAvailable(ally);
    }

    @Override
    public void onShowHandEmpty() {
        opponentHandAdapter = new OpponentHandAdapter(this, new ArrayList<>());
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
                            && !(snapshotTmp.getKey().equals("playing")) && !(snapshotTmp.getKey().equals("classroomBought"))
                            && !(snapshotTmp.getKey().equals("library"))) {
                        Player playerTmp = snapshotTmp.getValue(Player.class);
                        if (playerTmp.getPlayerName().equals(Common.currentUser.getUserName())) {
                            thisPlayer = new Player(playerTmp.getPlayerName());
                        } else {
                            opponent = new Player(playerTmp.getPlayerName());
                        }
                    }
                }
                listenerHelpers = new ListenerHelpers(database, thisPlayer, opponent);
                valueEventListenerOpponentHouse = listenerHelpers.getOpponentHouse(GameActivity.this, opponent_house_image);
                setListenerForFirstOpponentHand();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deckNeedShuffle(int sizeRequested) {
        if (ownDeck.size() < sizeRequested && !thisPlayer.getDiscarded().equals("")) {
            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck);
            thisPlayer.setDiscardedToEmpty();
            database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue("");
            discardPileShow();
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

    @Override
    public void onLibraryChange(int libraryFromDatabase) {
        library = libraryFromDatabase;
        if (library == 0) {
            libraryImageView.setVisibility(View.INVISIBLE);
            libraryImageView.setEnabled(false);
        } else {
            libraryImageView.setVisibility(View.VISIBLE);
            libraryImageView.setEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.stopMediaPlayer();
        killAllListeners();
        super.onStop();
    }
}