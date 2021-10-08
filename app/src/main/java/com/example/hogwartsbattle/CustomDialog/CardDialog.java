package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    int cardSpells, position;

    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;
    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    IOwnAllyListener iOwnAllyListener;

    ArrayList<Button> EffectsToDisable;
    ArrayList<Card> classroom;
    boolean chooseEffect, mayUseSpell = false;

    Helpers helper = new Helpers();
    ImageView cardImage;
    Button gold, attack, heart, banishClassroom, banishHand,
            banishDiscardPile, drawCard, drawCardDiscardOne, drawTwoCardDiscardOne,
            opponentHexToDiscardPile, opponentHexToHand, return_to_library, drawExtraCard, revealTopCard,
            discardOpponentAlly, discardPileDrawItem, banishHexFromDiscardPile, copySpell, opponentDiscardNonHexCard;

    LinearLayout layout;

    public CardDialog(Context context, int position, ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand,
                      Card activeCard, Player thisPlayer, Player opponentPlayer, ArrayList<Card> ownDeck,
                      ArrayList<Card> hand, ArrayList<Card> hexes, FirebaseDatabase database,
                      IUpdateAttackGoldHeart iUpdateAttackGoldHeart, IOwnAllyListener iOwnAllyListener, ArrayList<Card> classroom) {
        super(context, position);

        this.iCardAddOrDeletedFromHand = iCardAddOrDeletedFromHand;
        this.activeCard = activeCard;
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hand = hand;
        this.database = database;
        this.hexes = hexes;
        this.position = position;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.iOwnAllyListener = iOwnAllyListener;
        this.classroom = classroom;

        this.chooseEffect = false;
        this.EffectsToDisable = new ArrayList<>();
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
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setUiView(Dialog dialog) {
        gold = dialog.findViewById(R.id.gold);
        attack = dialog.findViewById(R.id.attack);
        heart = dialog.findViewById(R.id.heart);
        banishClassroom = dialog.findViewById(R.id.banishClassroom);
        banishHand = dialog.findViewById(R.id.banishHand);
        banishDiscardPile = dialog.findViewById(R.id.banishDiscardPile);
        drawCard = dialog.findViewById(R.id.drawCard);
        drawExtraCard = dialog.findViewById(R.id.drawExtraCard);
        drawCardDiscardOne = dialog.findViewById(R.id.drawCardDiscardOne);
        drawTwoCardDiscardOne = dialog.findViewById(R.id.drawTwoCardDiscardOne);
        opponentHexToDiscardPile = dialog.findViewById(R.id.opponentHexToDiscardPile);
        opponentHexToHand = dialog.findViewById(R.id.opponentHexToHand);
        return_to_library = dialog.findViewById(R.id.return_to_library);
        cardImage = dialog.findViewById(R.id.card_image);
        revealTopCard = dialog.findViewById(R.id.revealTopCard);
        discardOpponentAlly = dialog.findViewById(R.id.opponentDiscardAlly);
        discardPileDrawItem = dialog.findViewById(R.id.discardPileDrawItem);
        banishHexFromDiscardPile = dialog.findViewById(R.id.banishHexFromDiscardPile);
        copySpell = dialog.findViewById(R.id.copySpell);
        opponentDiscardNonHexCard = dialog.findViewById(R.id.opponentDiscardNonHexCard);
        layout =dialog.findViewById(R.id.linear_layout_active_card);
        setOnClickButtons(dialog);
    }



    private void showButtonsOnCardId(Dialog dialog) {
        switch (Integer.parseInt(activeCard.getId())) {
            case 0:
                cardSpells = 1;
                return_to_library.setVisibility(View.VISIBLE);
                gold.setVisibility(View.VISIBLE);
                chooseEffect = true;
                EffectsToDisable.add(return_to_library);
                EffectsToDisable.add(gold);
                break;
            case 4:
                cardSpells = 1;
                attack.setVisibility(View.VISIBLE);
                break;
            case 5:
                cardSpells = 1;
                heart.setVisibility(View.VISIBLE);
                gold.setVisibility(View.VISIBLE);
                chooseEffect = true;
                EffectsToDisable.add(heart);
                EffectsToDisable.add(gold);
                break;
            case 6:
                cardSpells = 1;
                gold.setVisibility(View.VISIBLE);


                //set the properties for button
                //createButton("Gold", )
                break;
            case 7:
                cardSpells = 1;
                revealTopCard.setVisibility(View.VISIBLE);
                break;
            case 8:
                cardSpells = 1;
                revealTopCard.setVisibility(View.VISIBLE);
                break;
            case 9:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(attack);
                EffectsToDisable.add(discardOpponentAlly);
                attack.setVisibility(View.VISIBLE);
                discardOpponentAlly.setVisibility(View.VISIBLE);
                break;
            case 10:
                cardSpells = 1;
                mayUseSpell = true;
                attack.setVisibility(View.VISIBLE);
                banishClassroom.setVisibility(View.VISIBLE);
                break;
            case 11:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(gold);
                EffectsToDisable.add(discardPileDrawItem);
                gold.setVisibility(View.VISIBLE);
                discardPileDrawItem.setVisibility(View.VISIBLE);
                break;
            case 12:
                cardSpells = 2;
                gold.setVisibility(View.VISIBLE);
                heart.setVisibility(View.VISIBLE);
                break;
            case 13:
                cardSpells = 1;
                chooseEffect = true;
                EffectsToDisable.add(gold);
                EffectsToDisable.add(drawCard);
                gold.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 14:
                cardSpells = 2;
                heart.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 15:
                cardSpells = 2;
                mayUseSpell = true;
                heart.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                banishHexFromDiscardPile.setVisibility(View.VISIBLE);
                break;
            case 16:
                cardSpells = 1;
                banishDiscardPile.setVisibility(View.VISIBLE);
                break;
            case 17:
                cardSpells = 2;
                attack.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 18:
                cardSpells = 2;
                gold.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 19:
                cardSpells = 1;
                copySpell.setVisibility(View.VISIBLE);
                break;
            case 20:
                cardSpells = 2;
                mayUseSpell = true;
                banishDiscardPile.setVisibility(View.VISIBLE);
                banishClassroom.setVisibility(View.VISIBLE);
                break;
            case 21:
                cardSpells = 1;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                break;
            case 22:
                cardSpells = 3;
                mayUseSpell = true;
                gold.setVisibility(View.VISIBLE);
                heart.setVisibility(View.VISIBLE);
                banishHexFromDiscardPile.setVisibility(View.VISIBLE);
                break;
            case 23:
                cardSpells = 3;
                attack.setVisibility(View.VISIBLE);
                gold.setVisibility(View.VISIBLE);
                revealTopCard.setVisibility(View.VISIBLE);
                break;
            case 24:
                cardSpells = 2;
                mayUseSpell = true;
                heart.setVisibility(View.VISIBLE);
                chooseEffect = true;
                EffectsToDisable.add(banishHand);
                EffectsToDisable.add(banishDiscardPile);
                banishHand.setVisibility(View.VISIBLE);
                banishDiscardPile.setVisibility(View.VISIBLE);
                break;
            case 25:
                cardSpells = 2;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 26:
                cardSpells = 2;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                opponentDiscardNonHexCard.setVisibility(View.VISIBLE);
                break;
            case 27:
                cardSpells = 3;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                attack.setVisibility(View.VISIBLE);
                heart.setVisibility(View.VISIBLE);
                break;
            case 28:
                cardSpells = 3;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                attack.setVisibility(View.VISIBLE);
                break;
            case 29:
                cardSpells = 2;
                opponentHexToDiscardPile.setVisibility(View.VISIBLE);
                attack.setVisibility(View.VISIBLE);
                break;
            case 30:
                cardSpells = 2;
                drawCard.setVisibility(View.VISIBLE);
                attack.setVisibility(View.VISIBLE);
                break;
            default:
                if (activeCard.getType().equals("ally")) {
                    if (thisPlayer.getAlly().equals("")) {
                        thisPlayer.setAlly(activeCard.getId());
                    } else {
                        thisPlayer.setAlly(thisPlayer.getAlly() + "," + activeCard.getId());
                    }
                    iOwnAllyListener.onOwnAllyChange(activeCard);
                    iCardAddOrDeletedFromHand.onDeleteCard(activeCard);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/ally").setValue(thisPlayer.getAlly());

                }
                // code block
        }

    }
    private void createButton(String title,Dialog dialog, int cardSpell){
        Button newButton = new Button(context);
        newButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newButton.setText("Button");
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if(mayUseSpell)
                    dialog.setCancelable(false);

                switch (title){
                    case "Gold":
                        thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeCard.getCoins()));
                        break;
                    case "Heart":
                        thisPlayer.setHeart(thisPlayer.getHeart() + Integer.parseInt(activeCard.getHeart()));
                        break;
                    case "Attack":

                        break;
                }


                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    gold.setVisibility(View.GONE);
                }
                newButton.setVisibility(View.INVISIBLE);
                checkForCardEnd(dialog);

            }
        });

        //btnTag.setId("@+id/gold");
        layout.addView(newButton);

    }
    private void setOnClickButtons(Dialog dialog) {
        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                thisPlayer.setCoins(thisPlayer.getCoins() + Integer.parseInt(activeCard.getCoins()));
                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    gold.setVisibility(View.GONE);
                }
                checkForCardEnd(dialog);
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                thisPlayer.setHeart(thisPlayer.getHeart() + Integer.parseInt(activeCard.getHeart()));
                checkForCardEnd(dialog);
            }
        });

        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                thisPlayer.setAttacks(thisPlayer.getAttacks() + Integer.parseInt(activeCard.getAttack()));
                if (Integer.parseInt(activeCard.getId()) == 28) {
                    ArrayList<Card> opponentDiscarded = Helpers.getInstance().returnCardsFromString(opponentPlayer.getDiscarded());
                    for (Card cardTmp : opponentDiscarded) {
                        if (cardTmp.getCardType().equals("Hex")) {
                            thisPlayer.setAttacks(thisPlayer.getAttacks() + 1);
                        }
                    }
                }
                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    attack.setVisibility(View.GONE);
                }
                checkForCardEnd(dialog);
            }
        });

        revealTopCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                if (Integer.parseInt(activeCard.getId()) == 7) {
                    if (Integer.parseInt(ownDeck.get(0).getCost()) > 3) {
                        hand.add(ownDeck.get(0));
                        thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                        ownDeck.remove(0);
                        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
                    } else {
                        thisPlayer.setCoins(thisPlayer.getCoins() + 2);
                    }
                } else if (Integer.parseInt(activeCard.getId()) == 23) {
                    if (ownDeck.size() <= 0) {
                        ownDeck = Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded());
                        Collections.shuffle(ownDeck);
                    }
                    DrawOrDiscardCardDialog.getInstance().showChooseAllyDialog(context, thisPlayer, iCardAddOrDeletedFromHand, ownDeck.get(0));
                    ownDeck.remove(0);
                }
                checkForCardEnd(dialog);
            }
        });

        discardOpponentAlly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    discardOpponentAlly.setVisibility(View.GONE);
                }
                if (!opponentPlayer.getAlly().equals("")) {
                    DiscardCard discardCard = new DiscardCard(context, database, opponentPlayer.getAlly(), 0, opponentPlayer, thisPlayer);
                    discardCard.showDialog();
                }
                checkForCardEnd(dialog);
            }
        });

        banishClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                DiscardCard discardCard = new DiscardCard(context, database, classroom, 1, opponentPlayer, thisPlayer);
                if (Integer.parseInt(activeCard.getId()) == 20)
                    discardCard.setGolds(1);
                discardCard.showDialog();

                banishClassroom.setVisibility(View.GONE);
                checkForCardEnd(dialog);

            }
        });

        discardPileDrawItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    discardOpponentAlly.setVisibility(View.GONE);
                }
                ArrayList<Card> discardedCards = helper.returnCardsFromString(thisPlayer.getDiscarded());
                ArrayList<Card> discardedCardsItems = new ArrayList<>();

                for (Card cardTmp : discardedCards) {
                    if (cardTmp.getType().equals("item")) {
                        discardedCardsItems.add(cardTmp);
                    }
                }
                if (discardedCardsItems.size() > 0) {
                    DiscardCard discardCard = new DiscardCard(context, database, discardedCardsItems, 2, opponentPlayer, thisPlayer);
                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                    discardCard.showDialog();
                }

                discardPileDrawItem.setVisibility(View.GONE);
                checkForCardEnd(dialog);
            }
        });

        banishHexFromDiscardPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    discardOpponentAlly.setVisibility(View.GONE);
                }
                ArrayList<Card> discardedCards = helper.returnCardsFromString(thisPlayer.getDiscarded());
                ArrayList<Card> discardedCardsHexes = new ArrayList<>();

                for (Card cardTmp : discardedCards) {
                    if (cardTmp.getCardType().equals("hex")) {
                        discardedCardsHexes.add(cardTmp);
                    }
                }
                if (discardedCardsHexes.size() > 0) {
                    DiscardCard discardCard = new DiscardCard(context, database, discardedCardsHexes, 3, opponentPlayer, thisPlayer);
                    if (Integer.parseInt(activeCard.getId()) == 15)
                        discardCard.setAttacks(2);
                    else if (Integer.parseInt(activeCard.getId()) == 22) {
                        discardCard.setCards(2);
                        discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                        //If deck don't have 2 cards we need new one
                        if (ownDeck.size() <= 1) {
                            ownDeck = Helpers.getInstance().getDeckFromDiscardPileAndHand(thisPlayer, ownDeck);
                        }
                        discardCard.setOwnDeck(ownDeck);
                    }
                    discardCard.showDialog();
                }

                banishHexFromDiscardPile.setVisibility(View.GONE);
                checkForCardEnd(dialog);

            }
        });

        drawCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);

                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    drawCard.setVisibility(View.GONE);
                }

                if (ownDeck.size() == 0) {
                    ownDeck = new ArrayList<>(Helpers.getInstance().returnCardsFromString(thisPlayer.getDiscarded()));
                }

                iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
                ownDeck.remove(0);

                checkForCardEnd(dialog);
            }
        });

        banishDiscardPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);

                DiscardCard discardCard = new DiscardCard(context, database, helper.returnCardsFromString(thisPlayer.getDiscarded()), 4, opponentPlayer, thisPlayer);
                if (Integer.parseInt(activeCard.getId()) == 16)
                    discardCard.setHearts(2);
                else if (Integer.parseInt(activeCard.getId()) == 20)
                    discardCard.setGolds(1);
                else
                    discardCard.setHearts(0);
                discardCard.showDialog();

                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    banishDiscardPile.setVisibility(View.GONE);
                }
                checkForCardEnd(dialog);
            }
        });

        banishHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);

                ArrayList<Card> handTmp = new ArrayList<>(hand);

                DiscardCard discardCard = new DiscardCard(context, database, handTmp, 6, opponentPlayer, thisPlayer);
                if (Integer.parseInt(activeCard.getId()) == 24)
                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                discardCard.showDialog();

                if (chooseEffect) {
                    for (Button buttonTmp : EffectsToDisable) {
                        buttonTmp.setVisibility(View.GONE);
                    }
                    EffectsToDisable.clear();
                } else {
                    banishHand.setVisibility(View.GONE);
                }
                checkForCardEnd(dialog);
            }
        });

        copySpell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                ArrayList<Card> playedCards = new ArrayList<>(helper.returnCardsFromString(thisPlayer.getPlayedCards()));
                ArrayList<Card> playedCardsSpells = new ArrayList<>();
                if (playedCards.size() > 0) {
                    for (Card cardTmp : playedCards) {
                        if (cardTmp.getType().equals("spell"))
                            playedCardsSpells.add(cardTmp);
                    }
                    DiscardCard discardCard = new DiscardCard(context, database, playedCardsSpells, 5, opponentPlayer, thisPlayer);
                    discardCard.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                    discardCard.showDialog();
                }

                checkForCardEnd(dialog);
            }
        });

        opponentHexToDiscardPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);
                opponentPlayer.setDiscarded(opponentPlayer.getDiscarded() + "," + hexes.get(0).getId());
                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discard").setValue(opponentPlayer.getDiscarded());
                hexes.remove(0);

                opponentHexToDiscardPile.setVisibility(View.GONE);
                checkForCardEnd(dialog);
            }
        });

        opponentDiscardNonHexCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                if (!mayUseSpell)
                    dialog.setCancelable(false);

                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discardCardSpell").setValue(1);

                opponentDiscardNonHexCard.setVisibility(View.GONE);
                checkForCardEnd(dialog);

            }
        });


        drawExtraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        drawCardDiscardOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        drawTwoCardDiscardOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                hand.add(ownDeck.get(0));
                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        opponentHexToHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/hand").setValue(opponentPlayer.getHand() + "," + hexes.get(0).getId());
                hexes.remove(0);

                checkForCardEnd(dialog);
            }
        });
    }

    private void checkForCardEnd(Dialog dialog) {
        if (cardSpells == 0 && !mayUseSpell) {
            iCardAddOrDeletedFromHand.onDeleteCard(activeCard);
            dialog.dismiss();
            if (thisPlayer.getPlayedCards() == "")
                thisPlayer.setPlayedCards(activeCard.getId());
            else
                thisPlayer.setPlayedCards(thisPlayer.getPlayedCards() + "," + activeCard.getId());
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        } else if (cardSpells == 0) {
            iCardAddOrDeletedFromHand.onDeleteCard(activeCard);
            dialog.setCancelable(true);
            if (thisPlayer.getPlayedCards() == "")
                thisPlayer.setPlayedCards(activeCard.getId());
            else
                thisPlayer.setPlayedCards(thisPlayer.getPlayedCards() + "," + activeCard.getId());
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        }
    }

    private boolean checkForSameHouse() {
        if (!activeCard.getHouse().equals("none")) {
            if (thisPlayer.getHouse().contains(activeCard.getHouse())) {
                return true;
            }
        }
        return false;
    }

}
