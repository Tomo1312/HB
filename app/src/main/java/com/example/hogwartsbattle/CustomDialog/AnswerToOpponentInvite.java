package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hogwartsbattle.Interface.LobbyActivityListener;
import com.example.hogwartsbattle.Model.User;
import com.example.hogwartsbattle.R;

import java.util.Timer;
import java.util.TimerTask;

public class AnswerToOpponentInvite {
    public static AnswerToOpponentInvite mDialog;

    ImageView accept, decline;
    TextView headingChallenge;

    public static AnswerToOpponentInvite getInstance() {
        if (mDialog == null)
            mDialog = new AnswerToOpponentInvite();
        return mDialog;
    }

    public void showInviteDialog(Context context, User opponentPlayer, LobbyActivityListener lobbyActivityListener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_invite);
        Timer timer = new Timer("Timer");

        headingChallenge = dialog.findViewById(R.id.text_view_challenge);
        accept = dialog.findViewById(R.id.btnAcceptChallengeInvite);
        decline = dialog.findViewById(R.id.btnDeclineChallengeInvite);

        headingChallenge.setText(opponentPlayer.getUserName() + " challenged you to battle!");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialog = null;
                timer.cancel();
                lobbyActivityListener.userAccepted(opponentPlayer);
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mDialog = null;
                timer.cancel();
                lobbyActivityListener.userDeclined(opponentPlayer);

            }
        });
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TimerTask task = new TimerTask() {
            public void run() {
                dialog.dismiss();
                mDialog = null;
                timer.cancel();
                lobbyActivityListener.userDeclined(opponentPlayer);
            }
        };
        timer.schedule(task, 1000 * 10);
    }
}
