package com.example.hogwartsbattle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.CustomDialog.CardDialog;
import com.example.hogwartsbattle.CustomDialog.OwnAllyDialog;
import com.example.hogwartsbattle.Interface.ICardDeletedFromRecyclerView;
import com.example.hogwartsbattle.Interface.IDisableAllyListener;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OwnAllyAdapter extends RecyclerView.Adapter<OwnAllyAdapter.MyViewHolder> implements IDisableAllyListener {

    Context context;
    List<CardView> cardViewList;
    Player ownPlayer, opponentPlayer;
    ArrayList<Card> ownDeck, hexes;
    ArrayList<Card> ownAllyCard;
    FirebaseDatabase database;

    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    IDisableAllyListener iDisableAllyListener;

    IOwnAllyListener iOwnAllyListener;

    public OwnAllyAdapter(Context context, Player ownPlayer,
                          Player opponentPlayer, ArrayList<Card> ownDeck, ArrayList<Card> hexes,
                          FirebaseDatabase database, IUpdateAttackGoldHeart iUpdateAttackGoldHeart) {
        this.context = context;
        this.ownAllyCard =  new ArrayList<>();
        this.cardViewList = new ArrayList<>();
        this.ownPlayer = ownPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ownDeck = ownDeck;
        this.hexes = hexes;
        this.database = database;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.iDisableAllyListener = this;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_allys, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String cardName = "ally" + ownAllyCard.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);

        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ownAllyCard.get(position).isUsed()) {
                    OwnAllyDialog ownAllyDialog = new OwnAllyDialog(context, position,
                            ownAllyCard.get(position), ownPlayer, opponentPlayer,
                            ownDeck, ownAllyCard, hexes, database, iUpdateAttackGoldHeart, iOwnAllyListener, iDisableAllyListener);
                    ownAllyDialog.showDialog();
                }

            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }

    public void addAlly(Card activeCard) {
        this.ownAllyCard.add(activeCard);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ownAllyCard.size();
    }

    public void updateAllies() {
        for(Card cardTmp : ownAllyCard){
            cardTmp.setUsed(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onDisableListener() {
        notifyDataSetChanged();
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
