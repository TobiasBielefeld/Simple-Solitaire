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

import android.graphics.PointF;
import android.widget.ImageView;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.CardDrawables.*;

/*
 *  Contains everything related to cards. it uses a picture view for drawable,
 *  like loading or saving cards orientation and setting the drawable files
 */

public class Card {

    final public static String CARD_DRAWABLES = "cardDrawables";                                    //some strings for saving and loading data
    final public static String CARD_BACKGROUND = "cardBackground";
    final private static String CARD_ = "card_";

    public static int sWidth, sHeight;                                                              //sWidth and sHeight calculated in relation of the screen dimensions in Main activity
    public static int[] sDrawables;                                                                 //array with the drawables of the cards
    public static int sBackground;                                                                  //background drawable of the cards
    public ImageView mView;                                                                         //the image view of the card, for easier code not private
    private int mColor;                                                                             //1=clubs 2=hearts 3=Spades 4=diamonds
    private int mValue;                                                                             //1=ace 2,3,4,5,6,7,8,9,10, 11=joker 12=queen 13=king
    private Stack mStack;                                                                           //saves the stack where the card is placed
    private int mID;                                                                                //internal id
    private boolean mIsUp;                                                                          //indicates if the card is placed upwards or backwards
    private PointF mOldLocation = new PointF();                                                     //old location so cards can be moved back if they can't placed on a new stack

    public Card(int ID) {                                                                           //Constructor: sets the mID and mValues
        mID = ID;
        mColor = (ID / 13) + 1;                                                                     //mColors are from 1 to 4
        mValue = (ID % 13) + 1;                                                                     //mValues are from 1 to 13
    }

    public static void updateCardDrawableChoice()     {                                             //set the drawables for the cards
        switch (savedData.getInt(CARD_DRAWABLES, 1)) {
            case 1:
                sDrawables = sDrawablesClassic;
                break;
            case 2:
                sDrawables = sDrawablesAbstract;
                break;
            case 3:
                sDrawables = sDrawablesSimple;
                break;
            case 4:
                sDrawables = sDrawablesModern;
                break;
            case 5:
                sDrawables = sDrawablesDark;
                break;
        }

        for (int i = 0; i < cards.length; i++)                                                      //and update all cards which are flipped up
            if (cards[i].isUp())
                cards[i].mView.setImageResource(sDrawables[i]);
    }

    public static void updateCardBackgroundChoice() {                                               //set the drawable for the card background
        switch (savedData.getInt(CARD_BACKGROUND, 1))
        {
            case 1:
                sBackground = R.drawable.background_1;
                break;
            case 2:
                sBackground = R.drawable.background_2;
                break;
            case 3:
                sBackground = R.drawable.background_3;
                break;
            case 4:
                sBackground = R.drawable.background_4;
                break;
            case 5:
                sBackground = R.drawable.background_5;
                break;
            case 6:
                sBackground = R.drawable.background_6;
                break;
            case 7:
                sBackground = R.drawable.background_7;
                break;
            case 8:
                sBackground = R.drawable.background_8;
                break;
            case 9:
                sBackground = R.drawable.background_9;
                break;
            case 10:
                sBackground = R.drawable.background_10;
                break;
            case 11:
                sBackground = R.drawable.background_11;
                break;
            case 12:
                sBackground = R.drawable.background_12;
                break;
        }

        for (Card card : cards)                                                                     //and update all cards which are flipped down
            if (!card.isUp())
                card.mView.setImageResource(sBackground);
    }

    public int getID() {                                                                            //gets the mID
        return mID;
    }

    public int getValue() {                                                                         //get the mValue (1 to 13)
        return mValue;
    }

    public Stack getStack() {                                                                       //gets the stack
        return mStack;
    }

    public void setStack(Stack stack) {                                                             //sets the stack
        mStack = stack;
    }

    public void setLocation(float pX, float pY) {                                                   //sets a location with movement
        mView.bringToFront();                                                                       //bring to front

        if (mView.getX() != pX || mView.getY() != pY)                                               //only start animation when not already on destination
            animate.moveCard(this, pX, pY);
    }

    public void setLocationWithoutMovement(float pX,float pY) {                                     //sets a location without moving
        mView.bringToFront();                                                                       //bring to front
        mView.setX(pX);                                                                             //and set the coordinates
        mView.setY(pY);
    }

    public void saveOldLocation() {                                                                 //sets current location as old location
        mOldLocation.x = mView.getX();
        mOldLocation.y = mView.getY();
    }

    public void returnToOldLocation() {                                                             //return to old location
        mView.setX(mOldLocation.x);
        mView.setY(mOldLocation.y);
    }

    public void flipUp() {                                                                          //flip a card up
        mIsUp = true;
        mView.setImageResource(sDrawables[mID]);                                                    //set image to the front of a card
    }

    void flipDown() {                                                                               //flip a card down
        mIsUp = false;
        mView.setImageResource(sBackground);                                                        //set image to the background
    }

    public void flip() {                                                                            //flips a card to the other direction
        if (isUp())
            flipDown();
        else
            flipUp();
    }

    public void flipWithAnim() {                                                                    //when moving a card on the tableau, flip the card under it up, or from undo flip down
        if (!isUp()) {
            scores.move(this, getStack());
            recordList.addFlip(this);
            mIsUp = true;
            animate.flipCard(this, true);
        } else {
            scores.undo(this, getStack());
            mIsUp = false;
            animate.flipCard(this, false);
        }
    }

    public boolean isUp() {                                                                         //returns if the card is up
        return mIsUp;
    }

    public boolean test(Stack stack) {                                                              // tests if card can be placed on new stack

        if ((!isUp() || (stack.getSize() != 0 && !stack.getTopCard().isUp())) && !autoComplete.isRunning())
            return false;

        if (stack.getID() < 7) {                                                                    //tableau stacks
            if (stack.getSize() == 0)                                                               //if there is no card
                return this.mValue == 13;                                                           //you can place a king
            else
                return (stack.getTopCard().mColor % 2 != this.mColor % 2)                           //else different mColor. black is uneven and red even
                        && (stack.getTopCard().mValue == this.mValue + 1);                          //and the mValue must match

        } else if (stack.getID() < 11 && movingCards.getSize() < 2) {                               //if its an foundation stack and only one card is moving (<2 because for eg undo, movingCards size is zero)
            if (stack.getSize() == 0)                                                               //if there is no card, you can place an ace
                return this.mValue == 1;
            else
                return (stack.getTopCard().mColor == this.mColor)                                   //else has to be the same colour
                        && (stack.getTopCard().mValue == this.mValue - 1);                          // and mValue has to match
        } else                                                                                      //else not place-able
            return false;//*/
    }

    public void save() {                                                                            //saves the direction of a card
        editor.putBoolean(CARD_ + mID, mIsUp);
    }

    public void load() {                                                                            //loads the  direction of a card
        if (savedData.getBoolean(CARD_ + mID, false))
            flipUp();
        else
            flipDown();
    }
}