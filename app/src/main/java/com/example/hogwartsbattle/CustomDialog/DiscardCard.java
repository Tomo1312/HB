package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hogwartsbattle.Adapters.DiscardCardAdapter;
import com.example.hogwartsbattle.Adapters.OwnHandAdapter;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Common.SpacesItemDecoration;
import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
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
    // classroom, cards from discard pile, from hand,etc...
    ArrayList<Card> allCardsToDisplay, classroom, hexes;
    int layout, extraAttacks, extraHearts, extraGolds, extraCards;

    RecyclerView viewCardsForDelete;
    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;
    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;
    OwnHandAdapter ownHandAdapter;
    ArrayList ownDeck;

    Helpers helper = new Helpers();

    public DiscardCard(Context context, FirebaseDatabase database, String opponentStringAlly, int layout, Player opponentPlayer, Player thisPlayer) {
        super(context, 0);
        this.database = database;
        this.allCardsToDisplay = helper.returnCardsFromString(opponentStringAlly);
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

    public OwnHandAdapter getOwnHandAdapter() {
        return ownHandAdapter;
    }

    public void setOwnHandAdapter(OwnHandAdapter ownHandAdapter) {
        this.ownHandAdapter = ownHandAdapter;
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

    public void setClassroom(ArrayList<Card> classroom) {
        this.classroom = classroom;
    }

    public void setHexes(ArrayList<Card> hexes) {
        this.hexes = hexes;
    }

    public void setIUpdateAttackGoldHeart(IUpdateAttackGoldHeart iUpdateAttackGoldHeart) {
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DiscardCardAdapter discardCardAdapter;
        LinearLayoutManager horizontalLayoutManagerOwnHand = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        switch (layout) {
            case 0:
            case 10:
                //0 - Discards opponent ally
                dialog.setContentView(R.layout.layout_discard_card_vertical);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(new GridLayoutManager(context, 1));
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                if (ownHandAdapter != null) {
                    discardCardAdapter.setOwnHandAdapter(ownHandAdapter);
                    discardCardAdapter.setClassroom(classroom);
                    discardCardAdapter.setHexes(hexes);
                    discardCardAdapter.setIUpdateAttackGoldHeart(iUpdateAttackGoldHeart);
                }
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
            case 7:
            case 9:
            case 11:
                //2- Opponent draw from discard pile Item
                //5- Copy any spell from played cards
                //6- Banish from hand
                //7 - Discard from hand
                //9 - Discard card from spell and gain effects
                //11 - Banish Hex from played cards
                dialog.setContentView(R.layout.layout_discard_card_horizontal);
                viewCardsForDelete = dialog.findViewById(R.id.recycler_view_cards_for_delete);
                viewCardsForDelete.setLayoutManager(horizontalLayoutManagerOwnHand);
                discardCardAdapter = new DiscardCardAdapter(context, allCardsToDisplay, layout, database, dialog, opponentPlayer, thisPlayer);
                if (iCardAddOrDeletedFromHand != null)
                    discardCardAdapter.setICardAddOrDeletedFromHand(iCardAddOrDeletedFromHand);
                else if (ownHandAdapter != null && extraAttacks > 0 && extraHearts > 0 && extraCards > 0) {
                    discardCardAdapter.setOwnHandAdapter(ownHandAdapter);
                    discardCardAdapter.setAttacks(1);
                    discardCardAdapter.setHearts(1);
                    discardCardAdapter.setCards(1);
                } else if (ownHandAdapter != null)
                    discardCardAdapter.setOwnHandAdapter(ownHandAdapter);
                break;
            case 3:
            case 8:
                //3- This player may banish a hex from discard pile
                //8- put card from thisPlayer discard pile to opponent discard pile
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

        if (layout != 7)
            dialog.setCancelable(true);
        else
            dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
