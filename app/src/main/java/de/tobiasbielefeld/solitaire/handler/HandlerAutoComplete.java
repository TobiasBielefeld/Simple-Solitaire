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

package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Handler for the auto complete: Move a card, wait a bit, move the next one and so on.
 * After the last card is moved, start a pause handler and it calls the win animation
 */

public class HandlerAutoComplete extends Handler {

    private final static int START_TIME = 300;                                                      //start velocity of the handler callings
    private final static int DELTA_TIME = 5;                                                        //will be decreased on every call by this number
    private final static int MIN_TIME = 50;                                                         //minimum to avoid errors
    private int currentTime;                                                                        //current velocity of the handler calling
    private boolean isFinished = false;                                                             //needed to know when to call the win animation
    private int phase = 1;

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        //if the phase is 1 (moving on the tableau) wait until the moving animation is over
        if (animate.cardIsAnimating() && phase == 1) {
            autoComplete.handlerAutoComplete.sendEmptyMessageDelayed(0, currentTime);
        }
        //if the auto complete is finished, wait until the movement of the cards stop and then show the win animation
        else if (isFinished) {
            if (animate.cardIsAnimating()) {
                autoComplete.handlerAutoComplete.sendEmptyMessageDelayed(0, currentTime);
            } else {
                autoComplete.reset();
                gameLogic.testIfWon();
            }
        }
        // else do the movement
        else if (autoComplete.isRunning()) {
            CardAndStack cardAndStack;

            cardAndStack = phase == 1 ? currentGame.autoCompletePhaseOne() : currentGame.autoCompletePhaseTwo();

            if (cardAndStack == null) {
                if (phase == 1) {
                    phase = 2;
                    autoComplete.handlerAutoComplete.sendEmptyMessageDelayed(0, 0);
                } else {
                    isFinished = true;
                    autoComplete.handlerAutoComplete.sendEmptyMessageDelayed(0, START_TIME);
                }
            } else {
                //if phase 1, move the card and every card above it
                if (phase == 1) {
                    ArrayList<Card> cards = new ArrayList<>();
                    Stack origin = cardAndStack.getCard().getStack();

                    for (int i = origin.getIndexOfCard(cardAndStack.getCard()); i < origin.getSize(); i++) {
                        cards.add(cardAndStack.getCard().getStack().getCard(i));
                    }

                    moveToStack(cards, cardAndStack.getStack());
                }
                //else phase 2, move only one card but without the moveToStack method, it would
                //result in card flickering
                else {
                    Card card = cardAndStack.getCard();
                    Stack destination = cardAndStack.getStack();

                    scores.move(card, destination);
                    card.removeFromCurrentStack();
                    destination.addCard(card,false);
                    card.view.bringToFront();
                    card.setLocation(destination.getX(),destination.getY());
                }

                currentTime = max(currentTime - DELTA_TIME, MIN_TIME);
                //start the next handler in some milliseconds
                autoComplete.handlerAutoComplete.sendEmptyMessageDelayed(0, currentTime);
            }
        }
    }

    public void reset() {
        phase = 1;
        isFinished = false;
        currentTime = START_TIME;
    }
}