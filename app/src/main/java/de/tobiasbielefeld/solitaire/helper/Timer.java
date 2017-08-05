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
     * @return The time to show on the screen
     */
    public long getCurrentTime() {
        return winningTime != 0 ? winningTime : currentTime;
    }

    public void setCurrentTime(long time) {
        currentTime = time;
    }

    /**
     * Save all necessary data to retreive the played time on the next load.
     */
    public void save() {
        running = false;
        if (!gameLogic.hasWon()) {
            putLong(TIMER_END_TIME, System.currentTimeMillis());
            putLong(TIMER_START_TIME, startTime);
        } else {
            putLong(TIMER_WINNING_TIME, winningTime);
        }
    }

    /**
     * Load the time, but subtract the time where the game was paused. Also load the winning time,
     * if there is one. The default is Zero, which is counted as no winning time
     */
    public void load() {
        running = true;

        startTime = getLong(TIMER_START_TIME, System.currentTimeMillis())
                + System.currentTimeMillis()
                - getLong(TIMER_END_TIME, System.currentTimeMillis());

        winningTime = getLong(TIMER_WINNING_TIME, DEFAULT_WINNING_TIME);

        handlerTimer.sendEmptyMessage(0);
    }

    /**
     * Reset all the data, so it will be shown as 0 seconds again.
     */
    public void reset() {
        running = true;
        putLong(TIMER_START_TIME, System.currentTimeMillis());
        putLong(TIMER_END_TIME, System.currentTimeMillis());

        putLong(TIMER_WINNING_TIME, DEFAULT_WINNING_TIME);
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
}
