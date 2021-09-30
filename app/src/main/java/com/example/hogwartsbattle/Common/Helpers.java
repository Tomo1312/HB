package com.example.hogwartsbattle.Common;

import android.content.Context;

import com.example.hogwartsbattle.Model.Card;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Helpers {
    public Helpers() {
    }


    public ArrayList<Card> getDeck(Context context) throws XmlPullParserException, IOException {
        XmlPullParserFactory parserFactory;
        XmlPullParser parser;
        parserFactory = XmlPullParserFactory.newInstance();
        parser = parserFactory.newPullParser();
        try {
            InputStream is = context.getAssets().open("Cards.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
        } catch (IOException e) {
        }
        ArrayList<Card> deck = new ArrayList<>();
        int eventType = parser.getEventType();
        Card currentCard = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if ("Card".equals(eltName)) {
                        currentCard = new Card();
                        deck.add(currentCard);
                    } else if (currentCard != null) {
                        if ("id".equals(eltName)) {
                            currentCard.setId(parser.nextText());
                        }else if ("name".equals(eltName)) {
                            currentCard.setName(parser.nextText());
                        } else if ("count".equals(eltName)) {
                            currentCard.setCount(parser.nextText());
                        } else if ("cost".equals(eltName)) {
                            currentCard.setCost(parser.nextText());
                        } else if ("house".equals(eltName)) {
                            currentCard.setHouse(parser.nextText());
                        } else if ("cardType".equals(eltName)) {
                            currentCard.setCardType(parser.nextText());
                        } else if ("coins".equals(eltName)) {
                            currentCard.setCoins(parser.nextText());
                        } else if ("heart".equals(eltName)) {
                            currentCard.setHeart(parser.nextText());
                        } else if ("attack".equals(eltName)) {
                            currentCard.setAttack(parser.nextText());
                        }else if ("type".equals(eltName)) {
                            currentCard.setType(parser.nextText());
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        return deck;
    }

    public ArrayList<Card> returnCardsFromString(String stringCards){

        ArrayList<Card> cardsList = new ArrayList<>();
        String[] cards = stringCards.split(",");
        for (String stringCardTmp : cards) {
            cardsList.add(Common.allCardsMap.get(Integer.valueOf(stringCardTmp)));
        }
        return cardsList;
    }
}
