package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hogwartsbattle.Interface.IChooseAllyDialog;
import com.example.hogwartsbattle.R;

public class ChooseAllyDialog {

    public static ChooseAllyDialog mDialog;
    IChooseAllyDialog iChooseAllyDialog;

    ImageView btnCat, btnFrog, btnOwl;

    public static ChooseAllyDialog getInstance() {
        if (mDialog == null)
            mDialog = new ChooseAllyDialog();
        return mDialog;
    }

    public void showChooseAllyDialog(Context context,
                                  IChooseAllyDialog iChooseAllyDialog) {
        this.iChooseAllyDialog = iChooseAllyDialog;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_ally_dialog_layout);
        btnCat = dialog.findViewById(R.id.btnCat);
        btnFrog = dialog.findViewById(R.id.btnFrog);
        btnOwl = dialog.findViewById(R.id.btnOwl);


        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView view = (ImageView) v;
                //overlay is black with transparency of 0x77 (119)
                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                btnCat.setImageResource(R.drawable.cat);
//                        btnCat.setImageBitmap(btnCat.getDrawable(R.drawable.img_down));
                iChooseAllyDialog.onChooseAlly(dialog, 2);
            }
        });

        btnFrog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseAllyDialog.onChooseAlly(dialog, 3);
            }
        });

        btnOwl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseAllyDialog.onChooseAlly(dialog, 1);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
