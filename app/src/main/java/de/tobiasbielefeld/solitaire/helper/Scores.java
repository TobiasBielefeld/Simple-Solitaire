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
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles scoring. It has two functions which tests movements and update the score.
 *  Also high scores are saved and updated
 */

public class Scores {

    public final static int MAX_SAVED_SCORES = 10;                                                  //set how many scores will be saved and shown

    private long score;                                                                             //the current score
    private long savedScores[][] = new long[MAX_SAVED_SCORES][2];                                   //array to hold the saved scores with score and time
    private GameManager gm;

    public Scores(GameManager gm){
        this.gm = gm;
    }

    public void move(Card card, Stack stack) {
        ArrayList<Card> cardArray = new ArrayList<>();
        cardArray.add(card);
        ArrayList<Stack> stackArray = new ArrayList<>();
        stackArray.add(stack);

        move(cardArray, stackArray);
    }

    public void move(ArrayList<Card> cards, ArrayList<Stack> stacks) {
        int[] originIDs = new int[cards.size()];
        int[] destinationIDs = new int[stacks.size()];

        for (int i=0;i<originIDs.length;i++){
            originIDs[i] = cards.get(i).getStack().getID();
            destinationIDs[i] = stacks.get(i).getID();
        }

        int points = currentGame.addPointsToScore(cards, originIDs, destinationIDs);

        update(points);
    }

    public void undo(Card card, Stack stack) {
        ArrayList<Card> cardArray = new ArrayList<>();
        cardArray.add(card);
        ArrayList<Stack> stackArray = new ArrayList<>();
        stackArray.add(stack);

        undo(cardArray, stackArray);
    }

    public void undo(ArrayList<Card> cards, ArrayList<Stack> stacks) {
        /*
         *  undo is the same as move, but the destinationIDs and originIDs are reversed and the
         *  result is negated
         */

        int[] originIDs = new int[cards.size()];
        int[] destinationIDs = new int[stacks.size()];

        for (int i=0;i<originIDs.length;i++){
            originIDs[i] = cards.get(i).getStack().getID();
            destinationIDs[i] = stacks.get(i).getID();
        }

        int points = - currentGame.addPointsToScore(cards, destinationIDs, originIDs);

        update(points);
    }

    public void update(int points){
        if (gameLogic.hasWon())
            return;

        score += points;
        output();
    }

    public void updateBonus() {
        int bonus = max((int)(2 * score - (10 * timer.getCurrentTime() / 1000)), 0);

        update(bonus);
    }

    public void save() {
        putLong(SCORE, score);
    }

    private void saveHighScore() {
        ArrayList<Long> listScores = new ArrayList<>();
        ArrayList<Long> listTimes = new ArrayList<>();

        for (int i=0;i<MAX_SAVED_SCORES;i++){
            listScores.add(savedScores[i][0]);
            listTimes.add(savedScores[i][0]);
        }

        putLongList(SAVED_SCORES + 0,listScores);
        putLongList(SAVED_SCORES + 1,listTimes);
    }

    public void addNewHighScore() {
        /*
         * new score will be inserted at the last position
         * and moved left until it is in the right position
         */

        int index = MAX_SAVED_SCORES - 1;

        //if the new score is greater than the last saved one or the last one is empty, override it
        if (score > savedScores[index][0] || savedScores[index][0] == 0) {
            savedScores[index] = new long[]{score, timer.getCurrentTime()};

            while (index > 0 && (savedScores[index - 1][0] == 0                                     //while the index is greater than 0 and the score before the index is empty
                    || savedScores[index - 1][0] < savedScores[index][0]                            //or the score at index is less than the score before it
                    || (savedScores[index - 1][0] == savedScores[index][0]                          //or the scores are the same...
                    && savedScores[index - 1][1] > savedScores[index][1]))) {                       //but the time is less
                long dummy[] = savedScores[index];
                savedScores[index] = savedScores[index - 1];
                savedScores[index - 1] = dummy;

                index--;
            }

            saveHighScore();
        }
    }

    public void load() {
        score = getLong(SCORE, 0);
        output();


        ArrayList<Long> listScores = getLongList(SAVED_SCORES + 0);
        ArrayList<Long> listTimes = getLongList(SAVED_SCORES + 1);

        if (listScores.size()==0 && listTimes.size()==0){
            //in case there isn't a score list saved yet
            for (int i = 0; i < MAX_SAVED_SCORES; i++) {
                savedScores[i][0] = 0;
                savedScores[i][1] = 0;
            }

            saveHighScore();
        }
        else {
            for (int i = 0; i < MAX_SAVED_SCORES; i++) {
                savedScores[i][0] = listScores.get(i);
                savedScores[i][1] = listTimes.get(i);
            }
        }
    }

    public void reset() {
        score = 0;
        output();
    }

    public void deleteHighScores() {
        /*
         * delete the high scores by just creating a new empty array and save it
         */

        savedScores = new long[MAX_SAVED_SCORES][2];

        saveHighScore();
    }

    public long get(int i, int j) {
        //get the score/time from the array
        return savedScores[i][j];
    }

    public void output() {
        gm.mainTextViewScore.setText(String.format("%s: %s",
                    gm.getString(R.string.game_score), score));
    }
}
