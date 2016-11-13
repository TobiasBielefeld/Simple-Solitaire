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
import android.util.Log;
import android.view.View;

import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.MovingCards.*;

/*
 *  if the last card on the tableau is flipped up, the auto complete can be run. it simply test
 *  every card from the tableau and the stock if they can be placed on the foundation.
 *  it continues until the last card was moved to the foundation. after that,
 *  the win animation will be started
 */

public class AutoComplete {

    private final static int START_TIME = 300;                                                      //start velocity of the handler callings
    private final static int DELTA_TIME = 5;                                                        //will be decreased on every call by this number

    private int mCurrentTime;                                                                       //current velocity of the handler calling
    private boolean mRunning=false;                                                                 //shows if the autocomplete is still mRunning
    private AutoCompleteHandler mAutoCompleteHandler = new AutoCompleteHandler();                   //handler to run the auto complete
    private PauseHandler mPauseHandler = new PauseHandler();                                        //handler for a little pause after the autoc omplete

    void reset() {
        mRunning=false;
    }

    public void start() {                                                                           //start the autocomplete
        mCurrentTime = START_TIME;                                                                  //reset the velocity
        mRunning = true;                                                                            //set it to mRunning
        editor.putInt(AUTO_COMPLETE_SHOWN,2).apply();
        mainActivity.buttonAutoComplete.setVisibility(View.GONE);
        mAutoCompleteHandler.sendEmptyMessage(0);                                                   //and start it
    }

    public boolean isRunning() {                                                             //returns if it runs;
        return mRunning;
    }

    private static class AutoCompleteHandler extends Handler {                                      //contains the autocomplete tests
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (autoComplete.mRunning && (stacks[7].getSize() < 13 || stacks[8].getSize() < 13      //autocomplete has to be still mRunning and the stacks 7 to 10 aren't full jet
                    || stacks[9].getSize() < 13 || stacks[10].getSize() < 13)) {
                for (int i = 7; i <= 10; i++) {                                                     //foundation fields
                    Stack destination = stacks[i];                                                  //get the destination for more visibility

                    for (int j = 0; j <= 6; j++) {                                                  //tableau fields
                        Stack origin = stacks[j];                                                   //get the origin for more visibility

                        if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {        //test if there are still cards on it and if the card test is successful
                            moveToStack(origin.getTopCard(), destination);                          //then move it
                            autoComplete.mAutoCompleteHandler.sendEmptyMessageDelayed(              //start the next handler in some milliseconds
                                    0, autoComplete.mCurrentTime -= DELTA_TIME);
                            return;                                                                 //and return
                        }
                    }

                    for (int j = 11; j <= 12; j++) {                                                //stock
                        Stack origin = stacks[j];                                                   //get the origin for more visibility

                        for (int k = 0; k < origin.getSize(); k++) {                                //loop through every card
                            if (origin.getCard(k).test(destination)) {                              //then test every card
                                origin.getCard(k).flipUp();                                         //because cards are from stock, flip up
                                moveToStack(origin.getCard(k), destination);                        //then move it
                                autoComplete.mAutoCompleteHandler.sendEmptyMessageDelayed(          //start the next handler in some milliseconds
                                        0, autoComplete.mCurrentTime -= DELTA_TIME);
                                return;                                                             //and return
                            }
                        }
                    }
                }

                Log.e("AutoCompleteError","No moveable card found in AutoComplete");
                autoComplete.mRunning = false;
                editor.putInt(AUTO_COMPLETE_SHOWN,0).apply();
            } else {                                                                                //else the autocomplete is finished
                autoComplete.mPauseHandler.sendEmptyMessageDelayed(0, 100);                         //start handler to start the win animation
            }
        }
    }

    private static class PauseHandler extends Handler {                                             //tests if cards are still moving, else start win animation
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (animate.cardIsAnimating())                                                          //if something still animates
                autoComplete.mPauseHandler.sendEmptyMessageDelayed(0, 100);                         //call this handler again
            else {
                autoComplete.mRunning = false;                                                      //else set mRunning to false
                editor.putInt(AUTO_COMPLETE_SHOWN,0).apply();                                       //save the shown value to zero, so the autocomplete test can run again
                game.testIfWon();                                                                   //test if won. There the win animation will be shown
            }
        }
    }
}
