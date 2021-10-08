package com.example.hogwartsbattle.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DiscardCardAdapter extends RecyclerView.Adapter<DiscardCardAdapter.MyViewHolder> {

    Context context;
    List<Card> cards;
    List<CardView> cardViewList;
    FirebaseDatabase database;
    Dialog dialog;
    Player opponentPlayer;
    Player thisPlayer;
    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;
    int extraAttacks, extraHearts, extraGolds, extraCards;
    ArrayList<Card> ownDeck;
    /*
     0 - layout allys
     1 - layout card
     2 - layout classroom
    */
    int layoutInt;

    public DiscardCardAdapter(Context context, ArrayList<Card> cards, int layout, FirebaseDatabase database, Dialog dialog, Player opponentPlayer, Player thisPlayer) {
        this.context = context;
        this.cards = cards;
        this.layoutInt = layout;
        cardViewList = new ArrayList<>();
        this.database = database;
        this.dialog = dialog;
        this.opponentPlayer = opponentPlayer;
        this.thisPlayer = thisPlayer;
    }

    public ICardAddOrDeletedFromHand getICardAddOrDeletedFromHand() {
        return iCardAddOrDeletedFromHand;
    }

    public void setICardAddOrDeletedFromHand(ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand) {
        this.iCardAddOrDeletedFromHand = iCardAddOrDeletedFromHand;
    }

    public void setAttacks(int extraAttacks) {
        this.extraAttacks = extraAttacks;
    }

    public void setHearts(int extraHearts) {
        this.extraHearts = extraHearts;
    }

    public void setOwnDeck(ArrayList<Card> ownDeck) {
        this.ownDeck = ownDeck;
    }

    public void setGolds(int extraGolds) {
        this.extraGolds = extraGolds;
    }

    public void setCards(int extraCards) {
        this.extraCards = extraCards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        //0 - Opponent discard ally
        //1 - Opponent discard from classroom
        //2 - Opponent draw from discard pile Item
        //3 - This player my banish a hex from discard pile
        switch (layoutInt) {
            case 0:
                itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_allys, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_discard, parent, false);
                break;

        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String cardName;
        switch (layoutInt) {
            case 0:
                cardName = "ally" + cards.get(position).getId();
                break;
            default:
                cardName = "id" + cards.get(position).getId();
                break;
        }
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);

        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (layoutInt) {
                    case 0:
                        discardOpponentAlly(cards.get(position));
                        break;
                    case 1:
                        discardFromClassroom(cards.get(position));
                        break;
                    case 2:
                        playerDrawItemFromDiscardPile(cards.get(position));
                        break;
                    case 3:
                        playerBanishHexInDiscardPile(cards.get(position));
                        break;
                    case 4:
                        playerBanishCardFromDiscardPile(cards.get(position));
                        break;
                    case 5:
                        copyPlayedSpell(cards.get(position));
                        break;
                    case 6:
                        banishFromHand(cards.get(position));
                        break;
                    default:
                        //cardName = "ally" + cards.get(position).getId();
                        break;
                }

            }
        });
//        if (!cardViewList.contains(holder.card_from_layout)) {
//            cardViewList.add(holder.card_Layout_card);
//        }
    }

    private void banishFromHand(Card banishedCard) {

        iCardAddOrDeletedFromHand.onDeleteCard(banishedCard);
        database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(banishedCard.getId());
        dialog.dismiss();
    }

    private void copyPlayedSpell(Card spellToCopy) {
        iCardAddOrDeletedFromHand.onAddCard(spellToCopy);
        String[] cards = thisPlayer.getPlayedCards().split(",");
        StringBuilder discardedCards = new StringBuilder();
        int i = 1;
        if (cards.length > 1) {
            for (String stringCardTmp : cards) {
                if (!stringCardTmp.equals(spellToCopy.getId()))
                    discardedCards.append(spellToCopy.getId());

                if (i != cards.length - 1)
                    discardedCards.append(",");
                i++;
            }
        }
        thisPlayer.setPlayedCards(discardedCards.toString());
        dialog.dismiss();

    }

    private void playerBanishCardFromDiscardPile(Card banishedCard) {
        String[] cards = thisPlayer.getDiscarded().split(",");
        int i = 1;
        StringBuilder newDiscardDeck = new StringBuilder();
        for (String cardTmp : cards) {
            if (cardTmp.equals(banishedCard.getId()))
                continue;
            newDiscardDeck.append(cardTmp);
            if (i == cards.length)
                newDiscardDeck.append(",");
        }

        if (banishedCard.getCardType().equals("hex")) {
            if (extraHearts > 0)
                thisPlayer.setLives(thisPlayer.getLives() + extraHearts);
        }

        if (extraGolds > 0) {
            thisPlayer.setCoins(thisPlayer.getCoins() + extraGolds);
        }
        thisPlayer.setDiscarded(newDiscardDeck.toString());
        database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(banishedCard.getId());
        dialog.dismiss();
    }

    private void playerBanishHexInDiscardPile(Card hexToBanish) {
        if (extraAttacks > 0) {
            thisPlayer.setAttacks(thisPlayer.getAttacks() + extraAttacks);
        } else if (extraCards > 0) {
            iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
            ownDeck.remove(0);
            iCardAddOrDeletedFromHand.onAddCard(ownDeck.get(0));
            ownDeck.remove(0);
        }
        database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(hexToBanish.getId());
        dialog.dismiss();
    }

    private void playerDrawItemFromDiscardPile(Card activeCard) {
        iCardAddOrDeletedFromHand.onAddCard(activeCard);
        String[] cards = thisPlayer.getDiscarded().split(",");
        StringBuilder discardedCards = new StringBuilder();
        int i = 1;
        if (cards.length > 0) {
            for (String stringCardTmp : cards) {
                if (!stringCardTmp.equals(activeCard.getId()))
                    discardedCards.append(activeCard.getId());

                if (i != cards.length - 1)
                    discardedCards.append(",");
                i++;
            }
        }
        thisPlayer.setDiscarded(discardedCards.toString());
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/discarded").setValue(thisPlayer.getDiscarded());
        dialog.dismiss();
    }

    private void discardFromClassroom(Card activeCard) {
        cards.remove(activeCard);
        StringBuilder stringCards = new StringBuilder();
        int i = 1;
        for (Card cardTmp : cards) {
            stringCards.append(cardTmp.getId());
            if (i != cards.size()) {
                stringCards.append(",");
            }
            i++;
        }
        if (extraGolds > 0) {
            thisPlayer.setCoins(thisPlayer.getCoins() + extraGolds);
        }
        database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(stringCards.toString());
        dialog.dismiss();
    }

    private void discardOpponentAlly(Card activeCard) {

        cards.remove(activeCard);
        StringBuilder stringCards = new StringBuilder();
        int i = 1;
        for (Card cardTmp : cards) {
            stringCards.append(cardTmp.getId());
            if (i != cards.size()) {
                stringCards.append(",");
            }
            i++;
        }
        database.getReference("rooms/" + Common.currentRoomName + "/" + opponentPlayer.getPlayerName() + "/ally").setValue(stringCards.toString());

        dialog.dismiss();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView card_from_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_from_layout = itemView.findViewById(R.id.card_from_layout);

        }
    }
}
