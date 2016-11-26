/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.classes;

import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Contains everything around a stack
 */

public class Stack {

    public static int defaultSpacing;                                                               //The default space between cards, will be calculated in onCreate of the Main activity
    public static int spacingMaxHeight;                                                             //max height of the stapled cards on a stack

    public ImageView view;                                                                          //Background of the stack
    private int ID;                                                                                 //ID: 0 to 6 tableau. 7 to 10 foundations. 11 and 12 discard and Main stack
    private int spacing;                                                                            //current spacing value
    public ArrayList<Card> currentCards = new ArrayList<>();                                        //the array of cards on the stack

    public Stack(int ID) {                                                                          //Constructor: set ID
        this.ID = ID;
    }

    public void reset() {                                                                           //removes all cards
        currentCards.clear();
    }

    public void addCard(Card card) {
        card.setStack(this);
        currentCards.add(card);

        if (ID <= currentGame.getLastTableauID() ) {
            updateSpacing();
        }
        else {
            card.setLocation(view.getX(), view.getY());
        }

        if (currentGame.hasMainStack() && ID >= currentGame.getMainStack().getID()) {
            card.flipDown();
        }
        else if (currentGame.hasDiscardStack() && ID >= currentGame.getDiscardStack().getID()) {
            card.flipUp();
        }
    }

    public void removeCard(Card card) {
        currentCards.remove(currentCards.indexOf(card));

        if (ID <= currentGame.getLastTableauID())
            updateSpacing();
    }

    public Card getCard(int index) {                                                                //get card from index
        return currentCards.get(index);
    }

    public Card getTopCard() {
        if (isEmpty()){
            Log.e("Stack.getTopCard()",
                    "Stack is empty so there is no card to return! Test with isEmpty()!");
        }

        return currentCards.get(currentCards.size() - 1);
    }

    public Card getCardFromTop(int index) {
        //returns cards in reversed order
        if (isEmpty()){
            Log.e("Stack.getCardFromTop()",
                    "Stack is empty so there is no card to return! Testing with isEmpty()!");
        }

        return currentCards.get(currentCards.size() - 1 - index);
    }

    public int getID() {                                                                            //gets the ID
        return ID;
    }

    public int getIndexOfCard(Card card) {
        return currentCards.indexOf(card);
    }

    public int getSize() {                                                                          //return how many cards are on the stack
        return currentCards.size();
    }

    public boolean isOnLocation(float pX, float pY) {
        //returns if a position matches the stack cooridinates
        return pX >= view.getX() && pX <= view.getX() + view.getLayoutParams().width
                && pY >= view.getY() && pY <= view.getY() + view.getLayoutParams().height
                + (ID <= currentGame.getLastTableauID() ? spacing * currentCards.size() : 0);
    }

    public void save() {
        putInt(STACK + ID + SIZE, currentCards.size());

        for (int j = 0; j < currentCards.size(); j++)
            putInt(STACK + ID + "_" + j, currentCards.get(j).getID());
    }

    public void load() {
        reset();

        int size = getInt(STACK + ID + SIZE, -1);

        for (int j = 0; j < size; j++)
        {
            int cardID = getInt(STACK + ID + "_" + j, -1);
            addCard(cards[cardID]);
        }
    }

    private void updateSpacing() {
        //calculate new spacing
        spacing = min((spacingMaxHeight - Card.height) / (currentCards.size() + 1), defaultSpacing);
        //and update the card position
        for (int i = 0; i < currentCards.size(); i++)
            currentCards.get(i).setLocation(view.getX(), view.getY() + spacing * i);
    }

    public Card getFirstUpCard() {
        for (Card card : currentCards)
            if (card.isUp())
                return card;

        return null;
    }

    public int getFirstUpCardPos() {
        for (int i=0;i<currentCards.size();i++){
            if (currentCards.get(i).isUp())
                return i;
        }

        return -1;
    }

    public float getYPosition(int offset) {
        //y position of a card on the stack
        return view.getY() + spacing * (currentCards.size() + offset);
    }

    public boolean isEmpty() {
        return getSize()==0;
    }

}
