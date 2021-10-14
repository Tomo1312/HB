package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class CardDialog extends CustomDialog {

    Player thisPlayer;
    Player opponentPlayer;
    Card activeCard;
    ArrayList<Card> ownDeck, hand, hexes;
    FirebaseDatabase database;
    int cardSpells, library;

    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;
    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    IOwnAllyListener iOwnAllyListener;

    OwnHandAdapter ownHandAdapter;

    ArrayList<Button> EffectsToDisable;
    ArrayList<Card> classroom;
    boolean chooseEffect, mayUseSpell = false;

    ImageView cardImage;

    LinearLayout layout;

    public CardDialog(Context context, int library, ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand,
                      Card activeCard, Player thisPlayer, Player opponentPlayer, ArrayList<Card> ownDeck,
                      ArrayList<Card> hand, ArrayList<Card> hexes, FirebaseDatabase database,
                      IUpdateAttackGoldHeart iUpdateAttackGoldHeart, IOwnAllyListener iOwnAllyListener, ArrayList<Card> classroom) {
        super(context, 0);

        this.iCardAddOrDeletedFromHand = iCardAddOrDeletedFromHand;
        this.activeCard = activeCard;
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hand = hand;
        this.database = database;
        this.hexes = hexes;
        this.library = library;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.iOwnAllyListener = iOwnAllyListener;
        this.classroom = classroom;

        this.chooseEffect = false;
        this.EffectsToDisable = new ArrayList<>();
    }

    public void setOwnHandAdapter(OwnHandAdapter ownHandAdapter) {
        this.ownHandAdapter = ownHandAdapter;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_card_active);


        setUiView(dialog);

        showButtonsOnCardId(dialog);

        String cardName = "id" + activeCard.getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        cardImage.setImageResource(id);

        dialog.setCancelable(true);
        if (!activeCard.getType().equals("ally"))
            dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setUiView(Dialog dialog) {
        layout = dialog.findViewById(R.id.linear_layout_active_card);
        cardImage = dialog.findViewById(R.id.card_image);
    }


    private void showButtonsOnCardId(Dialog dialog) {
        switch (Integer.parseInt(activeCard.getId())) {
            case 0:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.RETURN_TO_LIBRARY, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell));
                break;
            case 4:
                cardSpells = 1;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                break;
            case 5:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell));
                break;
            case 6:
                cardSpells = 1;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 7:
                cardSpells = 1;
                createButton(Common.REVEAL_TOP_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 8:
                cardSpells = 7;
                //FAKE
                createButton(Common.REVEAL_TOP_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 9:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.DISCARD_OPPONENTS_ALLY, dialog, R.drawable.button_border_normall_spell));
                break;
            case 10:
                cardSpells = 1;
                mayUseSpell = true;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.BANISH_FROM_CLASSROOM, dialog, R.drawable.button_border_normall_spell);
                break;
            case 11:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.DRAW_ITEM_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell));
                break;
            case 12:
                cardSpells = 2;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                break;
            case 13:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell));
                break;
            case 14:
                cardSpells = 2;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 15:
            case 22:
                cardSpells = 2;
                mayUseSpell = true;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.BANISH_HEX_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                break;
            case 16:
                cardSpells = 1;
                createButton(Common.BANISH_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                break;
            case 17:
            case 30:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 18:
                cardSpells = 2;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 19:
                cardSpells = 1;
                createButton(Common.COPY_SPELL, dialog, R.drawable.button_border_normall_spell);
                break;
            case 20:
                cardSpells = 2;
                mayUseSpell = true;
                createButton(Common.BANISH_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.BANISH_FROM_CLASSROOM, dialog, R.drawable.button_border_normall_spell);
                break;
            case 21:
                cardSpells = 1;
                createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                break;
            case 23:
                cardSpells = 3;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.REVEAL_TOP_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 24:
                cardSpells = 2;
                mayUseSpell = true;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.BANISH_FROM_HAND, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.BANISH_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell));
                break;
            case 25:
                cardSpells = 2;
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                break;
            case 26:
                cardSpells = 2;
                createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 27:
                cardSpells = 3;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                break;
            case 28:
                cardSpells = 2;
                createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                break;
            case 29:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_PUT_HEX_TO_HAND, dialog, R.drawable.button_border_normall_spell);
                break;
            case 31:
                cardSpells = 2;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.GOLD_PER_ALLY, dialog, R.drawable.button_border_normall_spell);
                break;
            case 32:
                cardSpells = 2;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.ATTACK_PER_ALLY, dialog, R.drawable.button_border_normall_spell);
                break;
            case 33:
                cardSpells = 3;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 34:
                cardSpells = 2;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 35:
                cardSpells = 3;
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                break;
            case 36:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DISCARD_OPPONENTS_ALLY, dialog, R.drawable.button_border_normall_spell);
                break;
            case 37:
                cardSpells = 2;
                createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_PUT_HEX_TO_HAND, dialog, R.drawable.button_border_normall_spell);
                break;
            case 44:
                cardSpells = 1;
                createButton(Common.DRAW_CARD_THEN_DISCARD_ANY, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.GOLD, dialog, R.drawable.button_border_ravenclaw_spell);
                }
                break;
            case 45:
                cardSpells = 1;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.HEART, dialog, R.drawable.button_border_ravenclaw_spell);
                }
                break;
            case 46:
                cardSpells = 1;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_ravenclaw_spell);
                }
                break;
            case 47:
                cardSpells = 2;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.ATTACK, dialog, R.drawable.button_border_ravenclaw_spell);
                }
                break;
            case 48:
                cardSpells = 2;
                mayUseSpell = true;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.PUT_CARD_FROM_YOUR_TO_OPPONENT_DISCARD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.DRAW_TWO_CARD_THEN_DISCARD_ANY, dialog, R.drawable.button_border_ravenclaw_spell);
                }
                break;
            case 53:
                cardSpells = 1;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.HEART_PER_ALLY, dialog, R.drawable.button_border_gryffindor_spell);
                }
                break;
            case 54:
                cardSpells = 3;
                mayUseSpell = true;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.BANISH_FROM_HAND, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    createButton(Common.BANISH_FROM_DISCARD_PILE, dialog, R.drawable.button_border_gryffindor_spell);
                }
                break;
            case 55:
                cardSpells = 1;
                createButton(Common.OPPONENT_DISCARD_RANDOM_CARD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.ATTACK, dialog, R.drawable.button_border_gryffindor_spell);
                }
                break;
            case 56:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_gryffindor_spell);
                }
                break;
            case 57:
                cardSpells = 2;
                createButton(Common.OPPONENT_DISCARD_RANDOM_CARD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.ATTACK, dialog, R.drawable.button_border_gryffindor_spell);
                }
                break;
            case 62:
                cardSpells = 1;
                createButton(Common.COPY_ALLY_SPELL, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    createButton(Common.DISCARD_OPPONENTS_ALLY, dialog, R.drawable.button_border_slytherin_spell);
                }
                break;
            case 63:
                cardSpells = 2;
                mayUseSpell = true;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.BANISH_FROM_CLASSROOM, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_slytherin_spell);
                }
                break;
            case 64:
                cardSpells = 1;
                createButton(Common.ATTACK_PER_OPPONENT_ALLY, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_slytherin_spell);
                }
                break;
            case 65:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell));

                if (checkForSameHouse()) {
                    cardSpells = 2;
                    createButton(Common.ATTACK, dialog, R.drawable.button_border_slytherin_spell);
                }
                break;
            case 66:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.OPPONENT_DISCARD_NON_HEX_CARD, dialog, R.drawable.button_border_normall_spell);

                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.OPPONENT_PUT_HEX_TO_HAND, dialog, R.drawable.button_border_slytherin_spell);
                }
                break;
            case 71:
                cardSpells = 2;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);

                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_hufflepuff_spell);
                }
                break;
            case 72:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.GOLD, dialog, R.drawable.button_border_hufflepuff_spell);
                }
                break;
            case 73:
                cardSpells = 2;
                createButton(Common.ATTACK, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_hufflepuff_spell);
                }
                break;
            case 74:
                cardSpells = 1;
                chooseEffect = true;
                mayUseSpell = true;
                EffectsToDisable.add(createButton(Common.BANISH_FROM_HAND, dialog, R.drawable.button_border_normall_spell));
                EffectsToDisable.add(createButton(Common.BANISH_FROM_DISCARD_PILE, dialog, R.drawable.button_border_normall_spell));
                if (checkForSameHouse()) {
                    cardSpells = 3;
                    createButton(Common.GOLD, dialog, R.drawable.button_border_hufflepuff_spell);
                    createButton(Common.HEART, dialog, R.drawable.button_border_hufflepuff_spell);
                }
                break;
            case 75:
                cardSpells = 3;
                createButton(Common.GOLD, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.HEART, dialog, R.drawable.button_border_normall_spell);
                createButton(Common.DRAW_CARD, dialog, R.drawable.button_border_normall_spell);
                if (checkForSameHouse()) {
                    cardSpells = 4;
                    createButton(Common.ATTACK, dialog, R.drawable.button_border_hufflepuff_spell);
                }
                break;
            default:
                if (activeCard.getType().equals("ally")) {
                    iOwnAllyListener.onOwnAllyChange(activeCard);
                    iCardAddOrDeletedFromHand.onPlayedAlly(activeCard);
                }
                // code block
        }

    }

    private Button createButton(String title, Dialog dialog, int buttonBackground) {
        Button newButton = new Button(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 20;
        newButton.setLayoutParams(lp);

        newButton.setText(title);
        newButton.setBackgroundResource(buttonBackground);
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
                        thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeCard.getCoins()));
                        break;
                    case Common.HEART:
                        thisPlayer.setHeart(thisPlayer.getHeart() + Integer.parseInt(activeCard.getHeart()));
                        break;
                    case Common.ATTACK:
                        thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeCard.getAttack()));
                        if (Integer.parseInt(activeCard.getId()) == 28) {
                            ArrayList<Card> opponentDiscarded = Helpers.getInstance().returnCardsFromString(opponentPlayer.getDiscarded());
                            for (Card cardTmp : opponentDiscarded) {
                                if (cardTmp.getCardType().equals("Hex")) {
                                    thisPlayer.setAttacks(thisPlayer.getAttacks() + 1);
                                }
                            }
                        }
                        break;
                    case Common.REVEAL_TOP_CARD:
                        if (ownDeck.size() < 1) {
                            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck);
                            thisPlayer.setDiscarded("");
                        }
                        ShowCardDialog.getInstance().showCardDialog(context, ownDeck.get(0), "Top Card");
                        if (Integer.parseInt(activeCard.getId()) == 7) {
                            if (Integer.parseInt(ownDeck.get(0).getCost()) > 3) {
                                iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                                ownDeck.remove(0);
                            } else {
                                thisPlayer.setCoins(thisPlayer.getCoins() + 2);
                            }
                        } else if (Integer.parseInt(activeCard.getId()) == 23) {
                            if (ownDeck.size() <= 0) {
                                ownDeck = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                                Collections.shuffle(ownDeck);
                                thisPlayer.setDiscarded("");
                            }
                            DrawOrDiscardCardDialog.getInstance().showChooseAllyDialog(context, thisPlayer, iCardAddOrDeletedFromHand, ownDeck.get(0));
                            ownDeck.remove(0);
                        }
                        break;
                    case Common.DISCARD_OPPONENTS_ALLY:
                        if (!opponentPlayer.getAlly().equals("")) {
                            discardCard = new DiscardCard(context, database, opponentPlayer.getAlly(), 0, opponentPlayer, thisPlayer);
                            if (activeCard.getId().equals("62")) {
                                discardCard.setOwnHandAdapter(ownHandAdapter);
                                discardCard.setClassroom(classroom);
                                discardCard.setHexes(hexes);
                                discardCard.setIUpdateAttackGoldHeart(iUpdateAttackGoldHeart);
                            }
                            discardCard.showDialog();
                        } else {
                            Toast.makeText(context, "Opponent don't have any ally!", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case Common.BANISH_FROM_CLASSROOM:
                        discardCard = new DiscardCard(context, database, classroom, 1, opponentPlayer, thisPlayer);
                        if (Integer.parseInt(activeCard.getId()) == 20)
                            discardCard.setGolds(1);
                        discardCard.showDialog();
                        break;
                    case Common.DRAW_ITEM_FROM_DISCARD_PILE:
                        if (thisPlayer.getDiscarded().equals("")) {
                            Toast.makeText(context, "Discard pile empty!", Toast.LENGTH_LONG).show();

                        } else {
                            discardedCards = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                            ArrayList<Card> discardedCardsItems = new ArrayList<>();

                            for (Card cardTmp : discardedCards) {
                                if (cardTmp.getType().equals("item")) {
                                    discardedCardsItems.add(cardTmp);
                                }
                            }
                            if (discardedCardsItems.size() > 0) {
                                discardCard = new DiscardCard(context, database, discardedCardsItems, 2, opponentPlayer, thisPlayer);
                                discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                                discardCard.showDialog();
                            }
                        }
                        break;
                    case Common.BANISH_HEX_FROM_DISCARD_PILE:
                        if (thisPlayer.getDiscarded().equals("")) {
                            Toast.makeText(context, "Discard pile empty!", Toast.LENGTH_LONG).show();
                        } else {
                            discardedCards = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                            ArrayList<Card> discardedCardsHexes = new ArrayList<>();

                            for (Card cardTmp : discardedCards) {
                                if (cardTmp.getCardType().equals("hex")) {
                                    discardedCardsHexes.add(cardTmp);
                                }
                            }
                            if (discardedCardsHexes.size() > 0) {
                                discardCard = new DiscardCard(context, database, discardedCardsHexes, 3, opponentPlayer, thisPlayer);
                                if (Integer.parseInt(activeCard.getId()) == 15)
                                    discardCard.setAttacks(2);
                                else if (Integer.parseInt(activeCard.getId()) == 22) {
                                    discardCard.setCards(2);
                                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                                    //If deck don't have 2 cards we need new one
                                    if (ownDeck.size() <= 1) {
                                        ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck);
                                        thisPlayer.setDiscarded("");
                                    }
                                    discardCard.setOwnDeck(ownDeck);
                                }
                                discardCard.showDialog();
                            }
                        }
                        break;
                    case Common.DRAW_CARD:
                        if (ownDeck.size() == 0) {
                            ownDeck = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()));
                            Collections.shuffle(ownDeck);
                            thisPlayer.setDiscarded("");
                        }
                        iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        break;
                    case Common.BANISH_FROM_DISCARD_PILE:
                        if (thisPlayer.getDiscarded().equals("")) {
                            Toast.makeText(context, "Discard pile empty!", Toast.LENGTH_LONG).show();
                        } else {
                            discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()), 4, opponentPlayer, thisPlayer);
                            if (Integer.parseInt(activeCard.getId()) == 16)
                                discardCard.setHearts(2);
                            else if (Integer.parseInt(activeCard.getId()) == 20)
                                discardCard.setGolds(1);
                            else
                                discardCard.setHearts(0);
                            discardCard.showDialog();
                        }
                        break;
                    case Common.BANISH_FROM_HAND:
                        ArrayList<Card> handTmp = new ArrayList<>(hand);
                        if (handTmp.size() < 1) {
                            Toast.makeText(context, "Hand empty!", Toast.LENGTH_LONG).show();

                        } else {
                            discardCard = new DiscardCard(context, database, handTmp, 6, opponentPlayer, thisPlayer);
                            discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                            if (Integer.parseInt(activeCard.getId()) == 24)
                                discardCard.setHearts(2);
                            discardCard.showDialog();
                        }
                        break;
                    case Common.COPY_SPELL:
                        if (thisPlayer.getPlayedCards().equals("")) {
                            Toast.makeText(context, "You haven't played any card yet!", Toast.LENGTH_LONG).show();
                        } else {
                            ArrayList<Card> playedCards = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards()));
                            ArrayList<Card> playedCardsSpells = new ArrayList<>();
                            if (playedCards.size() > 0) {
                                for (Card cardTmp : playedCards) {
                                    if (cardTmp.getType().equals("spell"))
                                        playedCardsSpells.add(cardTmp);
                                }
                                discardCard = new DiscardCard(context, database, playedCardsSpells, 5, opponentPlayer, thisPlayer);
                                discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                                discardCard.showDialog();
                            }
                        }
                        break;
                    case Common.COPY_ALLY_SPELL:
                        if (thisPlayer.getAlly().equals("")) {
                            Toast.makeText(context, "You haven't played any ally yet!", Toast.LENGTH_LONG).show();

                        } else {
                            ArrayList<Card> allysTmp = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getAlly()));
                            ArrayList<Card> playedAllys = new ArrayList<>();
                            if (allysTmp.size() > 0) {
                                for (Card cardTmp : allysTmp) {
                                    if (cardTmp.isUsed())
                                        playedAllys.add(cardTmp);
                                }
                                discardCard = new DiscardCard(context, database, playedAllys, 10, opponentPlayer, thisPlayer);
                                discardCard.setOwnAllyListener(iOwnAllyListener);
                                discardCard.showDialog();
                            } else {
                                Toast.makeText(context, "You don't have any ally!", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    case Common.OPPONENT_DISCARD_NON_HEX_CARD:
                        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(1);
                        break;
                    case Common.OPPONENT_DISCARD_RANDOM_CARD:
                        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(2);
                        break;
                    case Common.OPPONENT_PUT_HEX_TO_HAND:
                        if (opponentPlayer.getHand().equals(""))
                            opponentPlayer.setHand(hexes.get(0).getId());
                        else
                            opponentPlayer.setHand(opponentPlayer.getHand() + "," + hexes.get(0).getId());
                        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/hand").setValue(opponentPlayer.getHand());
                        hexes.remove(0);
                        break;
                    case Common.OPPONENT_PUT_HEX_TO_DISCARD_PILE:
                        if (opponentPlayer.getDiscarded().equals(""))
                            opponentPlayer.setDiscarded(hexes.get(0).getId());
                        else
                            opponentPlayer.setDiscarded(opponentPlayer.getDiscarded() + "," + hexes.get(0).getId());
                        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discarded").setValue(opponentPlayer.getDiscarded());
                        hexes.remove(0);
                        break;
                    case Common.DRAW_CARD_THEN_DISCARD_ANY:
                        if (ownDeck.size() == 0) {
                            ownDeck = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()));
                            Collections.shuffle(ownDeck);
                            thisPlayer.setDiscarded("");
                        }
                        iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getHand()), 7, opponentPlayer, thisPlayer);
                        discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                        discardCard.showDialog();
                        break;
                    case Common.DRAW_TWO_CARD_THEN_DISCARD_ANY:
                        if (ownDeck.size() <= 1) {
                            ownDeck = new ArrayList<>(Helpers.getInstance().getDeckFromDiscardPileAndDeck(thisPlayer, ownDeck));
                            thisPlayer.setDiscarded("");
                        }
                        iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                        ownDeck.remove(0);
                        discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getHand()), 7, opponentPlayer, thisPlayer);
                        discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                        discardCard.showDialog();
                        break;
                    case Common.GOLD_PER_ALLY:
                        String[] allysGold = thisPlayer.getAlly().split(",");
                        thisPlayer.setCoins(thisPlayer.getCoins() + allysGold.length);
                        break;
                    case Common.ATTACK_PER_ALLY:
                        String[] allysAttack = thisPlayer.getAlly().split(",");
                        thisPlayer.setAttacks(thisPlayer.getAttacks() + allysAttack.length);
                        break;
                    case Common.ATTACK_PER_OPPONENT_ALLY:
                        String[] opponentsAllysAttack = opponentPlayer.getAlly().split(",");
                        thisPlayer.setAttacks(thisPlayer.getAttacks() + opponentsAllysAttack.length);
                        break;
                    case Common.HEART_PER_ALLY:
                        String[] allysHeart = thisPlayer.getAlly().split(",");
                        thisPlayer.setHeart(thisPlayer.getHeart() + allysHeart.length);
                        break;
                    case Common.PUT_CARD_FROM_YOUR_TO_OPPONENT_DISCARD:
                        if (thisPlayer.getDiscarded().equals(""))
                            Toast.makeText(context, "Discard pile empty", Toast.LENGTH_LONG).show();
                        else {
                            discardCard = new DiscardCard(context, database, Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()), 8, opponentPlayer, thisPlayer);
                            discardCard.showDialog();
                        }
                        break;
                    case Common.RETURN_TO_LIBRARY:
                        library++;
                        database.getReference("rooms/" + Common.currentRoomName + "/library").setValue(library);
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
        layout.addView(newButton);

        return newButton;
    }
