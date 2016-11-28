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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Handler for the auto complete: Move a card, wait a bit, move the next one and so on.
 * After the last card is moved, start a pause handler and it calls the win animation
 */

public class AutoCompleteHandler extends Handler {

    private final static int START_TIME = 300;                                                      //start velocity of the handler callings
    private final static int DELTA_TIME = 5;                                                        //will be decreased on every call by this number
    private final static int MIN_TIME = 50;                                                         //minimum to avoid errors
    private int currentTime;                                                                        //current velocity of the handler calling
    private boolean isFinished = false;                                                             //needed to know when to call the win animation

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        //if the auto complete is finished, wait until the movement of the cards stop and then show the win animation
        if (isFinished) {
            if (animate.cardIsAnimating()) {
                autoComplete.autoCompleteHandler.sendEmptyMessageDelayed(0, currentTime);
                //animate.reset();
            }
            else {
                autoComplete.reset();
                gameLogic.testIfWon();
            }
        }
        else if (autoComplete.isRunning()) {
            int IDs[] = currentGame.autoCompleteMoveTest();

            if (IDs==null) {
                isFinished = true;
                autoComplete.autoCompleteHandler.sendEmptyMessageDelayed(0,currentTime);
            }
            else {
                moveToStack(cards[IDs[0]], stacks[IDs[1]]);
                currentTime = max(currentTime-DELTA_TIME,MIN_TIME);
                //start the next handler in some milliseconds
                autoComplete.autoCompleteHandler.sendEmptyMessageDelayed(0,currentTime);
            }
        }
    }

    public void reset(){
        isFinished=false;
        currentTime = START_TIME;
    }
}