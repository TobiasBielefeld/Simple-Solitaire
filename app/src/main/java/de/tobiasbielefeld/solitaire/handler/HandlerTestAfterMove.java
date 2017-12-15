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
 * Handler for the moveToStack() method. i need to wait until the card movement is done, so i use this handler
 */

public class HandlerTestAfterMove extends Handler {
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (animate.cardIsAnimating()) {
            handlerTestAfterMove.sendEmptyMessageDelayed(0, 100);
        }
        else {
            currentGame.testAfterMove();
            handlerTestIfWon.sendEmptyMessageDelayed(0, 200);

            if (!autoComplete.isRunning() && !gameLogic.hasWon())  {
                gameLogic.checkForAutoCompleteButton();
            }
        }
    }
}