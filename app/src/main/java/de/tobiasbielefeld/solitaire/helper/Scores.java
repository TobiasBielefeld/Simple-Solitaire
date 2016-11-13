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

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles scoring. It has two functions which tests movements and update the score.
 *  Also high scores are saved and updated
 */

public class Scores {

    private final static String SCORE = "score";                                                    //used for saving / loading data
    private final static String SAVED_SCORES_ = "savedScores_";

    public final static int MAX_SAVED_SCORES = 10;                                                  //set how many scores will be saved and shown

    static final int UNDO = 11, BONUS = 12, HINT = 13;                                              //theses are accessed from other classes
    private static final int TURN_OVER = 1, STOCK_TO_TABLEAU = 2, TRANSFER_TO_FOUNDATIONS = 3,      //some names for the actions
            FOUNDATIONS_TO_TABLEAU = 4, RE_DEAL = 5,
            RE_TURN_OVER = 6, RE_STOCK_TO_TABLEAU = 7, RE_TRANSFER_TO_FOUNDATIONS = 8,
            RE_FOUNDATIONS_TO_TABLEAU = 9, RE_RE_DEAL = 10;

    private long mScore;                                                                            //the current score
    private long mSavedScores[][] = new long[MAX_SAVED_SCORES][2];                                  //array to hold the saved scores with score and time

    public void move(Card card, Stack stack) {
        ArrayList<Card> card_array = new ArrayList<>();
        card_array.add(card);
        move(card_array, stack);
    }

    public void move(ArrayList<Card> cards, Stack stack) {                                          //scores of a movement
        if (cards.size() == 1) {
            if ((cards.get(0).getStack().getID() < 7 || cards.get(0).getStack().getID() == 11)
                    && stack.getID() >= 7 && stack.getID() <= 10)
                update(TRANSFER_TO_FOUNDATIONS);
            else if (cards.get(0).getStack().getID() == 11 && stack.getID() < 7)
                update(STOCK_TO_TABLEAU);
            else if (stack.getID() < 7 && cards.get(0).getStack().getID() >= 7
                    && cards.get(0).getStack().getID() <= 10)
                update(FOUNDATIONS_TO_TABLEAU);
            else if (cards.get(0).getStack() == stack)
                update(TURN_OVER);
        } else if (cards.get(0).getStack().getID() == 11 && stack.getID() == 12)
            scores.update(Scores.RE_DEAL);
    }

    public void undo(Card card, Stack stack) {
        ArrayList<Card> card_array = new ArrayList<>();
        card_array.add(card);
        undo(card_array, stack);
    }

    public void undo(ArrayList<Card> cards, Stack stack) {                                          //scores of a undo
        if (cards.size() == 1) {
            if ((stack.getID() < 7 || stack.getID() == 11) && cards.get(0).getStack().getID() >= 7
                    && cards.get(0).getStack().getID() <= 10)
                update(RE_TRANSFER_TO_FOUNDATIONS);
            else if (stack.getID() == 11 && cards.get(0).getStack().getID() < 7)
                update(RE_STOCK_TO_TABLEAU);
            else if (cards.get(0).getStack().getID() < 7 && stack.getID() >= 7
                    && stack.getID() <= 10)
                update(RE_FOUNDATIONS_TO_TABLEAU);
            else if (cards.get(0).getStack() == stack)
                update(RE_TURN_OVER);
        } else if (cards.get(0).getStack().getID() == 12 && stack.getID() == 11)
            scores.update(Scores.RE_RE_DEAL);
    }

