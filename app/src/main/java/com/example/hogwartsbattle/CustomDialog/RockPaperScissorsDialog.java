package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.hogwartsbattle.Interface.IChooseDialog;
import com.example.hogwartsbattle.R;

public class RockPaperScissorsDialog {

    public static RockPaperScissorsDialog mDialog;
    IChooseDialog iChooseDialog;


    ImageView rock, paper, scissors;

    public static RockPaperScissorsDialog getInstance() {
        if (mDialog == null)
            mDialog = new RockPaperScissorsDialog();
        return mDialog;
    }

    public void showRockPaperScissorsDialog(Context context, IChooseDialog iChooseDialog) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rock_paper_scissors_dialog_layout);

        rock = dialog.findViewById(R.id.btnRock);
        paper = dialog.findViewById(R.id.btnPaper);
        scissors = dialog.findViewById(R.id.btnScissors);

        rock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseDialog.onPaperRockScissorsChoose(dialog, "rock");
            }
        });

        paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseDialog.onPaperRockScissorsChoose(dialog, "paper");
            }
        });

        scissors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iChooseDialog.onPaperRockScissorsChoose(dialog, "scissors");
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
