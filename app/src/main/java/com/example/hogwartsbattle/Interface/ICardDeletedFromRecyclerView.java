package com.example.hogwartsbattle.Interface;

import com.example.hogwartsbattle.Model.Card;

public interface ICardDeletedFromRecyclerView {
    void onDeleteCard(Card card, int position);
    void onAddCard(Card card);
}
