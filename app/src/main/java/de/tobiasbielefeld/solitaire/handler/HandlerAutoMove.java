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

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.autoMove;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.movingCards;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;

/**
 * Handler for the auto complete: Move a card, wait a bit, move the next one and so on.
 * After the last card is moved, start a pause handler and it calls the win animation
 */

public class HandlerAutoMove extends Handler {

    private final static int DELTA_TIME = 100;
    private final static int DELTA_TIME_SHORT = 20;

    private boolean testAfterMove = false;
    private boolean movedFirstCard = false;

    private GameManager gm;

    public HandlerAutoMove(GameManager gm){
        this.gm = gm;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        // wait until the moving animation is over
        if (animate.cardIsAnimating()) {
            autoMove.handlerAutoMove.sendEmptyMessageDelayed(0, DELTA_TIME_SHORT);
        }
        //call the test after move method after every auto movement
        else if (testAfterMove) {
            currentGame.testAfterMove();
            testAfterMove = false;
            autoMove.handlerAutoMove.sendEmptyMessageDelayed(0, DELTA_TIME_SHORT);
            // else do the movement
        } else if (autoMove.isRunning()) {

            CardAndStack cardAndStack = currentGame.hintTest();

            if (cardAndStack != null && !currentGame.autoCompleteStartTest()) {
                movedFirstCard = true;
                movingCards.reset();

                if (currentGame instanceof Pyramid){    //TODO manage this in another way
                    currentGame.cardTest(cardAndStack.getStack(),cardAndStack.getCard());
                }

                movingCards.add(cardAndStack.getCard(), 0, 0);
                movingCards.moveToDestination(cardAndStack.getStack());

                testAfterMove = true;

                //start the next handler in some milliseconds
                autoMove.handlerAutoMove.sendEmptyMessageDelayed(0, DELTA_TIME);
            } else {
                if (!movedFirstCard) {
                    showToast(gm.getString(R.string.dialog_no_movement_possible),gm);
                }

                autoMove.reset();
                movedFirstCard = false;

            }
        }
    }
}