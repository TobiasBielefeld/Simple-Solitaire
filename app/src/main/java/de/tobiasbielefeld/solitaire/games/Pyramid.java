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
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.classes.Stack.ArrowDirection.LEFT;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_PYRAMID_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.helper.Preferences.PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES;

/**
 * Pyramid Solitaire! It has a lot of stacks.
 */

public class Pyramid extends Game {

    int[] stackAboveID = new int[28];

    ArrayList<Card> cardsToMove = new ArrayList<>();
    ArrayList<Stack> origins = new ArrayList<>();

    @Override
    public void reset() {
        super.reset();
        cardsToMove.clear();
    }

    public Pyramid() {
        setNumberOfDecks(1);
        setNumberOfStacks(32);

        setTableauStackIDs(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27);
        setDiscardStackIDs(29,30);
        setMainStackIDs(31);
        setDealFromID(30);

        //empty so all stacks have no spacing direction
        setDirections();

        setNumberOfRecycles(PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES,DEFAULT_PYRAMID_NUMBER_OF_RECYCLES);

        toggleRecycles(prefs.getSavedPyramidLimitedRecycles());
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardDimensions(layoutGame, 7 + 1, 5 + 1);

        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);

        int index = 0;
        for (int i = 0; i < 7; i++) {

            int startPosX = layoutGame.getWidth() / 2 - (i + 1) * Card.width / 2 - i * spacing / 2;
            int startPosY = (isLandscape ? Card.width / 4 : Card.width / 2) + i * Card.height / 2;

            for (int j = 0; j < i + 1; j++) {

                stackAboveID[index] = ((i + 1) * (i + 2)) / 2 + j;

                stacks[index].setX(startPosX + j * (spacing + Card.width));
                stacks[index].setY(startPosY);
                stacks[index].setImageBitmap(Stack.backgroundTransparent);

                index++;
            }
        }

