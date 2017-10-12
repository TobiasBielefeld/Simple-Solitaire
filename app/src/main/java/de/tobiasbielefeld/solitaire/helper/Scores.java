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

/**
 * Handles scoring. It has two methods which tests movements and update the score.
 * Also high scores are saved and updated
 */

public class Scores {

    public final static int MAX_SAVED_SCORES = 10;                                                  //set how many scores will be saved and shown

    private long score;                                                                             //the current score
    private long preBonus;
    private long bonus;
    private long savedScores[][] = new long[MAX_SAVED_SCORES][3];                                   //array to hold the saved scores with score and time
    private GameManager gm;

    public Scores(GameManager gm) {
        this.gm = gm;
    }

    /**
     * Adds scores of the given movement to the scores.
     * The origin of the cards is their current stack.
     *
     * @param card  The card of the movement
     * @param stack The destination of the movement
     */
    public void move(Card card, Stack stack) {
        ArrayList<Card> cardArray = new ArrayList<>();
        cardArray.add(card);
        ArrayList<Stack> stackArray = new ArrayList<>();
        stackArray.add(stack);

        move(cardArray, stackArray);
    }

    /**
     * Adds scores of the given movement to the scores.
     * The origins of the cards are their current stacks
     *
     * @param cards  The cards of the movement
     * @param stacks The destinations of the movement
     */
    public void move(ArrayList<Card> cards, ArrayList<Stack> stacks) {
        int[] originIDs = new int[cards.size()];
        int[] destinationIDs = new int[stacks.size()];

        for (int i = 0; i < originIDs.length; i++) {
            originIDs[i] = cards.get(i).getStackId();
            destinationIDs[i] = stacks.get(i).getId();
        }

        int points = currentGame.addPointsToScore(cards, originIDs, destinationIDs, false);

        update(points);
    }

    /**
     * Reverts scores of the given movement from the scores.
     * It uses the move() method, but destination and origin are changed and the result is negated.
     *
     * @param card  The card of the movement
     * @param stack The destination of the movement
     */
    public void undo(Card card, Stack stack) {
        ArrayList<Card> cardArray = new ArrayList<>();
        cardArray.add(card);
        ArrayList<Stack> stackArray = new ArrayList<>();
        stackArray.add(stack);

        undo(cardArray, stackArray);
    }

    /**
     * Reverts scores of the given movement from the scores.
     * It uses the move() method, but destination and origin are changed and the result is negated.
     *
     * @param cards  The cards of the movement
     * @param stacks The destinations of the movement
     */
    public void undo(ArrayList<Card> cards, ArrayList<Stack> stacks) {
        int[] originIDs = new int[cards.size()];
        int[] destinationIDs = new int[stacks.size()];

        for (int i = 0; i < originIDs.length; i++) {
            originIDs[i] = cards.get(i).getStackId();
            destinationIDs[i] = stacks.get(i).getId();
        }

        int points = -currentGame.addPointsToScore(cards, destinationIDs, originIDs, true);

        update(points);
    }

    /**
     * Updates the current score, but only if the game hasn't been won.
     *
     * @param points The points to add
     */
    public void update(int points) {
        if (gameLogic.hasWon())
            return;

        score += points;
        output();
    }

    /**
     * Updates the current score, but only if the game hasn't been won.
     *
     * @param points The points to add
     */
    public void update(long points) {
        if (gameLogic.hasWon())
            return;

        score += points;
        output();
    }

    /**
     * Adds a bonus to the score, used after a game has been won
     */
    public void updateBonus() {
        long currentTime =  timer.getCurrentTime();                                          //in seconds
        preBonus = score;

        if (currentGame.isBonusEnabled() && currentTime>0 && score>0) {
            bonus = 20 * (score/currentTime);
            //bonus = max((int) (2 * score - (5 * timer.getCurrentTime() / 1000)), 0);
            update(bonus);
        } else {
            bonus = 0;
        }
    }

    public void save() {
        prefs.saveScore(score);
    }

    /**
     * Adds a new high score to the list. New score will be inserted at the last position
     * and moved in direction of the highest score until it is in the correct position
     */
    public void addNewHighScore(long newScore, long timeTaken) {
        if (!currentGame.processScore(newScore) || newScore < 0){
            return;
        }

        long systemTime = System.currentTimeMillis();
        int index = MAX_SAVED_SCORES - 1;

        //Override the last score when the following conditions are fulfilled:
        //The new score is larger than the saved one OR
        //the new score is the same as the saved one BUT the time taken for the game is less than or equals the saved one OR
        //The saved score equals zero (so it is empty, nothing saved yet)
        if (newScore > savedScores[index][0] || savedScores[index][0] == 0 ||
                (newScore == savedScores[index][0] && timeTaken <= savedScores[index][1])) {
            savedScores[index] = new long[]{newScore, timeTaken, systemTime};

            while (index > 0 && (savedScores[index - 1][0] == 0                                     //while the index is greater than 0 and the score before the index is empty
                    || savedScores[index - 1][0] < savedScores[index][0]                            //or the score at index is less than the score before it
                    || (savedScores[index - 1][0] == savedScores[index][0]                          //or the scores are the same...
                    && savedScores[index - 1][1] >= savedScores[index][1]))) {                      //but the time is less
                long dummy[] = savedScores[index];
                savedScores[index] = savedScores[index - 1];
                savedScores[index - 1] = dummy;

                index--;
            }

            prefs.saveHighScores(savedScores);
        }
    }


    /**
     * Adds a new high score to the list. New score will be inserted at the last position
     * and moved in direction of the highest score until it is in the correct position
     */
    public void addNewHighScore() {
        addNewHighScore(score,timer.getCurrentTime());
    }

    /**
     * Loads the saved high score list
     */
    public void load() {
        score = prefs.getSavedScore();
        output();
        savedScores = prefs.getSavedHighScores();
    }

    /**
     * Resets the current score and updates the shown number
     */
    public void reset() {
        score = 0;
        preBonus = 0;
        bonus = 0;
        output();
    }

    /**
     * Deletes the high scores by just creating a new empty array and save it
     */
    public void deleteHighScores() {
        savedScores = new long[MAX_SAVED_SCORES][3];
        prefs.saveHighScores(savedScores);
    }

    /**
     * Gets the score
     *
     * @param i The index of the record
     * @param j THe part of the record:
     *          0 = score, 1 = time taken, 2 = date
     * @return The requested value
     */
    public long get(int i, int j) {
        //get the score/time from the array
        return savedScores[i][j];
    }

    public void output() {
        gm.mainTextViewScore.post(new Runnable() {
            public void run() {
                if (prefs.getSavedHideScore()) {
                            gm.mainTextViewScore.setText("");
                } else {
                    final String dollar = currentGame.isPointsInDollar() ? "$" : "";
                            gm.mainTextViewScore.setText(String.format("%s: %s %s",
                                    gm.getString(R.string.game_score), score, dollar));

                }
            }
        });
    }

    public long getScore(){
        return score;
    }

    public long getPreBonus(){
        return preBonus;
    }

    public long getBonus(){
        return bonus;
    }
}
