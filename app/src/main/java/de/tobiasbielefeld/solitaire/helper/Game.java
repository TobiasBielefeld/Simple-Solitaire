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

import android.util.Log;
import android.view.View;

import java.util.Random;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.MovingCards.AUTO_COMPLETE_SHOWN;

/*
 *  Contains the load and save functions of the current game and also some stuff
 *  used for the game
 */

public class Game {

    private final static String GAME_WON = "gameWon";                                                //some strings for saving / loading data
    private final static String GAME_NUMBER_OF_WON_GAMES = "gameNumberOfWonGames";
    private final static String GAME_RANDOM_CARDS_ = "gameRandomCards_";
    private final static String GAME_FIRST_RUN = "gameFirstRun";

    private final static int MODE_RE_DEAL = 1;                                                      //mode for a new game, if it's a re-deal (then don't shuffle cards again)

    private int mNumberWonGames;                                                                    //number of won games. It's shown in the high score activity
    private boolean mWon;                                                                           //shows if the player has won, needed to know if the timer can stop, or to deal new cards on game start

    private Card[] mRandomCards = new Card[cards.length];                                           //array to shuffle the cards

    public void save() {                                                                            //save the current game (called in onPause, because onDestroy is not everytime called when the app closes for some reason
        scores.save();                                                                              //saves the scores
        recordList.save();                                                                          //save the record list
        editor.putBoolean(GAME_WON, mWon);                                                          //save if won
        editor.putInt(GAME_NUMBER_OF_WON_GAMES, mNumberWonGames);                                   //save the number of won games
        /* Timer will be saved in onPause() */

        for (Stack stack : stacks)
            stack.save();                                                                           //save stacks
        for (Card card : cards)
            card.save();                                                                            //save cards

        for (int i = 0; i < mRandomCards.length; i++)                                               //save random cards
            editor.putInt(GAME_RANDOM_CARDS_ + i, mRandomCards[i].getID());

        //editor.apply() is called in onPause() (so right after this function is called)
    }

    public void load() {                                                                            //load the saved game
        boolean first_run = savedData.getBoolean(GAME_FIRST_RUN, true);                             //look up if it's the first game start
        mNumberWonGames = savedData.getInt(GAME_NUMBER_OF_WON_GAMES, 0);                            //get the number of won games
        mWon = savedData.getBoolean(GAME_WON, false);                                               //load if the saved game is won

        Card.updateCardDrawableChoice();                                                            //set the card drawables
        Card.updateCardBackgroundChoice();                                                          //and also the background drawable
        animate.reset();
        autoComplete.reset();

        if (first_run) {
            newGame();
            editor.putBoolean(GAME_FIRST_RUN, false).apply();
        } else if (mWon) {                                                                          //if the saved game has already been won
            scores.load();

            for (int i = 0; i < mRandomCards.length; i++)                                           //load random cards
                mRandomCards[i] = cards[savedData.getInt(GAME_RANDOM_CARDS_ + i, -1)];

            for (Card card : cards)
                card.setLocationWithoutMovement(mainActivity.layoutGame.getWidth(), 0);
        } else {                                                                                    //else load data
            try {
                for (Card card : cards)                                                             //set all cards to the stock, so they are dealt from there, looks better :)
                    card.setLocationWithoutMovement(stacks[12].mView.getX(), stacks[12].mView.getY());

                scores.load();                                                                      //load scores
                recordList.load();                                                                  //load record list
                timer.mCurrentTime = savedData.getLong(Timer.TIMER_CURRENT_TIME, 0);                //load the current time
                /* timer will be loaded in onResume() */

                for (Stack stack : stacks)
                    stack.load();                                                                   //load stacks
                for (Card card : cards)
                    card.load();                                                                    //load cards

                for (int i = 0; i < mRandomCards.length; i++)                                       //load random cards
                    mRandomCards[i] = cards[savedData.getInt(GAME_RANDOM_CARDS_ + i, -1)];

                if (savedData.getInt(AUTO_COMPLETE_SHOWN,0)>0)                                      //show autocomplete button if it was shown (occures when rotating screen while auto complete is running)
                   mainActivity.buttonAutoComplete.setVisibility(View.VISIBLE);
            } catch (Exception e) {                                                                 //if something went wrong
                Log.e("Loading data failed", "Some data wasn't saved properly");
                showToast(mainActivity.getString(R.string.game_load_error));
                newGame();                                                                          //just start a new game
            }
        }
    }

