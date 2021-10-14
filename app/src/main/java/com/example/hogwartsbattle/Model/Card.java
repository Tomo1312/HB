package com.example.hogwartsbattle.Model;

public class Card {
    String id, name, count, cost, house, cardType, coins, heart, attack, type;
    boolean used;
    //only for owl
    int savedGold;

    public Card() {
    }

    public Card(String id, String name, String count, String cost, String house, String cardType, String coins, String heart, String attack, String type) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.cost = cost;
        this.house = house;
        this.cardType = cardType;
        this.coins = coins;
        this.heart = heart;
        this.attack = attack;
        this.type = type;
        this.used = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coints) {
        this.coins = coints;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getSavedGold() {
        return savedGold;
    }

    public void setSavedGold() {
        this.savedGold += 1;
    }
    public void setSavedGoldToZero() {
        this.savedGold = 0;
    }
}
