package com.example.hogwartsbattle.CustomDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hogwartsbattle.Interface.IChooseAllyDialog;
import com.example.hogwartsbattle.Interface.IChooseHouseDialog;
import com.example.hogwartsbattle.R;

public class ChooseHouseDialog {

    public static ChooseHouseDialog mDialog;
    IChooseHouseDialog iChooseHouseDialog;


    ImageView gryffindor, ravenclaw, hufflepuff, slytherin;

    public static ChooseHouseDialog getInstance() {
        if (mDialog == null)
            mDialog = new ChooseHouseDialog();
        return mDialog;
    }

    public void showChooseHouseDialog(Context context,
                                     IChooseHouseDialog iChooseHouseDialog) {
        this.iChooseHouseDialog = iChooseHouseDialog;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_house_dialog_layout);

        gryffindor = dialog.findViewById(R.id.gryffindor);
        ravenclaw = dialog.findViewById(R.id.ravenclaw);
        hufflepuff = dialog.findViewById(R.id.hufflepuff);
        slytherin = dialog.findViewById(R.id.slytherin);

        gryffindor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseHouseDialog.onChooseHouse(dialog, 0);
            }
        });

        ravenclaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseHouseDialog.onChooseHouse(dialog, 1);
            }
        });

        hufflepuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseHouseDialog.onChooseHouse(dialog, 2);
            }
        });

        slytherin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseHouseDialog.onChooseHouse(dialog,3);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
