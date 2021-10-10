package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

public interface ICardAddOrDeletedFromHand {
    void onBanishCard(Card banishCard);
    void onAddCard(Card newCard);
    void onDiscardCard(Card discardCard);
    void onPlayedAlly(Card Ally);

}
