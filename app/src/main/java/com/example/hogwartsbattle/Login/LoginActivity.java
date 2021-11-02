package com.example.hogwartsbattle.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogwartsbattle.Common.Common;
import com.example.hogwartsbattle.Common.HarryMediaPlayer;
import com.example.hogwartsbattle.Game.LobbyActivity;
import com.example.hogwartsbattle.Model.User;
import com.example.hogwartsbattle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    TextView txt_eMail, txt_password, txt_Username;
    TextView eMailWand, passwordWand, usernameWand, tmpWand;
    boolean register = false;
    Button btnLogin, btnRegister;

    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    AlertDialog loading;
    FirebaseDatabase databse;
    DatabaseReference userRef;

    LinearLayout linear_layout_username;
    HarryMediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_eMail = findViewById(R.id.tvEmail);
        txt_password = findViewById(R.id.tvPassword);
        txt_Username = findViewById(R.id.tvUsername);
        eMailWand = findViewById(R.id.tvEmailWand);
        passwordWand = findViewById(R.id.tvPasswordWand);
        usernameWand = findViewById(R.id.tvUsernameWand);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        linear_layout_username = findViewById(R.id.linear_layout_username);
        mAuth = FirebaseAuth.getInstance();
        databse = FirebaseDatabase.getInstance();

        loading = new SpotsDialog.Builder().setCancelable(false).setContext(LoginActivity.this).build();
        loading.show();
        Paper.init(this);
        User user = Paper.book().read(Common.KEY_LOGGED, new User());
        if (!TextUtils.isEmpty(user.getUserId())) {

            databse.getReference("/Users/" + user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Common.currentUser = snapshot.getValue(User.class);
                    Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                    intent.putExtra(Common.KEY_USER_ID, user.getUserId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            if (loading.isShowing())
                loading.dismiss();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!register) {
                    loading.show();
                    String userEmail = txt_eMail.getText().toString();
                    String passwordTmp = txt_password.getText().toString();
                    if (checkForEmtpy())
                        validate(userEmail, passwordTmp);
                } else {
                    register = false;
                    linear_layout_username.setVisibility(View.GONE);
                }

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register) {
                    if (checkForEmtpy())
                        startEmailVerification();
                } else {
                    register = true;
                    linear_layout_username.setVisibility(View.VISIBLE);
                }

            }
        });
        txt_Username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tmpWand != null) {
                    tmpWand.setVisibility(View.INVISIBLE);
                }
                tmpWand = usernameWand;
                tmpWand.setVisibility(View.VISIBLE);
                return false;
            }
        });
        txt_eMail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tmpWand != null) {
                    tmpWand.setVisibility(View.INVISIBLE);
                }
                tmpWand = eMailWand;
                tmpWand.setVisibility(View.VISIBLE);
                return false;
            }
        });
        txt_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tmpWand != null) {
                    tmpWand.setVisibility(View.INVISIBLE);
                }
                tmpWand = passwordWand;
                tmpWand.setVisibility(View.VISIBLE);
                return false;
            }
        });

//        setMediaPlayer();
    }

    private void setMediaPlayer() {
        mediaPlayer = new HarryMediaPlayer(LoginActivity.this);
        mediaPlayer.startPlaying();
    }

    private void startEmailVerification() {
        loading.show();
        String userEmail = txt_eMail.getText().toString();
        String passwordTmp = txt_password.getText().toString();
        mAuth.createUserWithEmailAndPassword(userEmail, passwordTmp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendEmailVerification();
                } else {
                    Toast.makeText(LoginActivity.this, "Registration failed " + task.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(LoginActivity.this, "Verification mail has been send", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Verification mail has not been send", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    private void sendUserData() {
        Common.currentUser = new User(txt_Username.getText().toString(), txt_eMail.getText().toString());
        Common.currentUser.setUserId(mAuth.getUid());
        Common.currentUser.setFirstTime(true);
        userRef = databse.getReference("Users");
        User user = new User(txt_Username.getText().toString(), (mAuth.getUid()), txt_eMail.getText().toString());
        userRef.child(mAuth.getUid()).setValue(user);
        if (loading.isShowing())
            loading.dismiss();
    }

    private boolean checkForEmtpy() {
        if (TextUtils.isEmpty(txt_eMail.getText())) {
            return false;
        } else if (TextUtils.isEmpty(txt_password.getText())) {
            return false;
        }
        return true;
    }

    private void validate(String userEmail, String userPassword) {
        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkEmailVerification();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    if(loading.isShowing())
                        loading.dismiss();
                }
            }
        });

    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if (loading.isShowing())
            loading.dismiss();
        if (emailFlag) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    Common.currentUser = userProfile;
                    Paper.book().write(Common.KEY_LOGGED, Common.currentUser);
                    Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                    intent.putExtra(Common.KEY_USER_ID, firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "Verify your e-mail", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

    @Override
    protected void onDestroy() {
//        mediaPlayer.stopMediaPlayer();
        super.onDestroy();
        if (loading.isShowing())
            loading.dismiss();
    }
}