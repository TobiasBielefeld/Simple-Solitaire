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

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.autoComplete;
import static de.tobiasbielefeld.solitaire.SharedData.autoMove;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.max;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;
import static de.tobiasbielefeld.solitaire.SharedData.movingCards;
import static de.tobiasbielefeld.solitaire.SharedData.scores;

/**
 * Handler for the auto complete: Move a card, wait a bit, move the next one and so on.
 * After the last card is moved, start a pause handler and it calls the win animation
 */

public class HandlerAutoMove extends Handler {

    private final static int START_TIME = 300;                                                      //start velocity of the handler callings
    private final static int DELTA_TIME = 5;                                                        //will be decreased on every call by this number
    private final static int MIN_TIME = 50;                                                         //minimum to avoid errors
    private int currentTime;                                                                        //current velocity of the handler calling

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        //if the phase is 1 (moving on the tableau) wait until the moving animation is over
        if (animate.cardIsAnimating()) {
            autoMove.handlerAutoMove.sendEmptyMessageDelayed(0, currentTime);
        }
        // else do the movement
        else if (autoMove.isRunning()) {
            CardAndStack cardAndStack = currentGame.hintTest();

            if (cardAndStack != null) {
                movingCards.reset();
                movingCards.add(cardAndStack.getCard(), 0, 0);
                movingCards.moveToDestination(cardAndStack.getStack());

                currentTime = max(currentTime - DELTA_TIME, MIN_TIME);
                //start the next handler in some milliseconds
                autoMove.handlerAutoMove.sendEmptyMessageDelayed(0, currentTime);
            } else {
                autoMove.reset();
            }
        }
    }

    public void reset() {
        currentTime = START_TIME;
    }
}