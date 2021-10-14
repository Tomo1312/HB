package com.example.hogwartsbattle.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.HarryMediaPlayer;
import com.example.hogwartsbattle.Model.Player;
import com.example.hogwartsbattle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyActivity extends AppCompatActivity {

    ListView listView;
    Button btnCreateRoom;

    List<String> roomsList;

    String playerName;
    String roomName;

    FirebaseDatabase databse;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;

    ValueEventListener valueEventListenerRoom, valueEventListenerCreateRoom;

    Timer timer;
    HarryMediaPlayer mediaPlayer;
    ArrayList<Integer> playList;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

    mediaPlayer = new HarryMediaPlayer(this);
    mediaPlayer.startPlaying();
        databse = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("roomName");
            if (roomName != null) {
                databse.getReference("rooms/" + roomName).removeValue();
            }
        }
        setUiView();
    }

    private void setUiView() {

        playerName = Common.currentUser.getUserName();
        roomName = playerName + "'s room";
//        checkIfItsInRoom();

        listView = findViewById(R.id.listView);
        btnCreateRoom = findViewById(R.id.btnCreateRoom);

        roomsList = new ArrayList<>();
        roomsRef = databse.getReference("rooms");

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreateRoom.setText("Room created!");
                btnCreateRoom.setEnabled(false);
                roomRef = databse.getReference("rooms/" + roomName + "/" + playerName);
                databse.getReference("rooms/" + roomName + "/startGame").setValue("false");
                databse.getReference("rooms/" + roomName + "/classroom").setValue("");
                databse.getReference("rooms/" + roomName + "/classroomBought").setValue("");
                databse.getReference("rooms/" + roomName + "/banished").setValue("");
                databse.getReference("rooms/" + roomName + "/library").setValue("8");
                databse.getReference("rooms/" + roomName + "/playing").setValue("");
                Player player = new Player(playerName);
                addRoomEventListener();
                databse.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                roomName = roomsList.get(position);
                roomRef = databse.getReference("rooms/" + roomName + "/" + playerName);
                Player player = new Player(playerName);
                addRoomEventListener();
                databse.getReference("rooms/" + roomName + "/").child(playerName).setValue(player);
            }
        });
        addRoomsEventListener();
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
                listView.setAdapter(adapter);
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for (DataSnapshot snapshotTmp : rooms) {
                    if (!(snapshotTmp.getKey().equals("holder"))) {
                        if (snapshotTmp.child("startGame").getValue().equals("false")) {
                            roomsList.add(snapshotTmp.getKey());

                            adapter = new ArrayAdapter<>(LobbyActivity.this,
                                    R.layout.simple_list_lobby_layout, roomsList);
                            listView.setAdapter(adapter);
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
    protected void onStop() {
        mediaPlayer.stopMediaPlayer();
        if (valueEventListenerRoom != null)
            roomsRef.removeEventListener(valueEventListenerRoom);
        if (valueEventListenerCreateRoom != null)
            roomRef.removeEventListener(valueEventListenerCreateRoom);
        super.onStop();
    }

}