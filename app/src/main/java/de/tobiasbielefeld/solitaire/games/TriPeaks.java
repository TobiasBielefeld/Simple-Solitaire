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

package de.tobiasbielefeld.solitaire.games;

import android.content.Context;
import android.content.res.Resources;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * TriPeaks is nearly the same as Golf, but with a different field layout
 */

public class TriPeaks extends Game {

    static int MAX_SAVED_RUN_RECORDS;
    //contains which stack is above another stack. So stackAboveID[0]=3 means, that above stack
    //with index 0 are the stacks with index 3 and 3+1
    int[] stackAboveID = new int[]{3, 5, 7, 9, 10, 12, 13, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26};//28
    int runCounter = 0;                                                                                 //to count how many cards are moved in one "run"
    ArrayList<Integer> savedRunRecords = new ArrayList<>();                                         //need to save the scores of recorded movements, because the class RecordList can't do that

    public TriPeaks() {

        setNumberOfDecks(1);
        setNumberOfStacks(30);

        setTableauStackIDs(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27);
        setDiscardStackIDs(28);
        setMainStackIDs(29);

        setDirections(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        setSingleTapEnabled();
    }

    @Override
    public void reset() {
        super.reset();
        runCounter = 0;
    }

    @Override
    public void save() {
        prefs.saveRunCounter(runCounter);
    }

    @Override
    public void load() {
        MAX_SAVED_RUN_RECORDS = RecordList.maxRecords;
        runCounter = prefs.getSavedRunCounter();
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardDimensions(layoutGame, 11, 6);

        int spacing = setUpHorizontalSpacing(layoutGame, 10, 11);

        int startPosX = (int) (layoutGame.getWidth() / 2 - 3.5 * Card.width - 3 * spacing);
        int startPosY = (int) ((layoutGame.getHeight() - Card.height * 4.25 - (isLandscape ? Card.height / 4 : Card.height / 2)) / 2);

        for (int i = 0; i < 28; i++) {

            if (i == 3) {
                startPosX = (int) (layoutGame.getWidth() / 2 - 4 * Card.width - 3.5 * spacing);
                startPosY = (int) ((layoutGame.getHeight() - Card.height * 4.25 - (isLandscape ? Card.height / 4 : Card.height / 2)) / 2 + 0.75 * Card.height);
            } else if (i == 9) {
                startPosX = (int) (layoutGame.getWidth() / 2 - 4.5 * Card.width - 4 * spacing);
                startPosY = (int) ((layoutGame.getHeight() - Card.height * 4.25 - (isLandscape ? Card.height / 4 : Card.height / 2)) / 2 + 1.5 * Card.height);
            } else if (i == 18) {
                startPosX = (int) (layoutGame.getWidth() / 2 - 5 * Card.width - 4.5 * spacing);
                startPosY = (int) ((layoutGame.getHeight() - Card.height * 4.25 - (isLandscape ? Card.height / 4 : Card.height / 2)) / 2 + 2.25 * Card.height);
            }

            if (i > 3 && i < 9 && (i - 1) % 2 == 0)
                startPosX += Card.width + spacing;

            stacks[i].setX(startPosX);
            stacks[i].setY(startPosY);
            stacks[i].setImageBitmap(Stack.backgroundTransparent);


            if (i < 3)
                startPosX += 3 * Card.width + 3 * spacing;
            else
                startPosX += Card.width + spacing;
        }

        stacks[28].setX(layoutGame.getWidth() / 2 - Card.width - spacing);
        stacks[28].setY(stacks[18].getY() + Card.height + (isLandscape ? Card.height / 4 : Card.height / 2));

        stacks[29].setX(stacks[28].getX() + 2 * spacing + Card.width);
        stacks[29].setY(stacks[28].getY());
    }

    public boolean winTest() {
        for (int i = 0; i <= getLastTableauId(); i++) {
            if (!stacks[i].isEmpty())
                return false;
        }

        return true;
    }

    public void dealCards() {
        for (int i = 0; i < 28; i++) {
            moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);

            if (i > 17) {
                stacks[i].getTopCard().flipUp();
            }
        }

        moveToStack(getDealStack().getTopCard(), getDiscardStack(), OPTION_NO_RECORD);
    }

    public int onMainStackTouch() {
        if (getMainStack().getSize() > 0) {
            moveToStack(getMainStack().getTopCard(), getDiscardStack());
            runCounter = 0;

            return 1;
        }

        return 0;
    }

    public boolean cardTest(Stack stack, Card card) {
        return stack == getDiscardStack() &&
                (card.getValue() == 13 && stack.getTopCard().getValue() == 1
                        || card.getValue() == 1 && stack.getTopCard().getValue() == 13
                        || (card.getValue() == stack.getTopCard().getValue() + 1
                        || card.getValue() == stack.getTopCard().getValue() - 1));
    }

    public boolean addCardToMovementGameTest(Card card) {

        return card.getStackId() != getDiscardStack().getId();
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {
        for (int i = 0; i < 28; i++) {
            if (stacks[i].isEmpty() || !stacks[i].getTopCard().isUp())
                continue;

            if (!visited.contains(stacks[i].getTopCard()) && stacks[i].getTopCard().test(getDiscardStack()))
                return new CardAndStack(stacks[i].getTopCard(), getDiscardStack());
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {

        if (card.test(getDiscardStack()))
            return getDiscardStack();

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int points = 0;

        for (int i = 0; i < originIDs.length; i++) {
            if (originIDs[i] == destinationIDs[i]) {
                points += 25;
            }
        }

        if (originIDs[0] < 28 && destinationIDs[0] == 28) {

            if (!isUndoMovement) {
                runCounter++;
                updateLongestRun(runCounter);

                if (savedRunRecords.size() >= MAX_SAVED_RUN_RECORDS) {
                    savedRunRecords.remove(0);
                }

                savedRunRecords.add(runCounter * 50);
                points += runCounter * 50;
            } else if (savedRunRecords.size()>0) {
                points += savedRunRecords.get(savedRunRecords.size() - 1);                            //getHighScore last entry
                savedRunRecords.remove(savedRunRecords.size() - 1);                                   //and remove it

                if (runCounter > 0) {
                    runCounter--;
                }
            }
        }

        return points;
    }

    public void testAfterMove() {
        for (int i = 0; i < 18; i++) {
            if (!stacks[i].isEmpty() && !stacks[i].getTopCard().isUp() && stackIsFree(stacks[i])) {
                stacks[i].getTopCard().flipWithAnim();
            }
        }
    }

    @Override
    public boolean setAdditionalStatisticsData(Resources res, TextView title, TextView value) {
        title.setText(res.getString(R.string.game_longest_run));
        value.setText(String.format(Locale.getDefault(), "%d", prefs.getSavedLongestRun()));

        return true;
    }

    @Override
    public void deleteAdditionalStatisticsData() {
        prefs.saveLongestRun(0);
    }

    private boolean stackIsFree(Stack stack) {
        if (stack.getId() > 17)
            return true;

        Stack stackAbove1 = stacks[stackAboveID[stack.getId()]];
        Stack stackAbove2 = stacks[stackAboveID[stack.getId()] + 1];

        return stackAbove1.isEmpty() && stackAbove2.isEmpty();
    }

    private void updateLongestRun(int currentRunCount) {
        if (currentRunCount > prefs.getSavedLongestRun()) {
            prefs.saveLongestRun(currentRunCount);
        }
    }

    @Override
    protected boolean excludeCardFromMixing(Card card){
        return false;
    }
}
