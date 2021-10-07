package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.Helpers;
import com.example.hogwartsbattle.Interface.ICardDeletedFromRecyclerView;
import com.example.hogwartsbattle.Interface.IChooseHouseDialog;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CardDialog extends CustomDialog{

    Player thisPlayer;
    Player opponentPlayer;
    Card activeCard;
    ArrayList<Card> ownDeck, hand, hexes;
    FirebaseDatabase database;
    int cardSpells, position;

    ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView;
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
            discardOpponentAlly, discardPileDrawItem;

    public CardDialog(Context context, int position, ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView,
                      Card activeCard, Player thisPlayer, Player opponentPlayer, ArrayList<Card> ownDeck,
                      ArrayList<Card> hand, ArrayList<Card> hexes, FirebaseDatabase database,
                      IUpdateAttackGoldHeart iUpdateAttackGoldHeart, IOwnAllyListener iOwnAllyListener, ArrayList<Card> classroom){
        super(context,position);

        this.iCardDeletedFromRecyclerView = iCardDeletedFromRecyclerView;
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

        showButtonsOnCardId();

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
        setOnClickButtons(dialog);
    }

    private void showButtonsOnCardId() {
        switch (Integer.valueOf(activeCard.getId())) {
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
            default:
                if (activeCard.getType().equals("ally")) {
                    if (thisPlayer.getAlly().equals("")) {
                        thisPlayer.setAlly(activeCard.getId());
                    } else {
                        thisPlayer.setAlly(thisPlayer.getAlly() + "," + activeCard.getId());
                    }
                    iOwnAllyListener.onOwnAllyChange(activeCard);
                    iCardDeletedFromRecyclerView.onDeleteCard(activeCard, position);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/ally").setValue(thisPlayer.getAlly());

                }
                // code block
        }

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
                if (Integer.parseInt(ownDeck.get(0).getCost()) > 3) {
                    hand.add(ownDeck.get(0));
                    thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                    ownDeck.remove(0);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
                } else {
                    thisPlayer.setCoins(thisPlayer.getCoins() + 2);
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
                if(!opponentPlayer.getAlly().equals("")){
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
                if(!mayUseSpell)
                    dialog.setCancelable(false);
                DiscardCard discardCard = new DiscardCard(context, database, classroom, 1, opponentPlayer, thisPlayer);
                discardCard.showDialog();

                banishClassroom.setVisibility(View.GONE);
                checkForCardEnd(dialog);

            }
        });

        discardPileDrawItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
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

                for(Card cardTmp : discardedCards){
                    if(cardTmp.getType().equals("item")){
                        discardedCardsItems.add(cardTmp);
                    }
                }
                if(discardedCardsItems.size()>0){
                    DiscardCard discardCard= new DiscardCard(context, database, discardedCards, 2, opponentPlayer, thisPlayer);
                    discardCard.setICardDeletedFromRecyclerView(iCardDeletedFromRecyclerView);
                }

                discardPileDrawItem.setVisibility(View.GONE);
                checkForCardEnd(dialog);
            }
        });

        banishDiscardPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);

                checkForCardEnd(dialog);
            }
        });
        drawCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                iCardDeletedFromRecyclerView.onAddCard(ownDeck.get(0));
                thisPlayer.setHand(thisPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());

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
        opponentHexToDiscardPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/discard").setValue(opponentPlayer.getDiscarded() + "," + hexes.get(0).getId());
                hexes.remove(0);

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
            iCardDeletedFromRecyclerView.onDeleteCard(activeCard, position);
            dialog.dismiss();
            if (thisPlayer.getNotHandNotDiscarded() == "")
                thisPlayer.setNotHandNotDiscarded(activeCard.getId());
            else
                thisPlayer.setNotHandNotDiscarded(thisPlayer.getNotHandNotDiscarded() + "," + activeCard.getId());
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        }else if (cardSpells == 0 && mayUseSpell){
            iCardDeletedFromRecyclerView.onDeleteCard(activeCard, position);
            dialog.setCancelable(true);
            if (thisPlayer.getNotHandNotDiscarded() == "")
                thisPlayer.setNotHandNotDiscarded(activeCard.getId());
            else
                thisPlayer.setNotHandNotDiscarded(thisPlayer.getNotHandNotDiscarded() + "," + activeCard.getId());
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
