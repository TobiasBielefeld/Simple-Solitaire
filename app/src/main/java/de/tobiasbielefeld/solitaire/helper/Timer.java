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

import de.tobiasbielefeld.solitaire.handler.TimerHandler;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles the timer, updates, saves and load the current time of playing
 */

public class Timer {

    public TimerHandler timerHandler;                                                               //handler to show the current time

    private long currentTime;                                                                       //current system time
    private long startTime;                                                                         //set start and saved time both to current time
    private boolean running;                                                                        //indicates if the timer is currently runns

    public Timer(GameManager gm) {

        timerHandler = new TimerHandler(gm);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long time) {
        currentTime = time;
    }

    public void save() {
        running = false;
        if (!gameLogic.hasWon()) {
            putLong(TIMER_CURRENT_TIME, System.currentTimeMillis());
            putLong(TIMER_START_TIME, startTime);
            putLong(TIMER_SHOWN_TIME, currentTime);
        }
    }

    public void load() {
        running = true;

        startTime = getLong(TIMER_START_TIME, System.currentTimeMillis())
                + System.currentTimeMillis()
                - getLong(TIMER_CURRENT_TIME, System.currentTimeMillis());

        timerHandler.sendEmptyMessage(0);
    }

    public void reset() {
        running = true;
        putLong(TIMER_START_TIME, System.currentTimeMillis());
        putLong(TIMER_CURRENT_TIME, System.currentTimeMillis());
        startTime = System.currentTimeMillis();
        timerHandler.sendEmptyMessage(0);
    }

    public boolean isRunning() {
        return running;
    }

    public long getStartTime() {
        return startTime;
    }
}
