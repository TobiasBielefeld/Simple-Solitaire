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

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Handler to update the current time and show it
 */

public class HandlerTimer extends Handler {

    private GameManager gm;

    public HandlerTimer(GameManager gm) {
        this.gm = gm;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        //is always called at least once a game started, because this gets executed before the
        //won variable in gameLogic was loaded
        if (timer.isRunning() && !gameLogic.hasWon()) {
            timer.setCurrentTime((System.currentTimeMillis() - timer.getStartTime()) / 1000);
            timer.handlerTimer.sendEmptyMessageDelayed(0, 1000);
        }

        if (prefs.getSavedHideTime()) {
            gm.mainTextViewTime.setText("");
        }
        else {

            if (stopUiUpdates){
                return;
            }

            Long time = timer.getCurrentTime();

            gm.mainTextViewTime.setText(String.format(Locale.getDefault(),
                    "%s: %02d:%02d:%02d", gm.getString(R.string.game_time),
                    time / 3600, (time % 3600) / 60, (time % 60)));                                     //in hours:minutes:seconds format
        }
    }
}