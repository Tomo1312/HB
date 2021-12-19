package com.example.hogwartsbattle.CustomDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Game.LobbyActivity;
import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class InviteOpponent {
    public static InviteOpponent mDialog;

    Timer inviteTimer;
    AlertDialog inviteDialog;
    ValueEventListener valueEventListenerInviteAnswer;
    FirebaseDatabase database;

    public static InviteOpponent getInstance() {
        if (mDialog == null)
            mDialog = new InviteOpponent();
        return mDialog;
    }

    public void showCardDialog(Context context, List<String> playersOnline, List<String> playersOnlineId, FirebaseDatabase database) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inviteDialog = new SpotsDialog.Builder().setCancelable(false).setMessage("Waiting for respond (10 seconds)").setContext(context).build();
        this.database = database;

        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundResource(R.color.background);

        ListView listViewPlayersOnline = new ListView(context);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.simple_list_lobby_layout, playersOnline);
        listViewPlayersOnline.setAdapter(adapter);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        listViewPlayersOnline.setLayoutParams(lp);
        parent.addView(listViewPlayersOnline);


        listViewPlayersOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inviteDialog.show();
                database.getReference("Users/" + playersOnlineId.get(position) + "/invited").setValue(Common.currentUser.getUserId());
                valueEventListenerInviteAnswer = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue().equals("accepted")) {
                                Toast.makeText(context, "Player accepted!", Toast.LENGTH_LONG).show();
                                inviteDialog.dismiss();
                                inviteTimer.cancel();
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
                                dialog.dismiss();

                            } else if (snapshot.getValue().equals("declined")) {
                                Toast.makeText(context, "Player declined!", Toast.LENGTH_LONG).show();
                                inviteDialog.dismiss();
                                inviteTimer.cancel();
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").addValueEventListener(valueEventListenerInviteAnswer);
                TimerTask task = new TimerTask() {
                    public void run() {
                        inviteDialog.dismiss();
                        inviteTimer.cancel();
                        database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                        database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
                        dialog.dismiss();
                    }
                };
                inviteTimer = new Timer("Timer for invite player");
                inviteTimer.schedule(task, 1000 * 12);

            }
        });
        dialog.setContentView(parent);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
}
