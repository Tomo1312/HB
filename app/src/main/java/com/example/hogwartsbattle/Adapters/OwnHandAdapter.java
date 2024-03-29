package com.example.hogwartsbattle.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.CustomDialog.CardDialog;
import com.example.hogwartsbattle.CustomDialog.DiscardCard;
import com.example.hogwartsbattle.Game.GameActivity;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OwnHandAdapter extends RecyclerView.Adapter<OwnHandAdapter.MyViewHolder> implements ICardAddOrDeletedFromHand {

    Context context;
    List<CardView> cardViewList;
    Player thisPlayer, opponentPlayer;
    ArrayList<Card> ownDeck, hexes;
    ArrayList<Card> ownHandCard;
    ArrayList<Card> classroom;
    FirebaseDatabase database;
    IChooseDialog iChooseDialog;

    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;

    int library;

    public OwnHandAdapter(Context context, ArrayList<Card> ownHandCard, Player thisPlayer,
                          Player opponentPlayer, ArrayList<Card> ownDeck, ArrayList<Card> hexes,
                          FirebaseDatabase database, IChooseDialog iChooseDialog, ArrayList<Card> classroom) {
        this.context = context;
        this.ownHandCard = ownHandCard;
        cardViewList = new ArrayList<>();
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hexes = hexes;
        this.database = database;
        this.iChooseDialog = iChooseDialog;
        this.classroom = classroom;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        iCardAddOrDeletedFromHand = this;
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_hand, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnHandAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String cardName = "id" + ownHandCard.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);

        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thisPlayer.getHexes().contains("85") && !checkForOnlyOneItemPlayed() && ownHandCard.get(position).getType().equals("item")) {
                    Toast.makeText(context, "You can only play one item because of hex!", Toast.LENGTH_LONG).show();
                } else if (thisPlayer.getHexes().contains("88") && !checkForTwoSpellPlayed() && ownHandCard.get(position).getType().equals("spell")) {
                    Toast.makeText(context, "You can only play 2 spells because of hex!", Toast.LENGTH_LONG).show();
                } else if (thisPlayer.getHexes().contains("86") && ownHandCard.get(position).getType().equals("ally")) {
                    Toast.makeText(context, "You can't play ally because of hex!", Toast.LENGTH_LONG).show();
                } else {
                    CardDialog cardDialog = new CardDialog(context, library, iCardAddOrDeletedFromHand,
                            ownHandCard.get(position), thisPlayer, opponentPlayer,
                            ownDeck, ownHandCard, hexes, database, iChooseDialog, classroom);
                    cardDialog.setOwnHandAdapter(OwnHandAdapter.this);
                    cardDialog.showDialog();
                }
            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }

    private boolean checkForTwoSpellPlayed() {
        if (thisPlayer.getPlayedCards().equals(""))
            return true;
        int spellPlayed = 0;
        for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards())) {
            if (cardTmp.getType().equals("spell"))
                spellPlayed++;
        }
        if (spellPlayed < 2) {
            return true;
        }
        return false;
    }

    private boolean checkForOnlyOneItemPlayed() {
        if (thisPlayer.getPlayedCards().equals(""))
            return true;
        for (Card cardTmp : Helpers.getInstance().returnCardsFromString(thisPlayer.getPlayedCards())) {
            if (cardTmp.getType().equals("item"))
                return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return ownHandCard.size();
    }

    @Override
    public void onDiscardCard(Card discardCard) {
        ownHandCard.remove(discardCard);
        notifyDataSetChanged();
        thisPlayer.setHand(Helpers.getInstance().returnCardsFromArray(ownHandCard));
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(thisPlayer.getHand());
    }

    @Override
    public void onPlayedAlly(Card Ally) {
        ownHandCard.remove(Ally);
        notifyDataSetChanged();
        String handString = Helpers.getInstance().returnCardsFromArray(ownHandCard);
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(handString);
        thisPlayer.setHand(handString);
    }

    @Override
    public void onBanishCard(Card card) {
        ownHandCard.remove(card);
        notifyDataSetChanged();
        String handString = Helpers.getInstance().returnCardsFromArray(ownHandCard);
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(handString);
        database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(card.getId());
        thisPlayer.setHand(handString);
    }

    @Override
    public void onAddCard(Card card) {
        ownHandCard.add(card);
        if (card.getCardType().equals("hex")) {
            if (card.getId().equals("80")) {
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(card.getId());
                ownHandCard.remove(card);
            } else if (card.getId().equals("81")) {
                if (!thisPlayer.getAlly().equals("")) {
                    Toast.makeText(context, "You must discard ally because of Levicorpus!", Toast.LENGTH_LONG).show();
                    DiscardCard discardOwnAlly = new DiscardCard(context, database, thisPlayer.getAlly(), 13, null, thisPlayer);
                    discardOwnAlly.setIChooseDialog(iChooseDialog);
                    discardOwnAlly.showDialog();
                } else {
                    Toast.makeText(context, "You lucky wizard, you don't have any ally currently active!", Toast.LENGTH_LONG).show();
                }
            } else if (card.getId().equals("84")) {
                Toast.makeText(context, "You need to banish top card of your deck because of Jelly-brain jinx!", Toast.LENGTH_LONG).show();
                iChooseDialog.onShuffleOwnDeck(1);
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(ownDeck.get(0).getId());
                ownDeck.remove(0);
                ownHandCard.remove(card);
            } else if (card.getId().equals("87")) {
                Toast.makeText(context, "You got 2 hexes in discard pile and banished Geminio!", Toast.LENGTH_LONG).show();
                iChooseDialog.onShuffleOwnDeck(1);
                database.getReference("rooms/" + Common.currentRoomName + "/banished").setValue(card.getId());
                StringBuilder newTwoHexes = new StringBuilder();
                newTwoHexes.append(hexes.get(0).getId()).append(",").append(hexes.get(1).getId());
                hexes.remove(0);
                hexes.remove(0);
                thisPlayer.setDiscardedString(newTwoHexes.toString());
                ownHandCard.remove(card);
            }

            thisPlayer.setHexes(thisPlayer.getHexes() + "," + card.getId());
        }
        String handString = Helpers.getInstance().returnCardsFromArray(ownHandCard);
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/hand").setValue(handString);
        thisPlayer.setHand(handString);
        notifyDataSetChanged();
    }

    public void cleanCardsInHand() {
        if (ownHandCard.size() > 0) {
            thisPlayer.setDiscardedString(Helpers.getInstance().returnCardsFromArray(ownHandCard));
            ownHandCard.clear();
            notifyDataSetChanged();
        }
    }

    public void setLibrary(int library) {
        this.library = library;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView card_from_layout;
        CardView card_Layout_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_Layout_card = itemView.findViewById(R.id.card_Layout_card);
            card_from_layout = itemView.findViewById(R.id.card_from_layout);

        }
    }
}
