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

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Shows hints. It has a handler which shows up to MAX_NUMBER_OF_HINTS hints.
 *  The hint function tests the tableau and stock if a card can be moved. If so,
 *  the hint animation will be started and the card will be marked, so it won't be shown again
 *  as a hint
 */

public class Hint {

    private static final int MAX_NUMBER_OF_HINTS = 3;                                               //max number of hints which are shown when pressing the button

    private int mCounter = 0;                                                                       //counter to know how many hints were shown
    private Card[] mVisited = new Card[MAX_NUMBER_OF_HINTS];                                        //array for already shown cards in hint
    private ArrayList<Card> mCurrentCards = new ArrayList<>();                                      //array for cards to move as hint
    private HintHandler mHintHandler = new HintHandler();                                           //handler to show the hints

    public void show_hint() {                                                                       //starts the hints
        mHintHandler.sendEmptyMessage(0);                                                           //simply start the handler
    }

    private void hint() {                                                                           //shows a single hint then returns
        Card card;                                                                                  //card to test

        for (int i = 0; i <= 6; i++) {                                                              //loop through every stack on the tableau as origin

            Stack origin = stacks[i];                                                               //set the stack as origin

            if (origin.getSize() == 0 || !origin.getTopCard().isUp())                               //continue if it's empty or no card is flipped up
                continue;

            /* complete visible part of a stack to move on the tableau*/
            card = origin.getFirstUpCard();                                                         //get the first flipped up card from the stack

            if (!hasVisited(card) && !(card == origin.getCard(0) && card.getValue() == 13)          //it needs to be NOT already visited and NOT to be a king on the first position of the stack (this movement would be pretty pointless)
                    && card.getValue() != 1) {                                                      //also it shouldn't be an ace, because an ace will be placed on the foundations in the next part of this function
                for (int j = 0; j <= 6; j++) {                                                      //then loop through every other tableau stack as destination
                    if (j == i)
                        continue;                                                                   //if the destination and origin are the same, continue

                    if (card.test(stacks[j])) {                                                     //then test the card with the destination, if it can be placed there...
                        move(card, stacks[j]);                                                      //move the card
                        return;                                                                     //and return
                    }
                }
            }

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();                                                             //in this part, get the top card of a stack

            if (!hasVisited(card)) {                                                                //if this card hasn't been visited
                for (int j = 7; j <= 10; j++) {                                                     //loop through every foundation stack as destination
                    if (card.test(stacks[j])) {                                                     //then test

                        move(card, stacks[j]);                                                      //move
                        return;                                                                     //and return
                    }
                }
            }

        }

        /* card from trash of stock to every other stack*/
        if (stacks[11].getSize() > 0 && !hasVisited(stacks[11].getTopCard())) {                     //if the trash isn't empty and hasn't been visited
            for (int j = 10; j >=0; j--) {                                                          //loop through every other stack (expect stock ofc)
                if (stacks[11].getTopCard().test(stacks[j])) {                                      //test the top card
                    move(stacks[11].getTopCard(), stacks[j]);                                       //move
                    return;                                                                         //and return
                }
            }
        }

        mCounter = MAX_NUMBER_OF_HINTS;                                                             //if this part has been reached, no cards can be shown as a hit, so set the counter to the max value so the handler stops
    }

    private void move(Card card, Stack destination) {                                               //move cards as a hint
        if (mCounter == 0)
            scores.update(Scores.HINT);

        mVisited[mCounter] = card;                                                                  //add the card to visited

        Stack origin = card.getStack();                                                             //get the origin stack
        int index = origin.getIndexOfCard(card);                                                    //and the index on the stack

        reset();                                                                                    //reset the current cards array

        for (int i = index; i < origin.getSize(); i++)                                              //and get every card on the stack from the index to the top card
            mCurrentCards.add(origin.getCard(i));                                                   //add them to the current cards

        for (int i = 0; i < mCurrentCards.size(); i++)                                              //then for every card on current cards
            animate.cardHint(mCurrentCards.get(i), i, destination);                                 //start the hint animation
    }

    private boolean hasVisited(Card test_card) {                                                    //tests if a card has been already visited
        for (int i = 0; i < mCounter; i++)                                                          //loop through the current size
            if (test_card == mVisited[i])                                                           //and test if visited
                return true;                                                                        //then return true

        return false;                                                                               //otherwise not found, so not visited, return false
    }

    private void reset() {                                                                   //resets the current cards
        mCurrentCards.clear();
    }

    public boolean isWorking() {                                                             //returns if its working, so no input should be possible while working
        return mCounter != 0;
    }

    private static class HintHandler extends Handler {                                              //shows hints, waits until a movement is done and then starts the next hint
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (hint.mCounter < MAX_NUMBER_OF_HINTS) {                                              //it hasn't reached the max number of hints
                if (!animate.cardIsAnimating()) {                                                   //look if a previous card is still animating, if not...
                    hint.hint();                                                                    //...show another hint
                    hint.mCounter++;                                                                //and increment the counter
                }

                hint.mHintHandler.sendEmptyMessageDelayed(0, 100);                                  //look in 100 ms again
            } else                                                                                  //else the max number has been reached
                hint.mCounter = 0;                                                                  //so set the counter to zero, no new handler start
        }
    }

}