        stacks[28].setX(stacks[21].getX() + Card.width / 2 + spacing / 2);
        stacks[28].setY(stacks[21].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2));

        stacks[29].setX(stacks[24].getX() + Card.width / 2);
        stacks[29].setY(stacks[28].getY());

        stacks[31].setX(stacks[29].getX() + Card.width + spacing);
        stacks[31].setY(stacks[28].getY());
        setArrow(stacks[31], LEFT);

        stacks[30].setX(stacks[31].getX() + Card.width + spacing);
        stacks[30].setY(stacks[28].getY());
    }

    public boolean winTest() {
        for (int i = 0; i <= getLastTableauId(); i++)
            if (!stacks[i].isEmpty())
                return false;

        return prefs.getSavedPyramidDifficulty().equals("1") || getDiscardStack().isEmpty() && stacks[30].isEmpty();
    }

    public void dealCards() {
        flipAllCardsUp();

        for (int i = 0; i < 28; i++) {
            moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
        }

        moveToStack(getDealStack().getTopCard(), getDiscardStack(), OPTION_NO_RECORD);
    }

    public boolean testIfMainStackTouched(float X, float Y) {
        return (getDealStack().isEmpty() && getDealStack().isOnLocation(X, Y)) || getMainStack().isOnLocation(X, Y);
    }

    public int onMainStackTouch() {

        if (!getDealStack().isEmpty()) {
            moveToStack(getDealStack().getTopCard(), getDiscardStack());
            return 1;

        } else if (!getDiscardStack().isEmpty()) {
            recordList.add(getDiscardStack().currentCards);

            while (getDiscardStack().getSize() > 0)
                moveToStack(getDiscardStack().getTopCard(), getDealStack(), OPTION_NO_RECORD);

            scores.update(-200);    //because of no record, it isn't updated automatically
            return 2;
        }

        return 0;
    }


    public boolean cardTest(Stack stack, Card card) {

        if (stack.getId() == 31)
            return false;

        if (stack.getId() == 28 && card.getValue() == 13)
            return true;

        if (stack.getId() != 28 && !stack.isEmpty() && stackIsFree(stack) && card.getValue() + stack.getTopCard().getValue() == 13) {

            cardsToMove.clear();
            origins.clear();

            cardsToMove.add(stack.getTopCard());
            cardsToMove.add(card);

            origins.add(stack);
            origins.add(card.getStack());

            return true;
        }

        return card.getStack() == getDealStack() && stack == getDiscardStack();
    }


    public boolean addCardToMovementGameTest(Card card) {

        if (card.getStackId() == 28)
            return false;

        if (card.getStackId() == getDiscardStack().getId())
            return true;

        Stack currentStack = card.getStack();

        return currentStack.getId() > 20 || stackIsFree(currentStack);

    }

    public CardAndStack hintTest(ArrayList<Card> visited) {

        ArrayList<Stack> freeStacks = new ArrayList<>();

        for (int i = 0; i <= getLastTableauId(); i++) {
            if (stackIsFree(stacks[i]) && !stacks[i].isEmpty() && !visited.contains(stacks[i].getTopCard()))
                freeStacks.add(stacks[i]);
        }

        //first discard stack
        if (!getDiscardStack().isEmpty() && !visited.contains(getDiscardStack().getTopCard()))
            freeStacks.add(getDiscardStack());
        //second discard stack
        if (!stacks[30].isEmpty() && !visited.contains(stacks[30].getTopCard()))
            freeStacks.add(stacks[30]);

        for (Stack stack : freeStacks) {

            if (stack.getTopCard().getValue() == 13) {
                return new CardAndStack(stack.getTopCard(), stacks[28]);
            }

            for (Stack otherStack : freeStacks) {
                if (stack.getId() == otherStack.getId())
                    continue;

                if (stackIsFree(stack) && stack.getTopCard().getValue() + otherStack.getTopCard().getValue() == 13)
                    return new CardAndStack(stack.getTopCard(), otherStack);
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {

        Stack returnStack = null;

        if (card.getValue() == 13) {
            return stacks[28];
        }

        for (int i = getLastTableauId(); i >= 0; i--) {

            if (stacks[i].isEmpty())
                continue;

            if (card.getStackId() != i && stackIsFree(stacks[i]) && card.getValue() + stacks[i].getTopCard().getValue() == 13) {
                returnStack = stacks[i];
                break;
            }
        }

        if (returnStack == null && !getDiscardStack().isEmpty() && card.getStack() != getDiscardStack() && card.getValue() + getDiscardStack().getTopCard().getValue() == 13)
            returnStack = getDiscardStack();

        if (returnStack == null && !getDealStack().isEmpty() && card.getStack() != getDealStack() && card.getValue() + getDealStack().getTopCard().getValue() == 13)
            returnStack = getDealStack();

        if (returnStack != null) {
            cardsToMove.add(returnStack.getTopCard());
            cardsToMove.add(card);

            origins.add(returnStack);
            origins.add(card.getStack());
            return returnStack;
        }

        if (card.getStack() == getDealStack())
            return getDiscardStack();

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (destinationIDs[0] == 28)
            return 50;
        else if (cards.size() > 1 && originIDs[0] == getDiscardStack().getId() && destinationIDs[0] == getDealStack().getId())
            return -200;
        else
            return 0;
    }

    public void testAfterMove() {
        if (cardsToMove.size() > 0) {
            recordList.deleteLast();
            moveToStack(cardsToMove, stacks[28], OPTION_NO_RECORD);
            recordList.add(cardsToMove, origins);
            scores.update(50);

            cardsToMove.clear();
            origins.clear();

            handlerTestAfterMove.sendDelayed();
        }
        else if (prefs.getSavedPyramidAutoMove()) {
            ArrayList<Card> tempCards = new ArrayList<>();
            ArrayList<Stack> origins = new ArrayList<>();

            for (int i=0;i<32;i++){
                if (i==28){
                    continue;
                }

                if (!stacks[i].isEmpty() && stackIsFree(stacks[i]) && stacks[i].getTopCard().getValue()==13){
                    tempCards.add(stacks[i].getTopCard());
                    origins.add(stacks[i]);
                }
            }

            if (!tempCards.isEmpty()) {
                recordList.addToLastEntry(tempCards, origins);
                moveToStack(tempCards, stacks[28], OPTION_NO_RECORD);
            }
        }
    }

    private boolean stackIsFree(Stack stack) {
        if (stack.getId() > 20)
            return true;

        Stack stackAbove1 = stacks[stackAboveID[stack.getId()]];
        Stack stackAbove2 = stacks[stackAboveID[stack.getId()] + 1];

        return stackAbove1.isEmpty() && stackAbove2.isEmpty();
    }

    /*
     * override this in your games to customize behavior
     */
    protected boolean excludeCardFromMixing(Card card){
        return card.getStack() == stacks[28];
    }
}
