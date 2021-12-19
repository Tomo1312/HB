package com.example.hogwartsbattle.Game;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.HarryMediaPlayer;
import com.example.hogwartsbattle.CustomDialog.InviteOpponent;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;

public class RoomActivity extends AppCompatActivity {

    ListView listView;
    Button btnStartGame, btnLeaveRoom;

    ImageView room_lock, invite_opponent;
    // List<String> playersList;

    String playerName = "";
    String roomName = "";

    FirebaseDatabase databse;
    DatabaseReference plyersRef;
    String role;

    boolean room_locked = false;

    ValueEventListener valueEventListenerPlayerId, valueEventListenerPlayersInRoom, valueEventListenerStartGame;

    //boolean idSet = false;
    HarryMediaPlayer mediaPlayer;

    TextView opponent_name, own_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        databse = FirebaseDatabase.getInstance();
//        mediaPlayer = new HarryMediaPlayer(this);
//        mediaPlayer.startPlaying();
        Paper.init(this);
        setUiView();
        getPreload();
        saveRoomName();

        checkPlayersInRoomListener();
        checkIfGameIsStarted();
    }

    private void getPreload() {
        Bundle extras = getIntent().getExtras();
        roomName = extras.getString("roomName");
        playerName = Common.currentUser.getUserName();
        own_name.setText(playerName);

        //idCountReference = databse.getReference("rooms/" + roomName + "/idCount");
        if (extras != null) {
            if (roomName.equals(playerName + "'s room")) {
                Common.currentUser.setHost(true);
                role = "host";
            } else {
                Common.currentUser.setHost(false);
                role = "guest";
            }
        }

        if (role.equals("host")) {
            btnStartGame.setVisibility(View.VISIBLE);
            btnStartGame.setEnabled(true);
            room_lock.setVisibility(View.VISIBLE);
            invite_opponent.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 2;
            btnLeaveRoom.setLayoutParams(lp);
        }

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIdToPlayers();
            }
        });
        btnLeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equals("host")) {
                    killListeners();
                    Intent intent = new Intent(RoomActivity.this, LobbyActivity.class);
                    Common.currentRoomName = "";
                    finish();
                    startActivity(intent);
                    databse.getReference("rooms/" + roomName).removeValue();
                } else {
                    savePreloadData();
                    Intent intent = new Intent(RoomActivity.this, LobbyActivity.class);
                    Common.currentRoomName = "";
                    finish();
                    startActivity(intent);
                    databse.getReference("rooms/" + roomName + "/" + playerName).removeValue();
                }
            }
        });
        room_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (room_locked) {
                    room_locked = false;
                    databse.getReference("rooms/" + roomName + "/locked").setValue("false");
                    room_lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_open));
                    invite_opponent.setVisibility(View.INVISIBLE);
                } else {
                    room_locked = true;
                    databse.getReference("rooms/" + roomName + "/locked").setValue("true");
                    room_lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_locked));
                    invite_opponent.setVisibility(View.VISIBLE);
                }
            }
        });

        invite_opponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databse.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> users = snapshot.getChildren();
                        List<String> playersOnline = new ArrayList<>();
                        List<String> playersOnlineId = new ArrayList<>();
                        for (DataSnapshot snapshotTmp : users) {
                            if (snapshotTmp.child("status").getValue().equals("online") && !snapshotTmp.child("userId").getValue().equals(Common.currentUser.getUserId())) {
//                        playersOnlineId.add(snapshotTmp.getKey());
                                playersOnline.add(snapshotTmp.child("userName").getValue().toString());
                                playersOnlineId.add(snapshotTmp.child("userId").getValue().toString());
                            }
                            InviteOpponent.getInstance().showCardDialog(RoomActivity.this,playersOnline, playersOnlineId, databse);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void savePreloadData() {
        //String userId = Paper.book().read(Common.KEY_LOGGED);
        Paper.book().delete(Common.KEY_OPPONENT);
        Paper.book().delete(Common.KEY_THIS_PLAYER);
        Paper.book().delete(Common.KEY_ROOM);
        Paper.book().delete(Common.KEY_GENERAL_DECK);
        Paper.book().delete(Common.KEY_GENERAL_DECK_MAP);
        Paper.book().delete(Common.KEY_HEX_DECK);
        Paper.book().delete(Common.KEY_OWN_DECK);
        Paper.book().delete(Common.KEY_HAND);
        Paper.book().delete(Common.KEY_IS_PLAYING);
    }

    private void killListeners() {
        if (valueEventListenerStartGame != null)
            databse.getReference("rooms/" + roomName + "/startGame").removeEventListener(valueEventListenerStartGame);
        if (valueEventListenerPlayerId != null)
            databse.getReference("rooms/" + roomName).removeEventListener(valueEventListenerPlayerId);
        if (valueEventListenerPlayersInRoom != null)
            databse.getReference("rooms/" + roomName).removeEventListener(valueEventListenerPlayersInRoom);
    }

    private void setUiView() {
        btnStartGame = findViewById(R.id.btnStartGame);
        btnStartGame.setEnabled(false);
        btnStartGame.setVisibility(View.GONE);
        btnLeaveRoom = findViewById(R.id.btnLeaveRoom);

        room_lock = findViewById(R.id.room_lock);
        own_name = findViewById(R.id.own_name);
        opponent_name = findViewById(R.id.opponent_name);
        invite_opponent = findViewById(R.id.invite_opponent);
        //playersList = new ArrayList<>();
        //listView = findViewById(R.id.listPlayers);
    }

    private void saveRoomName() {
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("playerInRoom", true);
        editor.putString("roomName", roomName);
        editor.apply();
    }

    private void setIdToPlayers() {
        plyersRef = databse.getReference("rooms/" + roomName);
        databse.getReference("rooms/" + roomName + "/startGame").setValue("true");
    }

    private void checkIfGameIsStarted() {
        valueEventListenerStartGame = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue().equals("true")) {
                        killListeners();
                        Paper.book().write(Common.KEY_THIS_PLAYER, new Player());
                        Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                        intent.putExtra("roomName", roomName);
                        finish();
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databse.getReference("rooms/" + roomName + "/startGame").addValueEventListener(valueEventListenerStartGame);
    }

    private void checkPlayersInRoomListener() {
        valueEventListenerPlayersInRoom = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                btnStartGame.setEnabled(false);
                opponent_name.setText("");
//                playersList.clear();
                for (DataSnapshot snapshotTmp : snapshot.getChildren()) {
                    if (!(snapshotTmp.getKey().equals("startGame")) && !(snapshotTmp.getKey().equals("banished")) && !(snapshotTmp.getKey().equals("classroom"))
                            && !(snapshotTmp.getKey().equals("playing")) && !(snapshotTmp.getKey().equals("classroomBought")) && !(snapshotTmp.getKey().equals("locked"))
                            && !(snapshotTmp.getKey().equals("library")) && !(snapshotTmp.getKey().equals(Common.currentUser.getUserName()))) { // || !(snapshotTmp.getKey().equals("idCount"))

                        if (role.equals("host")) {
                            btnStartGame.setEnabled(true);
                            databse.getReference("rooms/" + roomName + "/playing").setValue("true");
                        }
                        opponent_name.setText(snapshotTmp.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databse.getReference("rooms/" + roomName).addValueEventListener(valueEventListenerPlayersInRoom);
    }
//    @Override
//    protected void onStop() {
////        Paper.book().write(Common.KEY_LOGGED, Common.currentUser);
////        Paper.book().write(Common.KEY_ROOM, Common.currentRoomName);
////        mediaPlayer.stopMediaPlayer();
////        if (valueEventListenerStartGame != null)
////            databse.getReference("rooms/" + roomName + "/startGame").removeEventListener(valueEventListenerStartGame);
////        if (valueEventListenerPlayerId != null)
////            databse.getReference("rooms/" + roomName).removeEventListener(valueEventListenerPlayerId);
////        if (valueEventListenerPlayersInRoom != null)
////            databse.getReference("rooms/" + roomName).removeEventListener(valueEventListenerPlayersInRoom);
////        super.onStop();
//    }
}