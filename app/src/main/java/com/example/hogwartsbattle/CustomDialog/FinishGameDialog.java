package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.R;

public class FinishGameDialog {
    public static FinishGameDialog mDialog;
    IChooseDialog iChooseDialog;

    ImageView btnCat, btnFrog, btnOwl;

    public static FinishGameDialog getInstance() {
        if (mDialog == null)
            mDialog = new FinishGameDialog();
        return mDialog;
    }

    public void showFinishGameDialog(Context context,
                                     IChooseDialog iChooseDialog) {
        this.iChooseDialog = iChooseDialog;
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
                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                btnCat.setImageResource(R.drawable.cat);
                iChooseDialog.onChooseReturnToLobby(dialog);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


}
