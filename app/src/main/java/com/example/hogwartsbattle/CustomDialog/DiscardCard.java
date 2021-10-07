package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Adapters.DiscardCardAdapter;
import com.example.hogwartsbattle.Adapters.OpponentAllyAdapter;
import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.Helpers;
import com.example.hogwartsbattle.Common.SpacesItemDecoration;
import com.example.hogwartsbattle.Game.GameActivity;
import com.example.hogwartsbattle.Interface.ICardDeletedFromRecyclerView;
import com.example.hogwartsbattle.Interface.IOwnAllyListener;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DiscardCard extends CustomDialog {

    Player opponentPlayer;
    Player thisPlayer;
    FirebaseDatabase database;
    int cardSpells, position;
    // classroom, cards from discard pile, from hand,etc...
    ArrayList<Card> allCardsToDisplay;
    String opponentStringAlly;
    int layout;

    RecyclerView viewCardsForDelete;
    ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView;

    Helpers helper = new Helpers();

    public DiscardCard(Context context, FirebaseDatabase database, String opponentStringAlly, int layout, Player opponentPlayer, Player thisPlayer) {
        super(context, 0);
        this.position = position;
        this.database = database;
        this.opponentStringAlly = opponentStringAlly;
        this.layout = layout;
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
    }

    public DiscardCard(Context context, FirebaseDatabase database, ArrayList allCardsToDisplay, int layout, Player opponentPlayer, Player thisPlayer) {
        super(context, 0);
        this.database = database;
        this.allCardsToDisplay = allCardsToDisplay;
        this.layout = layout;
        this.thisPlayer = thisPlayer;
        this.opponentPlayer = opponentPlayer;
    }

    public ICardDeletedFromRecyclerView getICardDeletedFromRecyclerView() {
        return iCardDeletedFromRecyclerView;
    }

    public void setICardDeletedFromRecyclerView(ICardDeletedFromRecyclerView iCardDeletedFromRecyclerView) {
        this.iCardDeletedFromRecyclerView = iCardDeletedFromRecyclerView;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DiscardCardAdapter discardCardAdapter;
        LinearLayoutManager horizontalLayoutManagerOwnHand = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        switch (layout) {
            case 0:
                //Opponent discard ally
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(new GridLayoutManager(context, 1));
                discardCardAdapter = new DiscardCardAdapter(context, helper.returnCardsFromString(opponentStringAlly), layout, database, dialog, opponentPlayer,thisPlayer);
                break;
            case 1:
            case 2:
                //1- Opponent discard from classroom
                //2- Opponent draw from discard pile Item
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                discardCardAdapter.setICardDeletedFromRecyclerView(iCardDeletedFromRecyclerView);
                break;
            default:
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                break;

        }


        viewCardsForDelete.setHasFixedSize(true);
        viewCardsForDelete.addItemDecoration(new SpacesItemDecoration(8));

        viewCardsForDelete.setAdapter(discardCardAdapter);

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
