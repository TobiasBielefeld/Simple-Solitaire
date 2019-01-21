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
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.*;

/**
 * Forty&Eight game! it's pretty hard to win
 */

public class FortyEight extends Game {

    public FortyEight() {
        setNumberOfDecks(2);
        setNumberOfStacks(18);

        setTableauStackIDs(0,1,2,3,4,5,6,7);
        setFoundationStackIDs(8,9,10,11,12,13,14,15);
        setDiscardStackIDs(16);
        setMainStackIDs(17);

        setMixingCardsTestMode(testMode.SAME_FAMILY);
        setNumberOfRecycles(PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES,DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES);

        toggleRecycles(prefs.getSavedFortyEightLimitedRecycles());

        setDirections(1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardWidth(layoutGame, isLandscape, 8 + 1, 8 + 4);

        int spacing = setUpHorizontalSpacing(layoutGame, 8, 9);
        int startPos = (int) (layoutGame.getWidth() / 2 - 4 * Card.width - 3.5 * spacing);

        stacks[17].view.setX((int) (layoutGame.getWidth() / 2 + 3 * Card.width + 3.5 * spacing));
        stacks[17].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        stacks[17].setImageBitmap(Stack.backgroundTalon);

        stacks[16].setX(stacks[17].getX() - spacing - Card.width);
        stacks[16].setY(stacks[17].getY());

        for (int i = 0; i < 8; i++) {
            stacks[8 + i].setX(startPos + i * (spacing + Card.width));
            stacks[8 + i].setY(stacks[17].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2));
            stacks[8 + i].setImageBitmap(Stack.background1);
        }

        for (int i = 0; i < 8; i++) {
            stacks[i].setX(startPos + i * (spacing + Card.width));
            stacks[i].setY(stacks[8].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2));
        }

    }

    public boolean winTest() {
        for (int i = 0; i < 8; i++) {
            if (stacks[8 + i].getSize() != 13) {
                return false;
            }
        }

        return true;
    }

    public void dealCards() {
        moveToStack(getDealStack().getTopCard(), getDiscardStack(), OPTION_NO_RECORD);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                stacks[i].getCard(j).flipUp();
            }
        }
    }


    public int onMainStackTouch() {

        if (!getMainStack().isEmpty()) {
            moveToStack(getMainStack().getTopCard(), getDiscardStack());
            return 1;

        } else if (getDiscardStack().getSize() != 0) {
            recordList.add(getDiscardStack().currentCards);

            while (getDiscardStack().getSize() > 0) {
                moveToStack(getDiscardStack().getTopCard(), getMainStack(), OPTION_NO_RECORD);
            }

            scores.update(-200);    //because of no record, it isn't updated automatically
            return 2;
        }

        return 0;
    }


    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 8) {
            //if there are as many cards moving as free stacks, and one of the free stacks was chosen, don't move
            int movingCards = card.getStack().getSize() - card.getIndexOnStack();

            return movingCards <= getPowerMoveCount(stack.isEmpty()) && canCardBePlaced(stack, card, SAME_FAMILY, DESCENDING);
        } else if (stack.getId() < 16 && movingCards.hasSingleCard()) {
            if (stack.isEmpty()) {
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);
            }
        }

        return false;
    }

    public boolean addCardToMovementGameTest(Card card) {
        Stack sourceStack = card.getStack();

        int startPos = max(sourceStack.getSize() - getPowerMoveCount(false), card.getStack().getIndexOfCard(card));

        return card.getStack().getIndexOfCard(card) >= startPos && testCardsUpToTop(sourceStack, startPos, SAME_FAMILY);
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {

        for (int i = 0; i < 8; i++) {

            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty()) {
                continue;
            }

            int startPos = max(sourceStack.getSize() - getPowerMoveCount(false), 0);

            for (int j = startPos; j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (visited.contains(cardToMove) || !testCardsUpToTop(sourceStack, j, SAME_FAMILY)) {
                    continue;
                }

                if (cardToMove.isTopCard()) {
                    for (int k = 8; k < 16; k++) {
                        if (cardToMove.test(stacks[k])) {
                            return new CardAndStack(cardToMove, stacks[k]);
                        }
                    }
                }

                if (cardToMove.getValue() == 13 && cardToMove.isFirstCard()) {
                    continue;
                }

                for (int k = 0; k < 8; k++) {
                    Stack destStack = stacks[k];
                    if (i == k || destStack.isEmpty()) {
                        continue;
                    }

                    if (cardToMove.test(destStack)) {
                        if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE_AND_FAMILY)) {
                            continue;
                        }

                        return new CardAndStack(cardToMove, destStack);
                    }
                }
            }
        }

        if (!getDiscardStack().isEmpty() && !visited.contains(getDiscardStack().getTopCard())) {
            Card cardToTest = getDiscardStack().getTopCard();

            for (int j = 0; j < 8; j++) {
                if (!stacks[j].isEmpty() && cardTest(stacks[j], cardToTest) && cardToTest.getValue() != 1) {
                    return new CardAndStack(cardToTest, stacks[j]);
                }

                if (stacks[j].isEmpty() && cardToTest.getValue() == 13){
                    return new CardAndStack(cardToTest, stacks[j]);
                }
            }

            for (int j = 0; j < 8; j++) {
                if (cardTest(stacks[8 + j], cardToTest)) {
                    return new CardAndStack(cardToTest, stacks[8 + j]);
                }
            }
        }

        /*if (!getDiscardStack().isEmpty() && !hint.hasVisited(getDiscardStack().getTopCard()) && getRemainingNumberOfRecycles() == 0) {
            Card cardToTest = getDiscardStack().getTopCard();

            for (int j = 0; j < 8; j++) {
                if (stacks[j].isEmpty()) {
                    return new CardAndStack(cardToTest, stacks[j]);
                }
            }
        }*/


        return findBestSequenceToMoveToEmptyStack(SAME_FAMILY);
    }

    @Override
    public Stack doubleTapTest(Card card) {

        //first foundation
        if (card.isTopCard()) {
            for (int j = 0; j < 8; j++) {
                if (cardTest(stacks[8 + j], card)) {
                    return stacks[8 + j];
                }
            }
        }

        //then non empty fields
        for (int j = 0; j < 8; j++) {
            if (cardTest(stacks[j], card) && !stacks[j].isEmpty()
                    && !(card.getStackId() <= getLastTableauId() && sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_FAMILY))) {
                return stacks[j];
            }
        }

        //then the empty fields
        for (int j = 0; j < 8; j++) {
            if (stacks[j].isEmpty() && cardTest(stacks[j], card)) {
                return stacks[j];
            }
        }


        return null;
    }

    public boolean autoCompleteStartTest() {
        for (int i = 0; i < 8; i++) {
            Stack stack = stacks[i];

            if ((!stack.isEmpty() && !stack.getCard(0).isUp()) || !testCardsUpToTop(stack, 0, DOESNT_MATTER)) {
                return false;
            }
        }

        return getMainStack().isEmpty() && getDiscardStack().isEmpty();
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i = 0; i < 8; i++) {
            if (stacks[i].isEmpty()) {
                continue;
            }

            Card cardToTest = stacks[i].getTopCard();

            for (int j = 0; j < 8; j++) {
                if (cardTest(stacks[8 + j], cardToTest)) {
                    return new CardAndStack(cardToTest, stacks[8 + j]);
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        //anywhere to foundation
        if (destinationIDs[0] >= 8 && destinationIDs[0] < 16 && (originIDs[0] < 8 || originIDs[0] >=16)) {
            return 45;
        }
        //foundation to tableau
        if (originIDs[0] >= 8 && originIDs[0] < 16 && destinationIDs[0] < 8) {
            return -60;
        }
        //discard to tableau
        if (originIDs[0] == getDiscardStack().getId() && destinationIDs[0] < 8) {
            return 60;
        }
        //redeal cards from discard to main stack
        if (originIDs[0] == getDiscardStack().getId() && destinationIDs[0] == getMainStack().getId() && originIDs.length > 0) {
            return -200;
        }

        return 0;
    }

    @Override
    public void testAfterMove() {
        //automatically move a card from main stack to discard stack, if discard stack is empty
        if (getDiscardStack().isEmpty() && !getMainStack().isEmpty()){
            recordList.addToLastEntry(getMainStack().getTopCard(), getMainStack());
            moveToStack(getMainStack().getTopCard(),getDiscardStack(), OPTION_NO_RECORD);
        }
    }

    private int getPowerMoveCount(boolean movingToEmptyStack){
        return getPowerMoveCount(new int[]{}, new int[]{0,1,2,3,4,5,6,7}, movingToEmptyStack);
    }
}
