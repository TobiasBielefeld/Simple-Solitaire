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

package de.tobiasbielefeld.solitairelite.handler;

import android.os.Handler;
import android.os.Message;

import de.tobiasbielefeld.solitairelite.R;
import de.tobiasbielefeld.solitairelite.classes.CardAndStack;
import de.tobiasbielefeld.solitairelite.helper.Hint;
import de.tobiasbielefeld.solitairelite.ui.GameManager;

import static de.tobiasbielefeld.solitairelite.SharedData.*;

/**
 * shows hints, waits until the movement is done and then starts the next hint
 */

public class HandlerHint extends Handler {

    boolean showedFirstHint = false;
    GameManager gm;

    public HandlerHint(GameManager gm){
        this.gm = gm;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (hint.getCounter() < Hint.MAX_NUMBER_OF_HINTS) {
            CardAndStack cardAndStack;

            if (!animate.cardIsAnimating()) {
                cardAndStack = currentGame.hintTest();

                if (cardAndStack == null) {
                    if (!showedFirstHint){
                        showToast(gm.getString(R.string.dialog_no_hint_available),gm);
                    }

                    hint.stop();

                } else {
                    if (!showedFirstHint) {
                        showedFirstHint = true;

                        int amount = prefs.getSavedTotalHintsShown() + 1;
                        prefs.saveTotalHintsShown(amount);
                    }

                    hint.move(cardAndStack.getCard(), cardAndStack.getStack());
                }

                hint.setCounter(hint.getCounter() + 1);
            }

            hint.handlerHint.sendEmptyMessageDelayed(0, 100);
        } else {
            showedFirstHint = false;
            hint.setCounter(0);
        }
    }
}