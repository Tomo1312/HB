package com.example.hogwartsbattle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.CustomDialog.CardBuyDialog;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.MyViewHolder> {

    Context context;
    ArrayList<Card> classroom;
    List<CardView> cardViewList;

    FirebaseDatabase database;

    Player thisPlayer;
    IChooseDialog iChooseDialog;
    ArrayList<Card> ownDeck;


    public ClassroomAdapter(Context context, ArrayList<Card> classroom, FirebaseDatabase database, Player thisPlayer, IChooseDialog iChooseDialog) {
        this.context = context;
        this.classroom = classroom;
        cardViewList = new ArrayList<>();
        this.database = database;
        this.thisPlayer = thisPlayer;
        this.iChooseDialog = iChooseDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_classroom, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String cardName = "id" + classroom.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);
        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardBuyDialog cardBuyDialog = new CardBuyDialog(context, classroom, position, classroom.get(position), thisPlayer, database, iChooseDialog, ownDeck);
                cardBuyDialog.showDialog();
            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }

    @Override
    public int getItemCount() {
        return classroom.size();
    }

    public void setOwnDeck(ArrayList<Card> ownDeck) {
        this.ownDeck = ownDeck;
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
