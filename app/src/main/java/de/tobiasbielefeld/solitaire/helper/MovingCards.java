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

package de.tobiasbielefeld.solitaire.helper;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;


import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles the input of cards to move around. When a card was touched, it adds all cards
 *  up to the stack top card. It moves the cards and also returns the cards to their old location
 *  if they can't be placed on another stack
 */

public class MovingCards {

    final static String AUTO_COMPLETE_SHOWN = "autoCompleteShown";

    private ArrayList<Card> mCurrentCards = new ArrayList<>();                                      //array list containing the current cards to move

    void reset() {                                                                                  //resets the moving cards
        mCurrentCards.clear();                                                                      //clear the array
    }

    public void add(Card card) {                                                                    //add cards to the movement
        Stack stack = card.getStack();                                                              //get the stack

        for (int i = stack.getIndexOfCard(card); i < stack.getSize(); i++) {                        //then add every card from index to end
            stack.getCard(i).saveOldLocation();                                                     //save the current location as old location, so they can be moved back if needed
            mCurrentCards.add(stack.getCard(i));                                                    //and add the card
        }
    }

    public void move(float X, float Y) {                                                            //move cards to the touch point
        for (Card card : mCurrentCards)                                                             //loop through every card...
            card.setLocationWithoutMovement(X - Card.sWidth / 2, (Y - Card.sHeight / 2)             //...and set the location
                    + mCurrentCards.indexOf(card) * Stack.sDefaultSpacing);
    }

    public void moveToDestination(Stack destination) {                                              //move cards to another stack
        Stack origin = mCurrentCards.get(0).getStack();

        moveToStack(mCurrentCards, destination);                                                    //move

        if (origin.getSize() > 0 && origin.getID() < 7 && !origin.getTopCard().isUp())              //flip the card under the first movement card, if there is any
            origin.getTopCard().flipWithAnim();

        mCurrentCards.clear();                                                                      //delete the array
        autoCompleteTest();
        game.testIfWon();                                                                           //and test if the player has won
    }

    public void returnToPos() {                                                                     //return cards if the movement wasn't successful
        for (Card card : mCurrentCards)                                                             //return every card
            card.returnToOldLocation();

        mCurrentCards.clear();                                                                      //and delete the array
    }

    public Card first() {                                                                           //get the first moving card
        return mCurrentCards.get(0);
    }

    public int getSize() {                                                                          //get the size
        return mCurrentCards.size();
    }

    public boolean hasCards() {                                                                     //tests if it has cards
        return !mCurrentCards.isEmpty();
    }

    private void autoCompleteTest() {
        if (savedData.getInt(AUTO_COMPLETE_SHOWN,0)==0) {                                           //test if the autocomplete button can be set visible
            for (int i = 0; i < 7; i++)                                                             //loop through every card on the tableau
                if (stacks[i].getSize() > 0 && !stacks[i].getCard(0).isUp())                        //if one card of the tableau is faced down
                    return;    //*/                                                                 //not won, so return

            animate.showAutoCompleteButton();
            editor.putInt(AUTO_COMPLETE_SHOWN,1).apply();
        }
    }
}
