package com.example.hogwartsbattle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.CustomDialog.CardDialog;
import com.example.hogwartsbattle.Interface.ICardDeletedFromRecyclerView;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OwnHandAdapter extends RecyclerView.Adapter<OwnHandAdapter.MyViewHolder> implements ICardDeletedFromRecyclerView {

    Context context;
    List<CardView> cardViewList;
    Player ownPlayer, opponentPlayer;
    ArrayList<Card> ownDeck, hexes;
    ArrayList<Card> ownHandCard;
    FirebaseDatabase database;

    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;

    ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView;
    IOwnAllyListener iOwnAllyListener;

    public OwnHandAdapter(Context context, ArrayList<Card> ownHandCard, Player ownPlayer,
                          Player opponentPlayer, ArrayList<Card> ownDeck, ArrayList<Card> hexes,
                          FirebaseDatabase database, IUpdateAttackGoldHeart iUpdateAttackGoldHeart, IOwnAllyListener iOwnAllyListener) {
        this.context = context;
        this.ownHandCard = ownHandCard;
        cardViewList = new ArrayList<>();
        this.ownPlayer = ownPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hexes = hexes;
        this.database = database;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.iOwnAllyListener = iOwnAllyListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        iCardDeletedFromRecyclerView = this;
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_hand, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnHandAdapter.MyViewHolder holder, int position) {
        String cardName = "id" + ownHandCard.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);

        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardDialog.getInstance().showCardFromHandDialog(context, position, iCardDeletedFromRecyclerView,
                        ownHandCard.get(position), ownPlayer, opponentPlayer,
                        ownDeck, ownHandCard, hexes, database, iUpdateAttackGoldHeart, iOwnAllyListener);

            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }
    @Override
    public int getItemCount() {
        return ownHandCard.size();
    }

    @Override
    public void onDeleteCard(Card card, int position) {
        ownHandCard.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        String handString ="";
        int i = 0;
        for(Card cardTmp : ownHandCard){
            if (i == ownHandCard.size()){
                handString += cardTmp.getId();
            }else{
                handString += cardTmp.getId() + ",";
            }
        }
        database.getReference("rooms/" + Common.currentRoomName + "/" + ownPlayer.getPlayerName() + "/hand").setValue(handString);
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
