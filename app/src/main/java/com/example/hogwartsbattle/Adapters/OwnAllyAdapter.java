package com.example.hogwartsbattle.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.CustomDialog.OwnAllyDialog;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Interface.IDisableAllyListener;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OwnAllyAdapter extends RecyclerView.Adapter<OwnAllyAdapter.MyViewHolder> implements IDisableAllyListener {

    Context context;
    List<CardView> cardViewList;
    Player thisPlayer, opponentPlayer;
    ArrayList<Card> ownDeck, hexes, classroom;
    ArrayList<Card> ownAllyCard;
    FirebaseDatabase database;

    IDisableAllyListener iDisableAllyListener;
    IChooseDialog iChooseDialog;

    OwnHandAdapter ownHandAdapter;

    public OwnAllyAdapter(Context context, Player thisPlayer,
                          Player opponentPlayer, ArrayList<Card> ownDeck, ArrayList<Card> hexes, ArrayList<Card> classroom,
                          FirebaseDatabase database, IChooseDialog iChooseDialog) {
        this.context = context;
        this.ownAllyCard = new ArrayList<>();
        this.cardViewList = new ArrayList<>();
        this.thisPlayer = thisPlayer;
        this.classroom = classroom;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hexes = hexes;
        this.database = database;
        this.iChooseDialog = iChooseDialog;
        this.iDisableAllyListener = this;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_allys, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String cardName = "ally" + ownAllyCard.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);


        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ownAllyCard.get(position).isUsed() && thisPlayer.isPlaying()) {
                    OwnAllyDialog ownAllyDialog = new OwnAllyDialog(context, ownAllyCard.get(position),
                            thisPlayer, opponentPlayer, ownDeck, classroom, hexes, database, iChooseDialog,
                            iDisableAllyListener, ownHandAdapter);
                    ownAllyDialog.showDialog();
                }

            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }

    public void addAlly(Card activeCard) {
        activeCard.setSavedGoldToZero();
        activeCard.setUsed(false);
        ownAllyCard.add(activeCard);
        notifyDataSetChanged();
        thisPlayer.setAlly(Helpers.getInstance().returnCardsFromArray(ownAllyCard));
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/ally").setValue(thisPlayer.getAlly());
    }

    public void removeAlly(Card removedAlly) {
        ownAllyCard.remove(removedAlly);
        notifyDataSetChanged();
        thisPlayer.setAlly(Helpers.getInstance().returnCardsFromArray(ownAllyCard));
        thisPlayer.setDiscarded(removedAlly.getId());
        database.getReference("rooms/" + Common.currentRoomName + "/" + thisPlayer.getPlayerName() + "/ally").setValue(thisPlayer.getAlly());
    }


    public void setAllyAvailable(Card ally) {
        for (Card cardTmp : ownAllyCard) {
            if (cardTmp.getId().equals(ally.getId())) {
                cardTmp.setUsed(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ownAllyCard.size();
    }

    public void updateAllies() {
        for (Card cardTmp : ownAllyCard) {
            cardTmp.setUsed(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onDisableListener() {
        notifyDataSetChanged();
    }

    public void setOwnHandAdapter(OwnHandAdapter ownHandAdapter) {
        this.ownHandAdapter = ownHandAdapter;
    }

    public boolean checkIfHandAdapterIsSet() {
        if (ownHandAdapter == null)
            return false;
        return true;
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
