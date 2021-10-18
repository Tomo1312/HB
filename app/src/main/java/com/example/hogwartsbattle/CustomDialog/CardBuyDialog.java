package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Helpers.Helpers;
import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CardBuyDialog extends CustomDialog {
    Player thisPlayer;
    Card activeCard;
    FirebaseDatabase database;

    IChooseDialog iChooseDialog;

    ImageView cardImage;
    Button buyCard;
    ArrayList<Card> classroom, ownDeck;
    int library;

    public CardBuyDialog(Context context, ArrayList<Card> classroom, int library, Card activeCard, Player thisPlayer,
                         FirebaseDatabase database, IChooseDialog iChooseDialog, ArrayList<Card> ownDeck) {
        super(context, 0);

        this.activeCard = activeCard;
        this.thisPlayer = thisPlayer;
        this.database = database;
        this.iChooseDialog = iChooseDialog;
        this.classroom = classroom;
        this.ownDeck = ownDeck;
        this.library = library;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_card_buy);
        buyCard = dialog.findViewById(R.id.buy_card_from_classroom);
        cardImage = dialog.findViewById(R.id.card_image);

        String cardName = "id" + activeCard.getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        cardImage.setImageResource(id);
        setOnClickButtons(dialog);

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setOnClickButtons(Dialog dialog) {
        buyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thisPlayer.getHexes().contains("82") && thisPlayer.getCoins() >= Integer.parseInt(activeCard.getCost()) + 1) {
                    buyCardFromClassroom(1, dialog);
                } else if (thisPlayer.getCoins() >= Integer.parseInt(activeCard.getCost())) {
                    buyCardFromClassroom(0, dialog);
                } else {
                    Toast.makeText(context, "Don't have enough money!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void buyCardFromClassroom(int addToPrice, Dialog dialog) {
        if (activeCard.getId().equals(0)) {
            //Card 46, says to put item on top of deck
            if (thisPlayer.getPlayedCards().contains("46")) {
                ownDeck.add(0, activeCard);
            } else {
                if (thisPlayer.getDiscarded() == "")
                    thisPlayer.setDiscarded(activeCard.getId());
                else
                    thisPlayer.setDiscarded(thisPlayer.getDiscarded() + "," + activeCard.getId());
            }
            library--;
            database.getReference("rooms/" + Common.currentRoomName + "/library").setValue(library);
        } else {
            if (thisPlayer.getPlayedCards().contains("46") && activeCard.getType().equals("item")) {
                ownDeck.add(0, activeCard);
            } else if (thisPlayer.getPlayedCards().contains("45") && activeCard.getType().equals("spell")) {
                ownDeck.add(0, activeCard);
            } else if (thisPlayer.getPlayedCards().contains("34") && activeCard.getType().equals("ally")) {
                ownDeck.add(0, activeCard);
            } else {
                if (thisPlayer.getDiscarded().equals(""))
                    thisPlayer.setDiscarded(activeCard.getId());
                else
                    thisPlayer.setDiscarded(thisPlayer.getDiscarded() + "," + activeCard.getId());
            }
            thisPlayer.setCoins(thisPlayer.getCoins() - Integer.parseInt(activeCard.getCost()) - addToPrice);
            classroom.remove(activeCard);

            iChooseDialog.onUpdateAttackGoldHeart();
            database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(Helpers.getInstance().returnCardsFromArray(classroom));
        }
        database.getReference("rooms/" + Common.currentRoomName + "/classroomBought").setValue(activeCard.getId());

        dialog.dismiss();
    }
}