    private void newGame() {                                                                         //deal a new game without an option (so default)
        newGame(0);
    }

    public void newGame(int mode) {                                                                 //generate new game
        mWon = false;                                                                               //set won to false
        scores.reset();                                                                             //reset scores
        movingCards.reset();                                                                        //reset moving cards
        recordList.reset();                                                                         //reset the record list
        timer.reset();                                                                              //and also the timer

        if (savedData.getInt(AUTO_COMPLETE_SHOWN,0)>0) {
            editor.putInt(AUTO_COMPLETE_SHOWN, 0).apply();
            mainActivity.buttonAutoComplete.setVisibility(View.GONE);
        }

        for (Card card : cards)                                                                     //set all cards to the stock, so they are dealt from there, looks better :)
            card.setLocationWithoutMovement(stacks[12].mView.getX(), stacks[12].mView.getY());

        for (Stack stack : stacks)
            stack.reset();                                                                          //reset the stacks

        if (mode != MODE_RE_DEAL) {                                                                 //if MODE_RE_DEAL the player wants the cards in the same order as before, so don't shuffle them again
            mRandomCards = cards.clone();                                                           //clone cards in a new array
            randomize(mRandomCards);                                                                //randomize the new array
        }

        for (Card card : mRandomCards)
            stacks[12].addCard(card);                                                               //first add all cards to the stock
        moveToStack(stacks[12].getTopCard(), stacks[11], OPTION_NO_RECORD);                         //move one card to the discard stack of stock

        for (int i = 0; i <= 6; i++) {                                                              //distribute cards
            for (int j = 0; j < i + 1; j++) {                                                       //every stack gets as many cards as the ID+1
                moveToStack(stacks[12].getTopCard(), stacks[i], OPTION_NO_RECORD);                  //move it
                if (j == i)
                    stacks[i].getTopCard().flipUp();                                                //and if it's the last card on the stack, flip it up
            }
        }
    }

    public void testIfWon() {                                                                       //tests if a player has won
        if (mWon || autoComplete.isRunning())
            return;                                                                                 //return if already won or Autocomplete is sRunning (after autocomplete this function will be called again)

        for (int i = 7; i <= 10; i++)                                                               //loop through every card on the foundation
            if (stacks[i].getSize() != 13)                                                          //if there is one field without 13 cards
                return;                                                                             //not won, so return

        /* if the code reaches this, the player has won */
        mWon = true;                                                                                //set won to true
        mNumberWonGames++;                                                                          //increment the won counter
        scores.update(Scores.BONUS);                                                                //updates the score
        scores.save_high_score();                                                                   //and save it as a new high score
        recordList.reset();                                                                         //delete the recordList
        animate.wonAnimation();                                                                     //and show the won animation
    }

    private void randomize(Card[] Array) {                                                          //randomize a card array with Fisher–Yates shuffle
        int index;                                                                                  //index for exchange
        Card dummy;                                                                                 //dummy for exchange
        Random rand = new Random();                                                                 //pseudo random number generator

        for (int i = Array.length - 1; i > 0; i--) {                                                //and the shuffle
            if ((index = rand.nextInt(i + 1)) != i) {
                dummy = Array[i];
                Array[i] = Array[index];
                Array[index] = dummy;
            }
        }
    }

    boolean hasWon() {                                                                              //returns if the player has won
        return mWon;
    }

    public int getNumberWonGames() {                                                                //returns the number of won games
        return mNumberWonGames;
    }

    public void deleteNumberWonGames() {                                                            //reset the number of won games
        mNumberWonGames = 0;
    }
}
