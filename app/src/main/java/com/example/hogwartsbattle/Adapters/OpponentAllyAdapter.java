package com.example.hogwartsbattle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.CustomDialog.ShowCardDialog;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.R;

import java.util.ArrayList;
import java.util.List;

public class OpponentAllyAdapter extends RecyclerView.Adapter<OpponentAllyAdapter.MyViewHolder> {

    Context context;
    List<CardView> cardViewList;
    ArrayList<Card> opponentAllys;

    public OpponentAllyAdapter(Context context, ArrayList<Card> opponentAllys) {
        this.context = context;
        this.opponentAllys = new ArrayList<>();
        this.cardViewList = new ArrayList<>();
        this.opponentAllys = opponentAllys;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_own_allys, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String cardName = "ally" + opponentAllys.get(position).getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        holder.card_from_layout.setImageResource(id);
        holder.card_from_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCardDialog.getInstance().showCardDialog(context, opponentAllys.get(position), null);
            }
        });
        if (!cardViewList.contains(holder.card_from_layout)) {
            cardViewList.add(holder.card_Layout_card);
        }
    }

    @Override
    public int getItemCount() {
        return opponentAllys.size();
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
