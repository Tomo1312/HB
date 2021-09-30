package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hogwartsbattle.Common.Common;
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

public class CardDialog {

    public static CardDialog mDialog;
    Player ownPlayer;
    Player opponentPlayer;
    Card activeCard;
    ArrayList<Card> ownDeck, hand, hexes;
    FirebaseDatabase database;
    int cardSpells, cardSpellsUsed, position;


    ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView;
    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    IOwnAllyListener iOwnAllyListener;

    ArrayList<Button> EffectsToDisable = new ArrayList<>();
    boolean chooseEffect;

    ImageView cardImage;
    Button gold, attack, heart, banishClassroom, banishHand,
            banishDiscardPile, drawCard, drawCardDiscardOne, drawTwoCardDiscardOne,
            opponentHexToDiscardPile, opponentHexToHand, return_to_library, drawExtraCard;

    public static CardDialog getInstance() {
        if (mDialog == null)
            mDialog = new CardDialog();
        return mDialog;
    }

    public void showCardFromHandDialog(Context context, int position, ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView,
                                       Card activeCard, Player thisPlayer, Player opponentPlayer, ArrayList<Card> ownDeck,
                                       ArrayList<Card> hand, ArrayList<Card> hexes, FirebaseDatabase database,
                                       IUpdateAttackGoldHeart iUpdateAttackGoldHeart, IOwnAllyListener iOwnAllyListener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_card_active);

        this.iCardDeletedFromRecyclerView = iCardDeletedFromRecyclerView;
        this.activeCard = activeCard;
        this.ownPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hand = hand;
        this.database = database;
        this.hexes = hexes;
        this.position = position;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.iOwnAllyListener = iOwnAllyListener;

        cardSpellsUsed = 0;
        chooseEffect = false;

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
        setOnClickButtons(dialog);

        showButtonsOnCardId();

        String cardName = "id" + activeCard.getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        cardImage.setImageResource(id);

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            case 9:
                cardSpells = 2;
                if (checkForSameHouse()) {
                    cardSpells++;
                    drawCard.setVisibility(View.VISIBLE);
                }
                attack.setVisibility(View.VISIBLE);
                heart.setVisibility(View.VISIBLE);
                break;
            case 11:
                cardSpells = 2;
                if (checkForSameHouse()) {
                    cardSpells++;
                    drawExtraCard.setVisibility(View.VISIBLE);
                }
                gold.setVisibility(View.VISIBLE);
                drawCard.setVisibility(View.VISIBLE);
                break;
            case 13:
                cardSpells = 1;
                if (checkForSameHouse()) {
                    chooseEffect = true;
                    EffectsToDisable.add(heart);
                    EffectsToDisable.add(gold);
                }
                break;
            case 15:
                cardSpells = 1;
                gold.setVisibility(View.VISIBLE);
                if (checkForSameHouse()) {
                    cardSpells++;
                    drawCard.setVisibility(View.VISIBLE);
                }
                break;
            default:
                if (activeCard.getType().equals("ally")) {
                    if (ownPlayer.getAlly().equals("")) {
                        ownPlayer.setAlly(activeCard.getId());
                    } else {
                        ownPlayer.setAlly(ownPlayer.getAlly() + "," + activeCard.getId());
                    }
                    iOwnAllyListener.onOwnAllyChange(activeCard);
                    iCardDeletedFromRecyclerView.onDeleteCard(activeCard, position);
                    database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/ally").setValue(ownPlayer.getAlly());

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
                ownPlayer.setCoins(ownPlayer.getCoins() + Integer.parseInt(activeCard.getCoins()));
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
                if (ownPlayer.getLives() < 7)
                    if (ownPlayer.getLives() + Integer.parseInt(activeCard.getHeart()) > 7) {
                        ownPlayer.setLives(7);
                    } else {
                        ownPlayer.setLives(ownPlayer.getLives() + Integer.parseInt(activeCard.getHeart()));
                    }
                checkForCardEnd(dialog);
            }
        });
        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                ownPlayer.setAttacks(ownPlayer.getAttacks() + Integer.parseInt(activeCard.getAttack()));
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
        banishClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
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
                hand.add(ownDeck.get(0));
                ownPlayer.setHand(ownPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/hand").setValue(ownPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        drawExtraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                ownPlayer.setHand(ownPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/hand").setValue(ownPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        drawCardDiscardOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                ownPlayer.setHand(ownPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/hand").setValue(ownPlayer.getHand());

                checkForCardEnd(dialog);
            }
        });
        drawTwoCardDiscardOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSpells--;
                dialog.setCancelable(false);
                hand.add(ownDeck.get(0));
                ownPlayer.setHand(ownPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                hand.add(ownDeck.get(0));
                ownPlayer.setHand(ownPlayer.getHand() + "," + ownDeck.get(0).getId());
                ownDeck.remove(0);
                database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/hand").setValue(ownPlayer.getHand());

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
        if (cardSpells == 0) {
            iCardDeletedFromRecyclerView.onDeleteCard(activeCard, position);
            dialog.dismiss();
            if (ownPlayer.getNotHandNotDiscarded() == "")
                ownPlayer.setNotHandNotDiscarded(activeCard.getId());
            else
                ownPlayer.setNotHandNotDiscarded(ownPlayer.getNotHandNotDiscarded() + "," + activeCard.getId());
            iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();
        }
    }

    private boolean checkForSameHouse() {
        if(!activeCard.getHouse().equals("none")){
            if(ownPlayer.getHouse().contains(activeCard.getHouse())){
                return true;
            }
        }
        return false;
    }
}
