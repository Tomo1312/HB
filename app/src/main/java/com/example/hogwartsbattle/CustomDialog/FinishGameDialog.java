package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.R;

import java.util.Timer;
import java.util.TimerTask;

public class FinishGameDialog {
    public static FinishGameDialog mDialog;
    IChooseDialog iChooseDialog;

    TextView textWinner;
    Button btnReturnToLobby;

    public static FinishGameDialog getInstance() {
        if (mDialog == null)
            mDialog = new FinishGameDialog();
        return mDialog;
    }

    public void showFinishGameDialog(Context context,
                                     IChooseDialog iChooseDialog, String winner) {
        this.iChooseDialog = iChooseDialog;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_finish_game);


        textWinner = dialog.findViewById(R.id.text_view_winner);
        btnReturnToLobby = dialog.findViewById(R.id.btnReturnToLobby);

        textWinner.setText(winner + " has won! easy!");

        btnReturnToLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iChooseDialog.onChooseReturnToLobby(dialog);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


}
