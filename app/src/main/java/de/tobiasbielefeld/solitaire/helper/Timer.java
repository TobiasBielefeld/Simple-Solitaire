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

import android.os.Handler;
import android.os.Message;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles the timer, updates, saves and load the current time of playing
 */

public class Timer {

    final static String TIMER_CURRENT_TIME = "savedCurrentTime";                                    //some final strings to use with saving data
    final private static String TIMER_START_TIME = "savedStartTime";
    final private static String TIMER_SHOWN_TIME = "savedShownTime";

    long mCurrentTime;                                                                              //current system time
    private long mStartTime;                                                                        //set start and saved time both to current time
    private boolean mIsRunning;                                                                     //indicates if the timer is currently sRunning
    private TimerHandler mTimerHandler = new TimerHandler();                                        //handler to show the current time

    private void output() {                                                                         //writes the time to the textView
        mainActivity.mainTextViewTime.setText(String.format(Locale.getDefault(),                   //write then current time to the textView
                "%s: %02d:%02d:%02d",mainActivity.getString(R.string.scores_time),                 //text with "Score:"
                mCurrentTime / 3600, (mCurrentTime % 3600) / 60, (mCurrentTime % 60)));             //in hours:minutes:seconds format
    }

    public void save() {                                                                            //save the time variables
        mIsRunning = false;
        editor.putLong(TIMER_CURRENT_TIME, System.currentTimeMillis());
        editor.putLong(TIMER_START_TIME, mStartTime);
        editor.putLong(TIMER_SHOWN_TIME, mCurrentTime);
    }

    public void load() {                                                                            //load the time variables
        mIsRunning = true;
        mStartTime = savedData.getLong(TIMER_START_TIME, System.currentTimeMillis())
                + System.currentTimeMillis()
                - savedData.getLong(TIMER_CURRENT_TIME, System.currentTimeMillis());
        mTimerHandler.sendEmptyMessage(0);
    }

    void reset() {                                                                                  //resets the current time
        mIsRunning = true;
        editor.putLong(TIMER_START_TIME, System.currentTimeMillis());
        editor.putLong(TIMER_CURRENT_TIME, System.currentTimeMillis());
        editor.apply();
        mStartTime = System.currentTimeMillis();
        mTimerHandler.sendEmptyMessage(0);
    }

    private static class TimerHandler extends Handler {                                             //handler to update the time
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (timer.mIsRunning && !game.hasWon()) {                                               //if it should run (will be set to false in onPause()) and the player hasn't won
                timer.mCurrentTime = ((System.currentTimeMillis() - timer.mStartTime) / 1000);      //get the current time
                timer.mTimerHandler.sendEmptyMessageDelayed(0, 1000);                               //update every second
            }

            timer.output();                                                                         //and update the textView
        }
    }
}
