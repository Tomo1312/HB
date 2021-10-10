package com.example.hogwartsbattle.Model;

import java.util.ArrayList;

public class Player {
    int deckCount, attacks, lives, deaths, id, coins, heart, discardCardSpell;
    boolean isPlaying;
    String hand;
    String playerName;
    String discarded;
    String deck;
    String ally;
    String firstPlayHand;
    String house;
    String playedCards;

    String idAllyToDiscard;
    String hexes;


    public Player(String playerName) {
        this.playerName = playerName;
        this.house = "";
        this.ally = "";
        this.deck = "";
        this.discarded = "";
        this.hand = "";
        this.deckCount = 0;
        this.attacks = 0;
        this.lives = 7;
        this.deaths = 0;
        this.firstPlayHand = "";
        this.coins = 0;
        this.playedCards = "";
        this.hexes = "";
        this.heart = 0;

        // When opponent discard your ally here will be written id
        this.idAllyToDiscard = "";
        // 0 - trigger nothing
        // 1 - discard non-hex
        // 2 - discard card
        this.discardCardSpell = 0;

    }

    public Player() {
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    public int getAttacks() {
        return attacks;
    }

    public void setAttacks(int attacks) {
        this.attacks = attacks;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lifes) {
        this.lives = lifes;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getId() {
        return id;
    }

    public void setIdPlayer(int id) {
        this.id = id;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }

    public String getDiscarded() {
        return discarded;
    }

    public void setDiscarded(String discarded) {
        this.discarded = discarded;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public String getAlly() {
        return ally;
    }

    public void setAlly(String ally) {
        this.ally = ally;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFirstPlayHand() {
        return firstPlayHand;
    }

    public void setFirstPlayHand(String firstPlayHand) {
        this.firstPlayHand = firstPlayHand;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(String notHandNotDiscarded) {
        this.playedCards = notHandNotDiscarded;
    }

    public String getHexes() {
        return hexes;
    }

    public void setHexes(String hexes) {
        this.hexes = hexes;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public String getIdAllyToDiscard() {
        return idAllyToDiscard;
    }

    public void setIdAllyToDiscard(String idAllyToDiscard) {
        this.idAllyToDiscard = idAllyToDiscard;
    }

    public int getDiscardCardSpell() {
        return discardCardSpell;
    }

    public void setDiscardCardSpell(int discardCardSpell) {
        this.discardCardSpell = discardCardSpell;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
