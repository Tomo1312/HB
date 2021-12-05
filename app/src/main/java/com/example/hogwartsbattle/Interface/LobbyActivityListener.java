package com.example.hogwartsbattle.Interface;


import com.example.hogwartsbattle.Model.User;

public interface LobbyActivityListener {
    void userAccepted(User OpponentPlayer);
    void userDeclined(User OpponentPlayer);
}
