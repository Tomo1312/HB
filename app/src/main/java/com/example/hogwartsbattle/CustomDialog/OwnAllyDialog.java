package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hogwartsbattle.Adapters.OwnHandAdapter;
import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Interface.IDisableAllyListener;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class OwnAllyDialog extends CustomDialog {

    Player thisPlayer;
    Player opponentPlayer;
    Card activeAlly;
    ArrayList<Card> ownDeck, classroom, hexes;
    FirebaseDatabase database;
    int cardSpells, cardSpellsUsed;
    IDisableAllyListener iDisableAllyListener;
    LinearLayout layoutAllyAttack;
    OwnHandAdapter ownHandAdapter;
    IChooseDialog iChooseDialog;

    public OwnAllyDialog(Context context, Card activeAlly, Player thisPlayer, Player opponentPlayer, ArrayList<Card> ownDeck,
                         ArrayList<Card> classroom, ArrayList<Card> hexes, FirebaseDatabase database,
                         IChooseDialog iChooseDialog, IDisableAllyListener iDisableAllyListener, OwnHandAdapter ownHandAdapter) {
        super(context, 0);
        this.activeAlly = activeAlly;
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.classroom = classroom;
        this.database = database;
        this.hexes = hexes;
        this.iChooseDialog = iChooseDialog;
        this.iDisableAllyListener = iDisableAllyListener;
        this.ownHandAdapter = ownHandAdapter;
    }

    boolean chooseEffect, mayUseSpell = false;
    ArrayList<Button> EffectsToDisable = new ArrayList<>();

    ImageView cardImage;


    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_own_ally_active);

        cardSpellsUsed = 0;
        chooseEffect = false;

        cardImage = dialog.findViewById(R.id.card_image);
        layoutAllyAttack = dialog.findViewById(R.id.layout_ally_attack);

