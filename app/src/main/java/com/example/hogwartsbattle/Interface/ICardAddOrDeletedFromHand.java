package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

public interface ICardAddOrDeletedFromHand {
    void onDeleteCard(Card card);
    void onAddCard(Card card);
}