    void update(int what) {                                                                         //updates the score
        if (game.hasWon())
            return;                                                                                 //do not update when won

        int add = 0;                                                                                //initialize the number

        switch (what) {                                                                             //then look whats the action is and set the number
            case TURN_OVER:
                add = 25;
                break;
            case STOCK_TO_TABLEAU:
                add = 45;
                break;
            case TRANSFER_TO_FOUNDATIONS:
                add = 60;
                break;
            case FOUNDATIONS_TO_TABLEAU:
                add = -75;
                break;
            case RE_DEAL:
                add = -200;
                break;
            case UNDO:
                add = -25;
                break;
            case RE_TURN_OVER:
                add = -25;
                break;
            case RE_STOCK_TO_TABLEAU:
                add = -45;
                break;
            case RE_TRANSFER_TO_FOUNDATIONS:
                add = -60;
                break;
            case RE_FOUNDATIONS_TO_TABLEAU:
                add = 75;
                break;
            case RE_RE_DEAL:
                add = 200;
                break;
            case HINT:
                add = -25;
                break;
            case BONUS:
                add = (int) (2 * mScore - (10 * timer.mCurrentTime / 1000));                        //bonus points calculated like this
                if (add < 0)
                    add = 0;                                                                        //no negative bonus, so set it to 0 if so
                break;
        }

        mScore += add;                                                                              //add it to the score
        output();                                                                                   //and show it on the textView
    }

    void save() {                                                                                   //save the actual score
        editor.putLong(SCORE, mScore);
    }

    void save_high_score() {                                                                        //tries to save a new high score
        /* new score will be inserted at the last positon
        and moved left until it is in the right position*/

        int index = MAX_SAVED_SCORES - 1;                                                           //get the index of the last score

        if (mScore > mSavedScores[index][0] || mSavedScores[index][0] == 0) {                       //if the new score is greater than the last saved one or the last one is empty
            mSavedScores[index] = new long[]{mScore, timer.mCurrentTime};                           //override it

            while (index > 0 && (mSavedScores[index - 1][0] == 0                                    //while the index is greater than 0 and the score before the index is empty
                    || mSavedScores[index - 1][0] < mSavedScores[index][0]                          //or the score at index is less than the score before it
                    || (mSavedScores[index - 1][0] == mSavedScores[index][0]                        //or the scores the scores are the same...
                    && mSavedScores[index - 1][1] > mSavedScores[index][1]))) {                     //but the time is less
                long dummy[] = mSavedScores[index];                                                 //swap them
                mSavedScores[index] = mSavedScores[index - 1];
                mSavedScores[index - 1] = dummy;

                index--;                                                                            //and moves one position to left
            }

            for (int i = 0; i < MAX_SAVED_SCORES; i++) {                                            //then save the scores permanently
                editor.putLong(SAVED_SCORES_ + i + 0, mSavedScores[i][0]);
                editor.putLong(SAVED_SCORES_ + i + 1, mSavedScores[i][1]);
            }
        }
    }

    void load() {                                                                                   //load current score and high scors at game start
        mScore = savedData.getLong(SCORE, 0);                                                       //get the current score
        output();                                                                                   //and show it

        for (int i = 0; i < MAX_SAVED_SCORES; i++) {                                                //and load the high scores
            mSavedScores[i][0] = savedData.getLong(SAVED_SCORES_ + i + 0, 0);
            mSavedScores[i][1] = savedData.getLong(SAVED_SCORES_ + i + 1, 0);
        }
    }

    void reset() {                                                                                  //resets the score
        mScore = 0;                                                                                 //set score to zero
        output();                                                                                   //and show it
    }

    public void delete_high_scores() {                                                              //delete high scores. (there is a button in the high score activity for that)
        mSavedScores = new long[MAX_SAVED_SCORES][2];                                               //just create a new area for the saved_scores

        for (int i = 0; i < MAX_SAVED_SCORES; i++) {                                                //and save the new empty scores
            editor.putLong(SAVED_SCORES_ + i + 0, mSavedScores[i][0]);
            editor.putLong(SAVED_SCORES_ + i + 1, mSavedScores[i][1]);
        }
    }

    public long get(int i, int j) {                                                                 //get the score/time from the array
        return mSavedScores[i][j];
    }

    private void output() {                                                                         //updates the textView
        mainActivity.mainTextViewScore.setText(String.format("%s: %s",
                    mainActivity.getString(R.string.scores_score), mScore));
    }
}