//        setOnClickButtons(dialog);

        showButtonsOnCardId(dialog);

        String cardName = "ally" + activeAlly.getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        cardImage.setImageResource(id);

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showButtonsOnCardId(Dialog dialog) {
        switch (Integer.parseInt(activeAlly.getId())) {
            case 1:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.SAVE_GOLD, dialog));
                EffectsToDisable.add(createButton(Common.COLLECT_ALL_GOLD, dialog));
                break;
            case 3:
                cardSpells = 1;
                createButton(Common.HEART, dialog);
                break;
            case 38:
            case 67:
            case 68:
                cardSpells = 1;
                createButton(Common.REVEAL_TOP_CARD, dialog);
                break;
            case 39:
                for (Card card : Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards())) {
                    if (card.getType().equals("item")) {
                        cardSpells = 2;
                        createButton(Common.GOLD, dialog);
                        createButton(Common.HEART, dialog);
                        break;
                    }
                }
                break;
            case 40:
                for (Card card : classroom) {
                    if (card.getType().equals("ally")) {
                        cardSpells = 2;
                        createButton(Common.ATTACK, dialog);
                        createButton(Common.HEART, dialog);
                        break;
                    }
                }
                break;
            case 41:
                if (!thisPlayer.getHexes().equals("")) {
                    for (Card card : Helpers.getInstance().returnCardsFromString(thisPlayer.getHexes())) {
                        if (card.getType().equals("ally")) {
                            int addAttack = Integer.parseInt(activeAlly.getAttack()) + 1;
                            int addHeart = Integer.parseInt(activeAlly.getHeart()) + 1;
                            activeAlly.setAttack(String.valueOf(addAttack));
                            activeAlly.setHeart(String.valueOf(addHeart));
                        }
                    }
                    cardSpells = 2;
                    createButton(Common.ATTACK, dialog);
                    createButton(Common.HEART, dialog);
                }
                break;
            case 42:
                if (thisPlayer.getHeart() > 0) {
                    cardSpells = 2;
                    createButton(Common.ATTACK, dialog);
                    createButton(Common.DRAW_CARD, dialog);
                }
                break;
            case 43:
                cardSpells = 4;
                createButton(Common.ATTACK, dialog);
                createButton(Common.GOLD, dialog);
                createButton(Common.HEART, dialog);
                createButton(Common.DRAW_CARD, dialog);
                break;
            case 49:
                cardSpells = 1;
                createButton(Common.DISCARD_CARD, dialog);
                break;
            case 50:
                if (checkIfPlayerPlayedItem()) {
                    cardSpells = 2;
                    createButton(Common.HEART, dialog);
                    createButton(Common.DRAW_CARD, dialog);
                }
                break;
            case 51:
                cardSpells = 1;
                createButton(Common.DRAW_CARD_THEN_DISCARD_ANY, dialog);
                break;
            case 52:
                cardSpells = 1;
                createButton(Common.DISCARD_SPELL, dialog);
                break;
            case 58:
                if (checkIfPlayerPlayedItem()) {
                    cardSpells = 2;
                    createButton(Common.ATTACK, dialog);
                    createButton(Common.GOLD, dialog);
                }
                break;
            case 59:
                if (checkIfPlayerPlayedThreeSpells()) {
                    cardSpells = 1;
                    createButton(Common.DRAW_CARD, dialog);
                }
                break;
            case 2:
            case 60:
                if (checkIfPlayerPlayedThreeSpells()) {
                    cardSpells = 1;
                    createButton(Common.ATTACK, dialog);
                }
                break;
            case 61:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog);
                createButton(Common.GOLD, dialog);
                break;
            case 69:
                if (checkIfPlayerPlayedItem()) {
                    cardSpells = 1;
                    createButton(Common.ATTACK_PER_OPPONENT_ALLY, dialog);
                }
                break;
            case 70:
                cardSpells = 1;
                createButton(Common.ATTACK, dialog);
                createButton(Common.HEART, dialog);
                createButton(Common.BANISH_HEX_FROM_DISCARD_PILE, dialog);
                break;
            case 76:
                if (checkIfPlayerPlayedItem()) {
                    cardSpells = 1;
                    createButton(Common.HEART, dialog);
                }
                break;
            case 77:
                if (!thisPlayer.getHexes().equals("")) {
                    cardSpells = 3;
                    mayUseSpell = true;
                    createButton(Common.BANISH_PLAYED_HEX, dialog);
                    createButton(Common.HEART, dialog);
                    createButton(Common.DRAW_CARD, dialog);
                }
                break;
            case 78:
                if (thisPlayer.getHeart() > 0) {
                    cardSpells = 2;
                    createButton(Common.HEART, dialog);
                    createButton(Common.ATTACK, dialog);
                }
                break;
            case 79:
                cardSpells = 2;
                createButton(Common.GOLD, dialog);
                createButton(Common.HEART, dialog);
                break;
        }
    }

    private boolean checkIfPlayerPlayedItem() {
        if (thisPlayer.getPlayedCards().equals(""))
            Toast.makeText(context, "You haven't played any card yet!", Toast.LENGTH_LONG).show();
        else {
            for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards())) {
                if (cardTmp.getType().equals("item")) {
                    return true;
                }
            }
            Toast.makeText(context, "You haven't played any item!", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private boolean checkIfPlayerPlayedThreeSpells() {
        if (thisPlayer.getPlayedCards().equals("")) {
            Toast.makeText(context, "You haven't played any card yet!", Toast.LENGTH_LONG).show();
        } else {
            int playedSpells = 0;
            for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards())) {
                if (cardTmp.getType().equals("spell")) {
                    playedSpells++;
                    if (playedSpells == 3)
                        return true;
                }
            }
            Toast.makeText(context, "You haven't played 3 spells!", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private Button createButton(String title, Dialog dialog) {
        Button newButton = new Button(context);
        newButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newButton.setText(title);
        if (activeAlly.getId().equals("1") && title.equals(Common.COLLECT_ALL_GOLD)) {
            newButton.setText(title + " (" + activeAlly.getSavedGold() + ")");
        }
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                DiscardCard discardCard;
                ArrayList<Card> discardedCards;
                switch (title) {
                    case Common.GOLD:
                        thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeAlly.getCoins()));
                        break;
                    case Common.HEART:
                        thisPlayer.setHeart(thisPlayer.getHeart() + Integer.parseInt(activeAlly.getHeart()));
                        break;
                    case Common.ATTACK:
                        thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeAlly.getAttack()));
                        break;
                    case Common.REVEAL_TOP_CARD:
                        if (ownDeck.size() < 1) {
                            shuffleDeck();
                        }
                        String title = "Top card:";
                        ShowCardDialog.getInstance().showCardDialog(context, ownDeck.get(0), title);
                        if (Integer.parseInt(activeAlly.getId()) == 38) {
                            if (Integer.parseInt(ownDeck.get(0).getCost()) > 0) {
                                thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeAlly.getAttack()));
                                thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeAlly.getCoins()));
                            }
                        } else if (Integer.parseInt(activeAlly.getId()) == 67) {
                            if (Integer.parseInt(ownDeck.get(0).getCost()) > 0) {
                                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(2);
                            }
                        } else if (Integer.parseInt(activeAlly.getId()) == 68) {
                            if (Integer.parseInt(ownDeck.get(0).getCost()) > 0) {
                                if (opponentPlayer.getHand().equals(""))
                                    opponentPlayer.setHand(hexes.get(0).getId());
                                else
                                    opponentPlayer.setHand(opponentPlayer.getHand() + "," + hexes.get(0).getId());
                                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/hand").setValue(opponentPlayer.getHand());
                                hexes.remove(0);
                            }
                        }
                        break;
                    case Common.SAVE_GOLD:
                        if (thisPlayer.getCoins() > 0) {
                            thisPlayer.setCoins(thisPlayer.getCoins() - 1);
                            activeAlly.setSavedGold();
                        } else {
                            Toast.makeText(context, "You don't have enough money fool!", Toast.LENGTH_LONG);
                        }
                        break;
                    case Common.COLLECT_ALL_GOLD:
                        if (activeAlly.getSavedGold() > 0) {
                            thisPlayer.setCoins(thisPlayer.getCoins() + activeAlly.getSavedGold());
                            activeAlly.setSavedGoldToZero();
                        } else {
                            Toast.makeText(context, "You don't have enough money fool!", Toast.LENGTH_LONG);
                        }
                        break;
                    case Common.DRAW_CARD:
                        if (ownDeck.size() < 1) {
                            shuffleDeck();
                        }
                        ownHandAdapter.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        break;
                    case Common.DISCARD_CARD:
                        if (ownDeck.size() < 1) {
                            shuffleDeck();
                        }
                        thisPlayerDiscardCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(1);
                        break;
                    case Common.DRAW_CARD_THEN_DISCARD_ANY:
                        if (ownDeck.size() < 1) {
                            shuffleDeck();
                        }
                        ownHandAdapter.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getHand()), 7, opponentPlayer, thisPlayer);
                        discardCard.setOwnHandAdapter(ownHandAdapter);
                        discardCard.showDialog();
                        break;
                    case Common.DISCARD_SPELL:
                        if (thisPlayer.getHand().equals("")) {
                            Toast.makeText(context, "You don't have any card in hand!", Toast.LENGTH_LONG).show();
                        } else {
                            for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getHand())) {
                                ArrayList<Card> spellCardsInHand = new ArrayList<>();
                                if (cardTmp.getType().equals("spell")) {
                                    spellCardsInHand.add(cardTmp);
                                }

                                if (spellCardsInHand.isEmpty()) {
                                    Toast.makeText(context, "You don't have any spell in hand!", Toast.LENGTH_LONG).show();
                                } else {
                                    if (ownDeck.size() < 1)
                                        ownDeck = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                                    discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getHand()), 9, opponentPlayer, thisPlayer);
                                    discardCard.setOwnHandAdapter(ownHandAdapter);
                                    discardCard.setAttacks(1);
                                    discardCard.setHearts(1);
                                    discardCard.setCards(1);
                                    discardCard.showDialog();
                                }
                            }
                        }
                        break;
                    case Common.BANISH_HEX_FROM_DISCARD_PILE:
                        if (thisPlayer.getDiscarded().equals("")) {
                            Toast.makeText(context, "Your discard card is empty!", Toast.LENGTH_LONG).show();
                            break;
                        }

                        discardedCards = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                        ArrayList<Card> discardedCardsHexes = new ArrayList<>();
                        for (Card cardTmp : discardedCards) {
                            if (cardTmp.getCardType().equals("hex")) {
                                discardedCardsHexes.add(cardTmp);
                            }
                        }

                        if (discardedCardsHexes.size() < 1) {
                            Toast.makeText(context, "Your discard card don't have hex!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        discardCard = new DiscardCard(context, database, discardedCardsHexes, 3, opponentPlayer, thisPlayer);
                        discardCard.showDialog();
                        break;
                    case Common.BANISH_PLAYED_HEX:
                        discardedCards = Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards());
                        ArrayList<Card> playedHexes = new ArrayList<>();
                        for (Card cardTmp : discardedCards) {
                            if (cardTmp.getCardType().equals("hex")) {
                                playedHexes.add(cardTmp);
                            }
                        }
                        discardCard = new DiscardCard(context, database, playedHexes, 11, opponentPlayer, thisPlayer);
                        discardCard.showDialog();
                        break;
                    default:
                        break;
                }

                if (chooseEffect && EffectsToDisable.contains(newButton)) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                }
                newButton.setVisibility(View.GONE);

                checkForCardEnd(dialog);

            }
        });

        //btnTag.setId("@+id/gold");
        layoutAllyAttack.addView(newButton);

        return newButton;
    }

    private void shuffleDeck() {
        Log.e("OwnAllyDialog", "before shuffleDeck:" + ownDeck.size());
        if (!thisPlayer.getDiscarded().equals("")) {
            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer,ownDeck);
            Collections.shuffle(ownDeck);
            thisPlayer.setDiscarded("");
            Log.e("OwnAllyDialog", "after shuffleDeck:" + ownDeck.size());
        }
    }