//
//    private void setOnClickButtons(Dialog dialog) {
//        gold.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeCard.getCoins()));
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
//
//        heart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                thisPlayer.setHeart(thisPlayer.getHeart() + Integer.parseInt(activeCard.getHeart()));
//                checkForCardEnd(dialog);
//            }
//        });
//
//        attack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeCard.getAttack()));
//                if (Integer.parseInt(activeCard.getId()) == 28) {
//                    ArrayList<Card> opponentDiscarded = Helpers.getInstance().returnCardsFromString(opponentPlayer.getDiscarded());
//                    for (Card cardTmp : opponentDiscarded) {
//                        if (cardTmp.getCardType().equals("Hex")) {
//                            thisPlayer.setAttacks(thisPlayer.getAttacks() + 1);
//                        }
//                    }
//                }
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
//
//        revealTopCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                if (Integer.parseInt(activeCard.getId()) == 7) {
//                    if (Integer.parseInt(ownDeck.get(0).getCost()) > 3) {
//                        hand.add(ownDeck.get(0));
//                        thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
//                        ownDeck.remove(0);
//                        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
//                    } else {
//                        thisPlayer.setCoins(thisPlayer.getCoins() + 2);
//                    }
//                } else if (Integer.parseInt(activeCard.getId()) == 23) {
//                    if (ownDeck.size() <= 0) {
//                        ownDeck = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
//                        Collections.shuffle(ownDeck);
//                    }
//                    DrawOrDiscardCardDialog.getInstance().showChooseAllyDialog(context, thisPlayer, iCardAddOrDeletedFromHand, ownDeck.get(0));
//                    ownDeck.remove(0);
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//
//        discardOpponentAlly.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                dialog.setCancelable(false);
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    discardOpponentAlly.setVisibility(View.GONE);
//                }
//                if (!opponentPlayer.getAlly().equals("")) {
//                    DiscardCard discardCard = new DiscardCard(context, database, opponentPlayer.getAlly(), 0, opponentPlayer, thisPlayer);
//                    discardCard.showDialog();
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//
//        banishClassroom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//                DiscardCard discardCard = new DiscardCard(context, database, classroom, 1, opponentPlayer, thisPlayer);
//                if (Integer.parseInt(activeCard.getId()) == 20)
//                    discardCard.setGolds(1);
//                discardCard.showDialog();
//
//                banishClassroom.setVisibility(View.GONE);
//                checkForCardEnd(dialog);
//
//            }
//        });
//
//        discardPileDrawItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    discardOpponentAlly.setVisibility(View.GONE);
//                }
//                ArrayList<Card> discardedCards =  Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
//                ArrayList<Card> discardedCardsItems = new ArrayList<>();
//
//                for (Card cardTmp : discardedCards) {
//                    if (cardTmp.getType().equals("item")) {
//                        discardedCardsItems.add(cardTmp);
//                    }
//                }
//                if (discardedCardsItems.size() > 0) {
//                    DiscardCard discardCard = new DiscardCard(context, database, discardedCardsItems, 2, opponentPlayer, thisPlayer);
//                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
//                    discardCard.showDialog();
//                }
//
//                discardPileDrawItem.setVisibility(View.GONE);
//                checkForCardEnd(dialog);
//            }
//        });
//
//        banishHexFromDiscardPile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    discardOpponentAlly.setVisibility(View.GONE);
//                }
//                ArrayList<Card> discardedCards =  Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
//                ArrayList<Card> discardedCardsHexes = new ArrayList<>();
//
//                for (Card cardTmp : discardedCards) {
//                    if (cardTmp.getCardType().equals("hex")) {
//                        discardedCardsHexes.add(cardTmp);
//                    }
//                }
//                if (discardedCardsHexes.size() > 0) {
//                    DiscardCard discardCard = new DiscardCard(context, database, discardedCardsHexes, 3, opponentPlayer, thisPlayer);
//                    if (Integer.parseInt(activeCard.getId()) == 15)
//                        discardCard.setAttacks(2);
//                    else if (Integer.parseInt(activeCard.getId()) == 22) {
//                        discardCard.setCards(2);
//                        discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
//                        //If deck don't have 2 cards we need new one
//                        if (ownDeck.size() <= 1) {
//                            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndHand(thisPlayer, ownDeck);
//                        }
//                        discardCard.setOwnDeck(ownDeck);
//                    }
//                    discardCard.showDialog();
//                }
//
//                banishHexFromDiscardPile.setVisibility(View.GONE);
//                checkForCardEnd(dialog);
//
//            }
//        });
//
//        drawCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    drawCard.setVisibility(View.GONE);
//                }
//
//                if (ownDeck.size() == 0) {
//                    ownDeck = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()));
//                }
//
//                iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
//                ownDeck.remove(0);
//
//                checkForCardEnd(dialog);
//            }
//        });
//
//        banishDiscardPile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//
//                DiscardCard discardCard = new DiscardCard(context, database,  Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()), 4, opponentPlayer, thisPlayer);
//                if (Integer.parseInt(activeCard.getId()) == 16)
//                    discardCard.setHearts(2);
//                else if (Integer.parseInt(activeCard.getId()) == 20)
//                    discardCard.setGolds(1);
//                else
//                    discardCard.setHearts(0);
//                discardCard.showDialog();
//
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    banishDiscardPile.setVisibility(View.GONE);
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//
//        banishHand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//
//                ArrayList<Card> handTmp = new ArrayList<>(hand);
//
//                DiscardCard discardCard = new DiscardCard(context, database, handTmp, 6, opponentPlayer, thisPlayer);
//                if (Integer.parseInt(activeCard.getId()) == 24)
//                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
//                discardCard.showDialog();
//
//                if (chooseEffect) {
//                    for (Button buttonTmp : EffectsToDisable) {
//                        buttonTmp.setVisibility(View.GONE);
//                    }
//                    EffectsToDisable.clear();
//                } else {
//                    banishHand.setVisibility(View.GONE);
//                }
//                checkForCardEnd(dialog);
//            }
//        });
//
//        copySpell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//                ArrayList<Card> playedCards = new ArrayList<>( Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards()));
//                ArrayList<Card> playedCardsSpells = new ArrayList<>();
//                if (playedCards.size() > 0) {
//                    for (Card cardTmp : playedCards) {
//                        if (cardTmp.getType().equals("spell"))
//                            playedCardsSpells.add(cardTmp);
//                    }
//                    DiscardCard discardCard = new DiscardCard(context, database, playedCardsSpells, 5, opponentPlayer, thisPlayer);
//                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
//                    discardCard.showDialog();
//                }
//
//                checkForCardEnd(dialog);
//            }
//        });
//
//        opponentHexToDiscardPile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//                opponentPlayer.setDiscarded(opponentPlayer.getDiscarded() + "," + hexes.get(0).getId());
//                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discard").setValue(opponentPlayer.getDiscarded());
//                hexes.remove(0);
//
//                opponentHexToDiscardPile.setVisibility(View.GONE);
//                checkForCardEnd(dialog);
//            }
//        });
//
//        opponentDiscardNonHexCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardSpells--;
//                if (!mayUseSpell)
//                    dialog.setCancelable(false);
//
//                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(1);
//
//                opponentDiscardNonHexCard.setVisibility(View.GONE);
//                checkForCardEnd(dialog);
//
//            }
//        });
//
//
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

    private void checkForCardEnd(Dialog dialog) {
        if (cardSpells == 0 && !mayUseSpell) {
            iCardAddOrDeletedFromHand.onDiscardCard(activeCard);
            dialog.dismiss();
            thisPlayerPlayedCard(activeCard);
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        } else if (cardSpells == 0) {
            iCardAddOrDeletedFromHand.onDiscardCard(activeCard);
            dialog.setCancelable(true);
            thisPlayerPlayedCard(activeCard);
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        }
    }

    void thisPlayerPlayedCard(Card card) {
        if (thisPlayer.getPlayedCards() == "")
            thisPlayer.setPlayedCards(card.getId());
        else
            thisPlayer.setPlayedCards(thisPlayer.getPlayedCards() + "," + card.getId());
    }

    private boolean checkForSameHouse() {
        if (!activeCard.getHouse().equals("none")) {
            if (thisPlayer.getHouse().contains(activeCard.getHouse()) || checkIfAllyIsSameHouse()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfAllyIsSameHouse() {
        if (!thisPlayer.getAlly().equals("")) {
            for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getAlly())) {
                if (cardTmp.getHouse().equals(activeCard.getHouse()))
                    return true;
            }
        }
        return false;
    }

}
