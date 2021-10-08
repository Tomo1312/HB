package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Adapters.DiscardCardAdapter;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Common.SpacesItemDecoration;
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
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
    int layout, extraAttacks, extraHearts, extraGolds, extraCards;

    RecyclerView viewCardsForDelete;
    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;
    ArrayList ownDeck;

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

    public ICardAddOrDeletedFromHand getICardAddOrDeletedFromHand() {
        return iCardAddOrDeletedFromHand;
    }

    public void setICardAddOrDeletedFromHand(ICardAddOrDeletedFromHand iCardDeletedFromRecyclerView) {
        this.iCardAddOrDeletedFromHand = iCardDeletedFromRecyclerView;
    }

    public void setAttacks(int attacks) {
        this.extraAttacks = attacks;
    }

    public void setHearts(int hearts) {
        this.extraHearts = hearts;
    }

    public void setGolds(int extraGolds) {
        this.extraGolds = extraGolds;
    }

    public void setCards(int extraCards) {
        this.extraCards = extraCards;
    }

    public void setOwnDeck(ArrayList<Card> ownDeck) {
        this.ownDeck = ownDeck;
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
                discardCardAdapter = new DiscardCardAdapter(context, helper.returnCardsFromString(opponentStringAlly), layout, database, dialog, opponentPlayer, thisPlayer);
                break;
            case 1:
                //1- Opponent discard from classroom
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                if (extraGolds > 0) {
                    discardCardAdapter.setGolds(extraGolds);
                }
                break;
            case 2:
            case 5:
            case 6:
                //2- Opponent draw from discard pile Item
                //5- Copy any spell from played cards
                //6- Banish from hand
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                discardCardAdapter.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                break;
            case 3:
                //3- This player my banish a hex from discard pile
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                if (extraAttacks > 0)
                    discardCardAdapter.setAttacks(extraAttacks);
                else if (extraCards > 0) {
                    discardCardAdapter.setCards(extraCards);
                    discardCardAdapter.setOwnDeck(ownDeck);
                    discardCardAdapter.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                }
                break;
            case 4:
                //4- Banish card from discard, if it's hex gain 2 hearts
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                discardCardAdapter.setHearts(extraHearts);
                break;
            default:
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
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