//    private void showButtonsOnCardId() {
//        switch (Integer.valueOf(activeAlly.getId())) {
//            case 0:
//                cardSpells = 1;
//                return_to_library.setVisibility(View.VISIBLE);
//                gold.setVisibility(View.VISIBLE);
//                chooseEffect = true;
//                EffectsToDisable.add(return_to_library);
//                EffectsToDisable.add(gold);
//                break;
//            case 4:
//                cardSpells = 1;
//                attack.setVisibility(View.VISIBLE);
//                break;
//            case 5:
//                cardSpells = 1;
//                heart.setVisibility(View.VISIBLE);
//                gold.setVisibility(View.VISIBLE);
//                chooseEffect = true;
//                EffectsToDisable.add(heart);
//                EffectsToDisable.add(gold);
//                break;
//            case 6:
//                cardSpells = 1;
//                gold.setVisibility(View.VISIBLE);
//                break;
//            case 9:
//                cardSpells = 2;
//                    cardSpells++;
//                    drawCard.setVisibility(View.VISIBLE);
//
//                attack.setVisibility(View.VISIBLE);
//                heart.setVisibility(View.VISIBLE);
//                break;
//            case 11:
//                cardSpells = 2;
//                    cardSpells++;
//                    drawExtraCard.setVisibility(View.VISIBLE);
//
//                gold.setVisibility(View.VISIBLE);
//                drawCard.setVisibility(View.VISIBLE);
//                break;
//            case 13:
//                cardSpells = 1;
//                    chooseEffect = true;
//                    EffectsToDisable.add(heart);
//                    EffectsToDisable.add(gold);
//
//                break;
//            case 15:
//                cardSpells = 1;
//                gold.setVisibility(View.VISIBLE);
//                    cardSpells++;
//                    drawCard.setVisibility(View.VISIBLE);
//
//                break;
//            default:
//        }
//
//    }

