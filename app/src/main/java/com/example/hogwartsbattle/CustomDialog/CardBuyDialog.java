package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Interface.IUpdateAttackGoldHeart;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CardBuyDialog extends CustomDialog {
    Player ownPlayer;
    Card activeCard;
    FirebaseDatabase database;

    IUpdateAttackGoldHeart iUpdateAttackGoldHeart;

    ImageView cardImage;
    Button buyCard;
    ArrayList<Card> classroom;

    public CardBuyDialog(Context context, ArrayList<Card> classroom, int position, Card activeCard, Player thisPlayer,
                         FirebaseDatabase database, IUpdateAttackGoldHeart iUpdateAttackGoldHeart) {
        super(context, position);

        this.activeCard = activeCard;
        this.ownPlayer = thisPlayer;
        this.database = database;
        this.iUpdateAttackGoldHeart = iUpdateAttackGoldHeart;
        this.classroom = classroom;
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
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setOnClickButtons(Dialog dialog) {
        buyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ownPlayer.getCoins() >= Integer.parseInt(activeCard.getCost())) {
                    if (ownPlayer.getDiscarded() == "")
                        ownPlayer.setDiscarded(activeCard.getId());
                    else
                        ownPlayer.setDiscarded(ownPlayer.getDiscarded() + "," + activeCard.getId());
                    ownPlayer.setCoins(ownPlayer.getCoins() - Integer.parseInt(activeCard.getCost()));
                    iUpdateAttackGoldHeart.onUpdateAttackGoldHeart();

                    String newClassroom ="";
                    classroom.remove(activeCard);
                    int i = 0;
                    for (Card cardTmp : classroom){

                        if(i==2){
                            newClassroom += cardTmp.getId();
                        }else{
                            newClassroom += cardTmp.getId() +",";
                        }
                        i++;
                    }

                    database.getReference("rooms/" + Common.currentRoomName + "/classroom").setValue(newClassroom);

                    dialog.dismiss();
                }
            }
        });
    }

}
