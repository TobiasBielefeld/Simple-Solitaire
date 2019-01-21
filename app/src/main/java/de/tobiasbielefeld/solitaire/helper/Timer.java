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

import de.tobiasbielefeld.solitaire.handler.HandlerTimer;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_WINNING_TIME;

/**
 * Handles the timer, updates, saves and load the current time of playing.
 * I thought about just incrementing a counter every second using a handler, but it could be
 * not precise enough (?) so I just go the bit more complex way using the System.currentTimeMillis().
 */

public class Timer {

    public HandlerTimer handlerTimer;                                                               //handler to show the current time

    private long currentTime;                                                                       //current system time, will be "frozen" if a game has been won
    private long startTime;                                                                         //time where the game was started
    private boolean running;                                                                        //indicates if the timer currently runs
    private long winningTime;

    public Timer(GameManager gm) {
        handlerTimer = new HandlerTimer(gm);
    }

    /**
     * Returns the current playing time. If a winning time was saved, show this instead.
     *
     * The time is in seconds!! not milliseconds!
     *
     * @return The time to show on the screen
     */
    public long getCurrentTime() {
        return winningTime != 0 ? winningTime : currentTime;
    }

    //sets the time in seconds!
    public void setCurrentTime(long time) {
        currentTime = time;
    }

    /**
     * Save all necessary data to retreive the played time on the next load.
     */
    public void save() {
        if (stopUiUpdates){
            return;
        }

        running = false;
        if (!gameLogic.hasWon()) {
            prefs.saveEndTime(System.currentTimeMillis());
            prefs.saveStartTime(startTime);
        } else {
            prefs.saveWinningTime(winningTime);
        }
    }

    /**
     * Load the time, but subtract the time where the game was paused. Also load the winning time,
     * if there is one. The default is Zero, which is counted as no winning time
     */
    public void load() {
        running = true;

        startTime = prefs.getSavedStartTime() + System.currentTimeMillis() - prefs.getSavedEndTime();

        winningTime = prefs.getSavedWinningTime();

        handlerTimer.sendEmptyMessage(0);
    }

    /**
     * Reset all the data, so it will be shown as 0 seconds again.
     */
    public void reset() {
        running = true;

        prefs.saveStartTime(System.currentTimeMillis());
        prefs.saveEndTime(System.currentTimeMillis());
        prefs.saveWinningTime(DEFAULT_WINNING_TIME);

        winningTime = 0;

        startTime = System.currentTimeMillis();
        handlerTimer.sendEmptyMessage(0);
    }

    public boolean isRunning() {
        return running;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setWinningTime() {
        winningTime = currentTime;
    }

    public void setStartTime(long time){
        startTime = time;
    }
}