//    private void setOnClickButtons(Dialog dialog) {
//        gold.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeAlly.getCoins()));
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    gold.setVisibility(View.GONE);
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//        heart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                if (thisPlayer.getLives() < 7)
//                    thisPlayer.setLives(thisPlayer.getLives() + Integer.parseInt(activeAlly.getHeart()));
//                checkForCardEnd(dialog);
//            }
//        });
//        attack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeAlly.getAttack()));
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    attack.setVisibility(View.GONE);
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//        banishClassroom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                checkForCardEnd(dialog);
//
//            }
//        });
//        banishDiscardPile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//
//                checkForCardEnd(dialog);
//            }
//        });
//        drawCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                hand.add(ownDeck.get(0));
//                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                ownDeck.remove(0);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
//
//                checkForCardEnd(dialog);
//            }
//        });
//        drawExtraCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                hand.add(ownDeck.get(0));
//                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                ownDeck.remove(0);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
//
//                checkForCardEnd(dialog);
//            }
//        });
//        drawCardDiscardOne.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                hand.add(ownDeck.get(0));
//                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                ownDeck.remove(0);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
//
//                checkForCardEnd(dialog);
//            }
//        });
//        drawTwoCardDiscardOne.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                hand.add(ownDeck.get(0));
//                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                ownDeck.remove(0);
//                hand.add(ownDeck.get(0));
//                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                ownDeck.remove(0);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
//
//                checkForCardEnd(dialog);
//            }
//        });
//        opponentHexToDiscardPile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discard").setValue(opponentPlayer.getDiscarded() + "," + hexes.get(0).getId());
//                hexes.remove(0);
//
//                checkForCardEnd(dialog);
//            }
//        });
//        opponentHexToHand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/hand").setValue(opponentPlayer.getHand() + "," + hexes.get(0).getId());
//                hexes.remove(0);
//
//                checkForCardEnd(dialog);
//            }
//        });
//    }

    void thisPlayerDiscardCard(Card card) {
        if (thisPlayer.getDiscarded() == "")
            thisPlayer.setDiscarded(card.getId());
        else
            thisPlayer.setDiscarded(thisPlayer.getDiscarded() + "," + card.getId());
    }

    private void checkForCardEnd(Dialog dialog) {
        if (cardSpells == 0) {
            activeAlly.setUsed(true);
            dialog.dismiss();
            iChooseDialog.onUpdateAttackGoldHeart();

            if (iDisableAllyListener != null)
                iDisableAllyListener.onDisableListener();
        }
    }

}
