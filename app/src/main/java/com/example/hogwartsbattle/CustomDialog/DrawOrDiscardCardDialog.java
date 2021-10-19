package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hogwartsbattle.Interface.ICardAddOrDeletedFromHand;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;

public class DrawOrDiscardCardDialog {

    public static DrawOrDiscardCardDialog mDialog;
    ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand;

    Button btnDiscard, btnDraw;
    ImageView cardImage;

    public static DrawOrDiscardCardDialog getInstance() {
        if (mDialog == null)
            mDialog = new DrawOrDiscardCardDialog();
        return mDialog;
    }

    public void showChooseAllyDialog(Context context, Player thisPlayer, ICardAddOrDeletedFromHand iCardAddOrDeletedFromHand, Card activeCard) {
        this.iCardAddOrDeletedFromHand = iCardAddOrDeletedFromHand;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_card_draw_or_discard);
        btnDiscard = dialog.findViewById(R.id.btnCat);
        btnDraw = dialog.findViewById(R.id.btnFrog);
        cardImage = dialog.findViewById(R.id.card_image);
        int id = context.getResources().getIdentifier("drawable/id" + activeCard.getId(), null, context.getPackageName());
        cardImage.setImageResource(id);

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisPlayer.setDiscarded(activeCard.getId());
                dialog.dismiss();
            }
        });

        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iCardAddOrDeletedFromHand.onAddCard(activeCard);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
