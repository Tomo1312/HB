package com.example.hogwartsbattle.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.HarryMediaPlayer;
import com.example.hogwartsbattle.CustomDialog.AnswerToOpponentInvite;
import com.example.hogwartsbattle.Helpers.ListenerHelpers;
import com.example.hogwartsbattle.Interface.LobbyActivityListener;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.Model.User;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LobbyActivity extends AppCompatActivity implements LobbyActivityListener {

    ListView listViewRooms, listViewPlayersOnline;
    Button btnCreateRoom;

    List<String> roomsList, playersOnline, playersOnlineId;

    String playerName;
    String roomName;

    TextView tvPlayerName, tvPlayerWins, tvPlayerLoses;
    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef, usersOnlineRef, usersInvitedRef;
    LobbyActivityListener lobbyActivityListener;
    AlertDialog inviteDialog;

    ValueEventListener valueEventListenerRoom, valueEventListenerCreateRoom, valueEventListenerPlayersOnline, valueEventListenerForInvited, valueEventListenerInviteAnswer;
    User user;
    HarryMediaPlayer mediaPlayer;
    Timer inviteTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Paper.init(this);
        lobbyActivityListener = this;
        inviteDialog = new SpotsDialog.Builder().setCancelable(false).setMessage("Waiting for respond (10 seconds)").setContext(LobbyActivity.this).build();
        database = FirebaseDatabase.getInstance();

        database.getReference("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().toString().equals(getApplicationContext().getString(R.string.app_version))) {
                        new AlertDialog.Builder(LobbyActivity.this)
                                .setTitle("Update Game")
                                .setMessage("Update game to the latest version")
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user = Paper.book().read(Common.KEY_THIS_USER, new User());
        Common.currentUser = user;
        getPreload();
//        mediaPlayer = new HarryMediaPlayer(this);
//        mediaPlayer.startPlaying();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("roomName");
            if (roomName != null) {
                database.getReference("rooms/" + roomName).removeValue();
            }
        }
        if (!TextUtils.isEmpty(user.getUserId())) {
            if (user.isFirstTime()) {
                new AlertDialog.Builder(this)
                        .setTitle("Tutorial")
                        .setMessage("This game don't have in game tutorial, so if you don't know how to play this game go watch on youtube :D")
                        .setPositiveButton("Watch video", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference("Users/" + user.getUserId() + "/firstTime").setValue(false);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=IIh5I_vdcYQ"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage("com.google.android.youtube");
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        setUiView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference("Users/" + Common.currentUser.getUserId() + "/").child("status").setValue("online");
        database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
        database.getReference("Users/" + Common.currentUser.getUserId() + "/invited").setValue("none");

    }

    private void getPreload() {
        boolean isPlaying = Paper.book().read(Common.KEY_IS_PLAYING, false);
//        Player thisPlayer = Paper.book().read(Common.KEY_THIS_PLAYER, new Player());
//
//        if(thisPlayer.getPlayerName().equals("")){
//            this.finishAffinity();
//        }
//        Log.e("LobbyActivity", "thisPlayer: " + thisPlayer.getPlayerName());
        if (isPlaying) {
            String currentRoom = Paper.book().read(Common.KEY_ROOM, "");
            if (!currentRoom.equals("")) {

                database.getReference("rooms/" + currentRoom).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                            intent.putExtra("roomName", currentRoom);
                            Common.currentRoomName = currentRoom;
                            finish();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void setUiView() {

        playerName = user.getUserName();
        roomName = playerName + "'s room";
//        checkIfItsInRoom();

        listViewRooms = findViewById(R.id.listViewRooms);
        listViewPlayersOnline = findViewById(R.id.listViewPlayersOnline);
        btnCreateRoom = findViewById(R.id.btnCreateRoom);
        tvPlayerName = findViewById(R.id.playerName);
        tvPlayerWins = findViewById(R.id.playerWins);
        tvPlayerLoses = findViewById(R.id.playerLoses);
        tvPlayerName.setText(playerName);
        tvPlayerWins.setText(user.getWins() + " Win");
        tvPlayerLoses.setText(user.getLoses() + " Loss");
        roomsList = new ArrayList<>();
        playersOnline = new ArrayList<>();
        playersOnlineId = new ArrayList<>();
        roomsRef = database.getReference("rooms");
        usersOnlineRef = database.getReference("Users");
        usersInvitedRef = database.getReference("Users/" + user.getUserId() + "/invited");
//        database.getReference("Users/" + Common.currentUser.getUserId() + "/").child("status").setValue("online");
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreateRoom.setText("Room created!");
                btnCreateRoom.setEnabled(false);
                roomRef = database.getReference("rooms/" + roomName + "/" + playerName);
                database.getReference("rooms/" + roomName + "/startGame").setValue("false");
                database.getReference("rooms/" + roomName + "/classroom").setValue("");
                database.getReference("rooms/" + roomName + "/classroomBought").setValue("");
                database.getReference("rooms/" + roomName + "/banished").setValue("");
                database.getReference("rooms/" + roomName + "/library").setValue("8");
                database.getReference("rooms/" + roomName + "/playing").setValue("");
                Player player = new Player(playerName);
                addRoomEventListener();
                database.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);
            }
        });

        listViewRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/" + roomName + "/" + playerName);
                Player player = new Player(playerName);
                addRoomEventListener();
                database.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);
            }
        });

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
                                Toast.makeText(LobbyActivity.this, "Player accepted!", Toast.LENGTH_LONG).show();
                                inviteDialog.dismiss();
                                stopAllListeners();
                                inviteTimer.cancel();
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");

                                roomRef = database.getReference("rooms/" + roomName + "/" + playerName);
                                database.getReference("rooms/" + roomName + "/startGame").setValue("false");
                                database.getReference("rooms/" + roomName + "/classroom").setValue("");
                                database.getReference("rooms/" + roomName + "/classroomBought").setValue("");
                                database.getReference("rooms/" + roomName + "/banished").setValue("");
                                database.getReference("rooms/" + roomName + "/library").setValue("8");
                                database.getReference("rooms/" + roomName + "/playing").setValue("");
                                Player player = new Player(playerName);
                                database.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);

                                Intent intent = new Intent(LobbyActivity.this, RoomActivity.class);
                                intent.putExtra("roomName", roomName);
                                Common.currentRoomName = roomName;
                                Log.e("LOBBYACTIVTIY", "RoomName= " + roomName);
                                finish();
                                startActivity(intent);
                            } else if (snapshot.getValue().equals("declined")) {
                                Toast.makeText(LobbyActivity.this, "Player declined!", Toast.LENGTH_LONG).show();
                                inviteDialog.dismiss();
                                inviteTimer.cancel();
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                                database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
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
                        database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").removeEventListener(valueEventListenerInviteAnswer);
                        database.getReference("Users/" + Common.currentUser.getUserId() + "/inviteAnswer").setValue("none");
                        inviteTimer.cancel();
                    }
                };
                inviteTimer = new Timer("Timer for invite player");
                inviteTimer.schedule(task, 1000 * 12);

            }
        });
        setAllListeners();
    }

    private void setAllListeners() {
        addRoomsEventListener();
        addPlayersOnlineListener();
        addListenerForInvited();
    }

    private void addListenerForInvited() {
        valueEventListenerForInvited = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().equals("none")) {
                        getPlayerNameById(snapshot.getValue().toString());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        usersInvitedRef.addValueEventListener(valueEventListenerForInvited);
    }

    private void getPlayerNameById(String opponentId) {
        database.getReference("Users/" + opponentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User opponentUser = snapshot.getValue(User.class);
                AnswerToOpponentInvite.getInstance().showInviteDialog(LobbyActivity.this, opponentUser, lobbyActivityListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addPlayersOnlineListener() {
        valueEventListenerPlayersOnline = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playersOnline.clear();
                playersOnlineId.clear();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(LobbyActivity.this,
                        R.layout.simple_list_lobby_layout, playersOnline);
                listViewPlayersOnline.setAdapter(adapter);
                Iterable<DataSnapshot> users = snapshot.getChildren();
                for (DataSnapshot snapshotTmp : users) {
                    if (snapshotTmp.child("status").getValue().equals("online") && !snapshotTmp.child("userId").getValue().equals(Common.currentUser.getUserId())) {
                        playersOnlineId.add(snapshotTmp.getKey());
                        playersOnline.add(snapshotTmp.child("userName").getValue().toString());
                        adapter = new ArrayAdapter<>(LobbyActivity.this,
                                R.layout.simple_list_lobby_layout, playersOnline);
                        listViewPlayersOnline.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        usersOnlineRef.addValueEventListener(valueEventListenerPlayersOnline);
    }

    private void addRoomEventListener() {
        valueEventListenerCreateRoom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                btnCreateRoom.setText("CRATE ROOM");
                btnCreateRoom.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                intent.putExtra("roomName", roomName);
                Common.currentRoomName = roomName;
                finish();
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnCreateRoom.setText("CRATE ROOM");
                btnCreateRoom.setEnabled(true);
                Toast.makeText(LobbyActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        };
        roomRef.addValueEventListener(valueEventListenerCreateRoom);

    }

    private void addRoomsEventListener() {
        valueEventListenerRoom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomsList.clear();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LobbyActivity.this,
                        R.layout.simple_list_lobby_layout, roomsList);
                listViewRooms.setAdapter(adapter);
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for (DataSnapshot snapshotTmp : rooms) {
                    if (!(snapshotTmp.getKey().equals("holder"))) {
                        if (snapshotTmp.child("startGame").getValue().equals("false")) {
                            roomsList.add(snapshotTmp.getKey());

                            adapter = new ArrayAdapter<>(LobbyActivity.this,
                                    R.layout.simple_list_lobby_layout, roomsList);
                            listViewRooms.setAdapter(adapter);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        roomsRef.addValueEventListener(valueEventListenerRoom);
    }

    @Override
    protected void onPause() {
        super.onPause();

        database.getReference("Users/" + Common.currentUser.getUserId() + "/").child("status").setValue("offline");
    }

    protected void stopAllListeners() {
        if (valueEventListenerRoom != null)
            roomsRef.removeEventListener(valueEventListenerRoom);
        if (valueEventListenerCreateRoom != null)
            roomRef.removeEventListener(valueEventListenerCreateRoom);
        if (valueEventListenerPlayersOnline != null)
            usersOnlineRef.removeEventListener(valueEventListenerPlayersOnline);
        if (valueEventListenerForInvited != null)
            usersInvitedRef.removeEventListener(valueEventListenerForInvited);
    }

    @Override
    protected void onStop() {
//        mediaPlayer.stopMediaPlayer();
        stopAllListeners();
        super.onStop();
    }

    @Override
    public void userAccepted(User OpponentPlayer) {
        database.getReference("Users/" + OpponentPlayer.getUserId() + "/").child("inviteAnswer").setValue("accepted");
        database.getReference("Users/" + Common.currentUser.getUserId() + "/").child("invited").setValue("none");

        stopAllListeners();
        roomName = OpponentPlayer.getUserName() + "'s room";
        roomRef = database.getReference("rooms/" + roomName + "/" + playerName);
        Player player = new Player(playerName);
        database.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);
        Intent intent = new Intent(LobbyActivity.this, RoomActivity.class);
        intent.putExtra("roomName", roomName);
        Common.currentRoomName = roomName;
        finish();
        startActivity(intent);
    }

    @Override
    public void userDeclined(User OpponentPlayer) {
        database.getReference("Users/" + OpponentPlayer.getUserId() + "/").child("inviteAnswer").setValue("declined");
        database.getReference("Users/" + Common.currentUser.getUserId() + "/").child("invited").setValue("none");

    }
}