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

import android.widget.ImageView;

import java.util.ArrayList;

import static de.tobiasbielefeld.solitaire.SharedData.OPTION_NO_RECORD;
import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.editor;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;
import static de.tobiasbielefeld.solitaire.SharedData.recordList;
import static de.tobiasbielefeld.solitaire.SharedData.savedData;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/*
 *  Contains everything around a stack
 */

public class Stack {

    private final static String STACK_ = "stack_";                                                   //some strings for loading / saving data
    private final static String _SIZE = "_size";

    public static int sDefaultSpacing;                                                              //The default space between cards, will be calculated in onCreate of the Main activity
    public static int sSpacingMaxHeight;                                                            //max height of the stapled cards on a stack

    public ImageView mView;                                                                         //Background of the stack
    private int mID;                                                                                //mID: 0 to 6 tableau. 7 to 10 foundations. 11 and 12 discard and Main stack
    private int mSpacing;                                                                           //current spacing mValue
    private ArrayList<Card> mCurrentCards = new ArrayList<>();                                      //the array of cards on the stack

    public Stack(int ID) {                                                                          //Constructor: set mID
        mID = ID;
    }

    public static void noCards() {                                                                  //if there are no cards on the Main stack
        if (stacks[11].getSize() != 0)  {                                                           //if there are cards on stack11 which can be moved
            recordList.add(stacks[11].mCurrentCards);                                               //save the record in normal order
            scores.move(stacks[11].mCurrentCards, stacks[12]);                                      //update scores

            while (stacks[11].getSize() > 0)                                                        //then place the top card from stack11 to stack12 until it is empty
                moveToStack(stacks[11].getTopCard(), stacks[12], OPTION_NO_RECORD);
        }
    }

    public void reset() {                                                                           //removes all cards
        mCurrentCards.clear();
    }

    public void addCard(Card card) {                                                                //add a card to stack
        card.setStack(this);                                                                        //set the new stack
        mCurrentCards.add(card);                                                                    //add it

        if (mID < 7)                                                                                //if the card is set on the tableau
            updateSpacing();                                                                        //update the spacing and card positions
        else
            card.setLocation(mView.getX(), mView.getY());                                           //else just use the XY coordinates from the stack

        if (mID == 12)
            card.flipDown();                                                                        //Stock, flip cards down
        else if (mID == 11)
            card.flipUp();                                                                          //trash, flip up
    }

    public void removeCard(Card card) {                                                             //remove a card
        mCurrentCards.remove(mCurrentCards.indexOf(card));

        if (mID < 7)                                                                                //if the card has been removed from the tableau
            updateSpacing();                                                                        //update the spacing and card positions
    }

    public Card getCard(int index) {                                                                //get card from index
        return mCurrentCards.get(index);
    }

    public Card getTopCard() {                                                                      //return the top card
        return mCurrentCards.get(mCurrentCards.size() - 1);
    }

    public int getID() {                                                                            //gets the mID
        return mID;
    }

    public int getIndexOfCard(Card card) {                                                          //searches the index of a card
        return mCurrentCards.indexOf(card);                                                         //return the index, or -1 otherwise
    }

    public int getSize() {                                                                          //return how many cards are on the stack
        return mCurrentCards.size();
    }

    public boolean isOnLocation(float pX, float pY) {                                               //returns if the coordinates matches the stack
        return pX >= mView.getX() && pX <= mView.getX() + mView.getLayoutParams().width
                && pY >= mView.getY() && pY <= mView.getY() + mView.getLayoutParams().height
                + (mID < 7 ? mSpacing * mCurrentCards.size() : 0);                                  //if tableau stack, + the height of the stack, otherwise + 0
    }

    public void save() {                                                                            //saves the cards of the stack
        editor.putInt(STACK_ + mID + _SIZE, mCurrentCards.size());                                  //first save the size

        for (int j = 0; j < mCurrentCards.size(); j++)                                              //then the card ids
            editor.putInt(STACK_ + mID + "_" + j, mCurrentCards.get(j).getID());
    }

    public void load() {                                                                            //load the cards of the stack
        reset();

        int size = savedData.getInt(STACK_ + mID + _SIZE, -1);                                      //first get the size

        for (int j = 0; j < size; j++)                                                              //then loop through every card
        {
            int card_mID = savedData.getInt(STACK_ + mID + "_" + j, -1);                            //get the id
            addCard(cards[card_mID]);                                                               //and add the card
        }
    }

    private void updateSpacing() {                                                                  //update the spacing and card positions
        mSpacing = (sSpacingMaxHeight - Card.sHeight) / (mCurrentCards.size() + 1);                 //update the spacing

        if (mSpacing > sDefaultSpacing)                                                             //if the new spacing is over the default one
            mSpacing = sDefaultSpacing;                                                             //set it to default

        for (int i = 0; i < mCurrentCards.size(); i++)                                              //and update all cards with the new spacing
            mCurrentCards.get(i).setLocation(mView.getX(), mView.getY() + mSpacing * i);
    }

    public Card getFirstUpCard() {                                                                  //get the first card which is up (used for hints)
        if (getSize() == 0)                                                                         //if there aren't any cards...
            return null;                                                                            //return null

        for (Card card : mCurrentCards)                                                             //loop through every card
            if (card.isUp())                                                                        //and if a card is up
                return card;                                                                        //return it

        return null;                                                                                //at this point, there aren't any up sided cards, so return null
    }

    public float getYPosition(int offset) {                                                         //get the y position for hint cards, so they are shown with the right spacing
        return mView.getY() + mSpacing * (mCurrentCards.size() + offset);
    }
}
